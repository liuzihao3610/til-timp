package com.tilchina.timp.mapper;

import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import com.tilchina.timp.model.User;
import com.tilchina.framework.mapper.BaseMapper;

/**
* 用户档案
*	
* @version 1.1.0
* @author XiaHong
*/
@Repository
public interface UserMapper extends BaseMapper<User> {

	int logicDeleteByPrimaryKey(Long id);

	User selectByPhone(@Param("phone") String phone, @Param("userType") Integer userType);

    int logicDeleteByPrimaryKeyList(int[] ids);

	void deleteList(int[] data);
	
	List<User> selectByCorpId(Map<String, Object> map);

	List<User> getDriverList(Map<String, Object> data);

	User getByDriverId(Long driverId);

	void updateBillStatus(Long driverId);
	
	List<User> selectRefer(Map<String, Object> map);
	
	/**
	 * 审核
	 * @param user
	 */
	void updateCheck(User user);
	
	User selectByDelPhone(@Param("phone")String phone,  @Param("userType") Integer userType);

	List<User> getReferenceList(Map<String, Object> map);


}
