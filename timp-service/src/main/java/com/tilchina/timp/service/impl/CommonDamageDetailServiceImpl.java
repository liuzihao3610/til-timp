package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.CommonDamageDetailMapper;
import com.tilchina.timp.model.CommonDamage;
import com.tilchina.timp.model.CommonDamageDetail;
import com.tilchina.timp.model.CommonDamagePhoto;
import com.tilchina.timp.service.CommonDamageDetailService;
import com.tilchina.timp.service.CommonDamagePhotoService;
import com.tilchina.timp.service.CommonDamageService;
import com.tilchina.timp.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
* 通用质损管理子表
*
* @version 1.0.0
* @author XueYuSong
*/
@Service
@Slf4j
public class CommonDamageDetailServiceImpl extends BaseServiceImpl<CommonDamageDetail> implements CommonDamageDetailService {

	@Autowired
	private CommonDamageService damageService;

	@Autowired
	private CommonDamagePhotoService damagePhotoService;

	@Autowired
    private CommonDamageDetailMapper damageDetailMapper;
	
	@Override
	protected BaseMapper<CommonDamageDetail> getMapper() {
		return damageDetailMapper;
	}

	@Override
	protected StringBuilder checkNewRecord(CommonDamageDetail commondamagedetail) {
		StringBuilder s = new StringBuilder();
		s.append(CheckUtils.checkInteger("YES", "damageDegree", "质损程度", commondamagedetail.getDamageDegree(), 10));
		s.append(CheckUtils.checkInteger("YES", "damageRegistration", "质损环节", commondamagedetail.getDamageRegistration(), 10));
		s.append(CheckUtils.checkString("YES", "damageCauseCn", "质损原因", commondamagedetail.getDamageCauseCn(), 200));
		s.append(CheckUtils.checkString("YES", "damageCauseEn", "质损原因", commondamagedetail.getDamageCauseEn(), 200));
		s.append(CheckUtils.checkDate("NO", "damageDate", "质损时间", commondamagedetail.getDamageDate()));
		s.append(CheckUtils.checkString("YES", "remark", "备注", commondamagedetail.getRemark(), 200));
		s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", commondamagedetail.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(CommonDamageDetail commondamagedetail) {
        StringBuilder s = checkNewRecord(commondamagedetail);
        s.append(CheckUtils.checkPrimaryKey(commondamagedetail.getDamageDetailId()));
		return s;
	}

	@Override
	public List<Long> selectDamageDetailIdByDamageId(Long damageId) {

		return damageDetailMapper.selectDamageDetailIdByDamageId(damageId);
	}

	@Override
	public List<CommonDamageDetail> selectDetailsByDamageId(Long damageId) {

		List<CommonDamageDetail> result = new ArrayList<>();
		List<CommonDamageDetail> damageDetails = damageDetailMapper.selectDetailsByDamageId(damageId);
		damageDetails.forEach(damageDetail -> result.add(queryById(damageDetail.getDamageDetailId())));
		return result;
	}

