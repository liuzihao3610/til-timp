package com.tilchina.timp.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.tilchina.auth.Auth;
import com.tilchina.auth.manager.RailtransOrderManager;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.model.RailtransOrder;
import com.tilchina.timp.model.RailtransOrderDetail;
import com.tilchina.timp.service.RailtransOrderDetailService;
import com.tilchina.timp.service.RailtransOrderService;
import com.tilchina.timp.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 铁路运输记录主表
 *
 * @author XueYuSong
 * @version 1.0.0
 */
@RestController
@RequestMapping(value = {"/s1/railtrans"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class RailtransOrderController {

    @Autowired
    private RailtransOrderService railtransOrderService;

    @Autowired
    private RailtransOrderDetailService railtransOrderDetailService;

    @Autowired
    private RailtransOrderManager manager;

    // @Override
    protected BaseService<RailtransOrder> getService() {
        return railtransOrderService;
    }

    /* 铁路运单主表方法 */
    // C: 新增一条铁路运输记录
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @Auth(ClientType.WEB)
    public ApiResult<String> add(@RequestBody ApiParam<RailtransOrder> param) {

        log.debug("/railtrans/add: {}", param);
        Environment environment = Environment.getEnv();
        try {

            RailtransOrder order = param.getData();
            order.setTransOrderCode(DateUtil.dateToStringCode(new Date()));
            order.setCreator(environment.getUser().getUserId());
            order.setCreateDate(Calendar.getInstance().getTime());
            order.setCorpId(environment.getCorp().getCorpId());
            railtransOrderService.add(order);

            if (order.getDetails() != null && order.getDetails().size() > 0) {

                order.getDetails().forEach(detail -> {

                    detail.setTransOrderId(order.getTransOrderId());
                    detail.setCreator(order.getCreator());
                    detail.setCreateDate(order.getCreateDate());
                    detail.setCorpId(order.getCorpId());
                });
                railtransOrderDetailService.add(order.getDetails());
            }

            return ApiResult.success();

        } catch (Exception e) {
            log.error("新增铁路运输记录失败: {}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    // R: 查询一条铁路运输记录
    @RequestMapping(value = "/get", method = RequestMethod.POST)
    @Auth(ClientType.WEB)
    public ApiResult<RailtransOrder> getById(@RequestBody ApiParam<Long> param) {

        log.debug("/railtrans/get: {}", param.getData());
        try {
            RailtransOrder record = railtransOrderService.queryById(param.getData());
            return ApiResult.success(record);
        } catch (Exception e) {
            log.error("查询铁路运输记录失败: {}", param.getData(), e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    @Auth(ClientType.WEB)
    public ApiResult<List<RailtransOrder>> getList(@RequestBody ApiParam<Map<String, Object>> param) {

        log.debug("/railtrans/list: {}", param);
        try {
            PageInfo<RailtransOrder> pageInfo = railtransOrderService.queryList((Map) param.getData(), param.getPageNum(), param.getPageSize());
            return ApiResult.success(pageInfo);
        } catch (Exception e) {
            log.error("查询铁路运输记录失败: {}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    // U: 更新铁路运输记录(部分信息)
    @RequestMapping(value = "/put", method = RequestMethod.POST)
    @Auth(ClientType.WEB)
    public ApiResult<String> putPart(@RequestBody ApiParam<String> param) {

        log.debug("/railtrans/update: {}", param);
        try {

            RailtransOrder record = JSON.parseObject(param.getData(), RailtransOrder.class);
            railtransOrderService.update(record);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("更新铁路运输记录(部分信息)失败: {}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    // D: 删除铁路运输记录
    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    @Auth(ClientType.WEB)
    public ApiResult<String> delete(@RequestBody ApiParam<Long> param) {

        log.debug("/railtrans/remove: {}", param);
        try {
            if (param.getData() == null) {
                return ApiResult.failure("9999", "Request data is null.");
            } else {
                railtransOrderService.delete(param.getData());
                return ApiResult.success();
            }
        } catch (Exception e) {
            log.error("删除铁路运输记录失败: {}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/removeList", method = RequestMethod.POST)
    @Auth(ClientType.WEB)
    public ApiResult<String> deleteList(@RequestBody ApiParam<Long[]> param) {

        log.debug("remove: {}", param);
        try {
            railtransOrderService.deleteList(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error(LanguageUtil.getMessage("9999"), "param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    /* 铁路运单主表方法 */

    /* 铁路运单子表方法 */
    @RequestMapping(value = "/addDetail", method = RequestMethod.POST)
    @Auth(ClientType.WEB)
    public ApiResult<String> postDetail(@RequestBody ApiParam<String> param) {

        log.debug("/railtrans/addDetail: {}", param);
        Environment environment = Environment.getEnv();
        try {

            RailtransOrderDetail order = JSON.parseObject(param.getData(), RailtransOrderDetail.class);
            order.setTransOrderId(order.getTransOrderId());
            order.setCreator(environment.getUser().getUserId());
            order.setCreateDate(Calendar.getInstance().getTime());
            order.setCorpId(environment.getCorp().getCorpId());

            railtransOrderDetailService.add(order);
            return ApiResult.success();

        } catch (Exception e) {
            log.error("ERROR(postDetail): {} {}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/getDetail", method = RequestMethod.POST)
    @Auth(ClientType.WEB)
    public ApiResult<RailtransOrderDetail> getDetail(@RequestBody ApiParam<Long> param) {

        log.debug("/railtrans/get: {}", param.getData());
        try {
            RailtransOrderDetail record = railtransOrderDetailService.queryById(param.getData());
            return ApiResult.success(record);
        } catch (Exception e) {
            log.error("ERROR(getDetail): {} {}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/getDetailList", method = RequestMethod.POST)
    @Auth(ClientType.WEB)
    public ApiResult<List<RailtransOrderDetail>> getDetailList(@RequestBody ApiParam<Map<String, Object>> param) {

        log.debug("/railtrans/list: {}", param);
        try {
            Map<String,Object> map = param.getData();
            String transOrderId = map.get("transOrderId")==null?null:map.get("transOrderId").toString();
            String cabinNumber = map.get("cabinNumber")==null?null:map.get("cabinNumber").toString();
            List<String> vinNumbers = map.get("vinNumbers")==null?null:(List)map.get("vinNumbers");

            if(StringUtils.isBlank(transOrderId) && StringUtils.isBlank(cabinNumber) && CollectionUtils.isEmpty(vinNumbers)){
                throw new BusinessException("9003");
            }

            List result = railtransOrderDetailService.getByTransOrderId(map);
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("ERROR(getDetailList): {} {}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/openQuery", method = RequestMethod.POST)
    public ApiResult<List<RailtransOrderDetail>> openQuery(@RequestBody ApiParam<Map<String, Object>> param) {

        log.debug("/railtrans/openQuery: {}", param);
        try {
            Map<String,Object> map = param.getData();
            String cabinNumber = map.get("cabinNumber")==null?null:map.get("cabinNumber").toString();
            List<String> vinNumbers = map.get("vinNumbers")==null?null:(List)map.get("vinNumbers");

            if(StringUtils.isBlank(cabinNumber) && CollectionUtils.isEmpty(vinNumbers)){
                throw new BusinessException("9003");
            }

            Map<String,Object> queryMap = new HashMap<>();
            queryMap.put("cabinNumber",cabinNumber);
            queryMap.put("vinNumbers",vinNumbers);

            List result = railtransOrderDetailService.getByTransOrderId(queryMap);

            return ApiResult.success(result,0,result.size());
        } catch (Exception e) {
            log.error("ERROR(openQuery): {} {}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/removeDetail", method = RequestMethod.POST)
    @Auth(ClientType.WEB)
    public ApiResult<String> deleteDetail(@RequestBody ApiParam<Long> param) {

        log.debug("/railtrans/remove: {}", param);
        try {
            if (param.getData() == null) {
                return ApiResult.failure("9999", "Request data is null.");
            } else {
                railtransOrderDetailService.delete(param.getData());
                return ApiResult.success();
            }
        } catch (Exception e) {
            log.error("ERROR(removeDetail): {} {}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/removeDetailList", method = RequestMethod.POST)
    @Auth(ClientType.WEB)
    public ApiResult<String> deleteDetailList(@RequestBody ApiParam<Long[]> param) {

        log.debug("remove: {}", param);
        try {
            railtransOrderDetailService.deleteList(param.getData());
            return ApiResult.success(LanguageUtil.getMessage("0000"));
        } catch (Exception e) {
            log.error("ERROR(removeDetailList): {} {}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/putDetail", method = RequestMethod.POST)
    @Auth(ClientType.WEB)
    public ApiResult<String> updateDetail(@RequestBody ApiParam<String> param) {

        log.debug("/railtrans/putDetail: {}", param);
        try {

            RailtransOrderDetail record = JSON.parseObject(param.getData(), RailtransOrderDetail.class);
            railtransOrderDetailService.update(record);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("ERROR(putDetail): {} {}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/getListByCarVin", method = RequestMethod.POST)
    @Auth(ClientType.WEB)
    public ApiResult<List<RailtransOrderDetail>> getListByCarVin(@RequestBody ApiParam<List<String>> param) {

        log.debug("/railtrans/putDetail: {}", param);
        try {

            List records;
            if (param.getData().size() > 0) {

                records = railtransOrderDetailService.getByCarVin(param.getData());
            } else {
                return ApiResult.failure("9999", "请填写车架号");
            }

            return ApiResult.success(records);
        } catch (Exception e) {
            log.error("ERROR(getDetailListByCarVin): {} {}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

    @RequestMapping(value = "/getListByCabinNumber", method = RequestMethod.POST)
    @Auth(ClientType.WEB)
    public ApiResult<List<RailtransOrderDetail>> getListByCabinNumber(@RequestBody ApiParam<List<String>> param) {

        log.debug("/railtrans/putDetail: {}", param);
        try {

            List records;
            if (param.getData().size() > 0) {

                records = railtransOrderDetailService.getByCabinNumber(param.getData());
            } else {
                return ApiResult.failure("9999", "请填写车厢号");
            }

            return ApiResult.success(records);
        } catch (Exception e) {
            log.error("ERROR(getListByCabinNumber): {} {}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }
    /* 铁路运单子表方法 */

    // Import: 导入TXT数据
    @RequestMapping(value = "/importText", method = RequestMethod.POST)
    @Auth(ClientType.WEB)
    public ApiResult<String> importText(@RequestParam("file") MultipartFile file) {

        File text;
        try {
            text = File.createTempFile("TEXT - ", ".txt");
            FileUtils.writeByteArrayToFile(text, file.getBytes());
            String result = manager.importText(text);
            if(!StringUtils.isBlank(result)){
                result = "车架号："+result+"不存在。";
            }
            return ApiResult.success(null,"0000",result);
        } catch (Exception e) {
            return ApiResult.failure("9999", "TXT文件格式有误");
        }
    }

    // Import: 导入EXCEL数据
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    @Auth(ClientType.WEB)
    public ApiResult<String> importExcel(@RequestParam("file") MultipartFile file) {

        log.info(String.format("File: %s", file.getOriginalFilename()));

        File excel;
        try {
            excel = File.createTempFile("EXCEL - ", ".xls");
            FileUtils.writeByteArrayToFile(excel, file.getBytes());
            manager.importExcel(excel);

            return ApiResult.success();
        } catch (IOException e) {
            return ApiResult.failure("9999", "EXCEL文件格式有误");
        }
    }

    // Export: 导出数据至EXCEL文件
    @RequestMapping(value = "/exportExcel", method = RequestMethod.POST)
    @Auth(ClientType.WEB)
    public ApiResult<String> exportExcel() {

        railtransOrderDetailService.selectDistinctCabinNumber().forEach(map -> System.out.println(map));

        return ApiResult.failure("9999", "fail");
    }

    // 获取指定车厢号当前最新状态
    @RequestMapping(value = "/updateCabinStatus", method = RequestMethod.POST)
    @Auth(ClientType.WEB)
    public ApiResult<String> updateCabinStatus(@RequestBody ApiParam<Map<String, String>> params) {
        try {
            manager.getSpecifiedCabinStatus(params.getData());
            return ApiResult.success();
        }catch(Exception e){
            log.error("更新在途信息失败！",e);
            return ApiResult.failure(e);
        }
    }

	// 获取指定车厢号当前最新状态
	@RequestMapping(value = "/updateByCabinNumber", method = RequestMethod.POST)
	@Auth(ClientType.WEB)
	public ApiResult<String> updateByCabinNumber(@RequestBody ApiParam<Map<String, String>> params) {
		try {
			manager.getSpecifiedCabinStatus(params.getData());
			return ApiResult.success();
		} catch (Exception e) {
			log.error("更新在途信息失败！", e);
			return ApiResult.failure(e);
		}
	}

    @RequestMapping(value = "/getInfoFromApi", method = RequestMethod.POST)
    public ApiResult<String> getInfoFromApi(@RequestBody ApiParam<String> params) {
        try {
            if(StringUtils.isBlank(params.getData())){
                throw new BusinessException("9003");
            }
            String html = manager.getHtmlFromCrscsc(params.getData());
            return ApiResult.success(html);
        }catch(Exception e){
            log.error("获取在途信息失败！",e);
            return ApiResult.failure(e);
        }
    }

    // 获取全部车厢号当前最新状态
    @RequestMapping(value = "/updateAllCabinStatus", method = RequestMethod.POST)
    @Auth(ClientType.WEB)
    public ApiResult<String> updateAllCabinStatus() {
        try {
	        manager.getLatestCabinStatus();
            return ApiResult.success();
        }catch(Exception e){
            log.error("更新全部在途信息失败！",e);
            return ApiResult.failure(e);
        }
    }
}