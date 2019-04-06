package com.tilchina.timp.service;

import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.Corp;

import java.util.List;
import java.util.Map;

/**
* 公司档案
*
* @version 1.1.0
* @author LiuShuqi
*/
public interface CorpService extends BaseService<Corp> {


	void deleteList(int[] data);

	PageInfo<Corp> getReferenceList(Map<String, Object> map, int pageNum,int pageSize);

	List<Corp> getTransCorp();

	Corp getByCorpName(String shipToPartyName);
	/**
	 * 按创建时间倒序排列
	 * @param data
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	PageInfo<Corp> getList(Map<String, Object> data, int pageNum, int pageSize);

	// 通过公司ID查询当前公司的上级公司
	List<Long> queryHigherCorpByCorpId(Long corpId);

	List<Long> queryByName(String corpName);

	// 通过公司ID获取其关联公司列表
	List<Corp> getRelation(Map<String, Object> params);

	// 通过公司ID获取其可管理公司列表
	List<Corp> getManagement(Long userId);

	List<Corp> queryByNames(List<String> customerCorpNames);

    List<Corp> queryByIds(List<Long> corpIds);

    Long queryIdByName(String corpName);
}