	@Override
	public CommonDamageDetail queryById(Long id) {

		try {
			CommonDamageDetail damageDetail = damageDetailMapper.selectByPrimaryKey(id);
			List<CommonDamagePhoto> damagePhotos = damagePhotoService.selectPhotosByDetailId(id);
			damageDetail.setDamagePhotos(damagePhotos);
			return damageDetail;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public PageInfo<CommonDamageDetail> queryList(Map<String, Object> map, int pageNum, int pageSize) {
		return super.queryList(map, pageNum, pageSize);
	}

	@Override
	public List<CommonDamageDetail> queryList(Map<String, Object> map) {
		return super.queryList(map);
	}

	@Transactional
	@Override
	public void add(CommonDamageDetail damageDetail) {

        StringBuilder sb;
		try {
			Environment env = Environment.getEnv();
			damageDetail.setCorpId(env.getUser().getCorpId());
			damageDetail.setFlag(0);
            sb = checkNewRecord(damageDetail);
            if (!StringUtils.isBlank(sb)) {
                throw new RuntimeException("数据检查失败：" + sb.toString());
            }
			damageDetailMapper.insertSelective(damageDetail);

			if (damageDetail.getDamagePhotos() != null) {
				damageDetail.getDamagePhotos().forEach(damagePhoto -> {
					damagePhoto.setDamageId(damageDetail.getDamageId());
					damagePhoto.setDamageDetailId(damageDetail.getDamageDetailId());
					damagePhoto.setCorpId(damageDetail.getCorpId());
				});
            damagePhotoService.add(damageDetail.getDamagePhotos());
			}
		} catch (Exception e) {
			log.error("{}", e);
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新提交！",e);
            } else if(e instanceof BusinessException){
                throw e;
            }else {
                throw new RuntimeException("操作失败", e);
            }
		}
	}

	@Transactional
	@Override
	public void add(List<CommonDamageDetail> records) {

		if (records == null || records.size() < 1) {
			throw new BusinessException("9005", "damageDetails");
		}

		try {
			damageDetailMapper.insert(records);
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}

		records.forEach(damageDetail -> {
			if (damageDetail.getDamagePhotos() != null) {
				damageDetail.getDamagePhotos().forEach(damagePhoto -> {
					damagePhoto.setDamageId(damageDetail.getDamageId());
					damagePhoto.setDamageDetailId(damageDetail.getDamageDetailId());
				});
				damagePhotoService.add(damageDetail.getDamagePhotos());
			}
		});
	}

	@Transactional
	@Override
	public void updateSelective(CommonDamageDetail damageDetail) {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append(CheckUtils.checkLong("NO", "damageDetailId", "质损明细ID", damageDetail.getDamageDetailId(), 20));
            if (!StringUtils.isBlank(sb)) {
                throw new RuntimeException("数据检查失败：" + sb.toString());
            }
            damageDetailMapper.updateByPrimaryKeySelective(damageDetail);
        } catch (Exception e) {
            log.error("{}", e);
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新提交！",e);
            } else if(e instanceof BusinessException){
                throw e;
            }else {
                throw new RuntimeException("操作失败", e);
            }
        }
	}

	@Transactional
	@Override
	public void deleteById(Long id) {

        StringBuilder sb = new StringBuilder();
        try {
            sb.append(CheckUtils.checkLong("NO", "damageDetailId", "质损明细ID", id, 20));
            if (!StringUtils.isBlank(sb)) {
                throw new RuntimeException("数据检查失败：" + sb.toString());
            }
            damageDetailMapper.deleteByPrimaryKey(id);

            List<Long> damagePhotoIds = damagePhotoService.selectPhotoIdByDamageDetailId(id);

            if (damagePhotoIds != null && damagePhotoIds.size() > 0) {
                damagePhotoIds.forEach(damagePhotoId -> damagePhotoService.deleteById(damagePhotoId));
            }
        } catch (Exception e) {
            log.error("{}", e);
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新提交！",e);
            } else if(e instanceof BusinessException){
                throw e;
            }else {
                throw new RuntimeException("操作失败", e);
            }
        }
	}

	@Transactional
	@Override
	public void addDamageDetailWithPhotos(
			CommonDamageDetail damageDetail,
			MultipartFile[] files,
			Integer[] photoType,
			String[] photoDesc) {

		Environment env = Environment.getEnv();
		CommonDamage damage = damageService.queryById(damageDetail.getDamageId());

		if (damage == null || damageDetail == null || files.length < 1) {
			log.error("damage:{} files:{} env:{}", damageDetail, files.length, env);
			throw new BusinessException("9005", "damage or damageDetail or files");
		}

        try {
            List<CommonDamagePhoto> damagePhotos = new ArrayList<>();
            for (int i = 0; i < files.length; i++) {
                CommonDamagePhoto damagePhoto = new CommonDamagePhoto();
                try {
                    if (files[i].getSize() > 0) {
                        damagePhoto.setFlag(0);
                        damagePhoto.setPhotoPath(FileUtil.uploadFile(files[i], damage.getCarVin()));
	                    if (photoType != null && photoType[i] != null) damagePhoto.setPhotoType(photoType[i]);
	                    if (photoDesc != null && photoDesc[i] != null) damagePhoto.setPhotoDesc(photoDesc[i]);
                        damagePhotos.add(damagePhoto);
                    }
                } catch (Exception e) {
                    log.error("{}", e);
                }
            }

            damageDetail.setDamagePhotos(damagePhotos);
            add(damageDetail);
        } catch (Exception e) {
            log.error("{}", e);
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新提交！",e);
            } else if(e instanceof BusinessException){
                throw e;
            }else {
                throw new RuntimeException("操作失败", e);
            }
        }
    }

	@Transactional
	@Override
	public void uploadDamageDetailPhotos(
			Long damageId,
			Long damageDetailId,
			MultipartFile[] files,
			Integer[] photoType,
			String[] photoDesc) {

		CommonDamage damage;
		CommonDamageDetail damageDetail;
		Environment environment = Environment.getEnv();
		try {
			damage = damageService.queryById(damageId);
			damageDetail = damageDetailMapper.selectByPrimaryKey(damageDetailId);

            List<CommonDamagePhoto> damagePhotos = new ArrayList<>();
            for (int i = 0; i < files.length; i++) {
                CommonDamagePhoto damagePhoto = new CommonDamagePhoto();
                try {
                    if (files[i].getSize() > 0) {
                        damagePhoto.setDamageId(damage.getDamageId());
                        damagePhoto.setDamageDetailId(damageDetail.getDamageDetailId());
                        damagePhoto.setFlag(0);
                        damagePhoto.setCorpId(environment.getUser().getCorpId());
                        damagePhoto.setPhotoPath(FileUtil.uploadFile(files[i], damage.getCarVin()));
	                    if (photoType != null && photoType[i] != null) damagePhoto.setPhotoType(photoType[i]);
	                    if (photoDesc != null && photoDesc[i] != null) damagePhoto.setPhotoDesc(photoDesc[i]);
                    }
                } catch (Exception e) {
                    log.error("{}", e);
                }
                damagePhotos.add(damagePhoto);
            }

            damagePhotoService.add(damagePhotos);
		} catch (Exception e) {
            log.error("{}", e);
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新提交！",e);
            } else if(e instanceof BusinessException){
                throw e;
            }else {
                throw new RuntimeException("操作失败", e);
            }
		}
	}
}
