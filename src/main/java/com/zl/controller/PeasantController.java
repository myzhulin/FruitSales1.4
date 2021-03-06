package com.zl.controller;

import com.zl.aop.SystemControllerLog;
import com.zl.pojo.*;
import com.zl.service.*;
import com.zl.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * @program: FruitSales
 * @description: 农民特有权限Controlle
 * @author: ZhuLlin
 * @create: 2019-01-13 17:55
 **/
@Controller
@RequestMapping("/peasant")
public class PeasantController {

    @Autowired
    private PeasantService peasantService;

    @Autowired
    private DealerService dealerService;

    @Autowired
    private GardenStuffService gardenStuffService;

    @Autowired
    private AccessoryService accessoryService;

    @Autowired
    private ContractService contractService;

    /**
     * @Description: 修改农民资料
     * @Param: [peasantInfo]
     * @return: com.zl.util.MessageBean
     * @Author: ZhuLin
     * @Date: 2019/1/17
     */
    @SystemControllerLog(description = "修改农民资料")
    @RequestMapping("/updatePeasant")
    @ResponseBody
    public MessageBean updatePeasant(PeasantDO peasantInfo) throws Exception{
        System.out.println(peasantInfo.toString());
        peasantService.updatePeasant(peasantInfo);
        return new MessageBean(true,Constants.SUCCESS_UPDATE);
    }

    /**
     * @Description: 跳转果蔬列表
     * @Param: []
     * @return: java.lang.String
     * @date: 2019/1/19 23:01
     */
    @RequestMapping("/gotoGardenStuffList")
    public String gotoGardenStuffList(){
        return "peasant/gardenstuffList";
    }

    /**
     * @Description: 获取果蔬列表
     * @Param: [ajaxPutPage, GardenStuffCondition]
     * @return: com.zl.util.AjaxResultPage<com.zl.pojo.GardenStuffDTO>
     * @Author: ZhuLin
     * @Date: 2019/1/22
     */
    @RequestMapping("/getGardenStuffList")
    @ResponseBody
    public AjaxResultPage<GardenStuffDTO> getGardenStuffList(AjaxPutPage<GardenStuffDTO> ajaxPutPage, GardenStuffDTO GardenStuffCondition){
        GardenStuffCondition.setGardenstuffPeasantid(BaseController.getSessionUser().getUserid());
        ajaxPutPage.setCondition(GardenStuffCondition);
        AjaxResultPage<GardenStuffDTO> result = gardenStuffService.listGardenStuff(ajaxPutPage);
        return result;
    }

    /**
     * @Description: 获取果蔬类别
     * @Param: []
     * @return: com.zl.util.AjaxResultPage<com.zl.pojo.CategoryDO>
     * @Author: ZhuLin
     * @Date: 2019/1/22
     */
    @RequestMapping("/getCategoryList")
    @ResponseBody
    public AjaxResultPage<CategoryDO> getCategoryList(AjaxPutPage<CategoryDO> ajaxPutPage){
        AjaxResultPage<CategoryDO> result = new AjaxResultPage<CategoryDO>();
        List<CategoryDO> list = gardenStuffService.listCategory(ajaxPutPage);
        result.setData(list);
        result.setCount(list.size());
        return result;
    }

    /**
     * @Description: 添加果蔬
     * @Param: [gardenStuffDTO]
     * @return: com.zl.util.MessageBean
     * @date: 2019/1/27 20:09
     */
    @SystemControllerLog(description = "添加果蔬")
    @RequestMapping("/addGardenStuff")
    @ResponseBody
    public MessageBean addGardenStuff(GardenStuffDO gardenStuffDO) throws Exception{
        gardenStuffDO.setGardenstuffPeasantid(BaseController.getSessionUser().getUserid());
        gardenStuffService.insertGardenStuff(gardenStuffDO);
        return new MessageBean(true,Constants.SUCCESS_MESSAGE);
    }

    /**
     * @Description: 修改果蔬信息
     * @Param: [gardenStuffDO]
     * @return: com.zl.util.MessageBean
     * @Author: ZhuLin
     * @Date: 2019/1/29
     */
    @SystemControllerLog(description = "修改果蔬信息")
    @RequestMapping("/updateGardenStuff")
    @ResponseBody
    public MessageBean updateGardenStuff(GardenStuffDO gardenStuffDO) throws Exception{
        gardenStuffService.updateGardenStuff(gardenStuffDO);
        return new MessageBean(true,Constants.SUCCESS_UPDATE);
    }


