package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.VehiclePhoto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
* 轿运车照片档案表
*
* @version 1.0.0
* @author XueYuSong
*/
@Repository
public interface VehiclePhotoMapper extends BaseMapper<VehiclePhoto> {

	List<VehiclePhoto> getPhotos(Map<String, Object> params);

	List<VehiclePhoto> getPhotosByVehicleId(@Param("vehicleId") Long vehicleId);
}
