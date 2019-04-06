package com.tilchina.timp.mapper;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.tilchina.timp.model.WayReport;
import com.tilchina.framework.mapper.BaseMapper;

/**
* 在途提报
*
* @version 1.0.0
* @author LiuShuqi
*/
@Repository
public interface WayReportMapper extends BaseMapper<WayReport> {

	List<WayReport> getByDriverId(long driverId, Date start, Date end);

}