    /**
     * @Description: 删除果蔬
     * @Param: [id]
     * @return: com.zl.util.MessageBean
     * @Author: ZhuLin
     * @Date: 2019/1/29
     */
    @SystemControllerLog(description = "删除果蔬")
    @RequestMapping("/deleteGardenStuff")
    @ResponseBody
    public MessageBean deleteGardenStuff(String id) throws Exception{
        try {
            gardenStuffService.deleteGardenStuff(id);
        } catch (MessageException e) {
            return new MessageBean(false,e.getExceptionMsg());
        }
        return new MessageBean(true,Constants.SUCCESS_DELETE);
    }

    /**
     * @Description: 批量删除果蔬
     * @Param: [deleteId]
     * @return: com.zl.util.MessageBean
     * @date: 2019/2/4 13:38
     */
    @SystemControllerLog(description = "批量删除果蔬")
    @RequestMapping("/batchesDelGardenStuff")
    @ResponseBody
    public MessageBean batchesDelGardenStuff(@RequestParam("deleteId[]")List<String> deleteId) throws Exception{
        try {
            gardenStuffService.batchesDelGardenStuff(deleteId);
        } catch (MessageException e) {
            return new MessageBean(false,"'" + e.getExceptionCode() + "'" + e.getExceptionMsg());
        }
        return new MessageBean(true,Constants.SUCCESS_DELETE);
    }

    /**
     * @Description: 返回和果蔬对应的附属品
     * @Param: [gardenstuffId]
     * @return: com.zl.util.AjaxResultPage<com.zl.pojo.AccessoryDO>
     * @Author: ZhuLin
     * @Date: 2019/1/31
     */
    @RequestMapping("/getAccessoryList")
    @ResponseBody
    public AjaxResultPage<AccessoryDO> getAccessoryList(AjaxPutPage<AccessoryDO> ajaxPutPage,AccessoryDO accessoryDO){
        ajaxPutPage.setCondition(accessoryDO);
        AjaxResultPage<AccessoryDO> result = accessoryService.listAccessoryByGardenId(ajaxPutPage);
        return result;
    }

    /**
     * @Description: 修改附属品信息
     * @Param: [accessoryDO]
     * @return: com.zl.util.MessageBean
     * @Author: ZhuLin
     * @Date: 2019/1/31
     */
    @SystemControllerLog(description = "修改附属品信息")
    @RequestMapping("/updateAccessory")
    @ResponseBody
    public MessageBean updateAccessory(AccessoryDO accessoryDO) throws Exception{
        accessoryService.updateAccessory(accessoryDO);
        return new MessageBean(true,Constants.SUCCESS_UPDATE);
    }

    /**
     * @Description: 删除附属品
     * @Param: [id]
     * @return: com.zl.util.MessageBean
     * @Author: ZhuLin
     * @Date: 2019/1/31
     */
    @SystemControllerLog(description = "删除附属品")
    @RequestMapping("/deleteAccessory")
    @ResponseBody
    public MessageBean deleteAccessory(String id) throws Exception{
        accessoryService.deleteAccessory(id);
        return new MessageBean(true,Constants.SUCCESS_DELETE);
    }

    /**
     * @Description: 添加附属品
     * @Param: [accessoryDO]
     * @return: com.zl.util.MessageBean
     * @Author: ZhuLin
     * @Date: 2019/1/31
     */
    @SystemControllerLog(description = "添加附属品")
    @RequestMapping("/addAccessory")
    @ResponseBody
    public MessageBean addAccessory(AccessoryDO accessoryDO) throws Exception{
        accessoryDO.setAccessoryId(UuidUtils.creatUUID());
        accessoryDO.setAccessoryTime(new Timestamp(new Date().getTime()));
        accessoryService.insertAccessory(accessoryDO);
        return new MessageBean(true,Constants.SUCCESS_MESSAGE);
    }

    /**
     * @Description: 跳转零售商列表
     * @Param: []
     * @return: java.lang.String
     * @date: 2019/1/19 23:01
     */
    @RequestMapping("/gotoDealerList")
    public String gotoDealerList(){
        return "peasant/dealerList";
    }

