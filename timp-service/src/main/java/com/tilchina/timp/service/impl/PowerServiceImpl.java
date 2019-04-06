package com.tilchina.timp.service.impl;

import com.tilchina.timp.enums.Checked;
import com.tilchina.timp.model.Group;
import com.tilchina.timp.model.Regist;
import com.tilchina.timp.model.UserGroup;
import com.tilchina.timp.service.UserGroupService;
import com.tilchina.timp.vo.RegistTreeVO;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.model.Power;
import com.tilchina.timp.service.GroupService;
import com.tilchina.timp.service.PowerService;
import com.tilchina.timp.service.RegistService;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.PowerMapper;
import org.springframework.transaction.annotation.Transactional;

/**
* 权限档案
*
* @version 1.1.0
* @author XiaHong
*/
@Service
@Slf4j
public class PowerServiceImpl extends BaseServiceImpl<Power> implements PowerService {

	@Autowired
    private PowerMapper powermapper;
	
	@Autowired
    private GroupService groupService;
	
	@Autowired
    private RegistService registService;

	@Autowired
	private UserGroupService userGroupService;

	@Override
	protected BaseMapper<Power> getMapper() {
		return powermapper;
	}

	@Override
	protected StringBuilder checkNewRecord(Power record) {
		StringBuilder s = new StringBuilder();
		return s;
	}


	@Override
	protected StringBuilder checkUpdate(Power power) {
        StringBuilder s = checkNewRecord(power);
        s.append(CheckUtils.checkPrimaryKey(power.getPowerId()));
		return s;
	}

