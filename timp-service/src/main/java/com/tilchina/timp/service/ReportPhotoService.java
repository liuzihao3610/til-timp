package com.tilchina.timp.service;

import java.util.List;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.ReportPhoto;

/**
* 在途提报照片
*
* @version 1.0.0
* @author LiuShuqi
*/
public interface ReportPhotoService extends BaseService<ReportPhoto> {
	
	
	List<ReportPhoto> queryByReportIdList(Long reportId);

}
