package com.tilchina.timp.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.OutsourcingDeduction;
import com.tilchina.timp.model.OutsourcingReconciliation;
import com.tilchina.timp.model.OutsourcingReconciliationDetail;
import com.tilchina.timp.service.OutsourcingDeductionService;
import com.tilchina.timp.service.OutsourcingReconciliationDetailService;
import com.tilchina.timp.service.OutsourcingReconciliationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
* 外协对账主表
*
* @version 1.0.0
* @author XueYuSong
*/
@RestController
@RequestMapping(value = {"/s1/outsourcingreconciliation"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class OutsourcingReconciliationController extends BaseControllerImpl<OutsourcingReconciliation>{

	@Autowired
	private OutsourcingReconciliationService service;

	@Autowired
	private OutsourcingReconciliationDetailService detailService;

	@Autowired
	private OutsourcingDeductionService deductionService;

	@Override
	protected BaseService<OutsourcingReconciliation> getService() {
		return service;
	}

	@PostMapping(value = "/importReconciliation")
	@Auth(ClientType.WEB)
	public ApiResult<String> importReconciliation(@RequestParam("vendorCorpId") Long vendorCorpId,
                                                  @RequestParam("file") MultipartFile file) {

		try {
			service.importReconciliation(vendorCorpId, file);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/checking")
	@Auth(ClientType.WEB)
	public ApiResult<String> checking(@RequestBody ApiParam<Map<String, Long>> params) {

		try {
			service.checking(params.getData().get("reconciliationId"));
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/delete")
	@Auth(ClientType.WEB)
	public ApiResult<String> logicalDelete(@RequestBody ApiParam<Map<String, Long>> params) {

		try {
			service.logicalDelete(params.getData().get("reconciliationId"));
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/checked")
	@Auth(ClientType.WEB)
	public ApiResult<String> checked(@RequestBody ApiParam<Map<String, Long>> params) {

		try {
			service.setDocumentsChecked(params.getData().get("reconciliationId"));
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
			service.setDocumentsUnchecked(params.getData().get("reconciliationId"));
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/exportFeedback")
	@Auth(ClientType.WEB)
	public ApiResult<String> exportFeedback(HttpServletResponse response, @RequestParam("reconciliationId") Long reconciliationId) {

		try {
			Workbook workbook = service.exportFeedback(reconciliationId);
			response.setHeader("Content-Disposition", "attachment; filename=feedback.xlsx");
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

	@PostMapping(value = "/confirmSettlement")
	@Auth(ClientType.WEB)
	public ApiResult<String> confirmSettlement(@RequestBody ApiParam<Map<String, Object>> params) {

		try {
			service.confirmSettlement(params.getData());
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/getDetailList")
	@Auth(ClientType.WEB)
	public ApiResult<List<OutsourcingReconciliationDetail>> getDetailList(@RequestBody ApiParam<Map<String, Object>> params) {

		try {
			PageInfo<OutsourcingReconciliationDetail> outsourcingReconciliationDetailPageInfo = detailService.queryList(params.getData(), params.getPageNum(), params.getPageSize());
			return ApiResult.success(outsourcingReconciliationDetailPageInfo);
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/deleteDetail")
	@Auth(ClientType.WEB)
	public ApiResult<String> deleteDetail(@RequestBody ApiParam<Map<String, Long>> params) {

		try {
			detailService.logicalDelete(params.getData().get("reconciliationDetailId"));
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/getDeductionList")
	@Auth(ClientType.WEB)
	public ApiResult<List<OutsourcingDeduction>> getDeductionList(@RequestBody ApiParam<Map<String, Object>> params) {

		try {
			PageInfo<OutsourcingDeduction> outsourcingDeductionPageInfo = deductionService.queryList(params.getData(), params.getPageNum(), params.getPageSize());
			return ApiResult.success(outsourcingDeductionPageInfo);
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/addDeduction")
	@Auth(ClientType.WEB)
	public ApiResult<String> addDeduction(@RequestBody ApiParam<String> params) {

		try {
			OutsourcingDeduction outsourcingDeduction = JSON.parseObject(params.getData(), OutsourcingDeduction.class);
			deductionService.add(outsourcingDeduction);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/updateDeduction")
	@Auth(ClientType.WEB)
	public ApiResult<String> updateDeduction(@RequestBody ApiParam<String> params) {

		try {
			OutsourcingDeduction outsourcingDeduction = JSON.parseObject(params.getData(), OutsourcingDeduction.class);
			deductionService.updateSelective(outsourcingDeduction);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/deleteDeduction")
	@Auth(ClientType.WEB)
	public ApiResult<List<OutsourcingReconciliationDetail>> deleteDeduction(@RequestBody ApiParam<Map<String, Long>> params) {

		try {
			deductionService.deleteById(params.getData().get("reconciliationDeductionId"));
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/modifyHedgeAmount")
	@Auth(ClientType.WEB)
	public ApiResult<String> modifyHedgeAmount(@RequestBody ApiParam<Map<String, Object>> params) {

		try {
			detailService.modifyHedgeAmount(params.getData());
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}
}