	@Transactional
	@Override
    public void add(Power power) {
        log.debug("add: {}",power);
        StringBuilder s;
        Environment env = Environment.getEnv();
        try {
            s = checkNewRecord(power);
            if (!StringUtils.isBlank(s)) {
                throw new RuntimeException("数据检查失败：" + s.toString());
            }
            // 删除选中角色的全部权限，再插入已勾选的数据；
			Group group = Optional.ofNullable(groupService.queryById(power.getGroupId()))
									.orElseThrow(() -> new BusinessException("所选角色为无效信息，角色ID："+power.getGroupId()));
			Regist regist = registService.queryById(power.getRegistId());
			List<Power> powers = queryByGroupId(group.getGroupId());
			List<Long> longs = new ArrayList<>();
			powers.forEach(power1 -> longs.add(power1.getPowerId()));
			List<Long> collect = longs.stream().distinct().collect(Collectors.toList());
			collect.forEach(powerId -> logicDeleteById(powerId));
			getMapper().insertSelective(power);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new RuntimeException("数据重复，请查证后重新保存！", e);
            } else {
                throw new RuntimeException("保存失败！", e);
            }
        }
    }

    @Transactional
	@Override
	public void savePower(List<RegistTreeVO> registList, Long groupId) {
		try {
			if (CollectionUtils.isEmpty(registList) || groupId == null){
				throw new BusinessException("9003");
			}
			List<Long> registIds = new ArrayList<Long>();
			registList.forEach(regist -> {
				List<RegistTreeVO> children = regist.getChildren();
				if (regist.getIchecked().intValue() == Checked.TRUE.getIndex() || regist.getIndeterminate()){
									registIds.add(regist.getRegistId());
								}
								if(!CollectionUtils.isEmpty(children)){
									children.forEach(children1 ->{
										List<RegistTreeVO> children2 = children1.getChildren();
										if (children1.getIchecked().intValue() == Checked.TRUE.getIndex() || children1.getIndeterminate()){
											registIds.add(children1.getRegistId());
						}
						if(!CollectionUtils.isEmpty(children2)){
							children2.forEach(children3 ->{
								if (children3.getIchecked().intValue() == Checked.TRUE.getIndex() || children3.getIndeterminate()){
									registIds.add(children3.getRegistId());
								}
							});
						}
					});
				}
			});
			List<Power> powers = queryByGroupId(groupId);
			powers.forEach(power -> deleteById(power.getPowerId()));
			List<Power> addPowers = new ArrayList<Power>();
			Environment environment = Environment.getEnv();
			registIds.forEach(registId ->{
				Power power = new Power();
				power.setCorpId(environment.getCorp().getCorpId());
				power.setGroupId(groupId);
				power.setRegistId(registId);
				addPowers.add(power);
			});

			add(addPowers);
		}catch (Exception e){
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
	}

	@Transactional
	@Override
	public void test() {
		Environment environment =  Environment.getEnv();
		List<Regist> regists = registService.queryList(new HashMap<>());
		List<Power> powers = new ArrayList<>();
		regists.forEach(regist -> {
			Power power = new Power();
			power.setRegistId(regist.getRegistId());
			power.setGroupId(Long.valueOf(22));
			power.setCorpId(environment.getCorp().getCorpId());
			powers.add(power);
		} );
		add(powers);
	}

	/*@Override
	public List<RegistTreeVO>  allotPower(Map<String, Object> map) {
		try {
			if (map == null){
				throw new BusinessException("9003");
			}
			if (map.get("groupId") == null){
				throw new BusinessException("9003");
			}
			Environment environment = Environment.getEnv();
			// 选中角色的功能注册信息列表
			List<Power> groupPowers = queryByGroupId(Long.valueOf(map.get("groupId").toString()));
			Set<Long> groupRegistIds = new HashSet<Long>();
			groupPowers.forEach(power -> groupRegistIds.add(power.getRegistId()));
			List<Regist> groupRegistList = new ArrayList<>();
			if (!CollectionUtils.isEmpty(groupRegistIds)) {
				groupRegistList = registService.queryByRegistId(groupRegistIds);
			}
			// 当前用户功能注册信息列表
		*//*	UserGroup userGroup = Optional.ofNullable(userGroupService.queryByUserId(environment.getUser().getUserId()))
					.orElseThrow(() ->new BusinessException("此用户："+environment.getUser().getUserName()+"没有可分配的权限！"));
			List<Power> userPowers = queryByGroupId(userGroup.getGroupId());
			Set<Long> userRegistIds = new HashSet<Long>();
			userPowers.forEach(power -> userRegistIds.add(power.getRegistId()));
			List<Regist> userRegistList = registService.queryByRegistId(userRegistIds);*//*
			List<Regist> userRegistList = Optional.ofNullable( environment.getRegists()).orElse(new ArrayList<Regist>());
			if (userRegistList.size() < 1){
				throw new BusinessException("此用户："+environment.getUser().getUserName()+"没有可分配的权限！");
			}
			// 添加勾选标志：勾选选中角色拥有的权限
			Map<Long,Regist> registMap = new HashMap<Long,Regist>();
			groupRegistList.forEach(regist -> registMap.put(regist.getRegistId(),regist));

			List<RegistTreeVO> regists = new ArrayList<RegistTreeVO>();
			userRegistList.forEach(userRegist -> {
				RegistTreeVO registTreeVO = new RegistTreeVO();
				registTreeVO.setTitle(userRegist.getRegistName());
				registTreeVO.setRegistId(userRegist.getRegistId());
				if (registMap.get(userRegist.getRegistId()) != null){
					registTreeVO.setIchecked(1);
					registTreeVO.setChecked(true);
				}else {
					registTreeVO.setChecked(false);
					registTreeVO.setIchecked(0);
				}
				regists.add(registTreeVO);
			});
			return regists;
		}catch (Exception e){
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
	}*/

	@Override
	public List<Power> queryByGroupId(Long groupId) {
		return powermapper.selectByGroupId(groupId);
	}




	@Override
	public void logicDeleteById(Long id) {
		log.debug("logicDeleteById: {}",id);
		StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "data", "权限ID", id, 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
   		    queryById(id);
			powermapper.logicDeleteByPrimaryKey(id);
		} catch (Exception e) {
            if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
        }
	}
	
	@Override
    public Power queryById(Long id) {
        log.debug("query: {}",id);
        Power power = null;
        StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "data", "权限ID", id, 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            power = getMapper().selectByPrimaryKey(id);
            if(power == null) {
            	throw new BusinessException("9008","权限ID");
            }
		} catch (Exception e) {
            if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
        }
        return power;
    }

	@Override
	public PageInfo<Power> queryByGroupId(Map<String, Object> map, int pageNum, int pageSize) {
		 log.debug("queryByGroupId: {}, page: {},{}", map,pageNum,pageSize);
         PageHelper.startPage(pageNum, pageSize);
		 StringBuilder s;
		 PageInfo<Power> PowerPage = null;
		try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkString("NO", "groupId", "角色ID", map.get("groupId").toString(), 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
   		 PowerPage = new PageInfo<Power>(powermapper.selectByGroupId(map));
		} catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新提交！", e);
            } else if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
        }
		return PowerPage;
	}

}
