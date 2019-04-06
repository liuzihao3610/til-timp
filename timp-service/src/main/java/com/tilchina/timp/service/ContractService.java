package com.tilchina.timp.service;

import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.Contract;

import java.util.Map;

/**
* 合同管理表
*
* @version 1.0.0
* @author XueYuSong
*/
public interface ContractService extends BaseService<Contract> {

    PageInfo<Map<String, String>> getReferenceList(Map<String, Object> map, int pageNum, int pageSize);

    void setDocumentsChecked(Contract contract);

    void setDocumentsUnchecked(Contract contract);

    void setDocumentsInvalid(Contract contract);
}
