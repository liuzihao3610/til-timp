package com.tilchina.timp.controller;

import com.tilchina.auth.Auth;
import com.tilchina.catalyst.param.ApiParam;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.manager.AutoAssemblyManager;
import com.tilchina.timp.model.TransportOrder;
import com.tilchina.timp.service.FreightService;
import com.tilchina.timp.service.TransportOrderService;
import com.tilchina.timp.vo.AssemblyParam;
import com.tilchina.timp.vo.TransporterCountVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by demon on 2018/6/5.
 */
@RestController
@RequestMapping(value = {"/s1/ai"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class AutoController {

    @Autowired
    private AutoAssemblyManager autoAssemblyManager;

    @RequestMapping(value = "/auto", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> auto(@RequestBody ApiParam<AssemblyParam> param) {
        log.debug("get: {}", param);
        try {
            AssemblyParam p = param.getData();
            p.setTransporterType(1);
            List<TransporterCountVO> counts = new ArrayList<>();

            if(p.getTrail1() != null && p.getTrail1().intValue() != 0) {
                TransporterCountVO v1 = new TransporterCountVO();
                v1.setTrailerId(103L);
                v1.setCount(p.getTrail1());
                counts.add(v1);
            }

            if(p.getTrail2() != null && p.getTrail2().intValue() != 0) {
                TransporterCountVO v2 = new TransporterCountVO();
                v2.setTrailerId(102L);
                v2.setCount(p.getTrail2());
                counts.add(v2);
            }

            if(CollectionUtils.isEmpty(counts)){
                throw new BusinessException("请输入板车数量！");
            }
            p.setCounts(counts);
            p.setSendCityId(37L);

            autoAssemblyManager.assembly(p);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("查询失败，param={}", param, e);
            return ApiResult.failure("9999", e.getMessage());
        }
    }

}
