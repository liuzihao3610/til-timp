package com.tilchina.timp.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.model.OilPriceRecord;
import com.tilchina.timp.service.OilPriceRecordService;
import com.tilchina.timp.util.DateUtil;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.OilPriceRecordMapper;

/**
* 油价变更记录表
*
* @version 1.1.0
* @author XiaHong
*/
@Service
@Slf4j
public class OilPriceRecordServiceImpl extends BaseServiceImpl<OilPriceRecord> implements OilPriceRecordService {

	@Autowired
    private OilPriceRecordMapper oilpricerecordmapper;
	
	@Autowired
	private  OilPriceRecordService oilPriceRecordService;
	
	@Override
	protected BaseMapper<OilPriceRecord> getMapper() {
		return oilpricerecordmapper;
	}
	
	private static boolean isNullAble(String nullable) {
        if ("YES".equals(nullable.toUpperCase()) || "Y".equals(nullable.toUpperCase())) {
            return true;
        }
        return false;
    }
	
	public static String checkBigDecimal(String nullable, String attName, String comment, BigDecimal decimal, int length, int scale) {
        String desc = comment+"["+attName+"]";
        if (decimal == null) {
            if (!isNullAble(nullable)) {
                return desc + " can not be null! ";
            } else {
                return "";
            }
        }
        if (decimal.toString().length() > length) {
            return desc + " value "+decimal+ " out of range [" + length + "].";
        }

        String scaleStr = decimal.toString();
        if (scale > 0 && scaleStr.contains(".") && scaleStr.split("\\.")[1].length() > scale) {
            return desc + " value "+decimal+ " scale out of range [" + scale + "].";
        }
        return "";
    }

