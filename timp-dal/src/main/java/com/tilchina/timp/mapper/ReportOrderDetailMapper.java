package com.tilchina.timp.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.tilchina.timp.model.ReportOrderDetail;
import com.tilchina.framework.mapper.BaseMapper;

/**
* 在途提报运单子表
*
* @version 1.0.0
* @author Xiahong
*/
@Repository
public interface ReportOrderDetailMapper extends BaseMapper<ReportOrderDetail> {

	List<ReportOrderDetail> selectByReportIdList(@Param("reportId")Long reportId);

	List<ReportOrderDetail> selectByCarVinList(@Param("carVin")String carVin);

}
