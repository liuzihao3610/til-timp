package com.tilchina.timp.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.timp.model.Group;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.UserGroup;
import com.tilchina.timp.service.UserGroupService;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.UserGroupMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* 用户关系表
*
* @version 1.0.0
* @author Xiahong
*/
@Service
@Slf4j
public class UserGroupServiceImpl/* extends BaseServiceImpl<UserGroup>*/ implements UserGroupService {

	@Autowired
    private UserGroupMapper usergroupmapper;
	
	protected BaseMapper<UserGroup> getMapper() {
		return usergroupmapper;
	}

	protected StringBuilder checkNewRecord(UserGroup usergroup) {
		StringBuilder s = new StringBuilder();
		return s;
	}

	protected StringBuilder checkUpdate(UserGroup usergroup) {
        StringBuilder s = checkNewRecord(usergroup);
        s.append(CheckUtils.checkPrimaryKey(usergroup.getUserGroupId()));
		return s;
	}
	
	@Override
    public void add(UserGroup record) {
        log.debug("add: {}",record);
        StringBuilder s;
        Environment env = Environment.getEnv();
        try {
            s = checkNewRecord(record);
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            record.setCorpId(env.getCorp().getCorpId());
            getMapper().insertSelective(record);
        } catch (Exception e) {
			log.error("用户角色关系档案添加失败！",e);
			if (e.getMessage().indexOf("IDX_") != -1) {
				throw new BusinessException("数据重复，请查证后重新保存！",e);
			} else if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败");
			}
		}
    }

	@Override
	public void add(List<UserGroup> userGroups) {
		log.debug("addBatch: {}",userGroups);
		StringBuilder s = new StringBuilder();
		boolean checkResult = true;
		try {
			for (int i = 0; i < userGroups.size(); i++) {
				UserGroup userGroup = userGroups.get(i);
				StringBuilder check = checkNewRecord(userGroup);
				if(!StringUtils.isBlank(check)){
					checkResult = false;
					s.append("第"+(i+1)+"行，"+check+"/r/n");
				}
			}
			if (!checkResult) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}

			getMapper().insert(userGroups);
		} catch (Exception e) {
			log.error("用户角色关系档案添加失败！",e);
			if (e.getMessage().indexOf("IDX_") != -1) {
				throw new BusinessException("数据重复，请查证后重新保存！",e);
			} else if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败");
			}
		}
	}

	@Override
	public void updateSelective(UserGroup userGroup) {
		log.debug("updateSelective: {}",userGroup);
		try {
			getMapper().updateByPrimaryKeySelective(userGroup);
		} catch (Exception e) {
			log.error("用户角色关系档案修改失败！",e);
			if (e.getMessage().indexOf("IDX_") != -1) {
				throw new BusinessException("数据重复，请查证后重新保存！",e);
			} else if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败");
			}
		}
	}

	@Override
	public void update(UserGroup userGroup) {
		log.debug("update: {}",userGroup);
		StringBuilder s;
		try {
			s = checkUpdate(userGroup);
			if (!StringUtils.isBlank(s)) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}

			getMapper().updateByPrimaryKey(userGroup);
		} catch (Exception e) {
			log.error("用户角色关系档案修改失败！",e);
			if (e.getMessage().indexOf("IDX_") != -1) {
				throw new BusinessException("数据重复，请查证后重新保存！",e);
			} else if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败");
			}
		}
	}

	@Override
	public void update(List<UserGroup> userGroups) {
		log.debug("updateBatch: {}",userGroups);
		StringBuilder s = new StringBuilder();
		boolean checkResult = true;
		try {
			for (int i = 0; i < userGroups.size(); i++) {
				UserGroup userGroup = userGroups.get(i);
				StringBuilder check = checkUpdate(userGroup);
				if(!StringUtils.isBlank(check)){
					checkResult = false;
					s.append("第"+(i+1)+"行，"+check+"/r/n");
				}
			}
			if (!checkResult) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}

			getMapper().update(userGroups);
		} catch (Exception e) {
			log.error("用户角色关系档案修改失败！",e);
			if (e.getMessage().indexOf("IDX_") != -1) {
				throw new BusinessException("数据重复，请查证后重新保存！",e);
			} else if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败");
			}
		}

	}

	@Override
	public void deleteById(Long id) {
		log.debug("delete: {}",id);
		getMapper().deleteByPrimaryKey(id);
	}

	@Override
	public void logicDeleteById(Long id) {
		log.debug("logicDeleteById: {}",id);
		try {
			if( id != null) {
				usergroupmapper.logicDeleteByPrimaryKey(id);
			}else {
				throw new BusinessException("9001","用户角色ID");
			}
		} catch (Exception e) {
			throw new RuntimeException("删除失败！"+e.getMessage(), e);
		}
	}
	
	@Override
    public UserGroup queryById(Long id) {
        log.debug("query: {}",id);
        UserGroup userGroup = null;
        try {
            if(id != null) {
            	userGroup = getMapper().selectByPrimaryKey(id); 
            }else {
            	throw new BusinessException("9001","用户角色ID");
            }
		} catch (Exception e) {
			if(e.getMessage().indexOf("不能为空") != -1) {
				throw new BusinessException("9006",e,"用户角色档案",e.getMessage());
			}else {
				throw new RuntimeException("查询失败！"+e.getMessage(), e);
			}
		}
        return userGroup;
    }

	@Override
	public PageInfo<UserGroup> queryList(Map<String, Object> map, int pageNum, int pageSize) {
		log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
		PageHelper.startPage(pageNum, pageSize);
		return new PageInfo<UserGroup>(getMapper().selectList(map));
	}

	@Override
	public List<UserGroup> queryList(Map<String, Object> map) {
		log.debug("queryList: {}", map);
		return getMapper().selectList(map);

	}

	@Override
	public UserGroup queryByUserId(Long userId) {
		 log.debug("queryByUserId: {}",userId);
    	 StringBuilder s;
    	 UserGroup userGroup = null;
    	 try {
         	s = new StringBuilder();
         	s.append(CheckUtils.checkLong("NO", "userId", "用户ID", userId, 20));
    		    if (!StringUtils.isBlank(s)) {
                 throw new BusinessException("数据检查失败：" + s.toString());
             }
    		 userGroup = usergroupmapper.selectByUserId(userId);
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
	    }
        return userGroup;
    }

    @Transactional
	@Override
	public void allotUser(Map<String, Object> map) {
		try {
			if (map == null){
				throw new BusinessException("9003");
			}
			if (map.get("groupId") == null && map.get("userIds") == null){
				throw new BusinessException("9003");
			}
			// 查询选中角色的全部用户
			Long groupId = Long.valueOf(map.get("groupId").toString());
			List<Long> userIds = JSON.parseArray(map.get("userIds").toString()).toJavaList(Long.class);
			userIds = userIds.stream().distinct().collect(Collectors.toList());
			Environment environment = Environment.getEnv();
			List<UserGroup> userGroups = queryByGroupId(groupId);
			// 删除添加重复的id，再插入已勾选的数据；
			List<Long> queryUserIds = new ArrayList<Long>();
			userGroups.forEach(userGroup -> queryUserIds.add(userGroup.getUserId()));
			List<Long> addUserIds = new ArrayList<Long>();
			addUserIds.addAll(userIds);
			addUserIds.addAll(queryUserIds);
			List<Long> collect = addUserIds.stream().distinct().collect(Collectors.toList());
			queryUserIds.forEach(userId -> deleteByUserId(userId));
			List<UserGroup> userGroupList = new ArrayList<UserGroup>();
			collect.forEach(userId ->{
				UserGroup userGroup = new UserGroup();
				userGroup.setUserId(userId);
				userGroup.setGroupId(groupId);
				userGroup.setCorpId(environment.getCorp().getCorpId());
				userGroupList.add(userGroup);
			});
			add(userGroupList);
		}catch (Exception e){
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
	}

	@Override
	public List<UserGroup> queryByGroupId(Long groupId) {
		return usergroupmapper.selectByGroupId(groupId);
	}

	@Override
	public void deleteByUserId(Long id) {
		log.debug("deleteByUserId: {}",id);
		try {
			if( id != null) {
				usergroupmapper.deleteByUserId(id);
			}else {
				throw new BusinessException("9001","用户ID");
			}
		} catch (Exception e) {
			throw new RuntimeException("删除失败！"+e.getMessage(), e);
		}
	}

}
