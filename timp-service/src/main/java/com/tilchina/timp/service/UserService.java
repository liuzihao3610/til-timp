package com.tilchina.timp.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.Login;
import com.tilchina.timp.model.User;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

/**
* 
*
* @version 1.1.0
* @author XiaHong
*/
public interface UserService extends BaseService<User> {

	void logicDeleteById(Long id) throws Exception;
	
	void logicDeleteByIdList(int[] ids);

	User queryByPhone(String phone);
	
	User queryByDelPhone(String phone);

	void updateLogin(Login login);

	void updateCheck(User user);

	PageInfo<User> queryByCorpId(Map<String, Object> map, int pageNum, int pageSize);
	
	void updateBlock(User user);
	
	JSONObject updatePasswords(User user);
	
	JSONObject addUser(User record);

	User getByDriverId(Long driverId);

	void updateBillStatus(Long driverId);
	
	PageInfo<User> getRefer(Map<String, Object> map, int pageNum, int pageSize);

	User getByPhone(String mobile);

	void addHeadAvatrar(MultipartFile file, Long userId);

    void addPhotoPath(MultipartFile file, Long userId);

    void importExcel(MultipartFile file) throws Exception;

    Workbook exportExcel() throws Exception;
}
