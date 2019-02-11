package com.zl.mapper;

import com.zl.pojo.ContractDO;
import com.zl.pojo.ContractDOExample;
import com.zl.pojo.ContractDTO;
import com.zl.pojo.ContractVO;
import com.zl.util.AjaxPutPage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ContractMapper {
    int countByExample(ContractDOExample example);

    int deleteByExample(ContractDOExample example);

    int deleteByPrimaryKey(String contractId);

    int insert(ContractDO record);

    int insertSelective(ContractDO record);

    List<ContractDO> selectByExample(ContractDOExample example);

    ContractDO selectByPrimaryKey(String contractId);

    int updateByExampleSelective(@Param("record") ContractDO record, @Param("example") ContractDOExample example);

    int updateByExample(@Param("record") ContractDO record, @Param("example") ContractDOExample example);

    int updateByPrimaryKeySelective(ContractDO record);

    int updateByPrimaryKey(ContractDO record);

    /**
     * @Description: 获取合同列表
     * @Param: [ajaxPutPage]
     * @return: java.util.List<com.zl.pojo.ContractDTO>
     * @date: 2019/2/5 15:59 
     */
    List<ContractDTO> listContract(AjaxPutPage<ContractDTO> ajaxPutPage);

    /**
     * @Description: 返回合同详情
     * @Param: [contractId]
     * @return: com.zl.pojo.ContractVO
     * @date: 2019/2/8 11:43 
     */
    ContractVO getContractInfo(String contractId);
}