package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.Transporter;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
* 轿运车档案
*
* @version 1.0.0
* @author Xiahong
*/
@Repository
public interface TransporterMapper extends BaseMapper<Transporter> {
	
	int logicDeleteByPrimaryKey(Long id);

	int logicDeleteByPrimaryKeyList(int[] ids);

	Transporter selectByDriverId(@Param("driverId") Long driverId);

	Transporter selectByContractorId(@Param("contractorId") Long contractorId);
	
	List<Transporter> selectRefer(Map<String, Object> map);
	
	/**
	 * 审核
	 * @param transporter
	 */
	void updateCheck(Transporter transporter);

    void deleteByIdList(int[] ids);

    void updateContractorById(Map<String, Object> params);
}
