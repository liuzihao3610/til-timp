package com.tilchina.timp.service;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.ContactsOld;
import com.tilchina.timp.vo.ContactsVO;

/**
* 收发货单位联系人
*
* @version 1.1.0
* @author XiaHong
*/
public interface ContactsOldService extends BaseService<ContactsOld> {

	void deleteList(int[] data);

	void adds(List<ContactsOld> data);
	
	List<ContactsOld> queryByUnitId(Long unitId);

	/**
	 * app运单:收发货联系人信息
	 * @param startUnitId
	 * @param EndUnitId
	 * @return
	 */
	ContactsVO appQueryByUnitId(Long startUnitId,Long EndUnitId);

	/**
	 * app运单:发货联系人信息
	 * @param startUnitId
	 * @return
	 */
	List<ContactsVO> queryByStartUnitId(Long startUnitId);

	/**
	 * app运单:收货联系人信息
	 * @param endUnitId
	 * @return
	 */
	List<ContactsVO> queryByEndUnitId(Long endUnitId);

	/**
	 * 按创建时间倒序排列
	 * @param data
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	PageInfo<ContactsOld> getList(Map<String, Object> data, int pageNum, int pageSize);

	/**
	 * 通过收发货单位联系人名称查询
	 * @param refUserName
	 * @return
	 */
    ContactsOld queryByName(String refUserName);
}
