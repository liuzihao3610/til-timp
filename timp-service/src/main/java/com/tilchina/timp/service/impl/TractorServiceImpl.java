package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.TractorMapper;
import com.tilchina.timp.model.Tractor;
import com.tilchina.timp.service.TractorService;
import com.tilchina.timp.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* 轿运车车头型号档案
*
* @version 1.1.0
* @author XiaHong
*/
@Service
@Slf4j
public class TractorServiceImpl extends BaseServiceImpl<Tractor> implements TractorService {

	@Autowired
    private TractorMapper tractormapper;
	
	@Autowired
    private TractorService tractorService;
	
	@Override
	protected BaseMapper<Tractor> getMapper() {
		return tractormapper;
	}
	
	private static boolean isNullAble(String nullable) {
		return "YES".equals(nullable.toUpperCase()) || "Y".equals(nullable.toUpperCase());
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
	protected StringBuilder checkNewRecord(Tractor tractor) {
		StringBuilder s = new StringBuilder();
		s.append(CheckUtils.checkString("YES", "tractorCode", "牵引车型号", tractor.getTractorCode(), 20));
        s.append(CheckUtils.checkString("NO", "tractorName", "牵引车名称", tractor.getTractorName(), 40));
        s.append(CheckUtils.checkInteger("YES", "tractorType", "牵引车类型(0=一般货车 1=半挂牵引车 2=全挂牵引车）", tractor.getTractorType(), 10));
        s.append(CheckUtils.checkInteger("YES", "baseType", "牵引座类型(0=固定型 1=轴式牵引座)", tractor.getBaseType(), 10));
        s.append(CheckUtils.checkInteger("NO", "truckType", "板车类型(0=大板 1=小板)", tractor.getTruckType(), 10));
        s.append(CheckUtils.checkInteger("YES", "tractorLong", "长(毫米）", tractor.getTractorLong(), 10));
        s.append(CheckUtils.checkInteger("YES", "tractorWidth", "宽(毫米)", tractor.getTractorWidth(), 10));
        s.append(CheckUtils.checkInteger("YES", "tractorHigh", "高(毫米)", tractor.getTractorHigh(), 10));
        s.append(checkBigDecimal("YES", "price", "价格(元)", tractor.getPrice(), 10, 2));
        s.append(CheckUtils.checkString("YES", "origin", "产地", tractor.getOrigin(), 200));
        s.append(CheckUtils.checkInteger("YES", "billStatus", "状态(0=制单", tractor.getBillStatus(), 10));
        s.append(CheckUtils.checkDate("YES", "createDate", "创建时间", tractor.getCreateDate()));
        s.append(CheckUtils.checkDate("YES", "checkDate", "审核时间", tractor.getCheckDate()));
        s.append(CheckUtils.checkInteger("YES", "flag", "封存标志", tractor.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(Tractor tractor) {
        StringBuilder s = checkNewRecord(tractor);
        s.append(CheckUtils.checkPrimaryKey(tractor.getTractorId()));
		return s;
	}
	
	@Override
	@Transactional
	public void deleteList(int[] data) {
		if (null==data || 0==data.length) {
			throw new BusinessException(LanguageUtil.getMessage("9999"));
		}
		tractormapper.deleteList(data);
		
	}
	@Override
 	@Transactional
    public void update(List<Tractor> records) {
        log.debug("updateBatch: {}",records);
        StringBuilder s = new StringBuilder();
        boolean checkResult = true;
        try {
        	if (null==records || 0==records.size()) {
    			throw new BusinessException(LanguageUtil.getMessage("9999"));
    		}
        	for (int i = 0; i < records.size(); i++) {
        		Tractor record = records.get(i);
                StringBuilder check = checkUpdate(record);
                if(!StringUtils.isBlank(check)){
                    checkResult = false;
                    s.append("第"+(i+1)+"行，"+check+"/r/n");
                }
            	tractormapper.updateByPrimaryKeySelective(records.get(i));
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
			Tractor tractor=tractormapper.selectByPrimaryKey(record);
			if (tractor==null) {
				throw new BusinessException("轿运车车头不存在");
			}
			if ("/til-timp/s1/tractor/audit".equals(path)) {
				Environment env=Environment.getEnv();
				tractor.setChecker(env.getUser().getUserId());
				tractor.setCheckDate(new Date());
				tractor.setBillStatus(1);
				tractormapper.updateByPrimaryKeySelective(tractor);
				
			}else if ("/til-timp/s1/tractor/unaudit".equals(path)) {
				tractor.setBillStatus(0);
				tractorService.removeDate(record);
			}
			

		
	}
	
	@Override
	@Transactional
	public PageInfo<Tractor> getReferenceList(Map<String, Object> map,int pageNum,int pageSize) {
		log.debug("getReferenceList: {}, page: {},{}", pageNum,pageSize);
		PageHelper.startPage(pageNum, pageSize);
		List<Tractor> list=tractormapper.getReferenceList(map);
		return new PageInfo<Tractor>(list);
	}
	
	//新增
	@Override
	@Transactional
    public void add(Tractor record) {
        log.debug("add: {}",record);
        StringBuilder s;
        try {
            s = checkNewRecord(record);
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            record.setTractorCode(DateUtil.dateToStringCode(new Date()));
            record.setBillStatus(0);
            Environment env=Environment.getEnv();
            record.setCreator(env.getUser().getUserId());
            record.setCreateDate(new Date());
            record.setCorpId(env.getCorp().getCorpId());
            tractormapper.insertSelective(record);
        } catch (Exception e) {
        	if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新提交！",e);
            } else if(e instanceof BusinessException){
                throw e;
            }else {
                throw new RuntimeException("新增轿运车车头失败");
            }
        }
    }
	
	
	//部分更新
	@Override
	@Transactional
    public void updateSelective(Tractor record) {
        log.debug("updateSelective: {}",record);
        	StringBuilder s = new StringBuilder();
        try {
                s = s.append(CheckUtils.checkLong("NO", "tractorId", "轿运车车头ID", record.getTractorId(), 20));
                if (!StringUtils.isBlank(s)) {
                    throw new BusinessException("数据检查失败：" + s.toString());
                }
                Tractor tractor=tractormapper.selectByPrimaryKey(record.getTractorId());
                if (tractor==null) {
                	throw new BusinessException("轿运车车头信息不存在!");
    			}
    			if (tractor.getBillStatus()==1){
                    throw new BusinessException("请先取消审核,在修改轿运车车头信息!");
                }
    			record.setTractorCode(null);
                tractormapper.updateByPrimaryKeySelective(record);
            
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
    public Tractor queryById(Long id) {
        log.debug("query: {}",id);
        StringBuilder s = new StringBuilder();
        try {
            s = s.append(CheckUtils.checkLong("NO", "tractorId", "轿运车车头ID", id, 20));
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }

            return tractormapper.selectByPrimaryKey(id);
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
            s = s.append(CheckUtils.checkLong("NO", "tractorId", "轿运车车头ID", id, 20));
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }

            tractormapper.deleteByPrimaryKey(id);
        } catch (Exception e) {
        	throw new BusinessException(e.getMessage());
        }
        
    }

	@Override
	@Transactional
	public void removeDate(Long tractorId) {
		 try {
			Tractor tractor=tractormapper.selectByPrimaryKey(tractorId);
			if (tractor==null) {
				throw new BusinessException("轿运车车头不存在");
			}
			tractormapper.removeDate(tractorId);
		} catch (Exception e) {
			throw e;
		}
		
	}

	@Override
	@Transactional
	public void unaudit(Long record) {
			
			
			if (null==record) {
				throw new BusinessException("参数为空");
			}
			Tractor tractor=tractormapper.selectByPrimaryKey(record);
			if (tractor==null) {
				throw new BusinessException("轿运车车头不存在");
			}
			
			tractor.setBillStatus(0);
			tractorService.removeDate(record);
		
	}

	@Override
	@Transactional
	public void audit(Long record) {
			
		if (null==record) {
			throw new BusinessException("参数为空");
		}
		Tractor tractor=tractormapper.selectByPrimaryKey(record);
		if (tractor==null) {
			throw new BusinessException("轿运车车头不存在");
		}
		Environment env=Environment.getEnv();
		tractor.setChecker(env.getUser().getUserId());
		tractor.setCheckDate(new Date());
		tractor.setBillStatus(1);
		tractormapper.updateByPrimaryKeySelective(tractor);
		
	}
	
	@Override
 	@Transactional
    public PageInfo<Tractor> queryList(Map<String, Object> map, int pageNum, int pageSize) {
        log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
        try {
            if (map!=null){
                DateUtil.addTime(map);
            }
            return new PageInfo<Tractor>(tractormapper.selectList(map));
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }

    }
    
    @Override
    @Transactional
    public List<Tractor> queryList(Map<String, Object> map) {
        log.debug("queryList: {}", map);
        return tractormapper.selectList(map);
    }

}
