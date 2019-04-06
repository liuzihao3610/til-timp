package com.tilchina.timp.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.RailtransOrderDetail;
import com.tilchina.timp.service.RailtransOrderDetailService;

/**
* 铁路运输记录子表
*
* @version 1.0.0
* @author XueYuSong
*/
@RestController
@RequestMapping(value = {"/s1/railtransorderdetail"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class RailtransOrderDetailController extends BaseControllerImpl<RailtransOrderDetail>{

	@Autowired
	private RailtransOrderDetailService railtransorderdetailservice;
	
	@Override
	protected BaseService<RailtransOrderDetail> getService() {
		return railtransorderdetailservice;
	}

}
