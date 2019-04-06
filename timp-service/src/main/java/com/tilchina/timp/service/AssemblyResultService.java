package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.AssemblyResult;
import com.tilchina.timp.model.AssemblyResultDetail;

import java.util.List;

/**
* 配板结果主表
*
* @version 1.0.0
* @author WangShengguang
*/
public interface AssemblyResultService extends BaseService<AssemblyResult> {

    List<AssemblyResultDetail> queryDetails(Long assemblyResultId);
}
