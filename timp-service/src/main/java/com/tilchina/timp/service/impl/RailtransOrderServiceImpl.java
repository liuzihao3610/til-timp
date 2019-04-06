package com.tilchina.timp.service.impl;

import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.RailtransOrderMapper;
import com.tilchina.timp.model.RailtransOrder;
import com.tilchina.timp.service.RailtransOrderDetailService;
import com.tilchina.timp.service.RailtransOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* 铁路运输记录主表
*
* @version 1.0.0
* @author XueYuSong
*/
@Service
@Slf4j
public class RailtransOrderServiceImpl extends BaseServiceImpl<RailtransOrder> implements RailtransOrderService {

	@Autowired
    private RailtransOrderMapper railtransOrderMapper;

	@Autowired
	private RailtransOrderDetailService railtransOrderDetailService;
	
	@Override
	protected BaseMapper<RailtransOrder> getMapper() {
		return railtransOrderMapper;
	}

	@Override
	protected StringBuilder checkNewRecord(RailtransOrder railtransorder) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("NO", "transOrderCode", "运单号", railtransorder.getTransOrderCode(), 20));
        s.append(CheckUtils.checkString("YES", "carrier", "运输商", railtransorder.getCarrier(), 20));
        s.append(CheckUtils.checkString("YES", "hubLocation", "Hub位置", railtransorder.getHubLocation(), 20));
        s.append(CheckUtils.checkString("YES", "carBatch", "批次", railtransorder.getCarBatch(), 20));
        s.append(CheckUtils.checkString("YES", "remark", "备注", railtransorder.getRemark(), 200));
        s.append(CheckUtils.checkDate("NO", "createDate", "创建时间", railtransorder.getCreateDate()));
        s.append(CheckUtils.checkDate("YES", "checkDate", "审核时间", railtransorder.getCheckDate()));
        s.append(CheckUtils.checkInteger("YES", "billStatus", "单据状态", railtransorder.getBillStatus(), 10));
        s.append(CheckUtils.checkInteger("YES", "flag", "封存标志", railtransorder.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(RailtransOrder railtransorder) {
        StringBuilder s = checkNewRecord(railtransorder);
        s.append(CheckUtils.checkPrimaryKey(railtransorder.getTransOrderId()));
		return s;
	}

    @Override
    public void update(RailtransOrder railtransOrder) {

        try {

            railtransOrderMapper.updateByPrimaryKey(railtransOrder);

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
    @Transactional
	public void delete(Long orderId) {

	    try {

		    railtransOrderMapper.deleteByPrimaryKey(orderId);
		    railtransOrderDetailService.deleteByOrderId(orderId);

        } catch (Exception e) {
		    if (e instanceof BusinessException) {
                throw e;
            } else {
                throw new RuntimeException("操作失败");
            }
        }
	}

    @Override
    public void deleteList(Long[] orderIds) {

        for (Long orderId : orderIds) {
            delete(orderId);
        }
    }
}
