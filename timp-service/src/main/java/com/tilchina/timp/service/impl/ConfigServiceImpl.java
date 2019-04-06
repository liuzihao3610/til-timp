package com.tilchina.timp.service.impl;

import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.ConfigMapper;
import com.tilchina.timp.model.Config;
import com.tilchina.timp.service.ConfigService;
import com.tilchina.timp.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
* 系统参数设置
*
* @version 1.1.0
* @author LiuShuqi
*/
@Service
@Slf4j
public class ConfigServiceImpl extends BaseServiceImpl<Config> implements ConfigService {

	@Autowired
    private ConfigMapper configmapper;
	
	@Override
	protected BaseMapper<Config> getMapper() {
		return configmapper;
	}

	@Override
	protected StringBuilder checkNewRecord(Config config) {
		StringBuilder s = new StringBuilder();
        config.setCode(DateUtil.dateToStringCode(new Date()));
        s.append(CheckUtils.checkString("YES", "code", "编码", config.getCode(), 20));
        s.append(CheckUtils.checkString("NO", "value", "值", config.getValue(), 20));
        s.append(CheckUtils.checkString("YES", "description", "描述", config.getDescription(), 100));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(Config config) {
        StringBuilder s = checkNewRecord(config);
        s.append(CheckUtils.checkPrimaryKey(config.getConfigId()));
		return s;
	}
	
	@Override
	@Transactional
    public void add(Config record) {
        log.debug("add: {}",record);
        StringBuilder s;
        try {
            s = checkNewRecord(record);
            if (!StringUtils.isBlank(s)) {
                throw new RuntimeException("数据检查失败：" + s.toString());
            }
            Environment env=Environment.getEnv();
            record.setCorpId(env.getCorp().getCorpId());
            configmapper.insertSelective(record);
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
	public void updateById(Long id) {
		if (null==id || 0==id.longValue()) {
			throw new BusinessException(LanguageUtil.getMessage("9999"));
		}
		configmapper.updateById(id);
		
	}

	@Override
	@Transactional
	public void deleteList(int[] data) {
		if (null==data || 0==data.length) {
			throw new RuntimeException(LanguageUtil.getMessage("9999"));
		}
		configmapper.deleteList(data);
		
	}
	
	@Override
	@Transactional
    public void update(List<Config> records) {
        log.debug(LanguageUtil.getMessage("9999"),records);
        StringBuilder s = new StringBuilder();
        boolean checkResult = true;
        try {
        	if (null==records || 0==records.size()) {
    			throw new BusinessException(LanguageUtil.getMessage("9999"));
    		}
        	for (int i = 0; i < records.size(); i++) {
        		Config record = records.get(i);
                StringBuilder check = checkUpdate(record);
                if(!StringUtils.isBlank(check)){
                    checkResult = false;
                    s.append("第"+(i+1)+"行，"+check+"/r/n");
                }
            	configmapper.updateByPrimaryKeySelective(records.get(i));
            }
        	if (!checkResult) {
                throw new RuntimeException("数据检查失败：" + s.toString());
            }
            
        } catch (Exception e) {
        	throw new RuntimeException(LanguageUtil.getMessage("9999"), e);
            
        }

    }
	
	
	//部分更新
	@Override
	@Transactional
    public void updateSelective(Config record) {
        log.debug("updateSelective: {}",record);
        try {
        	StringBuilder s = new StringBuilder();
            try {
                s = s.append(CheckUtils.checkLong("NO", "configId", "系统参数ID", record.getConfigId(), 20));
                if (!StringUtils.isBlank(s)) {
                    throw new RuntimeException("数据检查失败：" + s.toString());
                }
                Config config=configmapper.selectByPrimaryKey(record.getConfigId());
                if (config==null) {
                	throw new BusinessException("这条记录不存在!");
    			}
                record.setCode(null);
                configmapper.updateByPrimaryKeySelective(record);
            } catch (Exception e) {
            	throw new RuntimeException(e.getMessage());
            }
            
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
    public Config queryById(Long id) {
        log.debug("query: {}",id);
        StringBuilder s = new StringBuilder();
        try {
            s = s.append(CheckUtils.checkLong("NO", "configId", "系统参数ID", id, 20));
            if (!StringUtils.isBlank(s)) {
                throw new RuntimeException("数据检查失败：" + s.toString());
            }

            return configmapper.selectByPrimaryKey(id);
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
            s = s.append(CheckUtils.checkLong("NO", "configId", "系统参数ID", id, 20));
            if (!StringUtils.isBlank(s)) {
                throw new RuntimeException("数据检查失败：" + s.toString());
            }

            configmapper.deleteByPrimaryKey(id);
        } catch (Exception e) {
        	throw new RuntimeException(e.getMessage());
        }
        
    }
}
