package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.DeptMapper;
import com.tilchina.timp.model.Dept;
import com.tilchina.timp.service.DeptService;
import com.tilchina.timp.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* 部门档案
*
* @version 1.1.0
* @author XiaHong
*/
@Service
@Slf4j
public class DeptServiceImpl extends BaseServiceImpl<Dept> implements DeptService {

	@Autowired
    private DeptMapper deptmapper;

/*	@Autowired
	private*/
	
	@Override
	protected BaseMapper<Dept> getMapper() {
		return deptmapper;
	}

	@Override
	protected StringBuilder checkNewRecord(Dept dept) {
		StringBuilder s = new StringBuilder();
		dept.setDeptCode(DateUtil.dateToStringCode(new Date()));
		dept.setCreateDate(new Date());
        s.append(CheckUtils.checkString("NO", "deptCode", "部门编码", dept.getDeptCode(), 20));
        s.append(CheckUtils.checkString("NO", "deptName", "部门名称", dept.getDeptName(), 20));
        s.append(CheckUtils.checkDate("NO", "createDate", "创建时间", dept.getCreateDate()));
        s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", dept.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(Dept dept) {
        StringBuilder s = checkNewRecord(dept);
        s.append(CheckUtils.checkPrimaryKey(dept.getDeptId()));
		return s;
	}
	
	@Override
    public void add(Dept record) {
        log.debug("add: {}",record);
        Environment env = Environment.getEnv();
        StringBuilder s;
        try {
            s = checkNewRecord(record);
            if (!StringUtils.isBlank(s)) {
                 throw new BusinessException("数据检查失败：" + s.toString());
            }
            record.setCreator(env.getUser().getUserId());
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
	public void logicDeleteByIdList(int[] ids) {
		log.debug("logicDeleteById: {}",ids);
		StringBuilder s;
        try {
        	if(ids.length > 0) {
        		s = new StringBuilder();
	        	for (int id : ids) {
		    		s.append(CheckUtils.checkInteger("NO", "data", "部门ID", id, 20));
		   		    if (!StringUtils.isBlank(s)) {
		                throw new BusinessException("数据检查失败：" + s.toString());
		            }
		   		 queryById(Long.valueOf(id));
	    		}
	        	deptmapper.logicDeleteByPrimaryKeyList(ids);
        	}else {
        		throw new BusinessException("9001","部门ID");
        	}
        	
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
	}

	/**
	 * 检查该数据是否有附属记录
	 * @param dept
	 */
	private void Inspection(Dept dept) {

	}

	@Override
	public void logicDeleteById(Long id) throws Exception {
		log.debug("logicDeleteById: {}",id);
		StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "data", "部门ID", id, 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
			Dept dept = queryById(id);
			Inspection(dept);
			deptmapper.logicDeleteByPrimaryKey(id);
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
	}
	
	@Override
    public Dept queryById(Long id) {
        log.debug("query: {}",id);
        Dept dept = null;
        StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "data", "部门ID", id, 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            dept = getMapper().selectByPrimaryKey(id); 
            if(dept == null) {
   		    	throw new BusinessException("9008","部门ID");
   		    }
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
		}
        return dept;
    }
	
    @Override
    public void updateSelective(Dept record) {
        log.debug("updateSelective: {}",record);
        StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "deptId", "部门ID", record.getDeptId(), 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
        	queryById(record.getDeptId());
            getMapper().updateByPrimaryKeySelective(record);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新提交！", e);
            }else if(e instanceof BusinessException){
    				throw e;
    		}else {
    				throw new RuntimeException("操作失败！", e);
    		}
        }
    }
    
	@Override
	public Map<String, Object> queryDynamicList() {
		Map<String, Object>  map = new HashMap<>();
		List<Dept> list = null;
        try {
        	list = deptmapper.selectDynamicList(); 
        	for (Dept dept : list) {
        		map.put(dept.getDeptId().toString(), dept.getDeptName());
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
    public PageInfo<Dept> queryList(Map<String, Object> map, int pageNum, int pageSize) {
        log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<Dept> depts = null;
        try {
        	depts = new PageInfo<Dept>(getMapper().selectList(map));
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
		}
        
        return depts;
    }
    
	@Override
	public PageInfo<Dept>  getRefer(Map<String, Object> data,int pageNum, int pageSize) {
		log.debug("getRefer: {}, page: {},{}", data,pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<Dept>  depts = null;
		try {
			depts = new PageInfo<Dept> (deptmapper.selectRefer(data));
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
		}
		return depts;
	}
	
	/**
     * 查询结果按创建时间倒序排序
     */
	@Override
	@Transactional
	public PageInfo<Dept> getList(Map<String, Object> map, int pageNum, int pageSize) {
		log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
        
		try {
			PageHelper.startPage(pageNum, pageSize);
			return new PageInfo<Dept>(deptmapper.getList(map));
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public List<Dept> queryByDeptNames(List<String> deptNames) {
		return deptmapper.selectByDeptNames(deptNames);
	}

}
