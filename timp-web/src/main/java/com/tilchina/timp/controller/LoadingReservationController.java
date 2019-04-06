package com.tilchina.timp.controller;

import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.LoadingReservation;
import com.tilchina.timp.model.LoadingReservationDetail;
import com.tilchina.timp.model.TransportOrder;
import com.tilchina.timp.service.LoadingReservationDetailService;
import com.tilchina.timp.service.LoadingReservationService;

/**
* 预约装车表
*
* @version 1.1.0
* @author XiaHong
*/
@RestController
@RequestMapping(value = {"/s1/loadingreservation"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class LoadingReservationController extends BaseControllerImpl<LoadingReservation>{

	@Autowired
	private LoadingReservationService loadingreservationservice;
	
	@Autowired
	private LoadingReservationDetailService loadingReservationDetailService;
	
	@Override
	protected BaseService<LoadingReservation> getService() {
		return loadingreservationservice;
	}
	
    @RequestMapping(value = "/get", method =  RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Override
    public ApiResult<LoadingReservation> getById(@RequestBody ApiParam<Long> param) {
        log.debug("get: {}", param);
        try {
        	LoadingReservation t = getService().queryById(param.getData());
            log.debug("get result: {}", t);
            return ApiResult.success(t);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/getList", method =  RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Override
    public ApiResult<List<LoadingReservation>> getList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getList: {}", param);
        try {
            PageInfo<LoadingReservation> pageInfo = getService().queryList(param.getData(), param.getPageNum(), param.getPageSize());
            log.debug("get result: {}", pageInfo);
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
	@RequestMapping(value = "/getByprimary", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<LoadingReservation> getByprimaryId(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("get: {}", param);
        try {
        	LoadingReservation t = loadingreservationservice.queryById(param.getData(), param.getPageNum(), param.getPageSize());
            log.debug("get result: {}", t);
            return ApiResult.success(t);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	@RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Override
    @Auth(ClientType.WEB)
    public ApiResult<String> post(@RequestBody ApiParam<String> param) {
        log.debug("post: {}", param);
        try {
            LoadingReservation t = (LoadingReservation)JSON.parseObject(param.getData(), LoadingReservation.class);
            getService().add(t);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("添加失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
/*	@RequestMapping(value = "/putStatus", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<String> putStatus(@RequestBody ApiParam<String> param) {
        log.debug("put: {}", param);
        try {
            LoadingReservation t = (LoadingReservation)JSON.parseObject(param.getData(), LoadingReservation.class);
            loadingreservationservice.updateStatus(t);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("修改失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }*/
	
	@RequestMapping(value = "/remove", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Override
    public ApiResult<String> delete(@RequestBody ApiParam<Long> param) {
        log.debug("remove: {}", param);
        try {
        	loadingreservationservice.logicDeleteById(param.getData());
            return ApiResult.success();
        } catch (Exception e) {
            log.error("删除失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    @RequestMapping(value = "/removeList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<String> deleteList(@RequestBody ApiParam<int[]> param) {
        log.debug("remove: {}", param);
        try {
        	loadingreservationservice.logicDeleteByIdList(param.getData());
            return ApiResult.success("删除成功");
        } catch (Exception e) {
            log.error("删除失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
	@RequestMapping(value = "/removeDetail", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<String> deleteDetail(@RequestBody ApiParam<Long> param) {
        log.debug("remove: {}", param);
        try {
        	loadingReservationDetailService.logicDeleteById(param.getData());
            return ApiResult.success();
        } catch (Exception e) {
            log.error("删除失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    @RequestMapping(value = "/removeListDetail", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<String> deleteListDetail(@RequestBody ApiParam<int[]> param) {
        log.debug("remove: {}", param);
        try {
        	loadingReservationDetailService.logicDeleteByIdList(param.getData());
            return ApiResult.success("删除成功");
        } catch (Exception e) {
            log.error("删除失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    @RequestMapping(value = "/getDetail", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<LoadingReservationDetail> getByDetailId(@RequestBody ApiParam<Long> param) {
        log.debug("get: {}", param);
        try {
        	LoadingReservationDetail t = loadingReservationDetailService.queryById(param.getData());
            log.debug("get result: {}", t);
            return ApiResult.success(t);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/getListDetail", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<List<LoadingReservationDetail>> getListDetail(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getList: {}", param);
        try {
            PageInfo<LoadingReservationDetail> pageInfo = loadingReservationDetailService.queryList(param.getData(), param.getPageNum(), param.getPageSize());
            log.debug("get result: {}", pageInfo);
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/addDetail", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<String> postDetail(@RequestBody ApiParam<String> param) {
        log.debug("post: {}", param);
        try {
            LoadingReservationDetail t = (LoadingReservationDetail)JSON.parseObject(param.getData(), LoadingReservationDetail.class);
            loadingReservationDetailService.add(t);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("添加失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/putPartDetail", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<String> putPart(@RequestBody ApiParam<String> param) {
        log.debug("put: {}", param);
        try {
            LoadingReservationDetail t = (LoadingReservationDetail)JSON.parseObject(param.getData(), LoadingReservationDetail.class);
            loadingReservationDetailService.updateSelective(t);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("修改失败，param={}", param, e);
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
    public ApiResult<String> putCheck(@RequestBody ApiParam<LoadingReservation> param) {
        log.debug("putCheck: {}", param);
        try {
        	loadingreservationservice.check(param.getData(),0);
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
    public ApiResult<String> putCancelCheck(@RequestBody ApiParam<LoadingReservation> param) {
        log.debug("putCheck: {}", param);
        try {
        	loadingreservationservice.check(param.getData(),1);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("取消审核失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
}
