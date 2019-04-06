package com.tilchina.timp.controller;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.manager.AutoAssemblyManager;
import com.tilchina.timp.model.Brand;
import com.tilchina.timp.service.BrandService;
import com.tilchina.timp.vo.AssemblyParam;
import com.tilchina.timp.vo.TransporterCountVO;

@RestController
@RequestMapping(value = { "/s2/brand1" })
@Slf4j
public class ATestController extends BaseControllerImpl<Brand> {

	@Override
	protected BaseService<Brand> getService() {
		// TODO Auto-generated method stub
		return brandservice;
	}

	@Autowired
	private BrandService brandservice;
	
	@Autowired
    private AutoAssemblyManager autoAssemblyManager;

	@RequestMapping(value = "/auto2")
	public ApiResult<String> auto(@RequestBody ApiParam<AssemblyParam> param) {
		log.debug("get: {}", param);
		try {
			AssemblyParam p = param.getData();
			p.setTransporterType(1);
			List<TransporterCountVO> counts = new ArrayList<>();

			if (p.getTrail1() != null && p.getTrail1().intValue() != 0) {
				TransporterCountVO v1 = new TransporterCountVO();
				v1.setTrailerId(103L);
				v1.setCount(p.getTrail1());
				counts.add(v1);
			}

			if (p.getTrail2() != null && p.getTrail2().intValue() != 0) {
				TransporterCountVO v2 = new TransporterCountVO();
				v2.setTrailerId(102L);
				v2.setCount(p.getTrail2());
				counts.add(v2);
			}

			if (CollectionUtils.isEmpty(counts)) {
				throw new BusinessException("请输入板车数量！");
			}
			p.setCounts(counts);
			p.setSendCityId(37L);

			autoAssemblyManager.assembly(p);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("查询失败，param={}", param, e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}
}
