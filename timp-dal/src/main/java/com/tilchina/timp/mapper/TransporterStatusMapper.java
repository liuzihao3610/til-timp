package com.tilchina.timp.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.tilchina.timp.model.TransporterStatus;
import com.tilchina.framework.mapper.BaseMapper;

/**
* 
*
* @version 1.1.0
* @author XiaHong
*/
@Repository
public interface TransporterStatusMapper extends BaseMapper<TransporterStatus> {
	
	int logicDeleteByPrimaryKey(Long id);
	 
	int logicDeleteByPrimaryKeyList(int[] ids);
	
	int updateById(TransporterStatus transporterStatus);
	
	TransporterStatus selectByTransporterId(@Param("transporterId") Long transporterId);

	List<TransporterStatus> selectRefer(Map<String, Object> data);
	 
}
