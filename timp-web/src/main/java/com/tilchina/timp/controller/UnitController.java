package com.tilchina.timp.controller;

import com.alibaba.fastjson.JSONObject;
import com.tilchina.timp.model.UnitLogin;
import com.tilchina.timp.model.User;
import com.tilchina.timp.service.UnitLoginService;
import com.tilchina.timp.vo.UnitVO;
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
import com.tilchina.catalyst.utils.BeanUtils;
import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.CarStatus;
import com.tilchina.timp.model.Unit;
import com.tilchina.timp.service.UnitService;
import org.springframework.web.multipart.MultipartFile;

/**
* 收发货单位
*
* @version 1.1.0
* @author XiaHong
*/
@RestController
@RequestMapping(value = {"/s1/unit"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class UnitController /*extends BaseControllerImpl<Unit>*/{

	@Autowired
	private UnitService unitservice;

    @Autowired
    private UnitLoginService unitLoginService;
	
	@RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<UnitVO> post(@RequestBody ApiParam<String> param) {
        log.debug("post: {}", param);
        try {
            Unit t =  JSON.parseObject(param.getData(), Unit.class);
            UnitVO unitVO = unitservice.add(t);
            return ApiResult.success(unitVO);
        } catch (Exception e) {
            log.error("添加失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	@RequestMapping(value = "/remove", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> delete(@RequestBody ApiParam<Long> param) {
        log.debug("remove: {}", param);
        try {
            unitservice.deleteById(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
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
        	
        	unitservice.deleteList(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	@RequestMapping(value = "/getReferenceList", method =RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<List<Unit>> getReferenceList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("put: {}",param);
        try {
            
        	PageInfo<Unit> pageInfo =unitservice.getReferenceList(param.getData(),param.getPageNum(),param.getPageSize());
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"),"param={}",param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
    @RequestMapping(value = "/get", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<Unit> getById(@RequestBody ApiParam<Long> param) {
        log.debug("get: {}", param);
        try {
        	Unit t = unitservice.queryById(param.getData());
            log.debug("get result: {}", t);
            return ApiResult.success(t);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    @RequestMapping(value = "/getByName", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<Unit> getByName(@RequestBody ApiParam<String> param) {
        log.debug("get: {}", param);
        try {
        	Unit t =unitservice.getByName(param.getData());
            log.debug("get result: {}", t);
            return ApiResult.success(t);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/getList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<Unit>> getList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getList: {}", param);
        try {
            PageInfo<Unit> pageInfo = unitservice.queryList(param.getData(), param.getPageNum(), param.getPageSize());
            log.debug("get result: {}", pageInfo);
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/putPart", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> putPart(@RequestBody ApiParam<String> param) {
        log.debug("put: {}", param);
        try {
            Unit t = JSON.parseObject(param.getData(), Unit.class);
            unitservice.updateSelective(t);
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("修改失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    /**
     * 查看收发货单位登陆信息
     * @param param
     * @return
     */
    @RequestMapping(value = "/getLogin", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<UnitLogin> getLogin(@RequestBody ApiParam<Long> param) {
        log.debug("put: {}", param);
        try {
            UnitLogin unitLogin = unitLoginService.queryByUnitId(param.getData());
            return ApiResult.success(unitLogin);
        } catch (Exception e) {
            log.error("查看收发货单位登陆信息失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    /**
     * 用户解锁
     * @param param
     * @return
     */
    @RequestMapping(value = "/deblocking", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> deblocking(@RequestBody ApiParam<Unit> param) {
        log.debug("deblocking: {}", param);
        try {
            unitservice.updateBlock(param.getData());
            return ApiResult.success();
        } catch (Exception e) {
            log.error("收发货单位解锁 失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    /**
     * 重置密码
     * @param param
     * @return
     */
    @RequestMapping(value = "/resetPasswords", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<UnitVO> resetPasswords(@RequestBody ApiParam<Unit> param) {
        log.debug("resetPasswords: {}", param);
        try {
            UnitVO unitVO = unitservice.updatePasswords(param.getData());
            return ApiResult.success(unitVO);
        } catch (Exception e) {
            log.error("重置密码 失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/importContacts", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> importContacts(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("importContacts: {}", param);
        try {
            String message = unitservice.importContacts(param.getData());
            ApiResult.success().setMessage(message);
            return  ApiResult.success();
        } catch (Exception e) {
            log.error("导入收发货单位联系人 失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/importExcel", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> importExcel(@RequestParam("file") MultipartFile file) {
        log.debug("importExcel: {}", file);
        try {
            unitservice.importExcel(file);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("导入Excel 失败，param={}", file, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

}
