package com.tilchina.timp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.tilchina.catalyst.utils.BCryptUtil;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.enums.*;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.UserMapper;
import com.tilchina.timp.model.*;
import com.tilchina.timp.service.*;
import com.tilchina.timp.util.CheckUtil;
import com.tilchina.timp.util.DateUtil;
import com.tilchina.timp.util.ExcelUtil;
import com.tilchina.timp.util.FileUtil;
import com.tilchina.timp.utils.GenPassword;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

/**
* 用户档案
*
* @version 1.1.0
* @author XiaHong
*/
@Service
@Slf4j
public class UserServiceImpl extends BaseServiceImpl<User> implements UserService {

	@Autowired
    private UserMapper usermapper;

	@Autowired
    private LoginService loginService;
	
	@Autowired
    private CorpService corpService;

	@Autowired
	private DeptService deptService;

	@Autowired
	private PositionService positionService;

	@Override
	protected BaseMapper<User> getMapper() {
		return usermapper;
	}

    @Override
    public User queryByPhone(String phone) {
        return usermapper.selectByPhone(phone,0);
    }
    
    @Override
    public User queryByDelPhone(String phone) {
        return usermapper.selectByDelPhone(phone,0);
    }
    
    
    @Override
    protected StringBuilder checkNewRecord(User user) {
        StringBuilder s = new StringBuilder();
        user.setUserCode(DateUtil.dateToStringCode(new Date()));
        user.setCreateDate(new Date());
        s.append(CheckUtils.checkString("NO", "userCode", "用户编码", user.getUserCode(), 20));
        s.append(CheckUtils.checkString("NO", "userName", "用户名称", user.getUserName(), 20));
        s.append(CheckUtils.checkString("YES", "userEnName", "英文名", user.getUserEnName(), 20));
		s.append(CheckUtils.checkString("YES", "identityCardNumber", "身份证号码", user.getIdentityCardNumber(), 20));
        s.append(CheckUtils.checkString("NO", "phone", "手机号", user.getPhone(), 40));
        s.append(CheckUtils.checkString("YES", "email", "电子邮箱", user.getEmail(), 50));
        s.append(CheckUtils.checkString("YES", "qq", "QQ", user.getQq(), 20));
        s.append(CheckUtils.checkString("YES", "wechat", "微信", user.getWechat(), 20));
        s.append(CheckUtils.checkInteger("NO", "sex", "性别(0", user.getSex(), 10));
        s.append(CheckUtils.checkDate("NO", "birthday", "出生年月日", user.getBirthday()));
        s.append(CheckUtils.checkString("YES", "nation", "民族", user.getNation(), 10));
        s.append(CheckUtils.checkString("YES", "education", "学历", user.getEducation(), 10));
        s.append(CheckUtils.checkString("YES", "remark", "备注", user.getRemark(), 200));
        s.append(CheckUtils.checkString("YES", "avatrar", "头像地址", user.getAvatrar(), 30));
        s.append(CheckUtils.checkString("YES", "photoPath", "照片地址", user.getPhotoPath(), 30));
        s.append(CheckUtils.checkInteger("NO", "bindingPhone", "已绑定手机号:0=已绑定,1=未绑定", user.getBindingPhone(), 10));
        s.append(CheckUtils.checkDate("NO", "createDate", "创建时间", user.getCreateDate()));
        s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", user.getFlag(), 10));
        return s;
    }

	@Override
	protected StringBuilder checkUpdate(User user) {
        StringBuilder s = checkNewRecord(user);
        s.append(CheckUtils.checkPrimaryKey(user.getUserId()));
		return s;
	}

