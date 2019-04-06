package com.tilchina.timp.controller;

import com.tilchina.timp.common.LanguageUtil;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.TransporterLicense;
import com.tilchina.timp.service.TransporterLicenseService;
import org.springframework.web.multipart.MultipartFile;

/**
* 行驶证档案
*
* @version 1.1.0
* @author XiaHong
*/
@RestController
@RequestMapping(value = {"/s1/transporterlicense"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class TransporterLicenseController extends BaseControllerImpl<TransporterLicense>{

	@Autowired
	private TransporterLicenseService transporterlicenseservice;
	
	@Override
	protected BaseService<TransporterLicense> getService() {
		return transporterlicenseservice;
	}
	
	@RequestMapping(value = "/remove", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Override
    @Auth(ClientType.WEB)
    public ApiResult<String> delete(@RequestBody ApiParam<Long> param) {
    	log.debug("remove: {}", param);
        try {
        	/*transporterlicenseservice.logicDeleteById(param.getData());*/
        	transporterlicenseservice.deleteById(param.getData());
            return ApiResult.success("删除成功");
        } catch (Exception e) {
            log.error("删除失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    @RequestMapping(value = "/removeList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> deleteList(@RequestBody ApiParam<int[]> param) {
        log.debug("remove: {}", param);
        try {
        	/*transporterlicenseservice.logicDeleteByIdList(param.getData());*/
        	for (int del : param.getData()) {
        		transporterlicenseservice.deleteById(Long.valueOf(del));
			}
             return ApiResult.success("删除成功");
        } catch (Exception e) {
            log.error("删除失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    /**
     * 参照
     * @param param
     * @return
     */
    @RequestMapping(value = "/getReferenceList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<TransporterLicense>> getRefer(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getRefer: {}", param);
        try {
        	PageInfo<TransporterLicense> transporterLicenses = transporterlicenseservice.getRefer(param.getData(),param.getPageNum(), param.getPageSize());
            return ApiResult.success(transporterLicenses);
        } catch (Exception e) {
            log.error("查询参照失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    /**
     * 审核
     * @param param
     * @return
     */
    @RequestMapping(value = "/check", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> putCheck(@RequestBody ApiParam<String> param) {
        log.debug("putCheck: {}", param);
        try {
        	TransporterLicense transporterLicense = JSON.parseObject(param.getData(), TransporterLicense.class);
        	transporterLicense.setBillStatus(1);
        	transporterlicenseservice.updateCheck(transporterLicense);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("审核失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    /**
     * 取消审核
     * @param param
     * @return
     */
    @RequestMapping(value = "/cancelCheck", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> putCancelCheck(@RequestBody ApiParam<String> param) {
        log.debug("putCheck: {}", param);
        try {
        	TransporterLicense transporterLicense = JSON.parseObject(param.getData(), TransporterLicense.class);
        	transporterLicense.setBillStatus(0);
        	transporterlicenseservice.updateCheck(transporterLicense);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("取消审核失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/addPhoto", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> addPhoto(@RequestParam("file") MultipartFile file,
                                          @RequestParam("transporterLicenseId") Long transporterLicenseId
    ) {
        log.debug("addPhoto: {}");
        try {
            transporterlicenseservice.addHeadPhoto(file,transporterLicenseId);
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("添加失败，param={}", e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    
}
