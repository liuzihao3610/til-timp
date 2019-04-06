package com.tilchina.timp.service.impl;

import com.tilchina.timp.util.DateUtil;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
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
import com.tilchina.timp.model.Freight;
import com.tilchina.timp.model.OilDepot;
import com.tilchina.timp.model.OilDepotSupplyRecord;
import com.tilchina.timp.model.TransporterFuelRecord;
import com.tilchina.timp.service.OilDepotService;
import com.tilchina.timp.service.OilDepotSupplyRecordService;
import com.tilchina.timp.service.TransporterFuelRecordService;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.OilDepotMapper;

/**
* 油库管理
*
* @version 1.0.0
* @author LiushuQi
*/
@Service
@Slf4j
public class OilDepotServiceImpl implements OilDepotService {

	@Autowired
    private OilDepotMapper oildepotmapper;
	
	@Autowired
	private OilDepotSupplyRecordService oilDepotSupplyRecordService;
	
	@Autowired
	private TransporterFuelRecordService transporterFuelRecordService;
	
	protected BaseMapper<OilDepot> getMapper() {
		return oildepotmapper;
	}

	protected StringBuilder checkNewRecord(OilDepot oildepot) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("NO", "oilDepotCode", "油库编号", oildepot.getOilDepotCode(), 20));
        s.append(CheckUtils.checkInteger("NO", "oilDepotType", "油库类型(0=汽油", oildepot.getOilDepotType(), 10));
        s.append(CheckUtils.checkString("NO", "oilLabel", "标号", oildepot.getOilLabel(), 10));
        s.append(CheckUtils.checkInteger("YES", "oilDepotStatus", "状态(0=", oildepot.getOilDepotStatus(), 10));
        s.append(CheckUtils.checkString("YES", "remark", "备注", oildepot.getRemark(), 200));
        s.append(CheckUtils.checkString("NO", "phone", "联系电话", oildepot.getPhone(), 20));
        s.append(CheckUtils.checkString("NO", "oilDepotAddress", "地址", oildepot.getOilDepotAddress(), 200));
        s.append(CheckUtils.checkDate("YES", "createDate", "创建时间", oildepot.getCreateDate()));
        s.append(CheckUtils.checkInteger("YES", "flag", "封存标志(0=", oildepot.getFlag(), 10));
		return s;
	}

	protected StringBuilder checkUpdate(OilDepot oildepot) {
        StringBuilder s = checkNewRecord(oildepot);
        s.append(CheckUtils.checkPrimaryKey(oildepot.getOilDepotId()));
		return s;
	}
	
	/**
	 * 通过ID查询
	 */
	@Override
	@Transactional
    public OilDepot queryById(Long id) {
        log.debug("query: {}",id);
        return getMapper().selectByPrimaryKey(id);
    }
	/**
	 * 分页查询
	 */
    @Override
    @Transactional
    public PageInfo<OilDepot> queryList(Map<String, Object> map, int pageNum, int pageSize) {
        log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
        try {
            Environment environment=Environment.getEnv();
            if(map==null || map.size()==0) {
                Map<String, Object> m=new HashMap<>();
                m.put("corpId", environment.getCorp().getCorpId());
                return new PageInfo<OilDepot>(getMapper().selectList(m));
            }else {
                map.put("corpId", environment.getCorp().getCorpId());
                DateUtil.addTime(map);
                return new PageInfo<OilDepot>(getMapper().selectList(map));
            }
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
        
    }
    /**
     * 查询所有
     */
    @Override
    @Transactional
    public List<OilDepot> queryList(Map<String, Object> map) {
        log.debug("queryList: {}", map);
        Environment environment=Environment.getEnv();
        if (map==null) {
			map=new HashMap<>();
		}
        map.put("corpId", environment.getCorp().getCorpId());
        return getMapper().selectList(map);
    }
    /**
     * 新增
     */
    @Override
    @Transactional
    public void add(OilDepot record) {
        log.debug("add: {}",record);
        StringBuilder s;
        try {
            s = checkNewRecord(record);
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            if (record.getAllowance()<0) {
            	throw new BusinessException("油库余量有误,请核实" );
			}
            //record.setAllowance(new Double(0));
            Environment environment=Environment.getEnv();
            record.setCreator(environment.getUser().getUserId());
            record.setCreateDate(new Date());
            record.setCorpId(environment.getCorp().getCorpId());
            getMapper().insertSelective(record);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新保存！", e);
            } else if (e instanceof BusinessException) {
				throw e;
			}else{
                throw new BusinessException("保存失败！", e);
            }
        }
    }
    /**
     * 批量新增
     */
    @Override
    @Transactional
    public void add(List<OilDepot> records){
        log.debug("addBatch: {}",records);
        StringBuilder s = new StringBuilder();
        boolean checkResult = true;
        try {
            for (int i = 0; i < records.size(); i++) {
                OilDepot record = records.get(i);
                StringBuilder check = checkNewRecord(record);
                if(!StringUtils.isBlank(check)){
                    checkResult = false;
                    s.append("第"+(i+1)+"行，"+check+"/r/n");
                }
            }
            if (!checkResult) {
                throw new RuntimeException("数据检查失败：" + s.toString());
            }

            getMapper().insert(records);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new RuntimeException("数据重复，请查证后重新保存！", e);
            } else {
                throw new RuntimeException("批量保存失败！", e);
            }
        }
    }
    /**
     * 部分更新
     */
    @Override
    @Transactional
    public void updateSelective(OilDepot record) {
        log.debug("updateSelective: {}",record);
        StringBuilder s;
        try {
            s = checkNewRecord(record);
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            if (record.getAllowance()<0) {
            	throw new BusinessException("余量有误,请核实");
			}
        	OilDepot oilDepot=oildepotmapper.selectByPrimaryKey(record.getOilDepotId());
        	if (oilDepot==null) {
				throw new BusinessException("油库不存在");
			}
        	Environment environment=Environment.getEnv();
            record.setCreator(environment.getUser().getUserId());
            record.setCorpId(environment.getCorp().getCorpId());
            getMapper().updateByPrimaryKeySelective(record);
        } catch (Exception e) {
        	if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新保存！", e);
            } else if (e instanceof BusinessException) {
				throw e;
			}else{
                throw new BusinessException("保存失败！", e);
            }
        }
    }
    /**
     * 全部更新
     */
    @Override
    @Transactional
    public void update(OilDepot record) {
        log.debug("update: {}",record);
        StringBuilder s;
        try {
            s = checkUpdate(record);
            if (!StringUtils.isBlank(s)) {
                throw new RuntimeException("数据检查失败：" + s.toString());
            }

            getMapper().updateByPrimaryKey(record);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new RuntimeException("数据重复，请查证后重新提交！", e);
            } else {
                throw new RuntimeException("更新失败！", e);
            }
        }
    }
    /**
     * 批量更新
     */
    @Override
    @Transactional
    public void update(List<OilDepot> records) {
        log.debug("updateBatch: {}",records);
        StringBuilder s = new StringBuilder();
        boolean checkResult = true;
        try {
            for (int i = 0; i < records.size(); i++) {
                OilDepot record = records.get(i);
                StringBuilder check = checkUpdate(record);
                if(!StringUtils.isBlank(check)){
                    checkResult = false;
                    s.append("第"+(i+1)+"行，"+check+"/r/n");
                }
            }
            if (!checkResult) {
                throw new RuntimeException("数据检查失败：" + s.toString());
            }

            getMapper().update(records);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new RuntimeException("数据重复，请查证后重新提交！", e);
            } else {
                throw new RuntimeException("批量更新失败！", e);
            }
        }

    }
    /**
     * 通过ID删除
     */
    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("delete: {}",id);
        OilDepot oilDepot=oildepotmapper.selectByPrimaryKey(id);
    	if (oilDepot==null) {
			throw new BusinessException("油库不存在");
		}
        getMapper().deleteByPrimaryKey(id);
    }
    /**
     * 入库
     */
	@Override
	@Transactional
	public void supply(OilDepotSupplyRecord oilDepotSupplyRecord) {
		try {
			OilDepot oilDepot=oildepotmapper.selectByPrimaryKey(oilDepotSupplyRecord.getOilDepotId());
			if (oilDepot==null) {
				throw new BusinessException("油库不存在");
			}
			if (oilDepotSupplyRecord.getOilSupply()<0) {
				if (0-oilDepotSupplyRecord.getOilSupply()>oilDepot.getAllowance()) {
					throw new BusinessException("油库余量不足,请核实");
				}
			}
			
			//更改油库余量
			oilDepot.setAllowance(oilDepotSupplyRecord.getOilSupply()+oilDepot.getAllowance());
			oildepotmapper.updateByPrimaryKeySelective(oilDepot);
			//设置负责人,操作时间,总金额
			Environment environment=Environment.getEnv();
			BigDecimal supply=new BigDecimal(Double.valueOf(oilDepotSupplyRecord.getOilSupply()));
			oilDepotSupplyRecord.setTotalPrice(supply.multiply(oilDepotSupplyRecord.getUnitPrice()).setScale(2,BigDecimal.ROUND_HALF_UP));
			oilDepotSupplyRecord.setCreator(environment.getUser().getUserId());
			oilDepotSupplyRecord.setCreateDate(new Date());
			oilDepotSupplyRecordService.add(oilDepotSupplyRecord);
		} catch (Exception e) {
			throw e;
		}
		
	}
	/**
	 * 车辆加油
	 */
	@Override
	@Transactional
	public void fuel(TransporterFuelRecord transporterFuelRecord) {
		try {
			OilDepot oilDepot=oildepotmapper.selectByPrimaryKey(transporterFuelRecord.getOilDepotId());
			if (oilDepot==null) {
				throw new BusinessException("油库不存在");
			}
			
			if (oilDepot.getOilDepotStatus()==0) {
				throw new BusinessException("此油库是停用状态,情前往其他油库加油");
			}
			if (oilDepot.getAllowance()==0) {
            	throw new BusinessException("油库余量为零,请前往其他油库加油!");
			}
			if (transporterFuelRecord.getFuelCharge()<0) {
            	throw new BusinessException("加油量有误,请核实!");
			}
			if (oilDepot.getAllowance()<transporterFuelRecord.getFuelCharge()) {
				throw new BusinessException("油库余量不足,请核实");
			}
			//更改油库余量
			oilDepot.setAllowance(oilDepot.getAllowance()-transporterFuelRecord.getFuelCharge());
			oildepotmapper.updateByPrimaryKeySelective(oilDepot);
			transporterFuelRecord.setFuelDate(new Date());
			Environment environment =Environment.getEnv();
			transporterFuelRecord.setCreator(environment.getUser().getUserId());
			transporterFuelRecord.setCreateDate(new Date());
			transporterFuelRecordService.add(transporterFuelRecord);
		} catch (Exception e) {
			throw e;
		}
		
	}
	/**
	 * 启用
	 */
	@Override
	@Transactional
	public void start(Map<String, Object> map) {
		try {
			OilDepot oilDepot=oildepotmapper.selectByPrimaryKey(Long.parseLong(map.get("oilDepotId").toString()));
			if (oilDepot==null) {
				throw new BusinessException("油库不存在");
			}
			if (oilDepot.getOilDepotStatus()==1) {
				throw new BusinessException("油库已是启用状态");
			}
			oilDepot.setOilDepotStatus(1);
			oildepotmapper.updateByPrimaryKeySelective(oilDepot);
		} catch (Exception e) {
			throw e;
		}
		
	}
	
	/**
	 * 停用
	 */
	@Override
	@Transactional
	public void stop(Map<String, Object> map) {
		try {
			OilDepot oilDepot=oildepotmapper.selectByPrimaryKey(Long.parseLong(map.get("oilDepotId").toString()));
			if (oilDepot==null) {
				throw new BusinessException("油库不存在");
			}
			if (oilDepot.getOilDepotStatus()==0) {
				throw new BusinessException("油库已是停用状态");
			}
			oilDepot.setOilDepotStatus(0);
			oildepotmapper.updateByPrimaryKeySelective(oilDepot);
		} catch (Exception e) {
			throw e;
		}
		
	}
	
	/**
     * 查询结果按创建时间倒序排序
     */
	@Override
	@Transactional
	public PageInfo<OilDepot> getList(Map<String, Object> map, int pageNum, int pageSize) {
		log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
        
		try {
			PageHelper.startPage(pageNum, pageSize);
			return new PageInfo<OilDepot>(oildepotmapper.getList(map));
		} catch (Exception e) {
			throw e;
		}
	}
}
