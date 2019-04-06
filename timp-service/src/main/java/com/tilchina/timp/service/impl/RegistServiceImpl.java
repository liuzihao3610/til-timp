package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.BeanUtils;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.enums.Checked;
import com.tilchina.timp.enums.FlagStatus;
import com.tilchina.timp.enums.RegistType;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.RegistMapper;
import com.tilchina.timp.model.Power;
import com.tilchina.timp.model.Regist;
import com.tilchina.timp.service.PowerService;
import com.tilchina.timp.service.RegistService;
import com.tilchina.timp.service.UserService;
import com.tilchina.timp.util.CheckUtil;
import com.tilchina.timp.vo.RegistTreeVO;
import com.tilchina.timp.vo.RegistVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
* 
*
* @version 1.1.0
* @author XiaHong
*/
@Service
@Slf4j
public class RegistServiceImpl /*extends BaseServiceImpl<Regist>*/ implements RegistService {

	@Autowired
    private RegistMapper registmapper;
	
	@Autowired
    private	UserService userService;

	@Autowired
	private PowerService powerService;

	protected BaseMapper<Regist> getMapper() {
		return registmapper;
	}

	protected StringBuilder checkNewregist(Regist regist) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("NO", "regisytCode", "功能编码", regist.getRegistCode(), 50));
        s.append(CheckUtils.checkString("NO", "registName", "功能名称", regist.getRegistName(), 50));
        s.append(CheckUtils.checkString("YES", "urlPath", "URL地址", regist.getUrlPath(), 50));
        s.append(CheckUtils.checkInteger("NO", "registType", "功能类型,0=虚拟节点,1=功能节点,2=按扭", regist.getRegistType(), 10));
        s.append(CheckUtils.checkString("YES", "icon", "图标", regist.getIcon(), 50));
        s.append(CheckUtils.checkInteger("NO", "sequence", "显示顺序", regist.getSequence(), 10));
        s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", regist.getFlag(), 10));
		return s;
	}

	/**
	 * 去除首尾空格
	 * @param regist
	 * @return
	 */
	protected Regist checkTrim(Regist regist) {
		try {
			CheckUtil.checkStringTrim(regist.getRegistCode());
			CheckUtil.checkStringTrim(regist.getRegistName());
			CheckUtil.checkStringTrim(regist.getUrlPath());
			CheckUtil.checkStringTrim(regist.getIcon());
			if (regist.getUpRegistId() != null){
				Regist upRegist = queryById(regist.getUpRegistId());
				if (upRegist.getRegistId() != null && regist.getRegistId() != null){
					if (upRegist.getRegistId().longValue() == regist.getRegistId().longValue()){
						throw new BusinessException("功能注册档案中功能类型不能与上级节点的功能类型重复。当前功能类型："+RegistType.getName(regist.getRegistType())+",上级节点功能类型："+RegistType.getName(upRegist.getRegistType()));
					}
				}
			}
		}catch (Exception e){
			if (e instanceof BusinessException) {
				throw e;
			} else {
				throw new RuntimeException("操作失败");
			}
		}
		return regist;
	}
	
	protected StringBuilder checkUpdate(Regist regist) {
        StringBuilder s = checkNewregist(regist);
        s.append(CheckUtils.checkPrimaryKey(regist.getRegistId()));
		return s;
	}

	@Override
	public void add(Regist regist) {
		log.debug("add: {}",regist);
		StringBuilder s;
		try {
			s = checkNewregist(regist);
			Environment environment = Environment.getEnv();
			if (!StringUtils.isBlank(s)) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}
			regist.setFlag(FlagStatus.No.getIndex());
			regist.setCorpId(environment.getCorp().getCorpId());
			regist = checkTrim(regist);
			getMapper().insertSelective(regist);
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
			List<Power> groupPowers = powerService.queryByGroupId(Long.valueOf(map.get("groupId").toString()));
			Set<Long> groupRegistIds = new HashSet<Long>();
			groupPowers.forEach(power -> groupRegistIds.add(power.getRegistId()));
			List<Regist> groupRegistList = new ArrayList<>();
			if (!CollectionUtils.isEmpty(groupRegistIds)) {
				groupRegistList = queryByRegistId(groupRegistIds);
			}
			// 当前用户功能注册信息列表
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
				registTreeVO.setRegistType(userRegist.getRegistType());
				registTreeVO.setSequence(userRegist.getSequence());
				registTreeVO.setUpRegistId(userRegist.getUpRegistId());
				if (registMap.get(userRegist.getRegistId()) != null){
					registTreeVO.setIchecked(1);
					registTreeVO.setChecked(true);
				}else {
					registTreeVO.setChecked(false);
					registTreeVO.setIchecked(0);
				}
				regists.add(registTreeVO);
			});
			List<RegistTreeVO> registstTree = transitionTreeJson(regists);
			return registstTree;
		}catch (Exception e){
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
	}

	@Override
	public void add(List<Regist> regists) {
		log.debug("addBatch: {}",regists);
		StringBuilder s = new StringBuilder();
		boolean checkResult = true;
		List<Regist> registList = new ArrayList<>();
		try {
			Environment environment = Environment.getEnv();
			for (int i = 0; i < regists.size(); i++) {
				Regist regist = regists.get(i);
				regist = checkTrim(regist);
				StringBuilder check = checkNewregist(regist);
				if(!StringUtils.isBlank(check)){
					checkResult = false;
					s.append("第"+(i+1)+"行，"+check+"/r/n");
				}
				regist.setFlag(FlagStatus.No.getIndex());
				regist = checkTrim(regist);
				regist.setCorpId(environment.getCorp().getCorpId());
				registList.add(regist);
			}
			if (!checkResult) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}
			getMapper().insert(registList);
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
	public void updateSelective(Regist regist) {
		log.debug("updateSelective: {}",regist);
		try {
			getMapper().updateByPrimaryKeySelective(regist);
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
	public void update(Regist regist) {
		log.debug("update: {}",regist);
		StringBuilder s;
		try {
			s = checkUpdate(regist);
			if (!StringUtils.isBlank(s)) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}

			getMapper().updateByPrimaryKey(regist);
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
	public void update(List<Regist> regists) {
		log.debug("updateBatch: {}",regists);
		StringBuilder s = new StringBuilder();
		boolean checkResult = true;
		try {
			for (int i = 0; i < regists.size(); i++) {
				Regist regist = regists.get(i);
				StringBuilder check = checkUpdate(regist);
				if(!StringUtils.isBlank(check)){
					checkResult = false;
					s.append("第"+(i+1)+"行，"+check+"/r/n");
				}
			}
			if (!checkResult) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}

			getMapper().update(regists);
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
	public void deleteById(Long id) {
		log.debug("delete: {}",id);
		getMapper().deleteByPrimaryKey(id);
	}

	@Override
	public void logicDeleteByIdList(int[] ids) {
		log.debug("logicDeleteById: {}",ids);
		StringBuilder s;
        try {
        	if(ids.length > 0) {
        		s = new StringBuilder();
	        	for (int id : ids) {
		    		s.append(CheckUtils.checkInteger("NO", "data", "功能ID", id, 20));
		   		    if (!StringUtils.isBlank(s)) {
		                throw new BusinessException("数据检查失败：" + s.toString());
		            }
	    		}
	        	registmapper.logicDeleteByPrimaryKeyList(ids);
        	}else {
        		throw new BusinessException("9001","功能ID");
        	}

		} catch (Exception e) {
			if(e.getMessage().indexOf("不能为空") != -1) {
				throw new BusinessException("9004",e,"功能注册",e.getMessage());
			}else {
				throw new RuntimeException("删除失败！", e);
			}
		}
	}

	@Override
	public void logicDeleteById(Long id) {
		log.debug("logicDeleteById: {}",id);
		StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "data", "功能ID", id, 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
			registmapper.logicDeleteByPrimaryKey(id);
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
	}

	@Override
    public Regist queryById(Long id) {
        log.debug("query: {}",id);
        Regist regist = null;
        StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "data", "功能ID", id, 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
   		   regist = getMapper().selectByPrimaryKey(id);
   		   if(regist == null) {
  		    	throw new BusinessException("9008","功能ID");
   		   }
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
        return regist;
    }


	@Override
	public PageInfo<Regist> queryList(Map<String, Object> map, int pageNum, int pageSize) {
		log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
		PageHelper.startPage(pageNum, pageSize);
		return new PageInfo<Regist>(getMapper().selectList(map));
	}

	@Override
	public List<Regist> queryList(Map<String, Object> map) {
		log.debug("queryList: {}", map);
		return getMapper().selectList(map);
	}

	@Override
	public Map<String, Object> queryDynamicList() {
		Map<String, Object>  map = new HashMap<>();
		List<Regist> list = null;
        try {

        	list = registmapper.selectDynamicList();
        	for (Regist regist : list) {
        		map.put(regist.getRegistId().toString(), regist.getRegistName());
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
	public List<Regist> queryTreeByUserId(Long userId) {
		List<Regist> regists = null;
		StringBuilder s;
		try {
			s = new StringBuilder();
			s.append(CheckUtils.checkLong("NO", "data", "功能ID", userId, 20));
			if (!StringUtils.isBlank(s)) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}
			userService.queryById(userId);
			regists = registmapper.selectByUserId(userId);
			regists = transitionJson(regists);
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
		return regists;
	}

	@Override
    public List<Regist> queryByUserId(Long userId){
    	List<Regist> regists = null;
    	StringBuilder s;
    	try {
    		s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "data", "功能ID", userId, 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
   		    userService.queryById(userId);
    		regists = registmapper.selectByUserId(userId);
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
		return regists;
	}

	@Override
	public PageInfo<Regist> getRefer(Map<String, Object> map, int pageNum, int pageSize) {
		log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
		PageHelper.startPage(pageNum, pageSize);
		return new PageInfo<Regist>(registmapper.selectRefer(map));
	}

	/**
	 * 转换成前端所需要的json格式
	 * @param registList
	 * @return
	 */
	public List<RegistTreeVO> transitionTreeJson(List<RegistTreeVO> registList){
		try {
			List<RegistTreeVO> vintualList = new ArrayList<RegistTreeVO>();
			List<RegistTreeVO> functionsList = new ArrayList<RegistTreeVO>();
			List<RegistTreeVO> buttonList = new ArrayList<RegistTreeVO>();
			// 取出不同功能类型的功能注册信息
			registList.forEach(regist -> {
				if (regist.getRegistType().intValue() == RegistType.VIRTUAL.getIndex()){
					vintualList.add(regist);
				}else if (regist.getRegistType().intValue() == RegistType.FUNCTIONS.getIndex() && regist.getUpRegistId() != null){
					functionsList.add(regist);
				}else if (regist.getRegistType().intValue() == RegistType.BUTTON.getIndex() && regist.getUpRegistId() != null){
					buttonList.add(regist);
				}
			});
			// 按照顺序号排序：由低到高
			List<RegistTreeVO> vintualCollect = vintualList.stream().sorted((a, b) -> a.getSequence().compareTo(b.getSequence())).collect(Collectors.toList());
			List<RegistTreeVO> functionsCollect = functionsList.stream().sorted((a, b) -> a.getSequence().compareTo(b.getSequence())).collect(Collectors.toList());
			List<RegistTreeVO> buttonCollect = buttonList.stream().sorted((a, b) -> a.getSequence().compareTo(b.getSequence())).collect(Collectors.toList());
			Map<Boolean,Integer> functionsMap = new HashMap<>();
			Map<Boolean,Integer> vintualMap = new HashMap<>();
			// 取到每个节点的下级节点
			functionsCollect.stream().forEach(regist -> {
				List<RegistTreeVO> collect = buttonCollect.stream()
						.filter(regist1 -> regist1.getUpRegistId().longValue() == regist.getRegistId().longValue())
						.collect(Collectors.toList());
				collect.forEach(registTreeVO -> {
					if (registTreeVO.getIchecked().intValue() == Checked.FALSE.getIndex()){
						functionsMap.put(false,registTreeVO.getIchecked());
					}else if (registTreeVO.getIchecked().intValue() == Checked.TRUE.getIndex()){
						functionsMap.put(true,registTreeVO.getIchecked());
					}
				});
                for (RegistTreeVO registTreeVO : collect) {
                    // 子节点如果都没有选中   checked indeterminate 都为false
                    if (functionsMap.get(false) != null && functionsMap.get(true) == null ){
						regist.setIchecked(Checked.FALSE.getIndex());
						regist.setChecked(false);
                        regist.setIndeterminate(false);
                        // 如果子节点全部选中  checked = true indeterminate = fasle
                    }else if (functionsMap.get(false) == null && functionsMap.get(true) != null){
						regist.setIchecked(Checked.TRUE.getIndex());
						regist.setChecked(true);
                        regist.setIndeterminate(false);
                        // 如果子节点至少有一条为选中状态   checked = false   indeterminate = true
                    }else if (functionsMap.get(false) != null && functionsMap.get(true) != null){
						regist.setIchecked(Checked.FALSE.getIndex());
						regist.setChecked(false);
                        regist.setIndeterminate(false);
						break;

                    }
                }

				regist.setChildren(collect);
			});
			vintualCollect.stream().forEach(regist -> {
				List<RegistTreeVO> collect = functionsCollect.stream()
						.filter(regist1 -> regist1.getUpRegistId().longValue() == regist.getRegistId().longValue())
						.collect(Collectors.toList() );
				collect.forEach(registTreeVO -> {
					if (registTreeVO.getIchecked().intValue() == Checked.FALSE.getIndex()){
						vintualMap.put(false,registTreeVO.getIchecked());
					}else if (registTreeVO.getIchecked().intValue() == Checked.TRUE.getIndex()){
						vintualMap.put(true,registTreeVO.getIchecked());
					}
				});
                for (RegistTreeVO registTreeVO : collect) {
                    // 子节点如果都没有选中   checked indeterminate 都为false
                    if (functionsMap.get(false) != null && functionsMap.get(true) == null || !registTreeVO.getIndeterminate()){
						regist.setIchecked(Checked.FALSE.getIndex());
						regist.setChecked(false);
                        regist.setIndeterminate(false);
                        // 如果子节点全部选中  checked = true indeterminate = fasle
                    }else if (functionsMap.get(false) == null && functionsMap.get(true) != null|| !registTreeVO.getIndeterminate()){
						regist.setIchecked(Checked.TRUE.getIndex());
						regist.setChecked(true);
                        regist.setIndeterminate(false);
                        // 如果子节点至少有一条为选中状态   checked = false   indeterminate = true
                    }else if (functionsMap.get(false) != null && functionsMap.get(true) != null){
						regist.setIchecked(Checked.FALSE.getIndex());
						regist.setChecked(false);
                        regist.setIndeterminate(false);
						break;
                    }
                }
				regist.setChildren(collect);
			});
			return vintualCollect;
		}catch (Exception e){
			throw new RuntimeException("操作失败！", e);
		}
	}

	/**
	 * 转换成前端所需要的json格式
	 * @param registList
	 * @return
	 */
	public List<Regist> transitionJson(List<Regist> registList){
		try {
     			List<Regist> vintualList = new ArrayList<Regist>();
			List<Regist> functionsList = new ArrayList<Regist>();
			List<Regist> buttonList = new ArrayList<Regist>();
			// 取出不同功能类型的功能注册信息
			registList.forEach(regist -> {
				if (regist.getRegistType().intValue() == RegistType.VIRTUAL.getIndex()){
					regist.setTitle(regist.getRegistName());
					vintualList.add(regist);
				}else if (regist.getRegistType().intValue() == RegistType.FUNCTIONS.getIndex() && regist.getUpRegistId() != null){
					regist.setTitle(regist.getRegistName());
					functionsList.add(regist);
				}else if (regist.getRegistType().intValue() == RegistType.BUTTON.getIndex() && regist.getUpRegistId() != null){
					regist.setTitle(regist.getRegistName());
					buttonList.add(regist);
				}
			});
			// 按照顺序号排序：由低到高
			List<Regist> vintualCollect = vintualList.stream().sorted((a, b) -> a.getSequence().compareTo(b.getSequence())).collect(Collectors.toList());
			List<Regist> functionsCollect = functionsList.stream().sorted((a, b) -> a.getSequence().compareTo(b.getSequence())).collect(Collectors.toList());
			List<Regist> buttonCollect = buttonList.stream().sorted((a, b) -> a.getSequence().compareTo(b.getSequence())).collect(Collectors.toList());
			// 取到每个节点的下级节点
			functionsCollect.stream().forEach(regist -> {
				List<Regist> collect = buttonCollect.stream()
						.filter(regist1 -> regist1.getUpRegistId().longValue() == regist.getRegistId().longValue())
						.collect(Collectors.toList());
				regist.setChildren(collect);
			});
			vintualCollect.stream().forEach(regist -> {
				List<Regist> collect = functionsCollect.stream()
						.filter(regist1 -> regist1.getUpRegistId().longValue() == regist.getRegistId().longValue())
						.collect(Collectors.toList());
				regist.setChildren(collect);
			});
			return vintualCollect;
		}catch (Exception e){
			throw new RuntimeException("操作失败！", e);
		}
	}

    @Override
    public List<Regist> getRegist() {
        log.debug("getRegist: {}");
        try {
            Environment environment = Environment.getEnv();
			List<Regist> regists = Optional.ofNullable(environment.getRegists()).orElse(new ArrayList<>());
            regists = regists.stream().filter(regist -> (regist.getRegistType().intValue() == RegistType.VIRTUAL.getIndex()) || (regist.getRegistType().intValue() == RegistType.FUNCTIONS.getIndex())).collect(Collectors.toList());
			regists = transitionJson(regists);
			return regists;
        }catch (Exception e){
            throw new RuntimeException("操作失败！", e);
        }
    }

    @Override
    public List<RegistVO> getButtonRegist(Map<String, Object> data) {
        log.debug("getButtonRegist: {}",data);
        try {
            if (data.get("registCode") == null){
                throw new BusinessException("9003");
            }
			Environment env = Environment.getEnv();
            if (env.getRegists() == null){
				throw new BusinessException("登陆用户没有权限。");
			}
			List<Regist> userRegists = env.getRegists().stream()
														.filter(regist -> regist.getRegistType().intValue() == RegistType.BUTTON.getIndex())
														.collect(Collectors.toList());
			String registCode = data.get("registCode").toString();
			Regist regist = Optional.ofNullable(queryByRegistCode(registCode)).orElse(new Regist());

			List<Regist> downList = userRegists.stream()
												.filter(regist1 -> regist1.getUpRegistId() != null)
												.collect(Collectors.toList());

			downList = downList.stream()
					.filter(regist1 -> regist.getRegistId().longValue() == regist1.getUpRegistId().longValue())
					.collect(Collectors.toList());

			/*List<Regist> downList = registmapper.selectByDownList(regist.getRegistId());*/

			List<RegistVO> registVOList = new ArrayList<>();
			downList.forEach(regist1 -> {
				RegistVO info = new RegistVO();
				BeanUtils.copyProperties(regist1,info);
				registVOList.add(info);
			});
            return registVOList;
        }catch (Exception e){
            throw new RuntimeException("操作失败！", e);
        }
    }

	@Override
	public Regist queryByRegistCode(String registCode) {
		return registmapper.selectByRegistCode(registCode);
	}

	@Transactional
	@Override
	public void test() {
		log.debug("test: {}");
		try {
			List<Regist> queryRegists = registmapper.selectList(new HashMap<>());
			List<Regist> regists = new ArrayList<Regist>();
			Map<String,String> map = new HashMap<String,String>(){{
				put("add","添加");
				put("putPart","修改");
				put("put","修改");
				put("getList","查询");
				put("addUser","添加用户");
				put("removeList","批量删除");
				put("remove","删除");
				put("check","审核");
				put("cancelCheck","取消审核");

				put("deblocking","用户解锁");
				put("resetPasswords","重置密码");
				put("getReferenceList","参照");
				put("addAvatrar","添加头像地址");
				put("addPhotoPath","添加照片地址");
				put("get","根据ID查询");
				put("getByName","根据名称查询");

				put("getLogin","查看收发货单位登陆信息");
				put("importContacts","导入收发货单位联系人");
				put("importExcel","导入收发货单位");
				put("query","查询");

			}};
			for (Regist queryRegist : queryRegists) {
				String urlPath = queryRegist.getUrlPath();
				String registName = queryRegist.getRegistName();
					/*	.replaceAll("查询/查询","")
						.replaceAll("修改/修改","")
						.replaceAll("根据ID查询/根据ID查询","")
						.replaceAll("删除/删除","")
						.replaceAll("导入收发货单位/导入收发货单位","")
						.replaceAll("导入收发货单位联系人/导入收发货单位联系人","")
						.replaceAll("查看收发货单位登陆信息/查看收发货单位登陆信息","")
						.replaceAll("根据名称查询/根据名称查询","")
						.replaceAll("照片地址/照片地址","")
						.replaceAll("参照/参照","")
						.replaceAll("重置密码/重置密码","")
						.replaceAll("用户解锁/用户解锁","")
						.replaceAll("取消审核/取消审核","")
						.replaceAll("审核/审核","")
						.replaceAll("添加/添加","")
						.replaceAll("添加用户/添加用户","")
						.replaceAll("htmlhtml","")*/;
					/*queryRegist.setRegistName(registName);*/
			/*		String carnumRegex = "^[\\u4e00-\\u9fa5]{1,}";
					Pattern regex = Pattern.compile(carnumRegex);
					Matcher matcherNumber = regex.matcher(registName);
					if (matcherNumber.find()) {
						queryRegist.setRegistName(matcherNumber.group());
						updateSelective(queryRegist);
					}else {
						System.out.println(queryRegist.getRegistName());
					}
*/
				String carnumRegex = "[a-zA-z]{1,}$";
				Pattern regex = Pattern.compile(carnumRegex);
				Matcher matcherNumber = regex.matcher(urlPath);
				if (matcherNumber.find()) {
					//queryRegist.setRegistName(registName + matcherNumber.group());
					if ("html".equals(matcherNumber.group())){

					}else if (map.get(matcherNumber.group()) != null){
						queryRegist.setRegistName(registName + map.get(matcherNumber.group()));
					}else if (map.get(matcherNumber.group()) == null){
						queryRegist.setRegistName(registName + "/" +matcherNumber.group());
					}
					regists.add(queryRegist);
					update(queryRegist);
				}
			}

		}catch (Exception e){
			throw new RuntimeException("操作失败！", e);
		}
	}

	@Override
	public List<Regist> getUpReferences(Map<String, Object> data) {
		log.debug("getUpReferences: {}", data);
		try {
			// 拿到符合条件的功能注册信息
			List<Regist> registList = getMapper().selectList(data);
			List<Regist> regists = transitionJson(registList);
			return regists;
		}catch (Exception e){
			throw new RuntimeException("操作失败！", e);
		}
	}

	@Override
	public List<Regist> queryByRegistId(Set<Long> registIds) {
		try {
			// 拿到符合条件的功能注册信息
			List<Regist> registList = registmapper.selectByRegistId(registIds);
			// List<Regist> regists = transitionJson(registList);
			return registList;
		}catch (Exception e){
			throw new RuntimeException("操作失败！", e);
		}
	}

	/**
	 * @author XueYuSong
	 * 批量添加接口地址至功能注册表，勿删
	 */
	@Override
	public void addUrls() {

		try {
			File file = new File("C:\\Users\\YuSong Xue\\Desktop\\urls.txt");
			List<String> urls = new ArrayList<>();

			try(Stream<String> stream = Files.lines(Paths.get("C:\\Users\\YuSong Xue\\Desktop\\urls.txt"))) {
				urls = stream.collect(Collectors.toList());
			}

			for (String url : urls) {
				url = url.substring(3, url.length());
				String registCode = url.substring(url.lastIndexOf('/') + 1, url.length());

				Regist model = new Regist();
				model.setRegistCode(registCode);
				model.setRegistName(url);
				model.setUrlPath(url);
				model.setRegistType(2);
				model.setSequence(0);
				model.setCorpId(new Long(1));
				model.setFlag(0);
				add(model);
			}
			System.out.println();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



}