    /**
     * @Description: 获取零售商列表
     * @Param: [ajaxPutPage, dealerCondition]
     * @return: com.zl.util.AjaxResultPage<com.zl.pojo.DealerDO>
     * @Author: ZhuLin
     * @Date: 2019/1/22
     */
    @RequestMapping("/getDealerList")
    @ResponseBody
    public AjaxResultPage<DealerDO> getDealerList(AjaxPutPage<DealerDO> ajaxPutPage, DealerDO dealerCondition){
        ajaxPutPage.setCondition(dealerCondition);
        AjaxResultPage<DealerDO> result = dealerService.listDealer(ajaxPutPage);
        return result;
    }

    /** 
    * @Description: 返回对应零售商的签约合同数 
    * @Param: [dealerId] 
    * @return: com.zl.util.MessageBean 
    * @Author: ZhuLin
    * @Date: 2019/2/15 
    */ 
    @RequestMapping("/getContractCountByDealer")
    @ResponseBody
    public MessageBean getContractCountByDealer(String dealerId){
        Integer count = contractService.contractCountByDealerID(dealerId);
        return new MessageBean(true,null,count);
    }

    /**
     * @Description: 跳转合同列表界面
     * @Param: []
     * @return: java.lang.String
     * @date: 2019/2/5 20:12
     */
    @RequestMapping("/gotoContractList")
    public String gotoContractList(){
        return "peasant/contractList";
    }

    /**
     * @Description: 返回合同列表信息
     * @Param: [ajaxPutPage, contractCondition]
     * @return: com.zl.util.AjaxResultPage<com.zl.pojo.ContractDO>
     * @date: 2019/2/5 15:41
     */
    @RequestMapping("/getContractList")
    @ResponseBody
    public AjaxResultPage<ContractDTO> getContractList(AjaxPutPage<ContractDTO> ajaxPutPage, ContractDTO contractCondition){
        contractCondition.setPeasantId(BaseController.getSessionUser().getUserid());
        ajaxPutPage.setCondition(contractCondition);
        AjaxResultPage<ContractDTO> result = contractService.listContract(ajaxPutPage);
        return result;
    }

    /**
     * @Description: 返回合同详情
     * @Param: [contractId]
     * @return: com.zl.pojo.ContractVO
     * @date: 2019/2/8 12:23
     */
    @RequestMapping("/getContractInfo")
    @ResponseBody
    public MessageBean getContractInfo(String contractId){
        ContractVO contractVO = contractService.getContractInfo(contractId);
        return new MessageBean(true,null,contractVO);
    }

    /**
     * @Description: 添加合同
     * @Param: [ContractDO]
     * @return: com.zl.util.MessageBean
     * @date: 2019/4/24 21:25 
     */
    @SystemControllerLog(description = "添加合同")
    @RequestMapping("/addContract")
    @ResponseBody
    public MessageBean addContract(ContractDO contractDO,@RequestParam("TCdataId[]")List<String> TCdataId,@RequestParam("TCNumber[]")List<BigDecimal> TCNumber) throws Exception{
        contractDO.setPeasantId(BaseController.getSessionUser().getUserid());
        contractService.insertContractAndMiddle(contractDO,TCdataId,TCNumber);
        return new MessageBean(true,Constants.SUCCESS_MESSAGE);
    }

    /**
     * @Description: 库存验证
     * @Param: [gardenStuffDTO]
     * @return: com.zl.util.MessageBean
     * @date: 2019/5/14 11:02 
     */
    @RequestMapping("/gardenstuffNumberCheck")
    @ResponseBody
    public MessageBean gardenstuffNumberCheck(GardenStuffDTO gardenStuffDTO){
        boolean flag = gardenStuffService.gardenstuffNumberCheck(gardenStuffDTO.getGardenstuffId(),gardenStuffDTO.getGardenstuffNumber());
        return new MessageBean(flag,Constants.ERROR_NUMBER);
    }

    /**
     * @Description: 删除合同
     * @Param: [id]
     * @return: com.zl.util.MessageBean
     * @Author: ZhuLin
     * @Date: 2019/2/13
     */
    @SystemControllerLog(description = "删除合同")
    @RequestMapping("/deleteContract")
    @ResponseBody
    public MessageBean deleteContract(String id) throws Exception{
        contractService.deleteContractByKey(id);
        return new MessageBean(true,Constants.SUCCESS_DELETE);
    }
}
