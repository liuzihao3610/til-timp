package com.tilchina.timp.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.enums.FlagStatus;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.GroupMapper;
import com.tilchina.timp.model.Group;
import com.tilchina.timp.model.Unit;
import com.tilchina.timp.service.GroupService;
import com.tilchina.timp.util.CheckUtil;
import com.tilchina.timp.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
* 角色档案
*
* @version 1.1.0
* @author Xiahong
*/
@Service
@Slf4j
public class GroupServiceImpl /*extends BaseServiceImpl<Group>*/ implements GroupService {

	@Autowired
    private GroupMapper groupmapper;
	
	protected BaseMapper<Group> getMapper() {
		return groupmapper;
	}
	
	protected StringBuilder checkNewRecord(Group group) {
		StringBuilder s = new StringBuilder();
		if ("".equals(group.getGroupCode()) || group.getGroupCode() == null){
			group.setGroupCode(DateUtil.dateToStringCode(new Date()));
		}
		group.setCreateDate(new Date());
        s.append(CheckUtils.checkString("NO", "groupCode", "角色编码", group.getGroupCode(), 20));
        s.append(CheckUtils.checkString("NO", "groupName", "角色名称", group.getGroupName(), 20));
        s.append(CheckUtils.checkDate("NO", "createDate", "创建时间", group.getCreateDate()));
        s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", group.getFlag(), 10));
		return s;
	}

	/**
	 * 去除首尾空格
	 * @param group
	 * @return
	 */
	protected Group checkTrim(Group group) {
		CheckUtil.checkStringTrim(group.getGroupName());
		CheckUtil.checkStringTrim(group.getGroupCode());
		return group;
	}

	protected StringBuilder checkUpdate(Group group) {
        StringBuilder s = checkNewRecord(group);
        s.append(CheckUtils.checkPrimaryKey(group.getGroupId()));
		return s;
	}
	
