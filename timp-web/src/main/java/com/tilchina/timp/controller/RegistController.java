package com.tilchina.timp.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.tilchina.auth.Auth;
import com.tilchina.auth.manager.PowerManager;
import com.tilchina.auth.vo.RegistInfoVO;
import com.tilchina.auth.vo.UserInfoVO;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.enums.RegistType;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.service.PowerService;
import com.tilchina.timp.vo.RegistTreeVO;
import com.tilchina.timp.vo.RegistVO;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.Regist;
import com.tilchina.timp.service.RegistService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 功能档案
 * @author XiaHong
 * @version 1.1.0
 */
@RestController
@RequestMapping(value = {"/s1/regist"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class RegistController /*extends BaseControllerImpl<Regist> */{

    @Autowired
    private RegistService registservice;

    @Autowired
    private PowerManager powerManager;

    @Autowired
    private PowerService powerservice;

    protected BaseService<Regist> getService() {
        return registservice;
    }

    
    
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> post(@RequestBody ApiParam<String> param) {
        log.debug("post: {}", param);
        try {
        	Regist t = (Regist)JSON.parseObject(param.getData(), Regist.class);
            getService().add(t);
            return ApiResult.success();
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
            registservice.logicDeleteById(param.getData());
            return ApiResult.success();
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
            registservice.logicDeleteByIdList(param.getData());
            return ApiResult.success("删除成功");
        } catch (Exception e) {
            log.error("删除失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    @RequestMapping(value = "/putPart", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> putPart(@RequestBody ApiParam<String> param) {
        log.debug("put: {}", param);
        try {
            Regist t = (JSON.parseObject(param.getData(), Regist.class));
            getService().updateSelective(t);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("修改失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/put", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<String> put(@RequestBody ApiParam<String> param) {
        log.debug("put: {}", param);
        try {
            Regist t = (JSON.parseObject(param.getData(), Regist.class));
            getService().update(t);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("修改失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    @RequestMapping(value = "/getDynamicList", method =RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<Map<String, Object>> getDynamicList() {
        try {
            Map<String, Object> t = registservice.queryDynamicList();
            log.debug("get result: {}", t);
            return ApiResult.success(t);
        } catch (Exception e) {
            log.error("查询失败，param={}", e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/get", method =  RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<Regist> getById(@RequestBody ApiParam<Long> param) {
        log.debug("get: {}", param);
        try {
            Regist t = getService().queryById(param.getData());
            log.debug("get result: {}", t);
            return ApiResult.success(t);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
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
    public ApiResult<List<Regist>> getReferenceList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getList: {}", param);
        try {
            PageInfo<Regist> pageInfo = registservice.getRefer(param.getData(), param.getPageNum(), param.getPageSize());
            log.debug("get result: {}", pageInfo);
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }


    @RequestMapping(value = "/getList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<Regist>> getList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getList: {}", param);
        try {
            PageInfo<Regist> pageInfo = getService().queryList(param.getData(), param.getPageNum(), param.getPageSize());
            log.debug("get result: {}", pageInfo);
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    /**
     * 获取上级节点信息
     * @param param
     * @return
     */
    @RequestMapping(value = "/getUpReferences", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<Regist>> getUpReferences(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getUpReferences: {}", param);
        try {
            List<Regist> pageInfo = registservice.getUpReferences(param.getData());
            log.debug("getUpReferences result: {}", pageInfo);
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/getPower", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<RegistInfoVO> getPower() {
        try {
            RegistInfoVO info = powerManager.getPower();
            log.debug("getPower result: {}", info);
            return ApiResult.success(info);
        } catch (Exception e) {
            log.error("获取权限失败！，param={}", e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    /**
     * 返回树形功能注册列表(虚拟节点和功能节点)
     * @return
     */
    @RequestMapping(value = "/getRegist", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<Regist>> getRegist() {
        log.debug("getRegist: {}");
        try {
            List<Regist> pageInfo = registservice.getRegist();
            log.debug("getRegist result: {}", pageInfo);
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("查询失败，param={}", e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    /**
     * 传功能节点ID 返回所在节点下的所有按钮数据
     * @param param
     * @return
     */
    @RequestMapping(value = "/getButtonRegist", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<RegistVO>> getButtonRegist(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getButtonRegist: {}",param);
        try {
            List<RegistVO> pageInfo = registservice.getButtonRegist(param.getData());
            log.debug("getButtonRegist result: {}", pageInfo);
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("查询失败，param={}",param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    /**
     * 给功能注册列表功能名称命名
     * @param param
     * @return
     */
    @RequestMapping(value = "/test1", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> test1(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("test: {}", param);
        try {
            registservice.test();
            return ApiResult.success();
        } catch (Exception e) {
            log.error("【给功能注册列表功能名称命名】失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    /**
     * 分配权限
     * @param param
     * @return
     */
    @RequestMapping(value = "/allotPower", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<RegistTreeVO>> allotPower(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("allotPower: {}", param);
        try {

            List<RegistTreeVO> regists = registservice.allotPower(param.getData());
            return ApiResult.success(regists);
        } catch (Exception e) {
            log.error("【分配权限】查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    /**
     * 分配权限
     * @param param
     * @return
     */
    @RequestMapping(value = "/savePower", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> savePower(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("savePower: {}", param);
        try {
            Map<String, Object> data = param.getData();
            List<RegistTreeVO> regists = JSON.parseArray( data.get("node").toString()).toJavaList(RegistTreeVO.class);
            powerservice.savePower(regists,Long.valueOf(data.get("groupId").toString()));
            return ApiResult.success();
        } catch (Exception e) {
            log.error("【分配权限】查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

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
