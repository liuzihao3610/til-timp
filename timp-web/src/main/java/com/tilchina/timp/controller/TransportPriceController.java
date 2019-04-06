package com.tilchina.timp.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.TransportPrice;
import com.tilchina.timp.service.TransportPriceService;

/**
* 
*
* @version 1.1.0
* @author LiuShuqi
*/
@RestController
@RequestMapping(value = {"/s1/transportprice"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class TransportPriceController extends BaseControllerImpl<TransportPrice>{

	@Autowired
	private TransportPriceService transportpriceservice;
	
	@Override
	protected BaseService<TransportPrice> getService() {
		return transportpriceservice;
	}

}
