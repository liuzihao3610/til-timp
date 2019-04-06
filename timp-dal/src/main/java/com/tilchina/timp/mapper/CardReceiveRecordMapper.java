package com.tilchina.timp.mapper;

import org.springframework.stereotype.Repository;

import com.tilchina.timp.model.CardReceiveRecord;
import com.tilchina.framework.mapper.BaseMapper;

/**
* 卡领用记录
*
* @version 1.0.0
* @author LiushuQi
*/
@Repository
public interface CardReceiveRecordMapper extends BaseMapper<CardReceiveRecord> {

	CardReceiveRecord queryByCardId(Long cardResourceId);

}
