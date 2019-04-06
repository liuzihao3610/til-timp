package com.tilchina.timp.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.model.DriverLicense;
import com.tilchina.timp.model.User;
import com.tilchina.timp.service.DriverLicenseService;
import com.tilchina.timp.service.DriverService;
import com.tilchina.timp.util.FileUtil;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.DriverLicenseMapper;

/**
* 驾驶证档案
*
* @version 1.1.0
* @author XiaHong
*/
@Service
@Slf4j
public class DriverLicenseServiceImpl extends BaseServiceImpl<DriverLicense> implements DriverLicenseService {

	@Autowired
    private DriverLicenseMapper driverlicensemapper;
	
	@Autowired
	private DriverService driverService;
	
	@Override
	protected BaseMapper<DriverLicense> getMapper() {
		return driverlicensemapper;
	}

	@Override
	protected StringBuilder checkNewRecord(DriverLicense driverlicense) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("NO", "licenseName", "驾驶证名称", driverlicense.getLicenseName(), 20));
        s.append(CheckUtils.checkInteger("NO", "licenseType", "驾驶证类型", driverlicense.getLicenseType(), 10));
        s.append(CheckUtils.checkString("YES", "licensePath", "照片路径", driverlicense.getLicensePath(), 200));
        s.append(CheckUtils.checkInteger("YES", "billStatus", "状态(0=制单 1=审核)", driverlicense.getBillStatus(), 10));
        driverlicense.setCreateDate(new Date());
        s.append(CheckUtils.checkDate("YES", "createDate", "创建时间", driverlicense.getCreateDate()));
        s.append(CheckUtils.checkDate("YES", "checkDate", "审核时间", driverlicense.getCheckDate()));
        s.append(CheckUtils.checkInteger("YES", "flag", "封存标志", driverlicense.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(DriverLicense driverlicense) {
        StringBuilder s = checkNewRecord(driverlicense);
        s.append(CheckUtils.checkPrimaryKey(driverlicense.getDriverLicenseId()));
		return s;
	}
	
	
	@Override
	@Transactional
    public void add(DriverLicense record) {
        log.debug("add: {}",record);
        StringBuilder s;
        try {
            s = checkNewRecord(record);
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            DriverLicense driverLicense=driverlicensemapper.selectByDriverId(record.getDriverId());
            if (driverLicense!=null) {
            	throw new BusinessException("该用户已经添加驾驶证,无需再次添加");
			}
            Environment env=Environment.getEnv();
            record.setCreator(env.getUser().getUserId());
            driverlicensemapper.insertSelective(record);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新保存！", e);
            } else {
                throw e;
            }
        }
    }
	
	@Override
	@Transactional
	public void deleteList(int[] data) {
		if (null==data || 0==data.length) {
			throw new BusinessException(LanguageUtil.getMessage("9999"));
		}
		driverlicensemapper.deleteList(data);
		
	}
	@Override
 	@Transactional
    public void update(List<DriverLicense> driverLicenses) {
        log.debug("updateBatch: {}",driverLicenses);
        StringBuilder s = new StringBuilder();
        boolean checkResult = true;
        try {
        	if (null==driverLicenses || 0==driverLicenses.size()) {
    			throw new BusinessException(LanguageUtil.getMessage("9999"));
    		}
        	for (int i = 0; i < driverLicenses.size(); i++) {
        		DriverLicense record = driverLicenses.get(i);
                StringBuilder check = checkUpdate(record);
                if(!StringUtils.isBlank(check)){
                    checkResult = false;
                    s.append("第"+(i+1)+"行，"+check+"/r/n");
                }
            	driverlicensemapper.updateByPrimaryKeySelective(driverLicenses.get(i));
            }
        	if (!checkResult) {
                throw new RuntimeException("数据检查失败：" + s.toString());
            }
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new RuntimeException("数据重复，请查证后重新提交！", e);
            } else {
                throw new BusinessException(LanguageUtil.getMessage("9999"));
            }
        }

    }

	@Override
	@Transactional
	public void updateBillStatus(Long driverLicenseId,String path) {
		if (null==driverLicenseId ) {
			throw new BusinessException("参数为空");
		}
			DriverLicense driverLicense=driverlicensemapper.selectByPrimaryKey(driverLicenseId);
			if (driverLicense==null) {
				throw new BusinessException("驾驶证不存在");
			}
			if ("/s1/oilpricerecord/audit".equals(path)) {
				driverLicense.setBillStatus(1);
			}else if ("/s1/oilpricerecord/unaudit".equals(path)) {
				driverLicense.setBillStatus(0);
			}
			Environment env=Environment.getEnv();
			driverLicense.setChecker(env.getUser().getUserId());
			//driverLicense.setChecker(1l);
			driverLicense.setCheckDate(new Date());
			driverlicensemapper.updateByPrimaryKeySelective(driverLicense);
		
		
	}
	//部分更新
	@Override
	@Transactional
    public void updateSelective(DriverLicense record) {
        log.debug("updateSelective: {}",record);
        	StringBuilder s = new StringBuilder();
        try {
                s = s.append(CheckUtils.checkLong("NO", "driverLicenseId", "驾驶证ID", record.getDriverLicenseId(), 20));
                if (!StringUtils.isBlank(s)) {
                    throw new RuntimeException("数据检查失败：" + s.toString());
                }
                DriverLicense driverLicense=driverlicensemapper.selectByPrimaryKey(record.getDriverLicenseId());
                if (driverLicense==null) {
                	throw new BusinessException("这条记录不存在!");
    			}
                driverlicensemapper.updateByPrimaryKeySelective(record);
            
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new RuntimeException("数据重复，请查证后重新提交！", e);
            } else {
                throw new RuntimeException("更新失败！", e);
            }
        }
    }
	
	//通过ID查询
	@Override
	@Transactional
    public DriverLicense queryById(Long id) {
        log.debug("query: {}",id);
        StringBuilder s = new StringBuilder();
        try {
            s = s.append(CheckUtils.checkLong("NO", "driverLicenseId", "驾驶证ID", id, 20));
            if (!StringUtils.isBlank(s)) {
                throw new RuntimeException("数据检查失败：" + s.toString());
            }

            return driverlicensemapper.selectByPrimaryKey(id);
        } catch (Exception e) {
        	throw new RuntimeException(e.getMessage());
        }
    }
	//通过ID删除
	 @Override
	 @Transactional
    public void deleteById(Long id) {
        log.debug("delete: {}",id);
        StringBuilder s = new StringBuilder();
        try {
            s = s.append(CheckUtils.checkLong("NO", "driverLicenseId", "驾驶证ID", id, 20));
            if (!StringUtils.isBlank(s)) {
                throw new RuntimeException("数据检查失败：" + s.toString());
            }

            driverlicensemapper.deleteByPrimaryKey(id);
        } catch (Exception e) {
        	throw new RuntimeException(e.getMessage());
        }
        
    }
	 
	 	@Override
	 	@Transactional
	    public PageInfo<DriverLicense> queryList(Map<String, Object> map, int pageNum, int pageSize) {
	        log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
	        PageHelper.startPage(pageNum, pageSize);
	        return new PageInfo<DriverLicense>(driverlicensemapper.selectList(map));
	    }
	    
	    @Override
	    @Transactional
	    public List<DriverLicense> queryList(Map<String, Object> map) {
	        log.debug("queryList: {}", map);
	        return driverlicensemapper.selectList(map);
	    }

		@Override
		@Transactional
		public void addInfoAndPhoto(DriverLicense driverLicense, MultipartFile file) {
			log.debug("add: {}",driverLicense);
	        StringBuilder s;
	        try {
	            s = checkNewRecord(driverLicense);
	            if (!StringUtils.isBlank(s)) {
	                throw new RuntimeException("数据检查失败：" + s.toString());
	            }
	            Environment env=Environment.getEnv();
	            driverLicense.setCreator(env.getUser().getUserId());
	            driverLicense.setCorpId(env.getCorp().getCorpId());
//	            driverLicense.setCreator((long) 1);
//	            driverLicense.setCorpId((long) 1);
            	String photoPath=FileUtil.uploadFile(file,"driverLicense");
            	driverLicense.setLicensePath(photoPath);
            	driverlicensemapper.insertSelective(driverLicense);
	            
	        } catch (Exception e) {
	            if (e.getMessage().indexOf("IDX_") != -1) {
	                throw new RuntimeException("数据重复，请查证后重新保存！", e);
	            } else {
	                throw new RuntimeException("保存失败！", e);
	            }
	        }
			
		}
		//添加驾驶证照片
		@Override
		@Transactional
		public void addPhoto(MultipartFile file, String licenseName, int licenseType,Long driverId) {
			DriverLicense driverLicense=new DriverLicense();
			Environment environment=Environment.getEnv();
			try {
				User driver=driverService.queryById(driverId);
				if (driver==null) {
					throw new BusinessException("司机不存在");
				}
				DriverLicense license=driverlicensemapper.selectByDriverId(driverId);
				if (license!=null) {
					String licensePath=FileUtil.uploadFile(file,"driverLicense");
					license.setLicensePath(licensePath);
					license.setLicenseName(licenseName);
					license.setLicenseType(licenseType);
					driverlicensemapper.updateByPrimaryKeySelective(license);
				}else {
					driverLicense.setDriverId(driverId);
					driverLicense.setLicenseName(licenseName);
					driverLicense.setLicenseType(licenseType);
					String licensePath=FileUtil.uploadFile(file,"driverLicense");
					driverLicense.setLicensePath(licensePath);
					driverLicense.setBillStatus(0);
					driverLicense.setCreator(environment.getUser().getUserId());
					driverLicense.setCreateDate(new Date());
					driverLicense.setChecker(null);
					driverLicense.setCheckDate(null);
					driverLicense.setCorpId(driver.getCorpId());
					driverLicense.setFlag(0);
					driverlicensemapper.insertSelective(driverLicense);
				}
				
				
			} catch (Exception e) {
				throw new BusinessException(e);
			}
			
		}

		@Override
		@Transactional
		public void deleteByDriverIdList(int[] data) {
			if (null==data || 0==data.length) {
				throw new BusinessException("参数为空");
			}
			for (int i = 0; i < data.length; i++) {
				DriverLicense driverLicense=driverlicensemapper.selectByDriverId((long) data[i]);
				if (null==driverLicense) {
					throw new BusinessException("驾驶员ID"+data[i]+"不存在");
				}
			}
			driverlicensemapper.deleteByDriverIdList(data);
			
		}

		@Override
		@Transactional
		public void deleteByDriverId(Long id) {
			if (null==id) {
				throw new BusinessException("参数为空");
			}
			DriverLicense driverLicense=driverlicensemapper.selectByDriverId(id);
			if (null==driverLicense) {
				throw new BusinessException("驾驶员ID"+id+"不存在");
			}
			driverlicensemapper.deleteByDriverId(id);
			
		}

		@Override
		@Transactional
		public DriverLicense selectByDriverId(Long driverId) {
			try {
				if (driverId==null) {
					throw new BusinessException("参数为空");
				}
				
				return driverlicensemapper.selectByDriverId(driverId);
			} catch (Exception e) {
				throw e;
			}
			
		}

		@Override
		@Transactional
		public void audit(Long driverLicenseId) {
				if (null==driverLicenseId ) {
					throw new BusinessException("参数为空");
				}
				DriverLicense driverLicense=driverlicensemapper.selectByPrimaryKey(driverLicenseId);
				if (driverLicense==null) {
					throw new BusinessException("驾驶证不存在");
				}
				Environment env=Environment.getEnv();
				driverLicense.setChecker(env.getUser().getUserId());
				driverLicense.setBillStatus(1);
				driverLicense.setCheckDate(new Date());
				driverlicensemapper.updateByPrimaryKeySelective(driverLicense);
			
		}

		@Override
		@Transactional
		public void unaudit(Long driverLicenseId) {
			if (null==driverLicenseId ) {
				throw new BusinessException("参数为空");
			}
			DriverLicense driverLicense=driverlicensemapper.selectByPrimaryKey(driverLicenseId);
			if (driverLicense==null) {
				throw new BusinessException("驾驶证不存在");
			}
			Environment env=Environment.getEnv();
			driverLicense.setChecker(env.getUser().getUserId());
			driverLicense.setBillStatus(1);
			driverLicense.setCheckDate(new Date());
			driverlicensemapper.unaudit(driverLicenseId);
			
		}
		
		/**
	     * 查询结果按创建时间倒序排序
	     */
		@Override
		@Transactional
		public PageInfo<DriverLicense> getList(Map<String, Object> map, int pageNum, int pageSize) {
			log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
	        
			try {
				PageHelper.startPage(pageNum, pageSize);
				return new PageInfo<DriverLicense>(driverlicensemapper.getList(map));
			} catch (Exception e) {
				throw e;
			}
		}
}
