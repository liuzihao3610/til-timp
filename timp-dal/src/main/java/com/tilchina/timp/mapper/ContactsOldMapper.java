package com.tilchina.timp.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.tilchina.timp.model.ContactsOld;
import com.tilchina.framework.mapper.BaseMapper;

/**
* 收发货单位联系人
*
* @version 1.1.0
* @author XiaHong
*/
@Repository
public interface ContactsOldMapper extends BaseMapper<ContactsOld> {

	void deleteList(int[] data);

	List<ContactsOld> queryByUnitId(Long unitId);

	List<ContactsOld> getList(Map<String, Object> data);

    ContactsOld queryByName(String refUserName);
}
