package com.zl.aop;

import com.zl.controller.BaseController;
import com.zl.pojo.LogDO;
import com.zl.pojo.UserDTO;
import com.zl.service.LogService;
import com.zl.util.DateUtils;
import com.zl.util.UuidUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.NamedThreadLocal;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


/**
 * @program: FruitSales
 * @description: 系统日志切面类
 * @author: ZhuLlin
 * @create: 2019-01-10 10:45
 **/
@Aspect
@Component
public class SystemLogAspect {
    private static final Logger logger = LoggerFactory.getLogger(SystemLogAspect.class);

    private static final ThreadLocal<Date> beginTimeThreadLocal =
            new NamedThreadLocal<Date>("ThreadLocal beginTime");
    private static final ThreadLocal<LogDO> logThreadLocal =
            new NamedThreadLocal<LogDO>("ThreadLocal LogDO");

    private static final ThreadLocal<UserDTO> currentUser=new NamedThreadLocal<UserDTO>("ThreadLocal user");

    @Autowired(required=false)
    private HttpServletRequest request;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private LogService logService;

    /**
     * Controller层切点 注解拦截
     */
    @Pointcut("@annotation(com.zl.aop.SystemControllerLog)")
    public void controllerAspect(){}

    /**
     * 前置通知 用于拦截Controller层记录用户的操作的开始时间
     * @param joinPoint 切点
     * @throws InterruptedException
     */
    @Before("controllerAspect()")
    public void doBefore(JoinPoint joinPoint) throws InterruptedException{
        Date beginTime=new Date();
        beginTimeThreadLocal.set(beginTime);//线程绑定变量（该数据只有当前请求的线程可见）
        if (logger.isDebugEnabled()){//这里日志级别为debug
            logger.debug("开始计时: {}  URI: {}", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                    .format(beginTime), request.getRequestURI());
        }

        //读取session中的用户
        currentUser.set(BaseController.getSessionUser());
    }

    /**
     * 后置通知 用于拦截Controller层记录用户的操作
     * @param joinPoint 切点
     */
    @SuppressWarnings("unchecked")
    @After("controllerAspect()")
    public void doAfter(JoinPoint joinPoint) {
        UserDTO user = currentUser.get();
        if(user !=null){
            String title="";
            String type="info";                       //日志类型(info:入库,error:错误)
            String remoteAddr=request.getRemoteAddr();//请求的IP
            String requestUri=request.getRequestURI();//请求的Uri
            String method=request.getMethod();        //请求的方法类型(post/get)
            Map<String,String[]> params=request.getParameterMap(); //请求提交的参数

            try {
                title = getControllerMethodDescription2(joinPoint);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 打印JVM信息。
            long beginTime = beginTimeThreadLocal.get().getTime();//得到线程绑定的局部变量（开始时间）
            long endTime = System.currentTimeMillis();  //2、结束时间
            if (logger.isDebugEnabled()){
                logger.debug("计时结束：{}  URI: {}  耗时： {}   最大内存: {}m  已分配内存: {}m  已分配内存中的剩余空间: {}m  最大可用内存: {}m",
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(endTime),
                        request.getRequestURI(),
                        DateUtils.formatDateTime(endTime - beginTime),
                        Runtime.getRuntime().maxMemory()/1024/1024,
                        Runtime.getRuntime().totalMemory()/1024/1024,
                        Runtime.getRuntime().freeMemory()/1024/1024,
                        (Runtime.getRuntime().maxMemory()-Runtime.getRuntime().totalMemory()+Runtime.getRuntime().freeMemory())/1024/1024);
            }

            LogDO logDO =new LogDO();
            logDO.setLogId(UuidUtils.creatUUID());
            logDO.setTitle(title);
            logDO.setType(type);
            logDO.setRemoteAddr(remoteAddr);
            logDO.setRequestUri(requestUri);
            logDO.setMethod(method);
            logDO.setMapToParams(params);
            logDO.setUsername(user.getUsername());
            Date operateDate=beginTimeThreadLocal.get();
            logDO.setOperateDate(operateDate);
            logDO.setTimeout(DateUtils.formatDateTime(endTime - beginTime));

            //1.直接执行保存操作
            //this.logService.createSystemLog(LogDO);

            //2.优化:异步保存日志
            //new SaveLogThread(LogDO, logService).start();

            //3.再优化:通过线程池来执行日志保存
            threadPoolTaskExecutor.execute(new SaveLogThread(logDO, logService));
            logThreadLocal.set(logDO);
        }

    }

    /**
     * 异常通知
     * @param joinPoint
     * @param e
     */
    @AfterThrowing(pointcut = "controllerAspect()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Throwable e) {
        LogDO logDO = logThreadLocal.get();
        if(logDO != null){
            logDO.setType("error");
            String msg = e.toString();
            int len = msg.indexOf(':');
            logDO.setException(msg.substring(0,len));
            new UpdateLogThread(logDO, logService).start();
        }
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     *
     * @param joinPoint 切点
     * @return discription
     */
    public static String getControllerMethodDescription2(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        SystemControllerLog controllerLog = method
                .getAnnotation(SystemControllerLog.class);
        String discription = controllerLog.description();
        return discription;
    }

    /** 
    * @Description: 保存日志线程
    * @Param:  
    * @return:  
    * @Author: ZhuLin
    * @Date: 2019/1/10 
    */ 
    private static class SaveLogThread implements Runnable {
        private LogDO logDO;
        private LogService logService;

        public SaveLogThread(LogDO logDO, LogService logService) {
            this.logDO = logDO;
            this.logService = logService;
        }

        @Override
        public void run() {
            logService.insertLog(logDO);
        }
    }

    /**
     * @Description: 日志更新线程
     * @Param:
     * @return:
     * @Author: ZhuLin
     * @Date: 2019/1/10
     */
    private static class UpdateLogThread extends Thread {
        private LogDO logDO;
        private LogService logService;

        public UpdateLogThread(LogDO logDO, LogService logService) {
            super(UpdateLogThread.class.getSimpleName());
            this.logDO = logDO;
            this.logService = logService;
        }

        @Override
        public void run() {
            this.logService.updateLog(logDO);
        }
    }


}
