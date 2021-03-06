package com.zl.service.impl;

import com.zl.mapper.AccessoryMapper;
import com.zl.pojo.AccessoryDO;
import com.zl.pojo.AccessoryDOExample;
import com.zl.service.AccessoryService;
import com.zl.util.AjaxPutPage;
import com.zl.util.AjaxResultPage;
import com.zl.util.MessageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: FruitSales
 * @description:
 * @author: ZhuLlin
 * @create: 2019-01-31 14:12
 **/
@Service
public class AccessoryServiceImpl implements AccessoryService {

    @Autowired
    private AccessoryMapper accessoryMapper;

    @Override
    public AjaxResultPage<AccessoryDO> listAccessoryByGardenId(AjaxPutPage<AccessoryDO> ajaxPutPage) {
        AjaxResultPage<AccessoryDO> result = new AjaxResultPage<AccessoryDO>();
        List<AccessoryDO> list = accessoryMapper.listAccessoryByGardenId(ajaxPutPage);
        result.setData(list);
        result.setCount(accessoryMapper.listAccessoryByGardenIdCount(ajaxPutPage));
        return result;
    }

    @Override
    public void updateAccessory(AccessoryDO accessoryDO) throws MessageException{
        accessoryMapper.updateByPrimaryKeySelective(accessoryDO);
    }

    @Override
    public void deleteAccessory(String id) throws MessageException{
        accessoryMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void insertAccessory(AccessoryDO accessoryDO) throws MessageException{
        accessoryMapper.insertSelective(accessoryDO);
    }
}
