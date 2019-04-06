package com.tilchina.timp.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.model.StockCar;
import com.tilchina.timp.service.StockCarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

/**
* 商品车库存表
*
* @version 1.0.0
* @author Xiahong
*/
@RestController
@RequestMapping(value = {"/s1/stockcar"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class StockCarController {

	@Autowired
	private StockCarService stockcarservice;
	
	@RequestMapping(value = "/get", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<StockCar> getById(@RequestBody ApiParam<Long> param) {
        log.debug("get: {}", param);
        try {
        	StockCar t = stockcarservice.queryById(param.getData());
            log.debug("get result: {}", t);
            return ApiResult.success(t);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
	/**
	 * 测试通过车架号和所在单位去查询商品车库存表信息
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/getTest", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@Auth(ClientType.WEB)
    public ApiResult<StockCar> getTest(@RequestBody ApiParam<Long> param) {
        log.debug("get: {}", param);
        try {
        	StockCar t = stockcarservice.queryByCarVin("LBVHZ3103JMN49191");
            log.debug("get result: {}", t);
            return ApiResult.success(t);
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
	
    @RequestMapping(value = "/getList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<StockCar>> getList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getList: {}", param);
        try {
            PageInfo<StockCar> pageInfo = stockcarservice.queryList(param.getData(), param.getPageNum(), param.getPageSize());
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
            StockCar t = JSON.parseObject(param.getData(), StockCar.class);
            stockcarservice.add(t);
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
           
           StockCar t = JSON.parseObject(param.getData(), StockCar.class);
            stockcarservice.updateSelective(t);
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
           
           StockCar t = JSON.parseObject(param.getData(), StockCar.class);
            stockcarservice.update(t);
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
            stockcarservice.deleteById(param.getData());
            return ApiResult.success();
        } catch (Exception e) {
            log.error("删除失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    
    /**
     * 修改车辆状态
     * @param param
     * @return
     */
    @RequestMapping(value = "/carStatus", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> putCarStatus(@RequestBody ApiParam<String> param) {
        log.debug("put: {}", param);
        try {
           
           StockCar t = JSON.parseObject(param.getData(), StockCar.class);
            stockcarservice.updateCarStatus(t,0);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("修改失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    /**
     * 车架号参照
     * @param param
     * @return
     */
    @RequestMapping(value = "/stockCarRefer", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<StockCar>> stockCarRefer(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getTransportRefer: {}", param);
        try {
        	PageInfo<StockCar> depts = stockcarservice.stockCarRefer(param.getData(),param.getPageNum(), param.getPageSize());
            return ApiResult.success(depts);
        } catch (Exception e) {
            log.error("查询运单车架号参照失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    /**
     * 运单车架号参照
     * @param param
     * @return
     */
    @RequestMapping(value = "/transportOrderRefer", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<StockCar>> transportOrderRefer(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("transportOrderRefer: {}", param);
        try {
        	PageInfo<StockCar> depts = stockcarservice.transportOrderRefer(param.getData(),param.getPageNum(), param.getPageSize());
            return ApiResult.success(depts);
        } catch (Exception e) {
            log.error("查询运单车架号参照失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
    /**
     * 获取车辆状态在6-10之前的车架号列表
     * @param param
     * @return
     */
    @RequestMapping(value = "/getVinList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<List<StockCar>> getVinList(@RequestBody ApiParam<Map<String, Object>> param) {
        log.debug("getVinList: {}", param);
        try {
        	PageInfo<StockCar> vinList = stockcarservice.getVinList(param.getData(),param.getPageNum(),param.getPageSize());
            return ApiResult.success(vinList);
        } catch (Exception e) {
            log.error("查询运单车架号参照失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    
}
