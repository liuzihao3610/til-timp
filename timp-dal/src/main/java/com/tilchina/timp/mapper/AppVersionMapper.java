package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.AppVersion;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
* APP版本升级表
*
* @version 1.0.0
* @author XueYuSong
*/
@Repository
public interface AppVersionMapper extends BaseMapper<AppVersion> {

	AppVersion queryLatestVersion(@Param("appType") Integer appType);
}
