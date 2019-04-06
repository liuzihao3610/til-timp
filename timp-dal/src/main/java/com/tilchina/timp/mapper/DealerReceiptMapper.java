package com.tilchina.timp.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.tilchina.timp.model.DealerReceipt;
import com.tilchina.framework.mapper.BaseMapper;

/**
* 电子签收单
*
* @version 1.1.0
* @author LiuShuqi
*/
@Repository
public interface DealerReceiptMapper extends BaseMapper<DealerReceipt> {

	void deleteList(int[] data);

	List<DealerReceipt> getList(Map<String, Object> map);

}
