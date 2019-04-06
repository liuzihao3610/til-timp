package com.tilchina.timp.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.Login;
import com.tilchina.timp.model.User;
import com.tilchina.timp.service.UserService;
import com.tilchina.timp.util.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
* 用户档案
*
* @version 1.1.0
* @author XiaHong
*/
@RestController
@RequestMapping(value = {"/s1/user"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class UserController {

	@Autowired
	private UserService userservice;
	
    @RequestMapping(value = "/get", method =  RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<User> getById(@RequestBody ApiParam<Long> param) {
        log.debug("get: {}", param);
        try {
        	User t = userservice.queryById(param.getData());
            log.debug("get result: {}", t);
            return ApiResult.success(t);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/getList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<User>> getList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getList: {}", param);
        try {
            PageInfo<User> pageInfo = userservice.queryList(param.getData(), param.getPageNum(), param.getPageSize());
            log.debug("get result: {}", pageInfo);
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

	@RequestMapping(value = "/addUser", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
	public ApiResult<JSONObject> postUser(@RequestBody ApiParam<String> param) {
		log.debug("post: {}", param);
		try {
			User user = JSON.parseObject(param.getData(), User.class);
			JSONObject json = userservice.addUser(user);
			return ApiResult.success(json);
		} catch (Exception e) {
			log.error("添加失败，param={}", param, e.getMessage());
			return ApiResult.failure("9999", e.getMessage());
		}
	}
	
    @RequestMapping(value = "/remove", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> delete(@RequestBody ApiParam<Long> param) {
        log.debug("remove: {}", param);
        try {
        	userservice.logicDeleteById(param.getData());
            return ApiResult.success();
        } catch (Exception e) {
            log.error("删除失败，param={}", param, e.getMessage());
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    @RequestMapping(value = "/putPart", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> putPart(@RequestBody ApiParam<String> param) {
        log.debug("put: {}", param);
        try {
            User user = JSON.parseObject(param.getData(), User.class);
            userservice.updateSelective(user);
            return ApiResult.success("修改成功");
        } catch (Exception e) {
            log.error("修改失败，param={}", param, e.getMessage());
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    

    @RequestMapping(value = "/putPartLogin", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> putPartLogin(@RequestBody ApiParam<Login> param) {
        log.debug("put: {}", param);
        try {
            userservice.updateLogin(param.getData());
            return ApiResult.success("操作成功");
        } catch (Exception e) {
            log.error("操作失败，param={}", param, e.getMessage());
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
        	User user = JSON.parseObject(param.getData(), User.class);
        	user.setBillStatus(1);
        	userservice.updateCheck(user);
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
    @RequestMapping(value = "/cancelCheck", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> putCancelCheck(@RequestBody ApiParam<String> param) {
        log.debug("putCheck: {}", param);
        try {
        	User user = JSON.parseObject(param.getData(), User.class);
        	user.setBillStatus(0);
        	userservice.updateCheck(user);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("修改失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    
    @RequestMapping(value = "/getByCorpId", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<User>> getByDriverId(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getByCorpId: {}", param);
        try {
        	PageInfo<User> pageInfo = userservice.queryByCorpId(param.getData(), param.getPageNum(), param.getPageSize());
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e.getMessage());
            return ApiResult.failure("9999", e.getMessage());
        }
    }


    /***
     * 用户解锁
     * @param param
     * @return
     */
    @RequestMapping(value = "/deblocking", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> deblocking(@RequestBody ApiParam<User> param) {
        log.debug("putCheck: {}", param);
        try {
        	userservice.updateBlock(param.getData());
            return ApiResult.success();
        } catch (Exception e) {
            log.error("修改失败，param={}", param, e);
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
    public ApiResult<JSONObject> resetPasswords(@RequestBody ApiParam<User> param) {
        log.debug("putCheck: {}", param);
        try {
        	JSONObject json = userservice.updatePasswords(param.getData());
            return ApiResult.success(json);
        } catch (Exception e) {
            log.error("修改失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    /**
     * 参照
     * @param param
     * @return
     */
    @RequestMapping(value = "/getReferenceList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ApiResult<List<User>> getRefer(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getRefer: {}", param);
        try {
        	PageInfo<User> users = userservice.getRefer(param.getData(),param.getPageNum(), param.getPageSize());
            return ApiResult.success(users);
        } catch (Exception e) {
            log.error("查询参照失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/addAvatrar", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> addAvatrar(@RequestParam("file") MultipartFile file,
                                          @RequestParam("userId") Long userId
    ) {
        log.debug("addHeadAvatrar: {}");
        try {
            userservice.addHeadAvatrar(file,userId);
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("添加失败，param={}", e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/addPhotoPath", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> addPhotoPath(@RequestParam("file") MultipartFile file,
                                        @RequestParam("userId") Long userId
    ) {
        log.debug("addPhotoPath: {}");
        try {
            userservice.addPhotoPath(file,userId);
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("添加失败，param={}", e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }



    @RequestMapping(value = "/importExcel", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> importExcel(@RequestParam("file") MultipartFile file) {
        log.debug("importExcel: {}", file);
        try {
            userservice.importExcel(file);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("导入Excel 失败，param={}", file, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @GetMapping("exportExcel")
    @Auth(ClientType.WEB)
    public ApiResult<String> exportExcel(HttpServletResponse response) {
        try {
            Workbook workbook = userservice.exportExcel();
            ExcelUtil.returnToBrowser(response, workbook, "用户档案");
            return ApiResult.success();
        } catch (Exception e) {
            log.error("{}", e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }


}
