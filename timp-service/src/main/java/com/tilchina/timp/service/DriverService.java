package com.tilchina.timp.service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.User;

import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

/**
* 
*
* @version 1.1.0
* @author XiaHong
*/
public interface DriverService extends BaseService<User> {

	void deleteList(int[] data);

	User queryByPhone(String phone);

	void updateBillStatus(Long driverId, String path);

	PageInfo<User> getDriverList(Map<String, Object> data, int pageNum, int pageSize);

	JSONObject updatePasswords(User data);

	void updateBlock(User data);
	
	JSONObject addDriver(User record);

	void audit(Long data);

	void unaudit(Long data);

	PageInfo<User> getReferenceList(Map<String, Object> map, int pageNum, int pageSize);

	void addHeadPhoto(MultipartFile file, Long driverId);


    void importExcel(MultipartFile file) throws Exception;

	Workbook exportExcel() throws Exception;
}
