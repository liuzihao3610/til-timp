package com.tilchina.timp.controller;

import com.alibaba.fastjson.JSON;
import com.tilchina.auth.Auth;
import com.tilchina.catalyst.exception.BaseException;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.Regist;
import com.tilchina.timp.vo.RegistTreeVO;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.Power;
import com.tilchina.timp.service.PowerService;

/**
 * 功能档案
 *
 * @version 1.1.0
 * @author XiaHong
 */
@RestController
@RequestMapping(value = {"/s1/power"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class PowerController {

    @Autowired
    private PowerService powerservice;

    protected BaseService<Power> getService() {
        return powerservice;
    }

    @RequestMapping(value = "/get", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<Power> getById(@RequestBody ApiParam<Long> param) {
        log.debug("get: {}", param);
        try {
            Power t = getService().queryById(param.getData());
            log.debug("get result: {}", t);
            return ApiResult.success(t);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/getList", method =  RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<Power>> getList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getList: {}", param);
        try {
            PageInfo<Power> pageInfo = getService().queryList(param.getData(), param.getPageNum(), param.getPageSize());
            log.debug("get result: {}", pageInfo);
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> post(@RequestBody ApiParam<String> param) {
        log.debug("post: {}", param);
        try {
            Power t =  JSON.parseObject(param.getData(), Power.class);
            getService().add(t);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("添加失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/putPart", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> putPart(@RequestBody ApiParam<String> param) {
        log.debug("put: {}", param);
        try {
            Power t =  JSON.parseObject(param.getData(), Power.class);
            getService().updateSelective(t);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("修改失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/put", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> put(@RequestBody ApiParam<String> param) {
        log.debug("put: {}", param);
        try {
            Power t =  JSON.parseObject(param.getData(), Power.class);
            getService().update(t);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("修改失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> delete(@RequestBody ApiParam<Long> param) {
        log.debug("remove: {}", param);
        try {
            if(param.getData() == null){
                throw new BaseException("请求参数不能为空！");
            }
            getService().deleteById(param.getData());
            return ApiResult.success();
        } catch (Exception e) {
            log.error("删除失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

/*    @RequestMapping(value = "/getByGroupId", method = RequestMethod.Post, produces = "application/json;charset=utf-8")
    @Auth(ClientPowerype.WEB)
    public ApiResult<List<Power>> getByDriverId(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getByDriverId: {}", param);
        try {
        	PageInfo<Power> pageInfo = powerservice.queryByGroupId(param.getData(), param.getPageNum(), param.getPageSize());
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("根据角色ID查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }*/

    /**
     * 分配权限
     * @param param
     * @return
     */
    @RequestMapping(value = "/test", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> test(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("savePower: {}", param);
        try {
            powerservice.test();
            return ApiResult.success();
        } catch (Exception e) {
            log.error("【分配权限】查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

}