	@Override
	protected StringBuilder checkNewRecord(OilPriceRecord oilpricerecord) {
		StringBuilder s = new StringBuilder();
        s.append(checkBigDecimal("NO", "price", "变更后金额", oilpricerecord.getPrice(), 10, 3));
        s.append(CheckUtils.checkInteger("YES", "billStatus", "状态(0=制单", oilpricerecord.getBillStatus(), 10));
        oilpricerecord.setCreateTime(new Date());
        s.append(CheckUtils.checkDate("YES", "createTime", "创建时间", oilpricerecord.getCreateTime()));
        s.append(CheckUtils.checkDate("YES", "checkDate", "审核时间", oilpricerecord.getCheckDate()));
        s.append(CheckUtils.checkInteger("YES", "flag", "封存标志", oilpricerecord.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(OilPriceRecord oilpricerecord) {
        StringBuilder s = checkNewRecord(oilpricerecord);
        s.append(CheckUtils.checkPrimaryKey(oilpricerecord.getOilPriceRecordId()));
		return s;
	}
	
	@Override
	@Transactional
    public void add(OilPriceRecord record) {
        log.debug("add: {}",record);
        StringBuilder s;
        try {
            s = checkNewRecord(record);
            if (!StringUtils.isBlank(s)) {
                throw new RuntimeException("数据检查失败：" + s.toString());
            }
            Environment env=Environment.getEnv();
            record.setCreator(env.getUser().getUserId());
            oilpricerecordmapper.insertSelective(record);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new RuntimeException("数据重复，请查证后重新保存！", e);
            } else {
                throw new RuntimeException("保存失败！", e);
            }
        }
    }
	
	@Override
	@Transactional
	public void deleteList(int[] data) {
		if (null==data || 0==data.length) {
			throw new BusinessException(LanguageUtil.getMessage("9999"));
		}
		oilpricerecordmapper.deleteList(data);
		
	}
	

	
 	@Override
 	@Transactional
    public void update(List<OilPriceRecord> records) {
        log.debug("updateBatch: {}",records);
        StringBuilder s = new StringBuilder();
        boolean checkResult = true;
        try {
        	if (null==records || 0==records.size()) {
    			throw new BusinessException(LanguageUtil.getMessage("9999"));
    		}
        	for (int i = 0; i < records.size(); i++) {
        		OilPriceRecord record = records.get(i);
                StringBuilder check = checkUpdate(record);
                if(!StringUtils.isBlank(check)){
                    checkResult = false;
                    s.append("第"+(i+1)+"行，"+check+"/r/n");
                }
            	oilpricerecordmapper.updateByPrimaryKeySelective(records.get(i));
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
			OilPriceRecord oilPriceRecord=oilpricerecordmapper.selectByPrimaryKey(record);
			if (oilPriceRecord==null) {
				throw new BusinessException("变更记录不存在");
			}
			if ("/til-timp/s1/oilpricerecord/audit".equals(path)) {
				oilPriceRecord.setBillStatus(1);
				Environment env=Environment.getEnv();
				oilPriceRecord.setChecker(env.getUser().getUserId());
				oilPriceRecord.setCheckDate(new Date());
				oilpricerecordmapper.updateByPrimaryKeySelective(oilPriceRecord);
			}else if ("/til-timp/s1/oilpricerecord/unaudit".equals(path)) {
				oilpricerecordmapper.removeDate(record);
			}
			
		
		
	}	
	//部分更新
	@Override
	@Transactional
    public void updateSelective(OilPriceRecord record) {
        log.debug("updateSelective: {}",record);
        	StringBuilder s = new StringBuilder();
        try {
                s = s.append(CheckUtils.checkLong("NO", "oilPriceRecordId", "油价变更ID", record.getOilPriceRecordId(), 20));
                if (!StringUtils.isBlank(s)) {
                    throw new BusinessException("数据检查失败：" + s.toString());
                }
                OilPriceRecord oilPriceRecord=oilPriceRecordService.queryById(record.getOilPriceRecordId());
                if (oilPriceRecord==null) {
                	throw new BusinessException("这条记录不存在!");
    			}
                oilpricerecordmapper.updateByPrimaryKeySelective(record);
            
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
    public OilPriceRecord queryById(Long id) {
        log.debug("query: {}",id);
        StringBuilder s = new StringBuilder();
        try {
            s = s.append(CheckUtils.checkLong("NO", "oilPriceRecordId", "油价变更ID", id, 20));
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }

            return oilpricerecordmapper.selectByPrimaryKey(id);
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
            s = s.append(CheckUtils.checkLong("NO", "oilPriceRecordId", "油价变更ID", id, 20));
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }

            oilpricerecordmapper.deleteByPrimaryKey(id);
        } catch (Exception e) {
        	throw new BusinessException(e.getMessage());
        }
        
    }
	 
	 @Override
	 @Transactional
	public void removeDate(Long recoerId) {
		 try {
			OilPriceRecord oilPriceRecord=oilpricerecordmapper.selectByPrimaryKey(recoerId);
			if (oilPriceRecord==null) {
				throw new BusinessException("轿运车车头不存在");
			}
			oilpricerecordmapper.removeDate(recoerId);
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
			OilPriceRecord oilPriceRecord=oilpricerecordmapper.selectByPrimaryKey(record);
			if (oilPriceRecord==null) {
				throw new BusinessException("变更记录不存在");
			}
			oilPriceRecord.setBillStatus(1);
			Environment env=Environment.getEnv();
			oilPriceRecord.setChecker(env.getUser().getUserId());
			oilPriceRecord.setCheckDate(new Date());
			oilpricerecordmapper.updateByPrimaryKeySelective(oilPriceRecord);
			
		
	}

	@Override
	@Transactional
	public void unaudit(Long record) {
			if (null==record) {
				throw new BusinessException("参数为空");
			}
			OilPriceRecord oilPriceRecord=oilpricerecordmapper.selectByPrimaryKey(record);
			if (oilPriceRecord==null) {
				throw new BusinessException("变更记录不存在");
			}
			oilpricerecordmapper.removeDate(record);
	
	}
	/**
     * 查询结果按创建时间倒序排序
     */
	@Override
	@Transactional
	public PageInfo<OilPriceRecord> getList(Map<String, Object> map, int pageNum, int pageSize) {
		log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
        
		try {
			PageHelper.startPage(pageNum, pageSize);
			return new PageInfo<OilPriceRecord>(oilpricerecordmapper.getList(map));
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	@Transactional
	public PageInfo<OilPriceRecord> queryList(Map<String, Object> map, int pageNum, int pageSize) {
		log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
		PageHelper.startPage(pageNum, pageSize);
		try {
			if (map!=null){
				DateUtil.addTime(map);
			}
			return new PageInfo<OilPriceRecord>(oilpricerecordmapper.selectList(map));
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}

	}
	
}
