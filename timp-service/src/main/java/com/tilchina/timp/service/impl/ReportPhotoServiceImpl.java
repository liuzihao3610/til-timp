package com.tilchina.timp.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.model.ReportOrderDetail;
import com.tilchina.timp.model.ReportPhoto;
import com.tilchina.timp.service.ReportPhotoService;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.ReportPhotoMapper;

/**
* 在途提报照片
*
* @version 1.0.0
* @author LiuShuqi
*/
@Service
@Slf4j
public class ReportPhotoServiceImpl extends BaseServiceImpl<ReportPhoto> implements ReportPhotoService {

	@Autowired
    private ReportPhotoMapper reportphotomapper;
	
	@Override
	protected BaseMapper<ReportPhoto> getMapper() {
		return reportphotomapper;
	}

	@Override
	protected StringBuilder checkNewRecord(ReportPhoto reportphoto) {
		StringBuilder s = new StringBuilder();
		s.append(CheckUtils.checkLong("NO", "reportId", "在途提报ID", reportphoto.getReportId(), 20));
        s.append(CheckUtils.checkInteger("NO", "photoType", "照片类型", reportphoto.getPhotoType(), 10));
        s.append(CheckUtils.checkString("NO", "photoPath", "照片路劲", reportphoto.getPhotoPath(), 200));
        s.append(CheckUtils.checkString("YES", "description", "照片描述", reportphoto.getDescription(), 200));
        s.append(CheckUtils.checkLong("YES", "corpId", "公司ID", reportphoto.getCorpId(), 20));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(ReportPhoto reportphoto) {
        StringBuilder s = checkNewRecord(reportphoto);
        s.append(CheckUtils.checkPrimaryKey(reportphoto.getReportPhotoId()));
		return s;
	}

	@Override
	public List<ReportPhoto> queryByReportIdList(Long reportId) {
        log.debug("queryByReportIdList: {}",reportId);
        List<ReportPhoto> reportPhotos;
        try {
        	reportPhotos = reportphotomapper.selectByReportIdList(reportId);
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
        return reportPhotos;
    }
}
