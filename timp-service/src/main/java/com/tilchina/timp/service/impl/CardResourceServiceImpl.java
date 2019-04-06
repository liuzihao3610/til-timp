package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.enums.FlagStatus;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.CardResourceMapper;
import com.tilchina.timp.model.CardReceiveRecord;
import com.tilchina.timp.model.CardRechargeRecord;
import com.tilchina.timp.model.CardResource;
import com.tilchina.timp.service.CardReceiveRecordService;
import com.tilchina.timp.service.CardRechargeRecordService;
import com.tilchina.timp.service.CardResourceService;
import com.tilchina.timp.util.DateUtil;
import com.tilchina.timp.util.ExcelUtil;
import com.tilchina.timp.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* 卡资源管理
*
* @version 1.0.0
* @author LiushuQi
*/
@Service
@Slf4j
public class CardResourceServiceImpl implements CardResourceService {

	@Autowired
    private CardResourceMapper cardresourcemapper;
	
	@Autowired
	private CardReceiveRecordService cardReceiveRecordService;
	
	@Autowired
	private CardRechargeRecordService cardRechargeRecordService;
	
	protected BaseMapper<CardResource> getMapper() {
		return cardresourcemapper;
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
	protected StringBuilder checkNewRecord(CardResource cardresource) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("NO", "cardResourceCode", "卡号", cardresource.getCardResourceCode(), 20));
        s.append(CheckUtils.checkInteger("NO", "cardType", "卡类型(0=油卡", cardresource.getCardType(), 10));
        s.append(checkBigDecimal("YES", "balance", "余额", cardresource.getBalance(), 10, 2));
        s.append(CheckUtils.checkInteger("YES", "cardStatus", "状态(0=未领用", cardresource.getCardStatus(), 10));
        s.append(CheckUtils.checkInteger("NO", "source", "来源(0=采购", cardresource.getSource(), 10));
        s.append(CheckUtils.checkString("NO", "issueCompany", "发行公司(如:中石油,中石化)", cardresource.getIssueCompany(), 40));
        s.append(CheckUtils.checkString("YES", "remark", "备注", cardresource.getRemark(), 200));
        s.append(CheckUtils.checkDate("YES", "createDate", "创建时间", cardresource.getCreateDate()));
        s.append(CheckUtils.checkInteger("YES", "flag", "封存标志(0=", cardresource.getFlag(), 10));
		return s;
	}

	protected StringBuilder checkUpdate(CardResource cardresource) {
        StringBuilder s = checkNewRecord(cardresource);
        s.append(CheckUtils.checkPrimaryKey(cardresource.getCardResourceId()));
		return s;
	}
	/**
	 * 通过ID查询
	 */
	@Override
	@Transactional
    public CardResource queryById(Long id) {
        log.debug("query: {}",id);
        CardResource cardResource=cardresourcemapper.selectByPrimaryKey(id);
        if (cardResource==null) {
			throw new BusinessException("卡不存在");
		}
        return getMapper().selectByPrimaryKey(id);
    }
	/**
	 * 分页查询
	 */
    @Override
    @Transactional
    public PageInfo<CardResource> queryList(Map<String, Object> map, int pageNum, int pageSize) {
        log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
        try {
            Environment environment=Environment.getEnv();
            if(map==null || map.size()==0) {
                Map<String, Object> m=new HashMap<>();
                m.put("corpId", environment.getCorp().getCorpId());
                return new PageInfo<CardResource>(getMapper().selectList(m));
            }else {
                DateUtil.addTime(map);
                map.put("corpId", environment.getCorp().getCorpId());
                return new PageInfo<CardResource>(getMapper().selectList(map));
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
    public List<CardResource> queryList(Map<String, Object> map) {
        log.debug("queryList: {}", map);
        try {
            if (map!=null){
                DateUtil.addTime(map);
            }else{
                map=new HashMap<>();
            }
            Environment environment=Environment.getEnv();
            map.put("corpId", environment.getCorp().getCorpId());
            return cardresourcemapper.selectList(map);
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }

    }
    /**
     * 新增
     */
    @Override
    @Transactional
    public void add(CardResource record) {
        log.debug("add: {}",record);
        StringBuilder s;
        try {
            s = checkNewRecord(record);
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            if (record.getBalance().doubleValue()<0) {
            	throw new BusinessException("卡余额有误,请核实");
			}
            record.setBalance(new BigDecimal(0));
            Environment environment=Environment.getEnv();
            record.setCreator(environment.getUser().getUserId());
            record.setCreateDate(new Date());
            record.setCorpId(environment.getCorp().getCorpId());
            cardresourcemapper.insertSelective(record);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新保存！", e);
            } else if (e instanceof BusinessException) {
				throw e;
			}else{
                throw new RuntimeException("保存失败！", e);
            }
        }
    }
    /**
     * 批量新增
     */
    @Override
    @Transactional
    public void add(List<CardResource> records){
        log.debug("addBatch: {}",records);
        StringBuilder s = new StringBuilder();
        boolean checkResult = true;
        try {
            for (int i = 0; i < records.size(); i++) {
                CardResource record = records.get(i);
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
    public void updateSelective(CardResource record) {
        log.debug("updateSelective: {}",record);
        StringBuilder s;
        try {
            s = checkNewRecord(record);
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            if (record.getBalance().doubleValue()<0) {
            	throw new BusinessException("卡余额有误,请核实");
			}
        	CardResource cardResource=cardresourcemapper.selectByPrimaryKey(record.getCardResourceId());
        	if (cardResource==null) {
				throw new BusinessException("卡不存在");
			}
//        	if (record.getCardStatus()==0) {
//        		CardReceiveRecord cardReceiveRecord=cardReceiveRecordService.queryByCardId(record.getCardResourceId());
//            	if (cardReceiveRecord!=null) {
//    				cardReceiveRecordService.deleteById(cardReceiveRecord.getCardReceiveRecordId());
//    			}
//			}
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
    public void update(CardResource record) {
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
    public void update(List<CardResource> records) {
        log.debug("updateBatch: {}",records);
        StringBuilder s = new StringBuilder();
        boolean checkResult = true;
        try {
            for (int i = 0; i < records.size(); i++) {
                CardResource record = records.get(i);
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
        CardResource cardResource=cardresourcemapper.selectByPrimaryKey(id);
    	if (cardResource==null) {
			throw new BusinessException("卡不存在");
		}
        getMapper().deleteByPrimaryKey(id);
    }
    /**
     * 领用
     */
	@Override
	@Transactional
	public void receive(CardReceiveRecord cardReceiveRecord) {
		try {
			CardResource card=cardresourcemapper.selectByPrimaryKey(cardReceiveRecord.getCardResourceId());
			if (card==null) {
				throw new BusinessException("卡不存在");
			}
            if (card.getCardStatus()==1){
                throw new BusinessException("卡已被领用");
            }
            card.setDriverId(cardReceiveRecord.getDriverId());
			cardresourcemapper.updateDriverId(card);
			Environment environment =Environment.getEnv();
			cardReceiveRecord.setCreator(environment.getUser().getUserId());
			cardReceiveRecord.setCreateDate(new Date());
			cardReceiveRecordService.add(cardReceiveRecord);
			//更新卡为已领用
			card.setCardStatus(1);
			cardresourcemapper.updateByPrimaryKeySelective(card);
			
		} catch (Exception e) {
			throw e;
		}
		
	}
	/**
	 * 充值
	 */
	@Override
	@Transactional
	public void recharge(CardRechargeRecord cardRechargeRecord) {
		try {
			CardResource card=cardresourcemapper.selectByPrimaryKey(cardRechargeRecord.getCardResourceId());
			if (card==null) {
				throw new BusinessException("卡不存在");
			}
            if (card.getCardStatus()==2 || card.getCardStatus()==3){
                throw new BusinessException("卡处于丢失或作废状态不能被领用,请核实");
            }
			BigDecimal balanceBeforeRecharge=cardRechargeRecord.getBalanceBeforeRecharge();
			BigDecimal rechargeMoney=cardRechargeRecord.getRechargeMoney();
			if (rechargeMoney.doubleValue()<0) {
				if ((0-rechargeMoney.doubleValue())>balanceBeforeRecharge.doubleValue()) {
					throw new BusinessException("卡余额不足,请核实");
				}
			}
			card.setBalance(balanceBeforeRecharge.add(rechargeMoney));
			cardresourcemapper.updateByPrimaryKeySelective(card);
			Environment environment=Environment.getEnv();
			cardRechargeRecord.setCreator(environment.getUser().getUserId());
			cardRechargeRecord.setCreateDate(new Date());
			cardRechargeRecordService.add(cardRechargeRecord);
		} catch (Exception e) {
			throw e;
		}
		
	}
	/**
	 * 按创建时间倒序排列
	 * @param map
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@Override
    @Transactional
    public PageInfo<CardResource> getList(Map<String, Object> map, int pageNum, int pageSize) {
        log.debug("queryList: {}", map);
        PageHelper.startPage(pageNum, pageSize);
        Environment environment=Environment.getEnv();
        if (map==null) {
			map=new HashMap<>();
		}
        map.put("corpId", environment.getCorp().getCorpId());
        return new PageInfo<CardResource>(cardresourcemapper.getList(map)) ;
    }

    /**
     * 丢失
     * @param data
     */
    @Override
    @Transactional
    public void lose(Long data) {
        CardResource cardResource=cardresourcemapper.selectByPrimaryKey(data);
        if (cardResource==null){
            throw new BusinessException("卡不存在");
        }
        if (cardResource.getCardStatus()==2){
            throw new BusinessException("卡已是丢失状态");
        }
        cardResource.setCardStatus(2);
        cardresourcemapper.updateByPrimaryKeySelective(cardResource);
    }

    /**
     * 作废
     * @param data
     */
    @Override
    @Transactional
    public void invalid(Long data) {
        CardResource cardResource=cardresourcemapper.selectByPrimaryKey(data);
        if (cardResource==null){
            throw new BusinessException("卡不存在");
        }
        if (cardResource.getCardStatus()==3){
            throw new BusinessException("卡已是作废状态");
        }
        cardResource.setCardStatus(3);
        cardresourcemapper.updateByPrimaryKeySelective(cardResource);
    }

    /**
     * 取消领用/归还
     * @param data
     */
    @Override
    @Transactional
    public void unReceive(Long data) {
        CardResource cardResource=cardresourcemapper.selectByPrimaryKey(data);
        if (cardResource==null){
            throw new BusinessException("卡不存在");
        }
        if (cardResource.getCardStatus()!=1){
            throw new BusinessException("卡不是领用状态,请核实");
        }
        cardResource.setCardStatus(0);
        cardresourcemapper.updateByPrimaryKeySelective(cardResource);
        cardResource.setDriverId(null);
        cardresourcemapper.updateDriverId(cardResource);

    }

    /**
     * 取消丢失
     * @param data
     */
    @Override
    @Transactional
    public void find(Long data) {
        CardResource cardResource=cardresourcemapper.selectByPrimaryKey(data);
        if (cardResource==null){
            throw new BusinessException("卡不存在");
        }
        if (cardResource.getCardStatus()!=2){
            throw new BusinessException("卡不是丢失状态,请核实");
        }
        cardResource.setCardStatus(0);
        cardresourcemapper.updateByPrimaryKeySelective(cardResource);
        cardResource.setDriverId(null);
        cardresourcemapper.updateDriverId(cardResource);
    }

    /**
     * 取消作废
     * @param data
     */
    @Override
    @Transactional
    public void unInvalid(Long data) {
        CardResource cardResource=cardresourcemapper.selectByPrimaryKey(data);
        if (cardResource==null){
            throw new BusinessException("卡不存在");
        }
        if (cardResource.getCardStatus()!=3){
            throw new BusinessException("卡不是作废状态,请核实");
        }
        cardResource.setCardStatus(0);
        cardresourcemapper.updateByPrimaryKeySelective(cardResource);
        cardResource.setDriverId(null);
        cardresourcemapper.updateDriverId(cardResource);
    }

	@Override
	public void importExcel(MultipartFile file) throws Exception {
		try {
			Environment environment = Environment.getEnv();
			String filePath = FileUtil.uploadExcel(file);
			Workbook workbook = ExcelUtil.getWorkbook(filePath);

			Map<String,Boolean> nullableMap = new HashMap<>();
			nullableMap.put("卡号", false);
			nullableMap.put("卡类型", false);
			nullableMap.put("余额", false);
			nullableMap.put("来源", false);
			nullableMap.put("发行公司", false);

			String massage = ExcelUtil.validateWorkbook(workbook, nullableMap);
			if (!StringUtils.isBlank(massage)) {
				throw new BusinessException("数据检查失败：" + massage);
			}

			List<CardResource> models = ExcelUtil.getModelsFromWorkbook(workbook, CardResource.class);
			models.forEach(model -> {
				model.setCreator(environment.getUser().getUserId());
				model.setCreateDate(new Date());
				model.setCorpId(environment.getUser().getCorpId());
				model.setFlag(FlagStatus.No.getIndex());
			});
			add(models);
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	@Override
	public Workbook exportExcel() throws Exception {
		try {
			List<CardResource> results = queryList(new HashMap<>());
			Workbook workbook = ExcelUtil.getWorkbookFromModels(results);
			return workbook;
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}
}
