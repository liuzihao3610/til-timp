package com.tilchina.timp.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.CustomerReconciliation;
import com.tilchina.timp.model.CustomerReconciliationDeduction;
import com.tilchina.timp.model.CustomerReconciliationDetail;
import com.tilchina.timp.service.CustomerReconciliationDeductionService;
import com.tilchina.timp.service.CustomerReconciliationDetailService;
import com.tilchina.timp.service.CustomerReconciliationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
* 客户对账表
*
* @version 1.0.0
* @author XueYuSong
*/
@RestController
@MultipartConfig
@RequestMapping(value = {"/s1/customerreconciliation"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class CustomerReconciliationController extends BaseControllerImpl<CustomerReconciliation>{

	@Autowired
	private CustomerReconciliationService customerReconciliationService;

	@Autowired
	private CustomerReconciliationDetailService detailService;

	@Autowired
	private CustomerReconciliationDeductionService deductionService;
	
	@Override
	protected BaseService<CustomerReconciliation> getService() {
		return customerReconciliationService;
	}

	@PostMapping(value = "/checking")
	@Auth(ClientType.WEB)
	public ApiResult<String> checking(@RequestBody ApiParam<Map<String, String>> params) {

		customerReconciliationService.checking(params.getData());
		return ApiResult.success();
	}

	@PostMapping(value = "/modifyHedgeAmount")
	@Auth(ClientType.WEB)
	public ApiResult<String> modifyHedgeAmount(@RequestBody ApiParam<Map<String, Object>> params) {

		detailService.modifyHedgeAmount(params.getData());
		return ApiResult.success();
	}

	@PostMapping(value = "/confirmSettlement")
	@Auth(ClientType.WEB)
	public ApiResult<String> confirmSettlement(@RequestBody ApiParam<Map<String, Object>> params) {

		customerReconciliationService.confirmSettlement(params.getData());
		return ApiResult.success();
	}

	@PostMapping(value = "/importExcel")
	@Auth(ClientType.WEB)
	public ApiResult<String> importExcel(@RequestParam(value = "fileType", required = false) Integer fileType,
	                                     @RequestParam(value = "reconciliationNumber", required = false) String reconciliationNumber,
	                                     @RequestParam("file") MultipartFile file) {

		try {
			customerReconciliationService.importFile(fileType, reconciliationNumber, file);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/importReconciliation")
	@Auth(ClientType.WEB)
	public ApiResult<String> importReconciliation(@RequestParam("customerId") Long customerId,
			                                      @RequestParam("startDate") String startDate,
			                                      @RequestParam("endDate") String endDate,
			                                      @RequestParam("file") MultipartFile file) {

		try {
			String messages = customerReconciliationService.importReconciliation(customerId, startDate, endDate, file);
			return ApiResult.success(messages);
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/checked")
	@Auth(ClientType.WEB)
	public ApiResult<String> checked(@RequestBody ApiParam<Map<String, Long>> params) {

		try {
			customerReconciliationService.setDocumentsChecked(params.getData().get("reconciliationId"));
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/unchecked")
	@Auth(ClientType.WEB)
	public ApiResult<String> unchecked(@RequestBody ApiParam<Map<String, Long>> params) {

		try {
			customerReconciliationService.setDocumentsUnchecked(params.getData().get("reconciliationId"));
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/getDetailList")
	@Auth(ClientType.WEB)
	public ApiResult<List<CustomerReconciliationDetail>> getDetailList(@RequestBody ApiParam<Map<String, Object>> params) {

		try {
			PageInfo<CustomerReconciliationDetail> customerReconciliationDetailPageInfo = detailService.queryList(params.getData(), params.getPageNum(), params.getPageSize());
			return ApiResult.success(customerReconciliationDetailPageInfo);
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/getDeductionList")
	@Auth(ClientType.WEB)
	public ApiResult<List<CustomerReconciliationDeduction>> getDeductionList(@RequestBody ApiParam<Map<String, Object>> params) {

		try {
			PageInfo<CustomerReconciliationDeduction> customerReconciliationDeductionPageInfo = deductionService.queryList(params.getData(), params.getPageNum(), params.getPageSize());
			return ApiResult.success(customerReconciliationDeductionPageInfo);
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/delete")
	@Auth(ClientType.WEB)
	public ApiResult<String> deleteReconciliation(@RequestBody ApiParam<Map<String, Long>> params) {

		try {
			customerReconciliationService.logicalDelete(params.getData().get("reconciliationId"));
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/addDeduction")
	@Auth(ClientType.WEB)
	public ApiResult<String> addDeduction(@RequestBody ApiParam<String> params) {

		try {
			CustomerReconciliationDeduction deduction = JSON.parseObject(params.getData(), CustomerReconciliationDeduction.class);
			deductionService.add(deduction);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/putPartDeduction")
	@Auth(ClientType.WEB)
	public ApiResult<String> putPartDeduction(@RequestBody ApiParam<String> params) {

		try {
			CustomerReconciliationDeduction deduction = JSON.parseObject(params.getData(), CustomerReconciliationDeduction.class);
			deductionService.updateSelective(deduction);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/removeDeduction")
	@Auth(ClientType.WEB)
	public ApiResult<String> removeDeduction(@RequestBody ApiParam<Map<String, Long>> params) {

		try {
			deductionService.deleteById(params.getData().get("reconciliationDeductionId"));
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/refreshQuotation")
	@Auth(ClientType.WEB)
	public ApiResult<String> refreshQuotation(@RequestBody ApiParam<Map<String, Long>> params) {

		try {
			customerReconciliationService.refreshQuotation(params.getData().get("reconciliationId"));
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/exportExcel")
	public ApiResult<String> exportExcel(HttpServletResponse response,
	                        @RequestParam("reconciliationId") Long reconciliationId) {

		try {

			Workbook workbook = customerReconciliationService.exportExcel(reconciliationId);
			response.setHeader("Content-Disposition", "attachment; filename=reconciliation.xlsx");
			response.setContentType("application/vnd.ms-excel; charset=utf-8") ;
			OutputStream out = response.getOutputStream() ;
			workbook.write(out) ;
			out.flush();
			out.close();
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}
}
