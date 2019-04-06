package com.tilchina.timp.app.controller;

import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.model.ContactsOld;
import com.tilchina.timp.model.TransportOrderDetail;
import com.tilchina.timp.model.Unit;
import com.tilchina.timp.service.ContactsOldService;
import com.tilchina.timp.service.TransportOrderDetailService;
import com.tilchina.timp.service.UnitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = {"/s1/app/contacts"}, produces = {"application/json;charset=utf-8"})
public class AppContactsController {

	@Autowired
	private ContactsOldService contactsService;

	@Autowired
	private TransportOrderDetailService orderDetailService;

	@Autowired
	private UnitService unitService;

	@PostMapping("/get")
	public ApiResult<List<Unit>> get(@RequestBody ApiParam<Long> param) {

		// id = transport order id
		Long id = param.getData();

		Map<String, Object> map = new HashMap(){{ put("transportOrderId", id); }};

		try {
			List<TransportOrderDetail> orderDetails = orderDetailService.queryByOrderId(map);

			if (orderDetails != null && orderDetails.size() > 0) {

				List<Unit> units = new ArrayList<>();
				orderDetails.forEach(detail -> {
					ContactsOld contact = contactsService.queryById(detail.getReceiveUnitId());
					if (contact != null) {
						Unit unit = unitService.getContactInfoById(contact.getUnitId());
						units.add(unit);
					}

				});

				return ApiResult.success(units);
			}

			return ApiResult.failure("9999", LanguageUtil.getMessage("9005", "transportOrderId"));
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}
}
