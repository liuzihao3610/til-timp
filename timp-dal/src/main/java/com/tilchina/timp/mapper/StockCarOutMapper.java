package com.tilchina.timp.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.tilchina.timp.model.StockCarOut;
import com.tilchina.framework.mapper.BaseMapper;

import java.util.List;

/**
* 商品车出库记录表
*
* @version 1.0.0
* @author Xiahong
*/
@Repository
public interface StockCarOutMapper extends BaseMapper<StockCarOut> {

    List<StockCarOut> selectByStockIds(@Param("stockIds") List<Long> stockIds);

    void updateByPrimaryKeySelective(List<StockCarOut> records);
}
