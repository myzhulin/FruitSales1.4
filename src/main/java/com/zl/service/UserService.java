package com.zl.service;

import com.zl.pojo.DealerDO;
import com.zl.pojo.PeasantDO;
import com.zl.pojo.RoleDO;
import com.zl.pojo.UserDO;
import com.zl.util.MessageException;

/**
 * @program: FruitSales
 * @description:
 * @author: ZhuLlin
 * @create: 2019-01-07 23:14
 **/
public interface UserService {

    /** 
    * @Description: 用户登录与用户名查重 
    * @Param: [username] 
    * @return: com.zl.pojo.UserDO 
    * @Author: ZhuLin
    * @Date: 2019/1/7 
    */ 
    UserDO validateUserName(String username);

    /** 
    * @Description: 根据用户id获取果农详细信息
    * @Param: [userid] 
    * @return: com.zl.pojo.PeasantDO 
    * @Author: ZhuLin
    * @Date: 2019/1/7 
    */ 
    PeasantDO getPeasantInfo(String userid);

    /**
     * @Description: 根据用户id获取零售商详细信息
     * @Param: [userid]
     * @return: com.zl.pojo.PeasantDO
     * @Author: ZhuLin
     * @Date: 2019/1/7
     */
    DealerDO getDealerInfo(String userid);

    /** 
    * @Description: 根据当前用户的roleid获取对应的角色 
    * @Param: [roleid] 
    * @return: com.zl.pojo.RoleDO 
    * @Author: ZhuLin
    * @Date: 2019/1/8 
    */ 
    RoleDO getRoleById(Integer roleid);

    /** 
    * @Description: 修改用户密码
    * @Param: [password] 
    * @return: boolean 
    * @Author: ZhuLin
    * @Date: 2019/1/8 
    */ 
    void updateUserPassword(String userid,String username,String password) throws MessageException;

    /**
     * @Description: 重置密码
     * @Param: [userid]
     * @return: void
     * @date: 2019/1/19 16:17 
     */
    void resetUserPwd(String userid) throws MessageException;

    /*** 
    * @Description: 添加用户 
    * @Param: [userDO] 
    * @return: void 
    * @Author: ZhuLin
    * @Date: 2019/1/21 
    */ 
    void insertUser(UserDO userDO) throws MessageException;
}
