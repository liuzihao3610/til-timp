package com.tilchina.timp.controller;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.TransportRecordOuter;
import com.tilchina.timp.service.TransportRecordOuterService;

/**
* 运输记录表(外)
*
* @version 1.1.0
* @author Xiahong
*/
@RestController
@RequestMapping(value = {"/s1/transportrecordouter"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class TransportRecordOuterController extends BaseControllerImpl<TransportRecordOuter>{

	@Autowired
	private TransportRecordOuterService transportrecordouterservice;
	
	@Override
	protected BaseService<TransportRecordOuter> getService() {
		return transportrecordouterservice;
	}
	
}
