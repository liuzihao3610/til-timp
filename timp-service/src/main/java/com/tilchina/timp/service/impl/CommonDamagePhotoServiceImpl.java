package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.CommonDamagePhotoMapper;
import com.tilchina.timp.model.CommonDamagePhoto;
import com.tilchina.timp.service.CommonDamagePhotoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
* 质损照片
*
* @version 1.0.0
* @author XueYuSong
*/
@Service
@Slf4j
public class CommonDamagePhotoServiceImpl extends BaseServiceImpl<CommonDamagePhoto> implements CommonDamagePhotoService {

	@Autowired
    private CommonDamagePhotoMapper damagePhotoMapper;
	
	@Override
	protected BaseMapper<CommonDamagePhoto> getMapper() {
		return damagePhotoMapper;
	}

	@Override
	protected StringBuilder checkNewRecord(CommonDamagePhoto commondamagephoto) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkInteger("NO", "photoType", "照片类型", commondamagephoto.getPhotoType(), 10));
        s.append(CheckUtils.checkString("NO", "photoPath", "照片路径", commondamagephoto.getPhotoPath(), 200));
        s.append(CheckUtils.checkString("YES", "photoDesc", "照片描述", commondamagephoto.getPhotoDesc(), 200));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(CommonDamagePhoto commondamagephoto) {
        StringBuilder s = checkNewRecord(commondamagephoto);
        s.append(CheckUtils.checkPrimaryKey(commondamagephoto.getDamagePhotoId()));
		return s;
	}

	@Override
	public List<Long> selectPhotoIdByDamageId(Long damageId) {

		return damagePhotoMapper.selectDamagePhotoIdByDamageId(damageId);
	}

	@Override
	public List<CommonDamagePhoto> selectPhotosByDamageId(Long damageId) {

		return convertImagePath(damagePhotoMapper.selectDamagePhotoByDamageId(damageId));
	}

	@Override
	public List<CommonDamagePhoto> selectPhotosByDetailId(Long damageDetailId) {

		return convertImagePath(damagePhotoMapper.selectDamagePhotoByDamageDetailId(damageDetailId));
	}

	@Override
	public List<Long> selectPhotoIdByDamageDetailId(Long damageDetailId) {

		return damagePhotoMapper.selectDamagePhotoIdByDamageDetailId(damageDetailId);
	}

	@Override
	public CommonDamagePhoto queryById(Long id) {
		return super.queryById(id);
	}

	@Override
	public PageInfo<CommonDamagePhoto> queryList(Map<String, Object> map, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		return new PageInfo(convertImagePath(damagePhotoMapper.selectList(map)));
	}

	@Override
	public List<CommonDamagePhoto> queryList(Map<String, Object> map) {
		return convertImagePath(super.queryList(map));
	}

	@Override
	public void add(CommonDamagePhoto record) {
		super.add(record);
	}

	@Override
	public void add(List<CommonDamagePhoto> records) {

		StringBuilder sb;
		try {
			for (CommonDamagePhoto record : records) {
				sb = checkNewRecord(record);
				if (!StringUtils.isBlank(sb)) {
					throw new RuntimeException("数据检查失败：" + sb.toString());
				}
			}
			damagePhotoMapper.insert(records);
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

	@Override
	public void updateSelective(CommonDamagePhoto record) {
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(CheckUtils.checkLong("NO", "damageDetailId", "质损照片ID", record.getDamagePhotoId(), 20));
			if (!StringUtils.isBlank(sb)) {
				throw new RuntimeException("数据检查失败：" + sb.toString());
			}
			damagePhotoMapper.updateByPrimaryKeySelective(record);
		} catch (Exception e) {
			log.error("{}", e);
			if (e.getMessage().indexOf("IDX_") != -1) {
				throw new BusinessException("数据重复，请查证后重新提交！",e);
			} else if(e instanceof BusinessException) {
				throw e;
			}else {
				throw new RuntimeException("操作失败", e);
			}
		}
	}

	@Override
	public void update(CommonDamagePhoto record) {
		super.update(record);
	}

	@Override
	public void update(List<CommonDamagePhoto> records) {
		super.update(records);
	}

	@Override
	public void deleteById(Long id) {
		super.deleteById(id);
	}

	private List<CommonDamagePhoto> convertImagePath(List<CommonDamagePhoto> photos) {

		for (CommonDamagePhoto photo : photos) {

			String photoPath = photo.getPhotoPath();
			photoPath = String.format("http://10.8.12.123/%s", photoPath.substring(6, photoPath.length()).replace('\\', '/'));
			photo.setPhotoPath(photoPath);
		}

		return photos;
	}
}
