package com.tilchina.timp.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.tilchina.timp.model.DriverPhoto;
import com.tilchina.framework.mapper.BaseMapper;

/**
* 驾驶员照片档案
*
* @version 1.1.0
* @author XiaHong
*/
@Repository
public interface DriverPhotoMapper extends BaseMapper<DriverPhoto> {

	void deleteList(int[] data);

	void removeDate(Long driverPhotoId);

	void deleteByDriverIdList(int[] data);

	void deleteByDriverId(Long driverId);

	List<DriverPhoto> selectByDriverId(long l);

	List<DriverPhoto> getList(Map<String, Object> map);

}
