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
import com.tilchina.catalyst.utils.BeanUtils;
import com.tilchina.framework.controller.BaseControllerImpl;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.Car;
import com.tilchina.timp.model.Cargo;
import com.tilchina.timp.service.CargoService;

/**
 * 轿运车载货信息
 *
 * @author XiaHong
 * @version 1.1.0
 */
@RestController
@RequestMapping(value = {"/s1/cargo"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class CargoController extends BaseControllerImpl<Cargo> {

    @Autowired
    private CargoService cargoservice;

    @Override
    protected BaseService<Cargo> getService() {
        return cargoservice;
    }

    //快速查询
    @RequestMapping(value = "/quick", method = {RequestMethod.POST, RequestMethod.GET}, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<Cargo>> quick(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("put: {}", param);
        try {

            List<Cargo> cargos = cargoservice.queryList(param.getData());
            return ApiResult.success(cargos);
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"), "param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    @RequestMapping(value = "/get", method = { RequestMethod.POST,
            RequestMethod.GET }, produces = "application/json;charset=utf-8")
    @Override
    public ApiResult<Cargo> getById(@RequestBody ApiParam<Long> param) {
        log.debug("get: {}", param);
        try {
        	Cargo t = getService().queryById(param.getData());
            log.debug("get result: {}", t);
            return ApiResult.success(t);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/getList", method = { RequestMethod.POST,
            RequestMethod.GET }, produces = "application/json;charset=utf-8")
    @Override
    public ApiResult<List<Cargo>> getList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getList: {}", param);
        try {
            PageInfo<Cargo> pageInfo = cargoservice.queryList(param.getData(), param.getPageNum(), param.getPageSize());
            log.debug("get result: {}", pageInfo);
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Override
    public ApiResult<String> post(@RequestBody ApiParam<String> param) {
        log.debug("post: {}", param);
        try {
            Class<?> clazz = BeanUtils.getGenericClass(this.getClass());
            Cargo t = (Cargo)JSON.parseObject(param.getData(), clazz);
            getService().add(t);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("添加失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/putPart", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Override
    public ApiResult<String> putPart(@RequestBody ApiParam<String> param) {
        log.debug("put: {}", param);
        try {
            Class<?> clazz = BeanUtils.getGenericClass(this.getClass());
            Cargo t = (Cargo)JSON.parseObject(param.getData(), clazz);
            getService().updateSelective(t);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("修改失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/put", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Override
    public ApiResult<String> put(@RequestBody ApiParam<String> param) {
        log.debug("put: {}", param);
        try {
            Class<?> clazz = BeanUtils.getGenericClass(this.getClass());
            Cargo t = (Cargo)JSON.parseObject(param.getData(), clazz);
            getService().update(t);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("修改失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Override
    public ApiResult<String> delete(@RequestBody ApiParam<Long> param) {
        log.debug("remove: {}", param);
        try {
            getService().deleteById(param.getData());
            return ApiResult.success();
        } catch (Exception e) {
            log.error("删除失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }


}
