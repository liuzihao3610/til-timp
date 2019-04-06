package com.tilchina.timp.service.impl;

import com.tilchina.timp.model.User;
import com.tilchina.timp.util.DateUtil;
import com.tilchina.timp.util.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.model.TransporterLicense;
import com.tilchina.timp.service.TransporterLicenseService;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.TransporterLicenseMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
* 
*
* @version 1.1.0
* @author XiaHong
*/
@Service
@Slf4j
public class TransporterLicenseServiceImpl extends BaseServiceImpl<TransporterLicense> implements TransporterLicenseService {

	@Autowired
    private TransporterLicenseMapper transporterlicensemapper;
	
	@Override
	protected BaseMapper<TransporterLicense> getMapper() {
		return transporterlicensemapper;
	}

	@Override
	protected StringBuilder checkNewRecord(TransporterLicense transporterlicense) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("NO", "platrCode", "车牌号码", transporterlicense.getPlatrCode(), 10));
        s.append(CheckUtils.checkString("NO", "transporterType", "车牌类型", transporterlicense.getTransporterType(), 20));
        s.append(CheckUtils.checkString("YES", "address", "住址", transporterlicense.getAddress(), 20));
        s.append(CheckUtils.checkString("YES", "useType", "使用性质", transporterlicense.getUseType(), 20));
        s.append(CheckUtils.checkString("YES", "brandModer", "品牌型号", transporterlicense.getBrandModer(), 20));
        s.append(CheckUtils.checkString("NO", "vin", "车辆识别代号VIN", transporterlicense.getVin(), 20));
        s.append(CheckUtils.checkString("NO", "engineCode", "发动机号码", transporterlicense.getEngineCode(), 20));
        s.append(CheckUtils.checkDate("NO", "registDate", "注册日期", transporterlicense.getRegistDate()));
        s.append(CheckUtils.checkDate("YES", "issuedDate", "发证日期", transporterlicense.getIssuedDate()));
        s.append(CheckUtils.checkDate("YES", "validUntil", "有效期至", transporterlicense.getValidUntil()));
        s.append(CheckUtils.checkString("YES", "fileCode", "档案标号", transporterlicense.getFileCode(), 20));
        s.append(CheckUtils.checkInteger("YES", "loadNumber", "核定载人数", transporterlicense.getLoadNumber(), 10));
        s.append(CheckUtils.checkString("YES", "externalSize", "外廓尺寸(长×宽×高)单位:毫米", transporterlicense.getExternalSize(), 20));
        s.append(CheckUtils.checkString("YES", "remark", "备注", transporterlicense.getRemark(), 200));
        s.append(CheckUtils.checkString("YES", "imagePath", "照片路劲", transporterlicense.getImagePath(), 50));
        s.append(CheckUtils.checkInteger("NO", "billStatus", "状态:0=制单,1=审核", transporterlicense.getBillStatus(), 10));
        s.append(CheckUtils.checkDate("NO", "createDate", "创建时间", transporterlicense.getCreateDate()));
        s.append(CheckUtils.checkDate("YES", "checkDate", "审核时间", transporterlicense.getCheckDate()));
        s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", transporterlicense.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(TransporterLicense transporterlicense) {
        StringBuilder s = checkNewRecord(transporterlicense);
        s.append(CheckUtils.checkPrimaryKey(transporterlicense.getTransporterLicenseId()));
		return s;
	}
	
	@Override
	public void logicDeleteByIdList(int[] ids) {
		log.debug("logicDeleteById: {}",ids);
		StringBuilder s;
        try {
        	if(ids.length > 0) {
        		s = new StringBuilder();
	        	for (int id : ids) {
		    		s.append(CheckUtils.checkInteger("NO", "data", "行驶证ID", id, 20));
		   		    if (!StringUtils.isBlank(s)) {
		                throw new BusinessException("数据检查失败：" + s.toString());
		            }
	    		}
	        	transporterlicensemapper.logicDeleteByPrimaryKeyList(ids);
        	}else {
        		throw new BusinessException("9001","行驶证ID");
        	}
        	
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
	}

	@Override
	public void logicDeleteById(Long id) {
		log.debug("logicDeleteById: {}",id);
		StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "data", "行驶证ID", id, 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
			transporterlicensemapper.logicDeleteByPrimaryKey(id);
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
	}
	
	@Override
    public TransporterLicense queryById(Long id) {
        log.debug("query: {}",id);
        TransporterLicense transporterLicense = null;
        StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "data", "行驶证ID", id, 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
   		    transporterLicense = getMapper().selectByPrimaryKey(id); 
   		    if(transporterLicense == null) {
   		    	throw new BusinessException("9008","行驶证");
   		    }
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
        return transporterLicense;
    }

	@Override
	public PageInfo<TransporterLicense> getRefer(Map<String, Object> data, int pageNum, int pageSize) {
		log.debug("getRefer: {}, page: {},{}", data,pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<TransporterLicense> transporterLicenses = null;
		try {
			transporterLicenses = new PageInfo<TransporterLicense>(transporterlicensemapper.selectRefer(data));
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
		}
		return transporterLicenses;
	}

	@Override
	public void updateCheck(TransporterLicense updateTtransporterLicense) {
		log.debug("updateCheck:{}",updateTtransporterLicense);
		TransporterLicense transporterLicense = null;
		Environment env = Environment.getEnv();
		StringBuilder s;
		try {
			s = new StringBuilder();
			s.append(CheckUtils.checkLong("NO", "transporterLicenseId", "行驶证Id", updateTtransporterLicense.getTransporterLicenseId(), 20));
			if (!StringUtils.isBlank(s)) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}
			transporterLicense = queryById(updateTtransporterLicense.getTransporterLicenseId());
			if(transporterLicense.getBillStatus().intValue() == 1 && updateTtransporterLicense.getBillStatus().intValue() == transporterLicense.getBillStatus().intValue()) {
				throw new BusinessException("该行驶证档案已审核通过,请勿重复审核！");
			}else if(updateTtransporterLicense.getBillStatus() > 1) {
				throw new BusinessException("传入状态有误，请联系管理员！");
			}else if(transporterLicense.getBillStatus().intValue() == 0 && updateTtransporterLicense.getBillStatus().intValue() == transporterLicense.getBillStatus().intValue()) {
				throw new BusinessException("该行驶证档案未通过审核,不需要取消审核！");
			}else if(updateTtransporterLicense.getBillStatus().intValue() == 1) {
				updateTtransporterLicense.setChecker(env.getUser().getUserId());
				updateTtransporterLicense.setCheckDate(new Date());
			}else if(updateTtransporterLicense.getBillStatus().intValue() == 0) {
				updateTtransporterLicense.setChecker(null);
				updateTtransporterLicense.setCheckDate(null);
			}
			transporterlicensemapper.updateCheck(updateTtransporterLicense);
		} catch (Exception e) {
		   if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
	}

	@Transactional
	@Override
	public void addHeadPhoto(MultipartFile file, Long transporterLicenseId) {
		log.debug("addHeadPhoto: {},{}", file,transporterLicenseId);
		try {
			TransporterLicense transporterLicense = queryById(transporterLicenseId);
			if (transporterLicense == null) {
				throw new BusinessException("行驶证档案不存在");
			}
			String photoPath = FileUtil.uploadImage(file,"transporterLicense");
			transporterLicense.setImagePath(photoPath);

			updateSelective(transporterLicense);
		} catch (Exception e) {
			throw new BusinessException(e);
		}

	}

	@Override
    public PageInfo<TransporterLicense> queryList(Map<String, Object> map, int pageNum, int pageSize) {
		log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
		PageHelper.startPage(pageNum, pageSize);
		PageInfo<TransporterLicense> transporterLicenses = null;
		try {
			Map<String, Object> data = Optional.ofNullable(map).orElse(new HashMap<>());
			if(data.get("endTime") != null && !"".equals(data.get("endTime"))){
				data.put("endTime",DateUtil.dateToString(DateUtil.endTime(data.get("endTime").toString())));
			}
			transporterLicenses = new PageInfo<TransporterLicense>(getMapper().selectList(data));
		} catch (Exception e) {
			throw new RuntimeException("操作失败！", e);
		}
		return transporterLicenses;
    }
    
    @Override
    public List<TransporterLicense> queryList(Map<String, Object> map) {
        log.debug("queryList: {}", map);
        return getMapper().selectList(map);
    }
	
}
