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
import com.tilchina.timp.common.LanguageUtil;
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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
* 
*
* @version 1.1.0
* @author XiaHong
*/
@Service
@Slf4j
public class DriverServiceImpl extends BaseServiceImpl<User> implements DriverService {

	@Autowired
    private UserMapper usermapper;
	
	@Autowired
	private UserService userService;
	
	@Autowired
    private LoginService loginService;
	
	@Autowired
    private DriverPhotoService driverPhotoService;
	
	@Autowired
    private DriverLicenseService driverLicenseService;

	@Autowired
	private TransporterService transporterService;

	@Autowired
	private DeptService deptService;

	@Autowired
	private PositionService positionService;

	@Override
	protected BaseMapper<User> getMapper() {
		return usermapper;
	}


    @Override
    protected StringBuilder checkNewRecord(User user) {
        StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("YES", "userCode", "用户编码", user.getUserCode(), 20));
        s.append(CheckUtils.checkString("NO", "userName", "用户名称", user.getUserName(), 20));
        s.append(CheckUtils.checkString("YES", "userEnName", "英文名", user.getUserEnName(), 20));
		s.append(CheckUtils.checkString("NO", "identityCardNumber", "身份证号码", user.getIdentityCardNumber(), 20));
        s.append(CheckUtils.checkString("NO", "phone", "手机号", user.getPhone(), 40));
        s.append(CheckUtils.checkString("YES", "email", "电子邮箱", user.getEmail(), 50));
        s.append(CheckUtils.checkString("YES", "qq", "QQ", user.getQq(), 20));
        s.append(CheckUtils.checkString("YES", "wechat", "微信", user.getWechat(), 20));
        s.append(CheckUtils.checkInteger("NO", "sex", "性别(0", user.getSex(), 10));
        s.append(CheckUtils.checkDate("YES", "birthday", "出生年月日", user.getBirthday()));
        s.append(CheckUtils.checkString("YES", "nation", "民族", user.getNation(), 10));
        s.append(CheckUtils.checkString("YES", "education", "学历", user.getEducation(), 10));
        s.append(CheckUtils.checkString("YES", "remark", "备注", user.getRemark(), 200));
        s.append(CheckUtils.checkString("YES", "avatrar", "头像地址", user.getAvatrar(), 30));
        s.append(CheckUtils.checkString("YES", "photoPath", "照片地址", user.getPhotoPath(), 30));
        s.append(CheckUtils.checkInteger("YES", "bindingPhone", "已绑定手机号:0=已绑定,1=未绑定", user.getBindingPhone(), 10));
        s.append(CheckUtils.checkDate("YES", "createDate", "创建时间", user.getCreateDate()));
        s.append(CheckUtils.checkLong("NO", "corpId", "公司ID", user.getCorpId(), 20));
        s.append(CheckUtils.checkInteger("YES", "flag", "封存标志", user.getFlag(), 10));
		Pattern reg=Pattern.compile( "^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|" +
				"10|20|30|31)\\d{3}[0-9Xx]$");
		if (user.getIdentityCardNumber() != null){
			if (!reg.matcher(user.getIdentityCardNumber()).find()){
				s.append(String.format("身份证号码:%s格式错误",user.getIdentityCardNumber()));
			}
		}
        return s;
    }

	@Override
	protected StringBuilder checkUpdate(User user) {
        StringBuilder s = checkNewRecord(user);
        s.append(CheckUtils.checkPrimaryKey(user.getUserId()));
		return s;
	}
	
	@Override
	@Transactional
    public JSONObject addDriver(User user) {
		log.debug("add: {}",user);
        StringBuilder s;
        JSONObject json = null;
        Environment env = Environment.getEnv();
        try {
			user.setUserCode(DateUtil.dateToStringCode(new Date()));
        	s = checkNewRecord(user);
            if (!StringUtils.isBlank(s)) {
                 throw new BusinessException("数据检查失败：" + s.toString());
            }
            json = new JSONObject();
        	//验证邮箱名称和手机号
            if(!CheckUtil.checkMobileNumber(user.getPhone())) {
          	   throw new BusinessException("9007","手机号");
             }
			if(StringUtils.isNotBlank(user.getEmail())) {
        	  if(!CheckUtil.checkEmail(user.getEmail().replaceAll(" ", ""))) {
              	   throw new BusinessException("9007","邮箱号");
                 }
            }
            user.setUserCode(DateUtil.dateToStringCode(new Date()));
            user.setSuperManager(0);
            user.setCreator(env.getUser().getUserId());
            user.setCreateDate(new Date());
            user.setUserType(1);
            user.setCorpId(env.getCorp().getCorpId());
            getMapper().insertSelective(user);
            user.setDefPassword(GenPassword.get());
            // 维护认证信息
            Login login = null;//loginService.queryByUserId(user.getUserId());
            if(login == null){
                login = new Login();
                login.setUserId(user.getUserId());
                login.setCorpId(user.getCorpId());
                login.setPassword(user.getDefPassword());
                json.put("password",login.getPassword());
                login.setPassword(BCryptUtil.hash(login.getPassword()));
                
                login.setClientType(0);
                login.setBlock(0);
                login.setFlag(0);
                login.setErrorTimes(0);
                loginService.add(login);
            }
            json.put("userCode",user.getUserCode());
            json.put("userName",user.getUserName());
            
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
	@Transactional
	public void deleteList(int[] data) {
		if (null==data || 0==data.length) {
			throw new BusinessException("参数为空");
		}
		driverPhotoService.deleteByDriverIdList(data);
		driverLicenseService.deleteByDriverIdList(data);
		usermapper.logicDeleteByPrimaryKeyList(data);
		
	}

	@Override
	@Transactional
	public User queryByPhone(String phone) {
		return usermapper.selectByPhone(phone,1);
	}
	
 	@Override
 	@Transactional
    public void update(List<User> records) {
        log.debug("updateBatch: {}",records);
        StringBuilder s = new StringBuilder();
        boolean checkResult = true;
        try {
        	if (null==records || 0==records.size()) {
    			throw new BusinessException(LanguageUtil.getMessage("9999"));
    		}
        	for (int i = 0; i < records.size(); i++) {
        		User record = records.get(i);
                StringBuilder check = checkUpdate(record);
                if(!StringUtils.isBlank(check)){
                    checkResult = false;
                    s.append("第"+(i+1)+"行，"+check+"/r/n");
                }
            	usermapper.updateByPrimaryKeySelective(records.get(i));
            }
        	if (!checkResult) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
        } catch (Exception e) {
        	if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新提交！",e);
            } else if(e instanceof BusinessException){
                throw e;
            }else {
                throw new RuntimeException("操作失败");
            }
        }

    }


 	@Override
	@Transactional
	public void updateBillStatus(Long driverId,String path) {
		if (null==driverId ) {
			throw new BusinessException("参数为空");
		}
			User driver=userService.getByDriverId(driverId);
			if (driver==null) {
				throw  new BusinessException("驾驶员不存在,请核对参数");
			}
			Environment env=Environment.getEnv();
			if ("/til-timp/s1/driver/audit".equals(path)) {
				driver.setChecker(env.getUser().getUserId());
				driver.setCheckDate(new Date());
				driver.setBillStatus(1);
				usermapper.updateByPrimaryKeySelective(driver);
			}else if ("/til-timp/s1/driver/unaudit".equals(path)) {
				driver.setChecker(null);
				driver.setCheckDate(null);
				userService.updateBillStatus(driverId);
			}

	}	
	//部分更新
	@Override
	@Transactional
    public void updateSelective(User record) {
        log.debug("updateSelective: {}",record);
        	StringBuilder s = new StringBuilder();
        try {
        		checkNewRecord(record);
                s = s.append(CheckUtils.checkLong("NO", "driverId", "驾驶员ID", record.getUserId(), 20));
                if (!StringUtils.isBlank(s)) {
                    throw new BusinessException("数据检查失败：" + s.toString());
                }
                User user=usermapper.selectByPrimaryKey(record.getUserId());
                if (user==null) {
                	throw new BusinessException("用户信息不存在!");
    			}
    			if (user.getBillStatus()==1){
					throw new BusinessException("此用户信息不可修改!");
				}
                record.setUserCode(null);
                usermapper.updateByPrimaryKeySelective(record);
            
        } catch (Exception e) {
        	if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新提交！",e);
            } else if(e instanceof BusinessException){
                throw e;
            }else {
                throw new RuntimeException(e);
            }
        }
    }
	
	//通过ID查询
	@Override
	@Transactional
    public User queryById(Long id) {
        log.debug("query: {}",id);
        StringBuilder s = new StringBuilder();
        try {
            s = s.append(CheckUtils.checkLong("NO", "driverId", "驾驶员", id, 20));
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
			User driver = userService.getByDriverId(id);
			Transporter transporter = transporterService.queryByContractorId(id);
			if(transporter != null){
				driver.setTractorId(transporter.getTractorId());
				driver.setTractorCode(transporter.getRefTractorCode());
				driver.setTractorName(transporter.getRefTractorName());
				driver.setTractirPlateCode(transporter.getTractirPlateCode());
				driver.setTransporterId(transporter.getTransporterId());
			}
			return driver;
        } catch (Exception e) {
        	throw new BusinessException(e.getMessage());
        }
    }
	//通过ID删除
	 @Override
    public void deleteById(Long id) {
        log.debug("delete: {}",id);
        StringBuilder s = new StringBuilder();
        try {
            s = s.append(CheckUtils.checkLong("NO", "driverId", "驾驶员ID", id, 20));
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            List<DriverPhoto> driverPhoto=driverPhotoService.selectByDriverId(id);
            DriverLicense driverLicense=driverLicenseService.selectByDriverId(id);
            if (driverPhoto!=null ) {
            	driverPhotoService.deleteByDriverId(id);
			}
            if (driverLicense!=null) {
	    		driverLicenseService.deleteByDriverId(id);
			}
            
            usermapper.deleteByPrimaryKey(id);
        } catch (Exception e) {
        	throw new BusinessException(e.getMessage());
        }
        
    }


	@Override
	@Transactional
	public PageInfo<User> getDriverList(Map<String, Object> data, int pageNum, int pageSize) {
		log.debug("queryList: {}, page: {},{}", data,pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
		try {
			if (data!=null){
				DateUtil.addTime(data);
			}
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
        List<User> drivers=usermapper.getDriverList(data);
        for (User user : drivers) {
        	String path=user.getPhotoPath();
        	if (path!=null) {
				if (path.indexOf("img")!=-1 && path.indexOf("http://10.8.12.123/")==-1) {
					String photoPath=String.format("http://10.8.12.123/%s", path.substring(6, path.length()).replace('\\', '/'));
	            	user.setPhotoPath(photoPath);
				}
			}
        	List<DriverPhoto> driverPhotos=driverPhotoService.selectByDriverId(user.getUserId());
        	List<Map<String,Object>> photoInfo=new ArrayList<>();
        	if (driverPhotos!=null && driverPhotos.size()!=0) {
					for (int i=0;i<driverPhotos.size();i++) {
						String string=driverPhotos.get(i).getPhotoPath();
						if (string.indexOf("img")!=-1 && string.indexOf("http://10.8.12.123/")==-1) {
						string=String.format("http://10.8.12.123/%s", string.substring(6, string.length()).replace('\\', '/'));
						}
						Map<String,Object> map=new HashMap<>();
						map.put("photoDes", driverPhotos.get(i).getPhotoDes());
						map.put("photoId", driverPhotos.get(i).getDriverPhotoId());
						map.put("driverPhotoPath", string);
						photoInfo.add(map);
					}
					user.setPath(photoInfo);
			}
        	DriverLicense license=driverLicenseService.selectByDriverId(user.getUserId());
        	if (license!=null) {
        		String licensePath=license.getLicensePath();
        		user.setLicenseName(license.getLicenseName());
        		user.setLicenseType(license.getLicenseType());
        		if (licensePath!=null) {
    				if (licensePath.indexOf("img")!=-1  && licensePath.indexOf("http://10.8.12.123/")==-1) {
    					licensePath=String.format("http://10.8.12.123/%s", licensePath.substring(6, licensePath.length()).replace('\\', '/'));
    		        	user.setLicensePath(licensePath);
    				}
    			}
			}
        	
		}
        
		return new PageInfo<User>(drivers);
	}
	
	@Override
	@Transactional
	public JSONObject updatePasswords(User user) {
		log.debug("updatePasswords:{}",user);
		 StringBuilder s ;
		 Login login = null;
		 JSONObject json = null;
		 String password = null;
		 try {
			 s = new StringBuilder();
			 s.append(CheckUtils.checkLong("NO", "driverId", "驾驶员Id", user.getUserId(), 20));
			 if (!StringUtils.isBlank(s)) {
	              throw new BusinessException("数据检查失败：" + s.toString());
	         }
			 User u=queryById(user.getUserId());
			 if (u==null) {
				 throw new BusinessException("司机不存在" );
			}
			 login = new Login();
			 json = new JSONObject();
			 login.setLoginId(loginService.queryByUserId(user.getUserId()).getLoginId());
			 password = GenPassword.get();
			 login.setPassword(BCryptUtil.hash(password));
			 loginService.updateSelective(login);
			 json.put("userCode",u.getUserCode());
	         json.put("userName",u.getUserName());
	         json.put("password",password);
	         return json;
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
	@Transactional
	public void updateBlock(User user) {
		log.debug("updateBlock:{}",user);
		 StringBuilder s ;
		 Login login = null;
		 Login loginBak = null;
		 try {
			 s = new StringBuilder();
			 s.append(CheckUtils.checkLong("NO", "driverId", "驾驶员Id", user.getUserId(), 20));
			 if (!StringUtils.isBlank(s)) {
	              throw new BusinessException("数据检查失败：" + s.toString());
	         }
			 login = new Login();
			 User u=queryById(user.getUserId());
			 if (u==null) {
				 throw new BusinessException("司机不存在" );
			}
			 loginBak = loginService.queryByUserId(user.getUserId());
			 if (loginBak.getBlock()==0) {
				 throw new BusinessException("司机状态为未锁定" );
			}
			 login.setBlock(0);
			 login.setErrorTimes(0);
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
	@Transactional
	public void audit(Long driverId) {
			if (null==driverId ) {
				throw new BusinessException("参数为空");
			}
			User driver=userService.getByDriverId(driverId);
			if (driver==null) {
				throw  new BusinessException("驾驶员不存在,请核对参数");
			}
			Environment env=Environment.getEnv();
			
			driver.setChecker(env.getUser().getUserId());
			driver.setCheckDate(new Date());
			driver.setBillStatus(1);
			usermapper.updateByPrimaryKeySelective(driver);
		
		
	}


	@Override
	@Transactional
	public void unaudit(Long driverId) {
			if (null==driverId ) {
				throw new BusinessException("参数为空");
			}
			User driver=userService.getByDriverId(driverId);
			if (driver==null) {
				throw  new BusinessException("驾驶员不存在,请核对参数");
			}
			userService.updateBillStatus(driverId);
				
		
	}


	@Override
	@Transactional
	public PageInfo<User> getReferenceList(Map<String, Object> map,int pageNum, int pageSize) {
		log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
		try {
			PageHelper.startPage(pageNum, pageSize);
			return new PageInfo<>(usermapper.getReferenceList(map));
		} catch (Exception e) {
			throw e;
		}
		
	}

	/**
	 * 添加头像
	 */
	@Override
	@Transactional
	public void addHeadPhoto(MultipartFile file, Long driverId) {
		try {
			User driver=userService.queryById(driverId);
			if (driver==null) {
				throw new BusinessException("司机不存在");
			}
			String photoPath = FileUtil.uploadFile(file,"driverPhoto");
			driver.setPhotoPath(photoPath);
			userService.updateSelective(driver);
		} catch (Exception e) {
			throw new BusinessException(e);
		}
		
	}

	@Override
	public void importExcel(MultipartFile file) throws Exception {
		{
			String filePath;
			try {
				filePath = FileUtil.uploadExcel(file);
				Workbook workbook = ExcelUtil.getWorkbook(filePath);
				Map<String,Boolean> map = new HashMap<>();
				map.put("身份证号码",false);
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
					importUser.setUserType(UserType.DRIVER.getIndex());
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
					addDriver(importUser);
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
	}

	@Override
	public Workbook exportExcel() throws Exception {
		try {
			Map<String,Object> map = new HashMap<>();
			map.put("userType","1");
			List<User> users = queryList(map);
			Workbook workbook = ExcelUtil.getWorkbookFromModels(users);
			return workbook;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

}
