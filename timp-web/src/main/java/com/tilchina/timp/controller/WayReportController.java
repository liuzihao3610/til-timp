package com.tilchina.timp.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.WayReport;
import com.tilchina.timp.service.WayReportService;

/**
* 在途提报
*
* @version 1.0.0
* @author LiuShuqi
*/
@RestController
@RequestMapping(value = {"/s1/wayreport"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class WayReportController extends BaseControllerImpl<WayReport>{

	@Autowired
	private WayReportService wayreportservice;
	
	@Override
	protected BaseService<WayReport> getService() {
		return wayreportservice;
	}

}
