package com.tilchina.timp.mapper;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;
import com.tilchina.timp.model.TransporterLicense;
import com.tilchina.framework.mapper.BaseMapper;

/**
* 
*
* @version 1.1.0
* @author XiaHong
*/
@Repository
public interface TransporterLicenseMapper extends BaseMapper<TransporterLicense> {
	
	int logicDeleteByPrimaryKey(Long id);
	 
	int logicDeleteByPrimaryKeyList(int[] ids);
	
	List<TransporterLicense> selectRefer(Map<String, Object> map);
	
	/**
	 * 审核
	 * @param updateTtransporterLicense
	 */
	void updateCheck(TransporterLicense updateTtransporterLicense);
	
}
