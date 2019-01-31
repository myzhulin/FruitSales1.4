package com.zl.service;

import com.zl.pojo.AccessoryDO;

import java.util.List;

public interface AccessoryService {

    /** 
    * @Description: 根据果蔬id返回对应的附属品 
    * @Param: [gardenstuffId] 
    * @return: java.util.List<com.zl.pojo.AccessoryDO> 
    * @Author: ZhuLin
    * @Date: 2019/1/31 
    */ 
    List<AccessoryDO> listAccessoryByGardenId(String gardenstuffId);

    /** 
    * @Description: 修改附属品 
    * @Param: [accessoryDO] 
    * @return: void 
    * @Author: ZhuLin
    * @Date: 2019/1/31 
    */ 
    void updateAccessory(AccessoryDO accessoryDO);
    
    /** 
    * @Description: 删除附属品 
    * @Param: [id] 
    * @return: void 
    * @Author: ZhuLin
    * @Date: 2019/1/31 
    */ 
    void deleteAccessory(String id);
}
