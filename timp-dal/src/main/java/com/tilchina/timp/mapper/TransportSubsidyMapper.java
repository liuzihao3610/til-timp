package com.tilchina.timp.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.tilchina.timp.model.TransportSubsidy;
import com.tilchina.framework.mapper.BaseMapper;

/**
* 运单结算子表
*
* @version 1.0.0
* @author Xiahong
*/
@Repository
public interface TransportSubsidyMapper extends BaseMapper<TransportSubsidy> {

	List<TransportSubsidy> selectBySettlementIdList(Long transportSettlementId);

	List<TransportSubsidy> selectByTransportOrderIdList(Long transportOrderId);

}