	@Transactional
    @Override
    public JSONObject addUser(User record) {
        log.debug("add: {}",record);
        StringBuilder s;
        JSONObject json = null;
        Environment env = Environment.getEnv();
        User user = null;
        try {
        	
        	s = checkNewRecord(record);
            if (!StringUtils.isBlank(s)) {
                 throw new BusinessException("数据检查失败：" + s.toString());
            }
            user = queryByDelPhone(record.getPhone());
            if(user != null) {
            	deleteById(user.getUserId());
            }
	    	json = new JSONObject();
	      	//验证邮箱名称和手机号
	        if(!CheckUtil.checkMobileNumber(record.getPhone())) {
	           throw new BusinessException("9007","手机号");
	        }
	        if(StringUtils.isNotBlank(record.getEmail())) {
				if(!CheckUtil.checkEmail(record.getEmail().replaceAll(" ", ""))) {
					   throw new BusinessException("9007","邮箱号");
					 }
	        }
	        corpService.queryById(record.getCorpId());
	        record.setCreator(env.getUser().getUserId());
	        record.setUserType(0);
	        record.setCorpId(env.getCorp().getCorpId());
	        getMapper().insertSelective(record);
	        record.setDefPassword(GenPassword.get());
	        // 维护认证信息
	        Login login = null;//loginService.queryByUserId(record.getUserId());
	        if(login == null){
	            login = new Login();
	            login.setUserId(record.getUserId());
	            login.setCorpId(record.getCorpId());
	            login.setPassword(record.getDefPassword());
	            json.put("password",login.getPassword());
	            login.setPassword(BCryptUtil.hash(login.getPassword()));
	            
	            login.setClientType(0);
	            login.setBlock(0);
	            login.setFlag(0);
	            login.setErrorTimes(0);
	            loginService.add(login);
	        }
	        json.put("userCode",record.getUserCode());
	        json.put("userName",record.getUserName());
        } catch (Exception e) {
	        if (e.getMessage().indexOf("IDX_") != -1) {
	            throw new BusinessException("用户档案：手机号"+record.getPhone()+"数据重复，请查证后重新保存！", e);
	        } else if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
		return json;
    }
    


    @Override
    public void deleteById(Long id) {
        log.debug("delete: {}",id);
        StringBuilder s;
        Long loginId;
        try {
            s = new StringBuilder();
            s.append(CheckUtils.checkLong("NO", "userId", "用户Id",id, 20));
            if (!StringUtils.isBlank(s)) {
                 throw new BusinessException("数据检查失败：" + s.toString());
            }
            loginId = loginService.queryByUserId(id).getLoginId();
            getMapper().deleteByPrimaryKey(id);
            loginService.deleteById(loginId);
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
        
    }

    
	@Transactional
    @Override
    public void updateSelective(User record) {
        log.debug("update: {}",record);
        StringBuilder s;
        try {
            s = new StringBuilder();
            s.append(CheckUtils.checkLong("NO", "userId", "用户Id", record.getUserId(), 20));
            if (!StringUtils.isBlank(s)) {
                 throw new BusinessException("数据检查失败：" + s.toString());
            }
            //验证邮箱名称和手机号
            if(!CheckUtil.checkMobileNumber(record.getPhone())) {
          	   throw new BusinessException("9007","手机号");
             }
            if(StringUtils.isNotBlank(record.getEmail())) {
          	  if(!CheckUtil.checkEmail(record.getEmail().replaceAll(" ", ""))) {
                	   throw new BusinessException("9007","邮箱号");
                   }
              }
            
            //维护认证信息
            Login login = record.getRefLoginInfo();
            if(login != null){
                loginService.updateSelective(login);
            }
            queryById(record.getUserId());
            getMapper().updateByPrimaryKeySelective(record);
        } catch (Exception e) {
	        if (e.getMessage().indexOf("IDX_") != -1) {
				throw new BusinessException("用户档案：手机号"+record.getPhone()+"数据重复，请查证后重新保存！", e);
	        } else if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
    }
	
	@Transactional
	@Override
    public void update(List<User> records) {
        log.debug("updateBatch: {}",records);
        StringBuilder s;
        boolean checkResult = true;
        try {
        	s = new StringBuilder();
        	List<Login> logins = null;
            for (int i = 0; i < records.size(); i++) {
            	User record = records.get(i);
                //验证邮箱名称和手机号
                if(!CheckUtil.checkMobileNumber(record.getPhone())) {
              	   throw new BusinessException("9007","手机号");
                 }
                if(StringUtils.isNotBlank(record.getEmail())) {
              	  if(!CheckUtil.checkEmail(record.getEmail().replaceAll(" ", ""))) {
                    	   throw new BusinessException("9007","邮箱号");
                       }
                  }
                if(record.getRefLoginInfo() != null) {
                	logins.add(record.getRefLoginInfo());
                }
                StringBuilder check = checkUpdate(record);
                if(!StringUtils.isBlank(check)){
                    checkResult = false;
                    s.append("第"+(i+1)+"行，"+check+"/r/n");
                }
                queryById(record.getUserId());
            }
            if (!checkResult) {
                 throw new BusinessException("数据检查失败：" + s.toString());
            }
            //维护认证信息
            if(logins != null) {
            	loginService.update(logins);
            }
           
            getMapper().update(records);
        } catch (Exception e) {
	        if (e.getMessage().indexOf("IDX_") != -1) {
	            throw new BusinessException("数据重复，请查证后重新保存！", e);
	        } else if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }

    }

	/**
	 * 检查该数据是否有附属记录
	 * @param user
	 */
	private void Inspection(User user) {
		Boolean  flag = true;
		Map<String,Object> creatorMap = new HashMap<>();
		Map<String,Object> checkerMap = new HashMap<>();
		creatorMap.put("creator",user.getUserId());
		checkerMap.put("checker",user.getUserId());
		if (flag){
			List<User> users = Optional.ofNullable(queryList(creatorMap)).orElse(new ArrayList<>());
			if(users.size() > 0){
				flag = false;
				throw new BusinessException("姓名为："+user.getUserName()+"的用户档案再用户档案中创建人引用，不可删除,建议封存。");
			}
		}
		if (flag){
			List<User> users = Optional.ofNullable(queryList(checkerMap)).orElse(new ArrayList<>());
			if(users.size() > 0){
				flag = false;
				throw new BusinessException("姓名为："+user.getUserName()+"的用户档案再用户档案中审核人引用，不可删除,建议封存。");
			}
		}
	}

	@Transactional
	@Override
	public void logicDeleteById(Long id) throws Exception {
		log.debug("logicDeleteById: {}",id);
		StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "data", "用户ID", id, 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
			User user = queryById(id);
			//	删除一条记录时，应检查该数据是否有附属记录，若有则不可删除。
			Inspection(user);
			usermapper.logicDeleteByPrimaryKey(id);
		/*	loginService.logicDeleteById(id);*/
		} catch (Exception e) {
	        if (e.getMessage().indexOf("IDX_") != -1) {
	            throw new BusinessException("数据重复，请查证后重新保存！", e);
	        } else if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
	}
	
	
	
    @Override
	public void updateLogin(Login login) {
    	log.debug("updateLogin:{}",login);
    	try {
    		loginService.updateLogin(login);
    	} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
	}


	@Override
	public void updateCheck(User updateUser) {
		log.debug("updateCheck:{}",updateUser);
		 StringBuilder s ;
		 Environment env = Environment.getEnv();
		 User user = null;
		 try {
			 s = new StringBuilder();
			 s.append(CheckUtils.checkLong("NO", "userId", "用户Id", updateUser.getUserId(), 20));
			 if (!StringUtils.isBlank(s)) {
	              throw new BusinessException("数据检查失败：" + s.toString());
	         }
			 user = queryById(updateUser.getUserId());
			if(user.getBillStatus().intValue() == 1 && updateUser.getBillStatus().intValue() == user.getBillStatus().intValue()) {
				throw new BusinessException("该用户档案已审核通过,请勿重复审核！");
			}else if(updateUser.getBillStatus() > 1) {
				throw new BusinessException("传入状态有误，请联系管理员！");
			}else if(user.getBillStatus().intValue() == 0 && updateUser.getBillStatus().intValue() == user.getBillStatus().intValue()) {
				throw new BusinessException("该用户档案未通过审核,不需要取消审核！");
			}else if(updateUser.getBillStatus().intValue() == 1) {
				updateUser.setChecker(env.getUser().getUserId());
				updateUser.setCheckDate(new Date());
			}else if(updateUser.getBillStatus().intValue() == 0) {
				updateUser.setChecker(null);
				updateUser.setCheckDate(null);
			}
			usermapper.updateCheck(updateUser);
		} catch (Exception e) {
		 	log.error("{}", e);
	        if (e.getMessage().indexOf("IDX_") != -1) {
	            throw new BusinessException("数据重复，请查证后重新保存！", e);
	        } else if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
	}

	@Override
	public void logicDeleteByIdList(int[] ids) {
		StringBuilder s;
        try {
        	if(ids.length > 0) {
        		s = new StringBuilder();
	        	for (int id : ids) {
		    		s.append(CheckUtils.checkInteger("NO", "data", "用户ID", id, 20));
		   		    if (!StringUtils.isBlank(s)) {
		                throw new BusinessException("数据检查失败：" + s.toString());
		            }
	    		}
	        	usermapper.logicDeleteByPrimaryKeyList(ids);
        	}else {
        		throw new BusinessException("9001","用户ID");
        	}
        } catch (Exception e) {
	        if (e.getMessage().indexOf("IDX_") != -1) {
	            throw new BusinessException("数据重复，请查证后重新保存！", e);
	        } else if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
	}
	
	@Override
	public PageInfo<User> queryByCorpId(Map<String, Object> map, int pageNum, int pageSize) {
		 log.debug("queryByCorpId: {}, page: {},{}", map,pageNum,pageSize);
         PageHelper.startPage(pageNum, pageSize);
		 StringBuilder s;
		 PageInfo<User> pageInfo = null;
		try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkString("NO", "corpId", "公司ID", map.get("corpId").toString(), 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
   		    pageInfo = new PageInfo<User>(usermapper.selectByCorpId(map));
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
		return pageInfo;
	}
    
	@Override
	public void updateBlock(User user) {
		log.debug("updateBlock:{}",user);
		 StringBuilder s ;
		 Login login = null;
		 Login loginBak = null;
		 try {
			 s = new StringBuilder();
			 s.append(CheckUtils.checkLong("NO", "userId", "用户Id", user.getUserId(), 20));
			 if (!StringUtils.isBlank(s)) {
	              throw new BusinessException("数据检查失败：" + s.toString());
	         }
			 login = new Login();
			 queryById(user.getUserId());
			 loginBak = loginService.queryByUserId(user.getUserId());
			 login.setBlock(Block.UNLOCKED.getIndex());
			 login.setErrorTimes(ErrorTimes.INITIALIZE.getIndex());
			 login.setLoginId(loginBak.getLoginId());
			 loginService.updateSelective(login);
		} catch (Exception e) {
	        if (e.getMessage().indexOf("IDX_") != -1) {
	            throw new BusinessException("数据重复，请查证后重新保存！", e);
	        } else if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
	}
	
	
	@Override
	public JSONObject updatePasswords(User user) {
		log.debug("updatePasswords:{}",user);
		 StringBuilder s ;
		 Login login = null;
		 JSONObject json = null;
		 String password = null;
		 try {
			 s = new StringBuilder();
			 s.append(CheckUtils.checkLong("NO", "userId", "用户Id", user.getUserId(), 20));
			 if (!StringUtils.isBlank(s)) {
	              throw new BusinessException("数据检查失败：" + s.toString());
	         }
			 user = queryById(user.getUserId());
			 login = new Login();
			 json = new JSONObject();
			 login.setLoginId(loginService.queryByUserId(user.getUserId()).getLoginId());
			 password = GenPassword.get();
			 login.setPassword(BCryptUtil.hash(password));
			 login.setErrorTimes(ErrorTimes.INITIALIZE.getIndex());
			 login.setBlock(Block.UNLOCKED.getIndex());
			 loginService.updateSelective(login);
			 json.put("userCode",user.getUserCode());
	         json.put("userName",user.getUserName());
	         json.put("password",password);
			 
		} catch (Exception e) {
	        if (e.getMessage().indexOf("IDX_") != -1) {
	            throw new BusinessException("数据重复，请查证后重新保存！", e);
	        } else if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
		return json;
	}
	
	@Override
    public User queryById(Long id) {
        log.debug("query: {}",id);
        User user = null;
        StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "data", "用户ID", id, 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
        	user = getMapper().selectByPrimaryKey(id);
        	if(user == null) {
        		throw new BusinessException("9008","该用户");
        	}
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
        return user;
    }
	
    @Override
    public List<User> queryList(Map<String, Object> map) {
        log.debug("queryList: {}", map);
        List<User> users = null;
        try {
        	if(map == null) {
        		map = new HashMap<>();
        	}
        	map.put("userType", "0");
        	users = getMapper().selectList(map);
		} catch (Exception e) {
				throw new RuntimeException("操作失败！", e);
	    }
        return users;
    }
    
	@Override
    public PageInfo<User> queryList(Map<String, Object> map, int pageNum, int pageSize) {
        log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
        PageInfo<User> pageInfo = null;
        PageHelper.startPage(pageNum, pageSize);
		List<User> users = null;
        try {
        	if(map == null) {
        		map = new HashMap<>();
        	}
        	map.put("userType", "0");
	        users = getMapper().selectList(map);
        	pageInfo = new PageInfo<>(users);
		} catch (Exception e) {
        	log.error("{}", e);
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
        return pageInfo;
    }

	@Override
	public User getByDriverId(Long driverId) {
		User user = null;
		 StringBuilder s;
	        try {
	        	s = new StringBuilder();
	        	s.append(CheckUtils.checkLong("NO", "data", "用户档案司机ID", driverId, 20));
	   		    if (!StringUtils.isBlank(s)) {
	                throw new BusinessException("数据检查失败：" + s.toString());
	            }
	        	user = usermapper.getByDriverId(driverId);
	        	if(user == null) {
	        		throw new BusinessException("9008","司机");
	        	}
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
		return user;
	}

	@Override
	public void updateBillStatus(Long driverId) {
		try {
			usermapper.updateBillStatus(driverId);
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
	}
    
	@Override
	public PageInfo<User> getRefer(Map<String, Object> data,int pageNum, int pageSize) {
		log.debug("getRefer: {}, page: {},{}", data,pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<User> users = null;
		try {
			data.put("userType", "0");
			users = new PageInfo<User>(usermapper.selectRefer(data));
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
		}
		return users;
	}

	@Override
	public User getByPhone(String mobile) {
		try {
			return usermapper.selectByPhone(mobile,1);
		} catch (Exception e) {
			throw e;
		}
		
	}

	@Transactional
	@Override
	public void addHeadAvatrar(MultipartFile file, Long userId) {
		log.debug("addHeadAvatrar: {},{}", file,userId);
		try {
			User user = queryById(userId);
			if (user == null) {
				throw new BusinessException("用户不存在");
			}
			String photoPath = FileUtil.uploadImage(file,"user");
			user.setAvatrar(photoPath);
			updateSelective(user);
		} catch (Exception e) {
			throw new BusinessException(e);
		}

	}

	@Transactional
	@Override
	public void addPhotoPath(MultipartFile file, Long userId) {
		log.debug("addPhotoPath: {},{}", file,userId);
		try {
			User user = queryById(userId);
			if (user == null) {
				throw new BusinessException("用户不存在");
			}
			String photoPath = FileUtil.uploadImage(file,"user");
			user.setPhotoPath(photoPath);
			updateSelective(user);
		} catch (Exception e) {
			throw new BusinessException(e);
		}

	}

	@Override
	public void importExcel(MultipartFile file) throws Exception {
		String filePath;
		try {
			filePath = FileUtil.uploadExcel(file);
			Workbook workbook = ExcelUtil.getWorkbook(filePath);
			Map<String,Boolean> map = new HashMap<>();
			map.put("用户名称",false);
			map.put("手机号",false);
			map.put("出生年月日",false);
			String massage = ExcelUtil.validateWorkbook(workbook, map);
			if (!StringUtils.isBlank(massage)) {
				throw new BusinessException("数据检查失败：" + massage);
			}
			List<User> importUsers = ExcelUtil.getModelsFromWorkbook(workbook, User.class);
			StringBuilder errorMsg = new StringBuilder();
			List<String> deptNames = new ArrayList<String>();
			List<String> positionNames = new ArrayList<String>();
			// 校验
			importUsers.forEach(importUser ->{
				// 正则校验
				if (!CheckUtil.checkMobileNumber(importUser.getPhone())){
					errorMsg.append("用户名称："+importUser.getUserName()+"的手机号："+importUser.getPhone()+"检验不通过。");
				}
				if (importUser.getEmail() != null && !StringUtil.isEmpty(importUser.getEmail())){
					if (!CheckUtil.checkEmail(importUser.getPhone())){
						errorMsg.append("用户名称："+importUser.getUserName()+"的电子邮箱："+importUser.getEmail()+"检验不通过。");
					}
				}
				if (importUser.getIdentityCardNumber() != null && !StringUtil.isEmpty(importUser.getIdentityCardNumber())){
					if (!CheckUtil.checkIdentityCardNumber(importUser.getIdentityCardNumber())){
						errorMsg.append("用户名称："+importUser.getUserName()+"的身份证号码："+importUser.getIdentityCardNumber()+"检验不通过。");
					}
				}
				if (importUser.getQq() != null && !StringUtil.isEmpty(importUser.getQq())){
					if (!CheckUtil.checkQq(importUser.getQq())){
						errorMsg.append("用户名称："+importUser.getUserName()+"的QQ号码："+importUser.getQq()+"检验不通过。");
					}
				}
                // 入职时间、离职时间
                if (importUser.getJoinDate() != null && importUser.getResignationDate() != null){
                    if (!DateUtil.compare(importUser.getJoinDate(),importUser.getResignationDate())){
                        throw  new BusinessException("用户名称："+importUser.getUserName()+"的入职时间："+DateUtil.dateToYTD(importUser.getJoinDate())+"不能小于离职时间："+DateUtil.dateToYTD(importUser.getResignationDate())+"。");
                    }
                }
				// 枚举赋值
				if (importUser.getSex() == null){
					importUser.setSex(Sex.WOMAN.getIndex());
				}
				// 部门、职务
				if (importUser.getRefDeptName() != null){
					deptNames.add(importUser.getRefDeptName());
				}
				if (importUser.getRefPositionName() != null){
					positionNames.add(importUser.getRefPositionName());
				}

			});

			if (StringUtils.isNotBlank(errorMsg)) {
				throw new BusinessException(errorMsg.toString());
			}

			// 得到导入部门、职务信息
			Map<String,Dept> deptMap = new HashMap<String,Dept>();
			Map<String,Position> positionMap = new HashMap<String,Position>();
			if (!CollectionUtils.isEmpty(deptNames)){
				List<String> deptCollect = deptNames.stream().distinct().collect(Collectors.toList());
				List<Dept> depts = deptService.queryByDeptNames(deptCollect);
				depts.forEach(dept -> deptMap.put(dept.getDeptName(),dept));
			}
			if (!CollectionUtils.isEmpty(positionNames)){
				List<String> positionCollect = positionNames.stream().distinct().collect(Collectors.toList());
				List<Position> positions = positionService.queryByPositionNames(positionCollect);
				positions.forEach(position -> positionMap.put(position.getPositionName(),position));
			}

			importUsers.forEach(importUser -> {
				Environment environment = Environment.getEnv();
				importUser.setUserType(UserType.USER.getIndex());
				importUser.setDriverType(DriverType.NOT_SPECIFIED.getIndex());
				importUser.setAdmin(Admin.USER.getIndex());
				importUser.setSuperManager(SuperManager.USER.getIndex());
				importUser.setFlag(FlagStatus.No.getIndex());
				importUser.setBindingPhone(0);
				importUser.setBillStatus(BillStatus.Drafted.getIndex());

				importUser.setCreator(environment.getUser().getUserId());
				importUser.setCorpId(environment.getCorp().getCorpId());
				// 部门id、职务id
				Dept dept = deptMap.get(importUser.getRefDeptName());
				Position position = positionMap.get(importUser.getRefPositionName());
				if (dept != null){
					importUser.setDeptId(dept.getDeptId());
				}
				if (position != null){
					importUser.setPositionId(position.getPositionId());
				}
				addUser(importUser);
			});

		} catch (Exception e) {
			log.error("{}", e);
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
	}

	@Override
	public Workbook exportExcel() throws Exception {
		try {
			Map<String,Object> map = new HashMap<>();
			map.put("userType","0");
			List<User> users = queryList(map);
			Workbook workbook = ExcelUtil.getWorkbookFromModels(users);
			return workbook;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

}
