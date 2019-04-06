package com.tilchina.timp.controller;

import com.tilchina.timp.expection.BusinessException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.OilDepot;
import com.tilchina.timp.model.OilDepotSupplyRecord;
import com.tilchina.timp.model.TransporterFuelRecord;
import com.tilchina.timp.service.OilDepotService;
import com.tilchina.timp.service.OilDepotSupplyRecordService;
import com.tilchina.timp.service.TransporterFuelRecordService;

/**
* 油库管理
*
* @version 1.0.0
* @author LiushuQi
*/
@RestController
@RequestMapping(value = {"/s1/oildepot"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class OilDepotController{

	@Autowired
	private OilDepotService oildepotservice;
	
	@Autowired
	private OilDepotSupplyRecordService oilDepotSupplyRecordService;
	
	@Autowired
	private TransporterFuelRecordService transporterFuelRecordService;
	
	/**
	 * 通过ID 查询油库
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/get", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<OilDepot> getById(@RequestBody ApiParam<Long> param) {
        log.debug("get: {}", param);
        try {
        	OilDepot t = oildepotservice.queryById(param.getData());
            log.debug("get result: {}", t);
            return ApiResult.success(t);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	/**
	 * 查询所有油库
	 * @param param
	 * @return
	 */
    @RequestMapping(value = "/getList", method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<OilDepot>> getList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getList: {}", param);
        try {
            PageInfo<OilDepot> pageInfo = oildepotservice.queryList(param.getData(), param.getPageNum(), param.getPageSize());
            log.debug("get result: {}", pageInfo);
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    /**
	 * 新增油库
	 * @param param
	 * @return
	 */
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> post(@RequestBody ApiParam<OilDepot> param) {
        log.debug("post: {}", param);
        try {
            oildepotservice.add(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("添加油库失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    /**
	 * 部分更新油库
	 * @param param
	 * @return
	 */
    @RequestMapping(value = "/putPart", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> putPart(@RequestBody ApiParam<String> param) {
        log.debug("putPart: {}", param);
        try {
            Pattern p = Pattern.compile("[a-zA-z]");
            String allowance=param.getData().substring(param.getData().indexOf("allowance")+10,param.getData().indexOf("oilDepotStatus")-1);
            if(p.matcher(allowance).find()){
                throw new BusinessException("余量输入有误");
            }
            OilDepot t = (OilDepot)JSON.parseObject((String)param.getData(), OilDepot.class);
            oildepotservice.updateSelective(t);
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("修改失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    /**
	 * 更新全部油库
	 * @param param
	 * @return
	 */
    @RequestMapping(value = "/put", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> put(@RequestBody ApiParam<String> param) {
        log.debug("put: {}", param);
        try {
            Class<?> clazz = BeanUtils.getGenericClass(this.getClass());
            OilDepot t = (OilDepot)JSON.parseObject(param.getData(), clazz);
            oildepotservice.update(t);
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("修改失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    /**
	 * 通过ID 删除油库
	 * @param param
	 * @return
	 */
    @RequestMapping(value = "/remove", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> delete(@RequestBody ApiParam<Long> param) {
        log.debug("remove: {}", param);
        try {
        	oildepotservice.deleteById(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("删除油库失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    /**
	 * 收油
	 * @param param
	 * @return
	 */
    @RequestMapping(value = "/supply", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> supply(@RequestBody ApiParam<OilDepotSupplyRecord> param) {
        log.debug("supply: {}", param);
        try {
        	oildepotservice.supply(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("收油失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    /**
	 * 收油记录
	 * @param param
	 * @return
	 */
    @RequestMapping(value = "/supplyList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<OilDepotSupplyRecord>> supplyList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("supplyList: {}", param);
        try {
        	PageInfo<OilDepotSupplyRecord> list=oilDepotSupplyRecordService.queryList(param.getData(), param.getPageNum(), param.getPageSize());
            return ApiResult.success(list);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    /**
	 * 车辆加油
	 * @param param
	 * @return
	 */
    @RequestMapping(value = "/fuel", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> fuel(@RequestBody ApiParam<TransporterFuelRecord> param) {
        log.debug("remove: {}", param);
        try {
        	oildepotservice.fuel(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("加油失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    /**
	 * 车辆加油记录
	 * @param param
	 * @return
	 */
    @RequestMapping(value = "/fuelList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<TransporterFuelRecord>> fuelList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("remove: {}", param);
        try {
        	PageInfo<TransporterFuelRecord> list=transporterFuelRecordService.queryList(param.getData(), param.getPageNum(), param.getPageSize());
            return ApiResult.success(list);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    /**
   	 * 启用
   	 * @param param 油库ID
   	 * @return
   	 */
       @RequestMapping(value = "/start", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
       @Auth(ClientType.WEB)
       public ApiResult<String> start(@RequestBody ApiParam<Map<String, Object>> param) {
           log.debug("start: {}", param);
           try {
        	   oildepotservice.start(param.getData());
               return ApiResult.success(LanguageUtil.getMessage("0000"));
           } catch (Exception e) {
               log.error("启用失败，param={}", param, e);
               return ApiResult.failure("9999", e.getMessage());
           }
       }
       
       /**
     	 * 停用
     	 * @param param 油库ID
     	 * @return
     	 */
         @RequestMapping(value = "/stop", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
         @Auth(ClientType.WEB)
         public ApiResult<String> stop(@RequestBody ApiParam<Map<String, Object>> param) {
             log.debug("stop: {}", param);
             try {
           	  oildepotservice.stop(param.getData());
                 return ApiResult.success(LanguageUtil.getMessage("0000"));
             } catch (Exception e) {
                 log.error("停用失败，param={}", param, e);
                 return ApiResult.failure("9999", e.getMessage());
             }
         }




    /**
     * 通过ID 查询收油记录
     * @param param
     * @return
     */
    @RequestMapping(value = "/getBySupplyRecordId", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<OilDepotSupplyRecord> getBySupplyRecordId(@RequestBody ApiParam<Long> param) {
        log.debug("getBySupplyRecordId: {}", param);
        try {
            OilDepotSupplyRecord t = oilDepotSupplyRecordService.queryById(param.getData());
            log.debug("get result: {}", t);
            return ApiResult.success(t);
        } catch (Exception e) {
            log.error("查询收油记录失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    /**
     * 查询所有收油记录
     * @param param
     * @return
     */
    @RequestMapping(value = "/getSupplyRecordList", method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<OilDepotSupplyRecord>> getSupplyRecordList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getSupplyRecordList: {}", param);
        try {
            PageInfo<OilDepotSupplyRecord> pageInfo = oilDepotSupplyRecordService.queryList(param.getData(), param.getPageNum(), param.getPageSize());
            log.debug("get result: {}", pageInfo);
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("查询所有收油记录失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    /**
     * 新增收油记录
     * @param param
     * @return
     */
    @RequestMapping(value = "/addSupplyRecord", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> addSupplyRecord(@RequestBody ApiParam<OilDepotSupplyRecord> param) {
        log.debug("addSupplyRecord: {}", param);
        try {
            oilDepotSupplyRecordService.add(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("添加收油记录失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    /**
     * 部分更新收油记录
     * @param param
     * @return
     */
    @RequestMapping(value = "/putPartSupplyRecord", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> putPartSupplyRecord(@RequestBody ApiParam<OilDepotSupplyRecord> param) {
        log.debug("putPartSupplyRecord: {}", param);
        try {
            oilDepotSupplyRecordService.updateSelective(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("修改收油记录失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    /**
     * 通过ID 删除收油记录
     * @param param
     * @return
     */
    @RequestMapping(value = "/removeSupplyRecord", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> removeSupplyRecord(@RequestBody ApiParam<Long> param) {
        log.debug("removeSupplyRecord: {}", param);
        try {
            oilDepotSupplyRecordService.deleteById(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("删除收油失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    /**
     * 通过ID 查询加油记录
     * @param param
     * @return
     */
    @RequestMapping(value = "/getByFuelRecordId", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<TransporterFuelRecord> getByFuelRecordId(@RequestBody ApiParam<Long> param) {
        log.debug("getByFuelRecordId: {}", param);
        try {
            TransporterFuelRecord t = transporterFuelRecordService.queryById(param.getData());
            log.debug("get result: {}", t);
            return ApiResult.success(t);
        } catch (Exception e) {
            log.error("查询加油记录失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    /**
     * 查询所有加油记录
     * @param param
     * @return
     */
    @RequestMapping(value = "/getFuelRecordList", method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<TransporterFuelRecord>> getBySupplyRecordList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getFuelRecordList: {}", param);
        try {
            PageInfo<TransporterFuelRecord> pageInfo = transporterFuelRecordService.queryList(param.getData(), param.getPageNum(), param.getPageSize());
            log.debug("get result: {}", pageInfo);
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("查询所有加油记录失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    /**
     * 新增加油记录
     * @param param
     * @return
     */
    @RequestMapping(value = "/addFuelRecord", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> addFuelRecord(@RequestBody ApiParam<TransporterFuelRecord> param) {
        log.debug("addFuelRecord: {}", param);
        try {
            transporterFuelRecordService.add(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("添加加油记录失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    /**
     * 部分更新加油记录
     * @param param
     * @return
     */
    @RequestMapping(value = "/putPartFuelRecord", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> putPartFuelRecord(@RequestBody ApiParam<TransporterFuelRecord> param) {
        log.debug("putPartFuelRecord: {}", param);
        try {
            transporterFuelRecordService.updateSelective(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("修改加油记录失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    /**
     * 通过ID 删除加油记录
     * @param param
     * @return
     */
    @RequestMapping(value = "/removeFuelRecord", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> removeFuelRecord(@RequestBody ApiParam<Long> param) {
        log.debug("removeFuelRecord: {}", param);
        try {
            transporterFuelRecordService.deleteById(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("删除加油记录失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
       
    

}
