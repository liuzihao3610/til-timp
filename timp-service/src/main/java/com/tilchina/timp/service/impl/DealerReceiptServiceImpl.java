package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.common.LanguageUtil;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.DealerReceiptMapper;
import com.tilchina.timp.model.DealerReceipt;
import com.tilchina.timp.service.DealerReceiptService;
import com.tilchina.timp.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* 电子签收单
*
* @version 1.1.0
* @author LiuShuqi
*/
@Service
@Slf4j
public class DealerReceiptServiceImpl extends BaseServiceImpl<DealerReceipt> implements DealerReceiptService {

	@Autowired
    private DealerReceiptMapper dealerreceiptmapper;
	
	@Override
	protected BaseMapper<DealerReceipt> getMapper() {
		return dealerreceiptmapper;
	}

	@Override
	protected StringBuilder checkNewRecord(DealerReceipt dealerreceipt) {
		StringBuilder s = new StringBuilder();
		dealerreceipt.setDealerReceiptCode(DateUtil.dateToStringCode(new Date()));
        s.append(CheckUtils.checkString("YES", "dealerReceiptCode", "签收单号", dealerreceipt.getDealerReceiptCode(), 20));
        s.append(CheckUtils.checkString("NO", "carVin", "车架号", dealerreceipt.getCarVin(), 20));
        s.append(CheckUtils.checkInteger("NO", "damageSign", "质损标志", dealerreceipt.getDamageSign(), 10));
        s.append(CheckUtils.checkString("YES", "remark", "备注", dealerreceipt.getRemark(), 100));
        s.append(CheckUtils.checkDate("YES", "receptionDate", "签收时间", dealerreceipt.getReceptionDate()));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(DealerReceipt dealerreceipt) {
        StringBuilder s = checkNewRecord(dealerreceipt);
        s.append(CheckUtils.checkPrimaryKey(dealerreceipt.getDealerReceiptId()));
		return s;
	}
	
	
	@Override
	@Transactional
	public void deleteList(int[] data) {
		if (null==data || 0==data.length) {
			throw new BusinessException(LanguageUtil.getMessage("9999"));
		}
		dealerreceiptmapper.deleteList(data);
		
	}
	

	
 	@Override
 	@Transactional
    public void update(List<DealerReceipt> records) {
        log.debug("updateBatch: {}",records);
        StringBuilder s = new StringBuilder();
        boolean checkResult = true;
        try {
        	if (null==records || 0==records.size()) {
    			throw new BusinessException(LanguageUtil.getMessage("9999"));
    		}
        	for (int i = 0; i < records.size(); i++) {
        		DealerReceipt record = records.get(i);
                StringBuilder check = checkUpdate(record);
                if(!StringUtils.isBlank(check)){
                    checkResult = false;
                    s.append("第"+(i+1)+"行，"+check+"/r/n");
                }
            	dealerreceiptmapper.updateByPrimaryKeySelective(records.get(i));
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
 	
	//新增
	@Override
	@Transactional
    public void add(DealerReceipt record) {
        log.debug("add: {}",record);
        StringBuilder s;
        try {
            s = checkNewRecord(record);
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
            Environment env=Environment.getEnv();
            dealerreceiptmapper.insertSelective(record);
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
	
	
	//部分更新
	@Override
	@Transactional
    public void updateSelective(DealerReceipt record) {
        log.debug("updateSelective: {}",record);
        	StringBuilder s = new StringBuilder();
        try {
                s = s.append(CheckUtils.checkLong("NO", "dealerReceiptId", "电子签收单ID", record.getDealerReceiptId(), 20));
                if (!StringUtils.isBlank(s)) {
                    throw new BusinessException("数据检查失败：" + s.toString());
                }
                DealerReceipt dealerReceipt=dealerreceiptmapper.selectByPrimaryKey(record.getDealerReceiptId());
                if (dealerReceipt==null) {
                	throw new BusinessException("这条记录不存在!");
    			}
                record.setDealerReceiptCode(null);
                dealerreceiptmapper.updateByPrimaryKeySelective(record);
            
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
	@Transactional
    public DealerReceipt queryById(Long id) {
        log.debug("query: {}",id);
        StringBuilder s = new StringBuilder();
        try {
            s = s.append(CheckUtils.checkLong("NO", "dealerReceiptId", "电子签收单ID", id, 20));
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }

            return dealerreceiptmapper.selectByPrimaryKey(id);
        } catch (Exception e) {
        	throw new BusinessException(e.getMessage());
        }
    }
	//通过ID删除
	 @Override
	 @Transactional
    public void deleteById(Long id) {
        log.debug("delete: {}",id);
        StringBuilder s = new StringBuilder();
        try {
            s = s.append(CheckUtils.checkLong("NO", "dealerReceiptId", "电子签收单ID", id, 20));
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }

            dealerreceiptmapper.deleteByPrimaryKey(id);
        } catch (Exception e) {
        	throw new BusinessException(e.getMessage());
        }
        
    }
	 
		/**
	     * 查询结果按创建时间倒序排序
	     */
		@Override
		@Transactional
		public PageInfo<DealerReceipt> getList(Map<String, Object> map, int pageNum, int pageSize) {
			log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
	        
			try {
				PageHelper.startPage(pageNum, pageSize);
				return new PageInfo<DealerReceipt>(dealerreceiptmapper.getList(map));
			} catch (Exception e) {
				throw e;
			}
		}
}
