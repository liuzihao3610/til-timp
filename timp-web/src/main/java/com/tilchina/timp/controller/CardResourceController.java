package com.tilchina.timp.controller;

import com.github.pagehelper.PageInfo;
import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.CardReceiveRecord;
import com.tilchina.timp.model.CardRechargeRecord;
import com.tilchina.timp.model.CardResource;
import com.tilchina.timp.service.CardReceiveRecordService;
import com.tilchina.timp.service.CardRechargeRecordService;
import com.tilchina.timp.service.CardResourceService;
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
* 卡资源管理
*
* @version 1.0.0
* @author LiushuQi
*/
@RestController
@RequestMapping(value = {"/s1/cardresource"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class CardResourceController{

	@Autowired
	private CardResourceService cardresourceservice;
	
	@Autowired
	private CardReceiveRecordService cardReceiveRecordService;
	
	@Autowired
	private CardRechargeRecordService cardRechargeRecordService;

	/**
	 * 通过ID 查询
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/get", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<CardResource> getById(@RequestBody ApiParam<Long> param) {
        log.debug("get: {}", param);
        try {
        	CardResource t = cardresourceservice.queryById(param.getData());
            log.debug("get result: {}", t);
            return ApiResult.success(t);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	/**
	 * 查询所有
	 * @param param
	 * @return
	 */
    @RequestMapping(value = "/getList", method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<CardResource>> getList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getList: {}", param);
        try {
            PageInfo<CardResource> pageInfo = cardresourceservice.queryList(param.getData(), param.getPageNum(), param.getPageSize());
            log.debug("get result: {}", pageInfo);
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    /**
	 * 新增
	 * @param param
	 * @return
	 */
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> post(@RequestBody ApiParam<CardResource> param) {
        log.debug("post: {}", param);
        try {
            cardresourceservice.add(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("添加失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    /**
	 * 部分更新
	 * @param param
	 * @return
	 */
    @RequestMapping(value = "/putPart", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> putPart(@RequestBody ApiParam<CardResource> param) {
        log.debug("put: {}", param);
        try {
            cardresourceservice.updateSelective(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("修改失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    /**
	 * 更新全部
	 * @param param
	 * @return
	 */
    @RequestMapping(value = "/put", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> put(@RequestBody ApiParam<CardResource> param) {
        log.debug("put: {}", param);
        try {
            cardresourceservice.update(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("修改失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    /**
	 * 通过ID 删除
	 * @param param
	 * @return
	 */
    @RequestMapping(value = "/remove", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> delete(@RequestBody ApiParam<Long> param) {
        log.debug("remove: {}", param);
        try {
        	cardresourceservice.deleteById(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("删除失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    /**
	 * 领用
	 * @param param
	 * @return
	 */
    @RequestMapping(value = "/receive", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> receive(@RequestBody ApiParam<CardReceiveRecord> param) {
        log.debug("receive: {}", param);
        try {
        	cardresourceservice.receive(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("领用失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    /**
     * 取消领用/归还
     * @param param
     * @return
     */
    @RequestMapping(value = "/unReceive", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> unReceive(@RequestBody ApiParam<Long> param) {
        log.debug("unReceive: {}", param);
        try {
            cardresourceservice.unReceive(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("归还失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    /**
	 * 领用记录
	 * @param param
	 * @return
	 */
    @RequestMapping(value = "/receiveList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<CardReceiveRecord>> receiveList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("receiveList: {}", param);
        try {
        	PageInfo<CardReceiveRecord> list=cardReceiveRecordService.queryList(param.getData(), param.getPageNum(), param.getPageSize());
            return ApiResult.success(list);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    /**
	 * 充值
	 * @param param
	 * @return
	 */
    @RequestMapping(value = "/recharge", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> recharge(@RequestBody ApiParam<CardRechargeRecord> param) {
        log.debug("recharge: {}", param);
        try {
        	cardresourceservice.recharge(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("充值失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    /**
   	 * 充值记录
   	 * @param param
   	 * @return
   	 */
       @RequestMapping(value = "/rechargeList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
       @Auth(ClientType.WEB)
       public ApiResult<List<CardRechargeRecord>> rechargeList(@RequestBody ApiParam<Map<String, Object>> param) {
           log.debug("rechargeList: {}", param);
           try {
           	PageInfo<CardRechargeRecord> list=cardRechargeRecordService.queryList(param.getData(), param.getPageNum(), param.getPageSize());
               return ApiResult.success(list);
           } catch (Exception e) {
               log.error("查询失败，param={}", param, e);
               return ApiResult.failure("9999", e.getMessage());
           }
       }

    /**
     * 丢失
     * @param param
     * @return
     */
    @RequestMapping(value = "/lose", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> lose(@RequestBody ApiParam<Long> param) {
        log.debug("lose: {}", param);
        try {
            cardresourceservice.lose(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("修改丢失失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    /**
     * 取消丢失/找到
     * @param param
     * @return
     */
    @RequestMapping(value = "/find", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> find(@RequestBody ApiParam<Long> param) {
        log.debug("find: {}", param);
        try {
            cardresourceservice.find(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("修改丢失失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    /**
     * 作废
     * @param param
     * @return
     */
    @RequestMapping(value = "/invalid", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> invalid(@RequestBody ApiParam<Long> param) {
        log.debug("invalid: {}", param);
        try {
            cardresourceservice.invalid(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("修改作废失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    /**
     * 取消作废
     * @param param
     * @return
     */
    @RequestMapping(value = "/unInvalid", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> unInvalid(@RequestBody ApiParam<Long> param) {
        log.debug("unInvalid: {}", param);
        try {
            cardresourceservice.unInvalid(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("取消作废失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

	@PostMapping("importExcel")
	@Auth(ClientType.WEB)
	public ApiResult<String> importExcel(@RequestParam("file") MultipartFile file) {
		try {
			cardresourceservice.importExcel(file);
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

	@PostMapping("exportExcel")
	@Auth(ClientType.WEB)
	public ApiResult<String> exportExcel(HttpServletResponse response) {
		try {
			Workbook workbook = cardresourceservice.exportExcel();
			ExcelUtil.returnToBrowser(response, workbook, "卡资源管理档案");
			return ApiResult.success();
		} catch (Exception e) {
			log.error("{}", e);
			return ApiResult.failure("9999", e.getMessage());
		}
	}

}
