package com.tilchina.timp.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.tilchina.timp.model.ReportPhoto;
import com.tilchina.framework.mapper.BaseMapper;

/**
* 在途提报照片
*
* @version 1.0.0
* @author LiuShuqi
*/
@Repository
public interface ReportPhotoMapper extends BaseMapper<ReportPhoto> {

	List<ReportPhoto> selectByReportIdList(Long reportId);

}
