package com.tilchina.timp.controller;

import com.github.pagehelper.PageInfo;
import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.Quotation;
import com.tilchina.timp.model.QuotationDetail;
import com.tilchina.timp.model.QuotationPrice;
import com.tilchina.timp.service.QuotationDetailService;
import com.tilchina.timp.service.QuotationPriceService;
import com.tilchina.timp.service.QuotationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
* 客户报价
*
* @version 1.0.0
* @author XueYuSong
*/
@RestController
@RequestMapping(value = {"/s1/quotation"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class QuotationController extends BaseControllerImpl<Quotation>{

	@Autowired
	private QuotationService quotationService;

	@Autowired
	private QuotationDetailService quotationDetailService;

	@Autowired
	private QuotationPriceService quotationPriceService;
	
	@Override
	protected BaseService<Quotation> getService() {
		return quotationService;
	}

	@PostMapping(value = "/addQuotation")
	@Auth(ClientType.WEB)
	public ApiResult<String> post(@RequestParam("customerId") Long customerId,
	                              @RequestParam("vendorCorpId") Long vendorCorpId,
	                              @RequestParam("quotationType") Integer quotationType,
	                              @RequestParam(value = "contractId", required = false) Long contractId,
	                              @RequestParam("effectiveDate") String effectiveDate,
	                              @RequestParam("expirationDate") String expirationDate,
	                              @RequestParam("templateType") Integer templateType,
	                              @RequestParam("carBrandId") Long carBrandId,
	                              @RequestParam(value = "carTypeId", required = false) Long carTypeId,
	                              @RequestParam(value = "carModelId", required = false) Long carModelId,
	                              @RequestParam("quotationPlan") Integer quotationPlan,
	                              @RequestParam("jobType") Integer jobType,
	                              @RequestParam("file") MultipartFile multipartFile) {

		try {
			quotationService.importExcel(
					customerId,
					vendorCorpId,
					quotationType,
					contractId,
					effectiveDate,
					expirationDate,
					templateType,
					carBrandId,
					carTypeId,
					carModelId,
					quotationPlan,
					jobType,
					multipartFile);

			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/addUniversalQuotation")
	@Auth(ClientType.WEB)
	public ApiResult<String> addUniversalQuotation(
	                              @RequestParam("quotationType") Integer quotationType,
	                              @RequestParam("quotationPlan") Integer quotationPlan,
	                              @RequestParam(value = "contractId", required = false) Long contractId,
	                              @RequestParam("jobType") Integer jobType,
	                              @RequestParam("file") MultipartFile multipartFile) {

		try {
			quotationService.importUniversalExcel(quotationType, quotationPlan, contractId, jobType, multipartFile);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/deleteQuotation")
	@Auth(ClientType.WEB)
	public ApiResult<String> deleteQuotation(@RequestBody ApiParam<Map<String, Long>> param) {

		try {
			Long quotationId = param.getData().get("quotationId");
			quotationService.deleteById(quotationId);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/checked")
	@Auth(ClientType.WEB)
	public ApiResult<String> checked(@RequestBody ApiParam<Map<String, Long>> param) {

		try {
			Long quotationId = param.getData().get("quotationId");
			quotationService.setDocumentsChecked(quotationId);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/unchecked")
	@Auth(ClientType.WEB)
	public ApiResult<String> unchecked(@RequestBody ApiParam<Map<String, Long>> param) {

		try {
			Long quotationId = param.getData().get("quotationId");
			quotationService.setDocumentsUnchecked(quotationId);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}
	/**
	 * 封存
	 * @param param 客户报价ID
	 * @return
	 */
	@PostMapping(value = "/blocked")
	@Auth(ClientType.WEB)
	public ApiResult<String> blocked(@RequestBody ApiParam<Map<String, Long>> param) {

		try {
			quotationService.blocked(param.getData().get("quotationId"));
			return ApiResult.success();
		} catch (Exception e) {
			log.error("封存失败，param={}", param, e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}
	
	/**
	 * 取消封存
	 * @param param 客户报价ID
	 * @return
	 */
	@PostMapping(value = "/unblocked")
	@Auth(ClientType.WEB)
	public ApiResult<String> unblocked(@RequestBody ApiParam<Map<String, Long>> param) {

		try {
			quotationService.unblocked(param.getData().get("quotationId"));
			return ApiResult.success();
		} catch (Exception e) {
			log.error("取消封存失败，param={}", param, e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/getDetailList")
	@Auth(ClientType.WEB)
	public ApiResult<List<QuotationDetail>> getDetailList(@RequestBody ApiParam<Map<String, Object>> params) {

		try {
			PageInfo<QuotationDetail> quotationDetailPageInfo = quotationDetailService.queryList(params.getData(), params.getPageNum(), params.getPageSize());
			return ApiResult.success(quotationDetailPageInfo);
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping(value = "/getPriceList")
	@Auth(ClientType.WEB)
	public ApiResult<List<QuotationPrice>> getPriceList(@RequestBody ApiParam<Map<String, Object>> params) {

		try {
			PageInfo<QuotationPrice> quotationPricePageInfo = quotationPriceService.queryList(params.getData(), params.getPageNum(), params.getPageSize());
			return ApiResult.success(quotationPricePageInfo);
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}
}