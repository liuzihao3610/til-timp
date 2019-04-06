package com.tilchina.timp.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.WayReport;

/**
* 在途提报
*
* @version 1.0.0
* @author LiuShuqi
*/
public interface WayReportService extends BaseService<WayReport> {

	void submission(String json, MultipartFile[] files);

	List<WayReport> getByDriverId();

//	void submission(String location, double originLng, double originLat, double lng, double lat, MultipartFile[] files);

}