	@Override
    public void add(Group record) {
        log.debug("add: {}",record);
        StringBuilder s;
        Environment env = Environment.getEnv();
        try {
        	 s = checkNewRecord(record);
            if (!StringUtils.isBlank(s)) {
                 throw new BusinessException("数据检查失败：" + s.toString());
            }
            record.setFlag(FlagStatus.No.getIndex());
            record.setCreator(env.getUser().getUserId());
			record.setCorpId(env.getCorp().getCorpId());
			record = checkTrim(record);
            getMapper().insertSelective(record);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新提交！", e);
            } else if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
        }
    }

	@Override
	public void add(List<Group> records) {
		log.debug("addBatch: {}",records);
		StringBuilder s = new StringBuilder();
		boolean checkResult = true;
		List<Group> groups = new ArrayList<>();
		try {
            Environment env = Environment.getEnv();
			for (int i = 0; i < records.size(); i++) {
				Group record = records.get(i);
				StringBuilder check = checkNewRecord(record);
				if(!StringUtils.isBlank(check)){
					checkResult = false;
					s.append("第"+(i+1)+"行，"+check+"/r/n");
				}
                record.setFlag(FlagStatus.No.getIndex());
                record.setCreator(env.getUser().getUserId());
                record.setCorpId(env.getCorp().getCorpId());
				record = checkTrim(record);
				groups.add(record);
			}
			if (!checkResult) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}

			getMapper().insert(records);
		} catch (Exception e) {
			if (e.getMessage().indexOf("IDX_") != -1) {
				throw new BusinessException("数据重复，请查证后重新提交！", e);
			} else if (e instanceof BusinessException) {
				throw e;
			} else {
				throw new RuntimeException("操作失败");
			}
		}
	}

	@Override
	public void logicDeleteById(Long id) {
		log.debug("logicDeleteById: {}",id);
		StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "data", "角色ID", id, 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
   		    queryById(id);
			groupmapper.logicDeleteByPrimaryKey(id);
		} catch (Exception e) {
            if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
        }
	}


	@Override
	public void logicDeleteByIdList(int[] ids) {
		log.debug("logicDeleteById: {}",ids);
		StringBuilder s;
        try {
        	if(ids.length > 0) {
        		s = new StringBuilder();
	        	for (int id : ids) {

		    		s.append(CheckUtils.checkInteger("NO", "data", "角色ID", id, 20));
		   		    if (!StringUtils.isBlank(s)) {
		                throw new BusinessException("数据检查失败：" + s.toString());
		            }
		   		    queryById(Long.valueOf(id));
	    		}
	        	groupmapper.logicDeleteByPrimaryKeyList(ids);
        	}else {
        		throw new BusinessException("9001","角色ID");
        	}
		} catch (Exception e) {
            if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
        }
	}
	
	@Override
    public Group queryById(Long id) {
        log.debug("query: {}",id);
        Group group = null;
        StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "data", "角色ID", id, 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            group = getMapper().selectByPrimaryKey(id); 
            if(group == null) {
   		    	throw new BusinessException("9008","角色ID");
   		    }
		} catch (Exception e) {
            if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
        }
        return group;
    }

	@Override
	public PageInfo<Group> queryList(Map<String, Object> map, int pageNum, int pageSize) {
		log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
		PageHelper.startPage(pageNum, pageSize);
		return new PageInfo<Group>(getMapper().selectList(map));
	}

	@Override
	public List<Group> queryList(Map<String, Object> map) {
		log.debug("queryList: {}", map);
		return getMapper().selectList(map);
	}

	@Override
	public Map<String, Object> queryDynamicList() {
		Map<String, Object>  map = new HashMap<>();
		List<Group> list = null;
        try {
        	list = groupmapper.selectDynamicList(); 
        	for (Group group : list) {
        		map.put(group.getGroupId().toString(), group.getGroupName());
			}
		} catch (Exception e) {
            if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
        }
        return map;
    }

	@Override
	public PageInfo<Group> getRefer(Map<String, Object> map, int pageNum, int pageSize) {
		log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
		PageHelper.startPage(pageNum, pageSize);
		return new PageInfo<Group>(groupmapper.selectRefer(map));
	}

	@Override
    public void updateSelective(Group record) {
        log.debug("updateSelective: {}",record);
        StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "groupId", "角色ID", record.getGroupId(), 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
        	queryById(record.getGroupId());
            getMapper().updateByPrimaryKeySelective(record);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新提交！", e);
            } else if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
        }
    }

	@Override
	public void update(Group record) {
		log.debug("update: {}",record);
		StringBuilder s;
		try {
			s = checkUpdate(record);
			if (!StringUtils.isBlank(s)) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}

			getMapper().updateByPrimaryKey(record);
		} catch (Exception e) {
			if (e.getMessage().indexOf("IDX_") != -1) {
				throw new RuntimeException("数据重复，请查证后重新提交！", e);
			} else {
				throw e;
			}
		}
	}

	@Override
	public void update(List<Group> groups) {
		log.debug("updateBatch: {}",groups);
		StringBuilder s = new StringBuilder();
		boolean checkResult = true;
		try {
			for (int i = 0; i < groups.size(); i++) {
				Group group = groups.get(i);
				StringBuilder check = checkUpdate(group);
				if(!StringUtils.isBlank(check)){
					checkResult = false;
					s.append("第"+(i+1)+"行，"+check+"/r/n");
				}
			}
			if (!checkResult) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}

			getMapper().update(groups);
		} catch (Exception e) {
			if (e.getMessage().indexOf("IDX_") != -1) {
				throw new RuntimeException("数据重复，请查证后重新提交！", e);
			} else {
				throw e;
			}
		}

	}

	@Override
	public void deleteById(Long id) {
		log.debug("delete: {}",id);
		getMapper().deleteByPrimaryKey(id);
	}

}
