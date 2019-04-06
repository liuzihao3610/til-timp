package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.TransporterAndDriver;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
* 
*
* @version 1.1.0
* @author LiuShuqi
*/
@Repository
public interface TransporterAndDriverMapper extends BaseMapper<TransporterAndDriver> {
	
	int logicDeleteByPrimaryKey(Long id);
	 
	int logicDeleteByPrimaryKeyList(int[] ids);
	
	TransporterAndDriver selectByKeyId(Map<String,Long> map);

	List<Map<String,Object>> selectIdleTransporter();
	
	/**
	 * 参照
	 * @param data
	 * @return
	 */
	List<TransporterAndDriver> selectRefer(Map<String, Object> data);
	
	/**
	 * 获取车辆信息
	 * @param data
	 * @return
	 */
	List<TransporterAndDriver> selectByAppDriverId(Map<String, Object> data);

	void setInvalidStatusByTransporterId(@Param("transporterId") Long transporterId);

	void setInvalidStatusById(@Param("transporterAndDriverId") Long transporterAndDriverId);
}
