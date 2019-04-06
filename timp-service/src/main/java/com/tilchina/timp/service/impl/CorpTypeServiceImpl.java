package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.CorpTypeMapper;
import com.tilchina.timp.model.CorpType;
import com.tilchina.timp.service.CorpTypeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
* 公司类型
*
* @version 1.1.0
* @author LiuShuqi
*/
@Service
@Slf4j
public class CorpTypeServiceImpl extends BaseServiceImpl<CorpType> implements CorpTypeService {

	@Autowired
    private CorpTypeMapper corptypemapper;
	
	@Override
	protected BaseMapper<CorpType> getMapper() {
		return corptypemapper;
	}

	@Override
	protected StringBuilder checkNewRecord(CorpType corptype) {
		StringBuilder s = new StringBuilder();
		s.append(CheckUtils.checkLong("NO", "corpId", "公司ID", corptype.getCorpId(), 20));
        s.append(CheckUtils.checkInteger("NO", "typeKey", "类型值(0=承运公司 1=运输公司 2=客户 3=经销店)", corptype.getTypeKey(), 11));
        s.append(CheckUtils.checkString("NO", "typeName", "类型名称", corptype.getTypeName(), 20));
        s.append(CheckUtils.checkInteger("YES", "flag", "封存标志", corptype.getFlag(), 10));
        
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(CorpType corptype) {
        StringBuilder s = checkNewRecord(corptype);
        s.append(CheckUtils.checkPrimaryKey(corptype.getCorpTypeId()));
		return s;
	}
	
	@Override
 	@Transactional
    public void add(CorpType corpType) {
        log.debug("add: {}",corpType);
        StringBuilder s;
        try {
        	
            s = checkNewRecord(corpType);
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            Environment env=Environment.getEnv();
            corptypemapper.insertSelective(corpType);
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
		corptypemapper.deleteList(data);
		
	}
	
	@Override
	@Transactional
    public void update(List<CorpType> records) {
        log.debug("updateBatch: {}",records);
        StringBuilder s = new StringBuilder();
        boolean checkResult = true;
        try {
        	if (null==records || 0==records.size()) {
    			throw new BusinessException(LanguageUtil.getMessage("9999"));
    		}
        	for (int i = 0; i < records.size(); i++) {
        		CorpType record = records.get(i);
                StringBuilder check = checkUpdate(record);
                if(!StringUtils.isBlank(check)){
                    checkResult = false;
                    s.append("第"+(i+1)+"行，"+check+"/r/n");
                }
            	corptypemapper.updateByPrimaryKeySelective(records.get(i));
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
	public PageInfo<CorpType> getCorpTypeNameList(Map<String, Object> map,int pageNum,int pageSize) {
		log.debug("getCorpTypeNameList: {}, page: {},{}", pageNum,pageSize);
		PageHelper.startPage(pageNum, pageSize);
		List<CorpType> list=corptypemapper.getCorpTypeNameList(map);
		return new PageInfo<CorpType>(list);
	}
	
	//部分更新
	@Override
    public void updateSelective(CorpType record) {
        log.debug("updateSelective: {}",record);
        try {
        	StringBuilder s = new StringBuilder();
            try {
                s = s.append(CheckUtils.checkLong("NO", "corpTypeId", "公司类型ID", record.getCorpTypeId(), 20));
                if (!StringUtils.isBlank(s)) {
                    throw new BusinessException("数据检查失败：" + s.toString());
                }
                CorpType corpType=corptypemapper.selectByPrimaryKey(record.getCorpTypeId());
                if (corpType==null) {
                	throw new BusinessException("这条记录不存在!");
    			}
                corptypemapper.updateByPrimaryKeySelective(record);
            } catch (Exception e) {
            	throw new BusinessException(e.getMessage());
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
	
	//通过ID查询
	@Override
    public CorpType queryById(Long id) {
        log.debug("query: {}",id);
        StringBuilder s = new StringBuilder();
        try {
            s = s.append(CheckUtils.checkLong("NO", "corpTypeId", "公司类型ID", id, 20));
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }

            return corptypemapper.selectByPrimaryKey(id);
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
            s = s.append(CheckUtils.checkLong("NO", "corpTypeId", "公司类型ID", id, 20));
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }

            corptypemapper.deleteByPrimaryKey(id);
        } catch (Exception e) {
        	throw new BusinessException(e.getMessage());
        }
        
    }
	 
	 @Override
	public PageInfo<CorpType> getReferenceList(Map<String, Object> map,int pageNum,int pageSize) {
		log.debug("getReferenceList: {}, page: {},{}", pageNum,pageSize);
		PageHelper.startPage(pageNum, pageSize);
		List<CorpType> list=corptypemapper.getReferenceList(map);
		return new PageInfo<CorpType>(list);
	}

	@Override
	public CorpType queryByCorpId(Long corpCarrierId) {
		
		return corptypemapper.queryByCorpId(corpCarrierId);
	}
	
}
