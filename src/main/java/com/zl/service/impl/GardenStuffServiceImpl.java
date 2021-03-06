package com.zl.service.impl;

import com.zl.mapper.AccessoryMapper;
import com.zl.mapper.CategoryMapper;
import com.zl.mapper.GardenStuffMapper;
import com.zl.mapper.MiddleMapper;
import com.zl.pojo.*;
import com.zl.service.GardenStuffService;
import com.zl.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * @program: FruitSales
 * @description:
 * @author: ZhuLlin
 * @create: 2019-01-22 15:14
 **/
@Service
public class GardenStuffServiceImpl implements GardenStuffService {

    @Autowired
    private GardenStuffMapper gardenStuffMapper;

    @Autowired
    private AccessoryMapper accessoryMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private MiddleMapper middleMapper;

    @Override
    public AjaxResultPage<GardenStuffDTO> listGardenStuff(AjaxPutPage<GardenStuffDTO> ajaxPutPage) {
        AjaxResultPage<GardenStuffDTO> result = new AjaxResultPage<GardenStuffDTO>();
        List<GardenStuffDTO> list = gardenStuffMapper.listGardenStuff(ajaxPutPage);
        result.setData(list);
        result.setCount(gardenStuffMapper.listGardenStuffCount(ajaxPutPage));
        return result;
    }

    @Cacheable(value = "welcome")
    @Override
    public Integer getGardenStuffCount() {
        return gardenStuffMapper.countByExample(null);
    }

    @Override
    public List<CategoryDO> listCategory(AjaxPutPage<CategoryDO> ajaxPutPage) {
        return categoryMapper.listCategory(ajaxPutPage);
    }

    @Override
    public void insertGardenStuff(GardenStuffDO gardenStuffDO) throws MessageException {
        gardenStuffDO.setGardenstuffId(UuidUtils.creatUUID());
        gardenStuffDO.setGardenstuffTime(new Timestamp(new Date().getTime()));
        gardenStuffMapper.insertSelective(gardenStuffDO);
    }

    @Override
    public void deleteGardenStuffByPeasantid(String id) throws MessageException{
        GardenStuffDOExample example = new GardenStuffDOExample();
        example.createCriteria().andGardenstuffPeasantidEqualTo(id);
        gardenStuffMapper.deleteByExample(example);
    }

    @Override
    public void updateGardenStuff(GardenStuffDO gardenStuffDO) throws MessageException{
        gardenStuffMapper.updateByPrimaryKeySelective(gardenStuffDO);
    }

    @Override
    public void deleteGardenStuff(String id) throws MessageException{
        MiddleDOExample middleDOExample = new MiddleDOExample();
        middleDOExample.createCriteria().andGardenstuffIdEqualTo(id);
        List<MiddleDO> middleDOS = middleMapper.selectByExample(middleDOExample);
        if(middleDOS.size() <= 0){
            gardenStuffMapper.deleteByPrimaryKey(id);
            AccessoryDOExample example = new AccessoryDOExample();
            example.createCriteria().andGardenstuffIdEqualTo(id);
            accessoryMapper.deleteByExample(example);
        }else{
            throw new MessageException(Constants.ERROR_CODE,Constants.ERROR_DELETE);
        }
    }

    @Override
    public void batchesDelGardenStuff(List<String> deleteId) throws MessageException {
        for (String id : deleteId){
            MiddleDOExample middleDOExample = new MiddleDOExample();
            middleDOExample.createCriteria().andGardenstuffIdEqualTo(id);
            List<MiddleDO> middleDOS = middleMapper.selectByExample(middleDOExample);
            if (middleDOS.size() > 0){
                throw new MessageException(gardenStuffMapper.selectByPrimaryKey(id).getGardenstuffName(),Constants.ERROR_DELETE);
            }
        }
        AccessoryDOExample example = new AccessoryDOExample();
        example.createCriteria().andGardenstuffIdIn(deleteId);
        accessoryMapper.deleteByExample(example);
        GardenStuffDOExample example1 = new GardenStuffDOExample();
        example1.createCriteria().andGardenstuffIdIn(deleteId);
        gardenStuffMapper.deleteByExample(example1);
    }

    @Override
    public void updateCategory(CategoryDO categoryDO) throws MessageException {
        categoryMapper.updateByPrimaryKeySelective(categoryDO);
    }

    @Override
    public void insertCategory(CategoryDO categoryDO) throws MessageException {
        categoryMapper.insertSelective(categoryDO);
    }

    @Override
    public void batchesDelCategoey(List<Integer> deleteId) throws MessageException {
        categoryMapper.batchesDelCategory(deleteId);
        CategoryDOExample example = new CategoryDOExample();
        example.createCriteria().andCategoryIdIn(deleteId);
        categoryMapper.deleteByExample(example);
    }

    @Override
    public boolean gardenstuffNumberCheck(String gardenstuffId, Integer gardenstuffNumber) {
        return gardenStuffMapper.gardenstuffNumberCheck(gardenstuffId,gardenstuffNumber);
    }


}
