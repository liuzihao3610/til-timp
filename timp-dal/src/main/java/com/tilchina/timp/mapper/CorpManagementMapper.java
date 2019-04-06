package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.CorpManagement;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
* 公司管理表
*
* @version 1.0.0
* @author XueYuSong
*/
@Repository
public interface CorpManagementMapper extends BaseMapper<CorpManagement> {

	void physicalDelete(@Param("managementId") Long managementId);

    List<CorpManagement> selectByUserId(@Param("userId")Long userId);

    List<Long> getManagementCorpListByUserId(@Param("userId") Long userId);

	List<CorpManagement> getManagement(@Param("userId") Long userId);

	Long queryIdByManagementCorpId(Map<String, Object> params);
}
