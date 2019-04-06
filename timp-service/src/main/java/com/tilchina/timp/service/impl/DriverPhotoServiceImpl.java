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
import com.tilchina.timp.model.Brand;
import com.tilchina.timp.model.CarType;
import com.tilchina.timp.model.DriverLicense;
import com.tilchina.timp.model.DriverPhoto;
import com.tilchina.timp.model.Trailer;
import com.tilchina.timp.model.User;
import com.tilchina.timp.service.DriverPhotoService;
import com.tilchina.timp.service.DriverService;
import com.tilchina.timp.util.DateUtil;
import com.tilchina.timp.util.FileUtil;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.DriverPhotoMapper;

/**
* 驾驶员照片档案
*
* @version 1.1.0
* @author XiaHong
*/
@Service
@Slf4j
public class DriverPhotoServiceImpl extends BaseServiceImpl<DriverPhoto> implements DriverPhotoService {

	@Autowired
    private DriverPhotoMapper driverphotomapper;
	
	@Autowired
	private DriverService driverService;
	
	@Override
	protected BaseMapper<DriverPhoto> getMapper() {
		return driverphotomapper;
	}

	@Override
	protected StringBuilder checkNewRecord(DriverPhoto driverphoto) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("NO", "photoDes", "照片名称", driverphoto.getPhotoDes(), 20));
        s.append(CheckUtils.checkInteger("NO", "photoType", "照片类型(0=头像", driverphoto.getPhotoType(), 10));
        s.append(CheckUtils.checkString("YES", "photoPath", "照片路径", driverphoto.getPhotoPath(), 200));
        s.append(CheckUtils.checkInteger("YES", "billStatus", "状态(0=制单", driverphoto.getBillStatus(), 10));
        driverphoto.setCreateDate(new Date());
        s.append(CheckUtils.checkDate("YES", "createDate", "创建时间", driverphoto.getCreateDate()));
        s.append(CheckUtils.checkDate("YES", "checkDate", "审核时间", driverphoto.getCheckDate()));
        s.append(CheckUtils.checkInteger("YES", "flag", "封存标志", driverphoto.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(DriverPhoto driverphoto) {
        StringBuilder s = checkNewRecord(driverphoto);
        s.append(CheckUtils.checkPrimaryKey(driverphoto.getDriverPhotoId()));
		return s;
	}
	
	@Override
	@Transactional
    public void add(DriverPhoto record) {
        log.debug("add: {}",record);
        StringBuilder s;
        try {
            s = checkNewRecord(record);
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            Environment env=Environment.getEnv();
            record.setDriverId(env.getUser().getUserId());
            record.setCreator(env.getUser().getUserId());
            driverphotomapper.insertSelective(record);
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
	public void deleteList(int[] data) {
		if (null==data || 0==data.length) {
			throw new BusinessException(LanguageUtil.getMessage("9999"));
		}
		driverphotomapper.deleteList(data);
		
	}
	
	@Override
	@Transactional
    public void update(List<DriverPhoto> records) {
        log.debug("updateBatch: {}",records);
        StringBuilder s = new StringBuilder();
        boolean checkResult = true;
        try {
        	if (null==records || 0==records.size()) {
    			throw new BusinessException(LanguageUtil.getMessage("9999"));
    		}
        	for (int i = 0; i < records.size(); i++) {
        		DriverPhoto record = records.get(i);
                StringBuilder check = checkUpdate(record);
                if(!StringUtils.isBlank(check)){
                    checkResult = false;
                    s.append("第"+(i+1)+"行，"+check+"/r/n");
                }
            	driverphotomapper.updateByPrimaryKeySelective(records.get(i));
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
	public void updateBillStatus(Long record,String path) {
		if (null==record) {
			throw new BusinessException("参数为空");
		}
			DriverPhoto driverPhoto=driverphotomapper.selectByPrimaryKey(record);
			if (driverPhoto==null) {
				throw new BusinessException("照片不存在");
			}
			if ("/til-timp/s1/driverphoto/audit".equals(path)) {
				
				Environment env=Environment.getEnv();
				driverPhoto.setChecker(env.getUser().getUserId());
				driverPhoto.setCheckDate(new Date());
				driverPhoto.setBillStatus(1);
				driverphotomapper.updateByPrimaryKeySelective(driverPhoto);
			}else if ("/til-timp/s1/driverphoto/unaudit".equals(path)) {
				driverphotomapper.removeDate(record);
			}
			
		
		
	}
	
	//部分更新
	@Override
	@Transactional
    public void updateSelective(DriverPhoto record) {
        log.debug("updateSelective: {}",record);
        	StringBuilder s = new StringBuilder();
        try {
                s = s.append(CheckUtils.checkLong("NO", "driverPhotoId", "驾驶员照片ID", record.getDriverPhotoId(), 20));
                if (!StringUtils.isBlank(s)) {
                    throw new BusinessException("数据检查失败：" + s.toString());
                }
                DriverPhoto driverPhoto=driverphotomapper.selectByPrimaryKey(record.getDriverPhotoId());
                if (driverPhoto==null) {
                	throw new BusinessException("这条记录不存在!");
    			}
                driverphotomapper.updateByPrimaryKeySelective(record);
            
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
	
	//通过ID查询
	@Override
	@Transactional
    public DriverPhoto queryById(Long id) {
        log.debug("query: {}",id);
        StringBuilder s = new StringBuilder();
        try {
            s = s.append(CheckUtils.checkLong("NO", "driverPhotoId", "驾驶员照片ID", id, 20));
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }

            return driverphotomapper.selectByPrimaryKey(id);
        } catch (Exception e) {
        	throw new BusinessException(e.getMessage());
        }
    }
	//通过ID删除
	 @Override
	 @Transactional
    public void deleteById(Long id) {
        log.debug("delete: {}",id);
        StringBuilder s = new StringBuilder();
        try {
            s = s.append(CheckUtils.checkLong("NO", "driverPhotoId", "驾驶员照片ID", id, 20));
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }

            driverphotomapper.deleteByPrimaryKey(id);
        } catch (Exception e) {
        	throw new BusinessException(e.getMessage());
        }
        
    }

	@Override
	@Transactional
	public void addInfoAndPhoto(DriverPhoto driverPhoto, MultipartFile file) {
		log.debug("add: {}",driverPhoto);
        StringBuilder s;
        try {
            s = checkNewRecord(driverPhoto);
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            Environment env=Environment.getEnv();
            driverPhoto.setDriverId(env.getUser().getUserId());
            driverPhoto.setCreator(env.getUser().getUserId());
            driverPhoto.setCorpId(env.getCorp().getCorpId());
//            driverPhoto.setDriverId((long) 1);
//            driverPhoto.setCreator((long) 1);
//            driverPhoto.setCorpId((long) 1);
        	String photoPath=FileUtil.uploadFile(file,"driverPhoto");
        	driverPhoto.setPhotoPath(photoPath);
            driverphotomapper.insertSelective(driverPhoto);
            
        } catch (Exception e) {
        	if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新提交！",e);
            } else if(e instanceof BusinessException){
                throw new BusinessException(e);
            }else {
                throw new RuntimeException("操作失败");
            }
        }
		
	}

	@Override
	@Transactional
	public void addPhoto(MultipartFile file, String photoDes, int photoType,Long driverId) {
		DriverPhoto driverPhoto=new DriverPhoto();
		String photoPath;
		Environment environment=Environment.getEnv();
			try {
				User driver=driverService.queryById(driverId);
				if (driver==null) {
					throw new BusinessException("司机不存在");
				}
					photoPath = FileUtil.uploadFile(file,"driverPhoto");
					driverPhoto.setDriverId(driverId);
					driverPhoto.setPhotoDes(photoDes);
					driverPhoto.setPhotoType(photoType);
					driverPhoto.setPhotoPath(photoPath);
					driverPhoto.setBillStatus(0);
					driverPhoto.setCreator(environment.getUser().getUserId());
					driverPhoto.setCreateDate(new Date());
					driverPhoto.setChecker(null);
					driverPhoto.setCheckDate(null);
					driverPhoto.setCorpId(driver.getCorpId());
					driverPhoto.setFlag(0);
		            driverphotomapper.insertSelective(driverPhoto);
				
				
			} catch (Exception e) {
				throw new BusinessException(e);
			}
        	
		
		
	}
	
	 @Override
	 @Transactional
	public void removeDate(Long photoId) {
		 try {
			DriverPhoto driverPhoto=driverphotomapper.selectByPrimaryKey(photoId);
			if (driverPhoto==null) {
				throw new BusinessException("轿运车车头不存在");
			}
			driverphotomapper.removeDate(photoId);
		} catch (Exception e) {
			throw e;
		}
		
	}

	@Override
	@Transactional
	public void audit(Long record) {
			if (null==record) {
				throw new BusinessException("参数为空");
			}
			DriverPhoto driverPhoto=driverphotomapper.selectByPrimaryKey(record);
			if (driverPhoto==null) {
				throw new BusinessException("照片不存在");
			}
				
			Environment env=Environment.getEnv();
			driverPhoto.setChecker(env.getUser().getUserId());
			driverPhoto.setCheckDate(new Date());
			driverPhoto.setBillStatus(1);
			driverphotomapper.updateByPrimaryKeySelective(driverPhoto);
		
	}

	@Override
	@Transactional
	public void unaudit(Long record) {
			if (null==record) {
				throw new BusinessException("参数为空");
			}
			DriverPhoto driverPhoto=driverphotomapper.selectByPrimaryKey(record);
			if (driverPhoto==null) {
				throw new BusinessException("照片不存在");
			}
			
			driverphotomapper.removeDate(record);
			
		
	}

	@Override
	@Transactional
	public void deleteByDriverIdList(int[] data) {
		if (null==data || 0==data.length) {
			throw new BusinessException("参数为空");
		}
		for (int i = 0; i < data.length; i++) {
			List<DriverPhoto> driverPhoto=driverphotomapper.selectByDriverId(data[i]);
		if (null==driverPhoto) {
			throw new BusinessException("驾驶员ID"+data[i]+"不存在");
		}
		}
		driverphotomapper.deleteByDriverIdList(data);
		
		
	}

	@Override
	@Transactional
	public void deleteByDriverId(Long id) {
		if (null==id) {
			throw new BusinessException("参数为空");
		}
		List<DriverPhoto> driverPhoto=driverphotomapper.selectByDriverId(id);
		if (driverPhoto==null) {
			throw new BusinessException("驾驶员ID"+id+"不存在");
		}
		driverphotomapper.deleteByDriverId(id);
		
	}

	@Override
	@Transactional
	public List<DriverPhoto> selectByDriverId(Long driverId) {
		try {
			if (driverId==null) {
				throw new BusinessException("参数为空");
			}
			
			return driverphotomapper.selectByDriverId(driverId);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	@Transactional
	public void add(String photoDes, int photoType, MultipartFile file) {
		
		try {
			DriverPhoto driverPhoto=new DriverPhoto();
			driverPhoto.setPhotoDes(photoDes);
			driverPhoto.setPhotoType(photoType);
			driverPhoto.setPhotoPath(FileUtil.uploadFile(file, "photoPath"));
		} catch (Exception e) {
			throw new BusinessException(e);
		}
		
		
	}
	
	/**
     * 查询结果按创建时间倒序排序
     */
	@Override
	@Transactional
	public PageInfo<DriverPhoto> getList(Map<String, Object> map, int pageNum, int pageSize) {
		log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
        
		try {
			PageHelper.startPage(pageNum, pageSize);
			return new PageInfo<DriverPhoto>(driverphotomapper.getList(map));
		} catch (Exception e) {
			throw e;
		}
	}
	
}
