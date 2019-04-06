package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.Contract;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
* 合同管理表
*
* @version 1.0.0
* @author XueYuSong
*/
@Repository
public interface ContractMapper extends BaseMapper<Contract> {

    void setCheckState(Contract contract);

    List<Map<String, String>> getReferenceList(Map<String, Object> map);
}
