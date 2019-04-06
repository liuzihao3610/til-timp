package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.PositionMapper;
import com.tilchina.timp.model.Position;
import com.tilchina.timp.service.PositionService;
import com.tilchina.timp.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* 职务档案
*
* @version 1.1.0
* @author XiaHong
*/
@Service
@Slf4j
public class PositionServiceImpl extends BaseServiceImpl<Position> implements PositionService {

	@Autowired
    private PositionMapper positionmapper;
	
	@Override
	protected BaseMapper<Position> getMapper() {
		return positionmapper;
	}

	@Override
	protected StringBuilder checkNewRecord(Position position) {
		StringBuilder s = new StringBuilder();
		position.setPositionCode(DateUtil.dateToStringCode(new Date()));
		position.setCreateDate(new Date());
        s.append(CheckUtils.checkString("NO", "positionCode", "职务编码", position.getPositionCode(), 20));
        s.append(CheckUtils.checkString("NO", "positionName", "职务名称", position.getPositionName(), 20));
        s.append(CheckUtils.checkDate("NO", "createDate", "创建时间", position.getCreateDate()));
        s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", position.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(Position position) {
        StringBuilder s = checkNewRecord(position);
        s.append(CheckUtils.checkPrimaryKey(position.getPositionId()));
		return s;
	}
	
 @Override
    public void add(Position record) {
        log.debug("add: {}",record);
        StringBuilder s;
        Environment env = Environment.getEnv();
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
		    		s.append(CheckUtils.checkInteger("NO", "data", "职务ID", id, 20));
		   		    if (!StringUtils.isBlank(s)) {
		                throw new BusinessException("数据检查失败：" + s.toString());
		            }
		   		    queryById(Long.valueOf(id));
	    		}
	        	positionmapper.logicDeleteByPrimaryKeyList(ids);
        	}else {
        		throw new BusinessException("9001","职务ID");
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
	public void logicDeleteById(Long id) {
		log.debug("logicDeleteById: {}",id);
		StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "data", "职务ID", id, 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
   		    queryById(id);
			positionmapper.logicDeleteByPrimaryKey(id);
		} catch (Exception e) {
            if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
        }
	}
	

	@Override
    public Position queryById(Long id) {
        log.debug("query: {}",id);
        Position position = null;
        StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "data", "职务ID", id, 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            position = getMapper().selectByPrimaryKey(id); 
            if(position == null) {
            	throw new BusinessException("9008","职务ID");
            }
		} catch (Exception e) {
            if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
        }
        return position;
    }
	
	@Override
	public Map<String, Object> queryDynamicList() {
		Map<String, Object>  map = new HashMap<>();
		List<Position> list = null;
        try {
        	list = positionmapper.selectDynamicList(); 
        	for (Position position : list) {
        		map.put(position.getPositionId().toString(),position.getPositionName());
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
    public void updateSelective(Position record) {
        log.debug("updateSelective: {}",record);
        StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "groupId", "职务ID", record.getPositionId(), 20));
        	queryById(record.getPositionId());
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
	public PageInfo<Position> getRefer(Map<String, Object> data,int pageNum, int pageSize) {
		log.debug("getRefer: {}, page: {},{}", data,pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<Position> positions = null;
		try {
			positions = new PageInfo<Position>(positionmapper.selectRefer(data));
		} catch (Exception e) {
	        if(e instanceof BusinessException){
					throw e;
			}else {
					throw new RuntimeException("操作失败！", e);
			}
		}
		return positions;
	}

	@Override
	public List<Position> queryByPositionNames(List<String> positionNames) {
		return positionmapper.selectByPositionNames(positionNames);
	}

}
