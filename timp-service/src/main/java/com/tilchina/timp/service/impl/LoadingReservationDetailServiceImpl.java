package com.tilchina.timp.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.model.LoadingReservationDetail;
import com.tilchina.timp.model.TransportOrderDetail;
import com.tilchina.timp.service.LoadingReservationDetailService;
import com.tilchina.timp.service.LoadingReservationService;
import com.tilchina.timp.service.TransportOrderService;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.LoadingReservationDetailMapper;

/**
* 预约装车子表
*
* @version 1.0.0
* @author Xiahong
*/
@Service
@Slf4j
public class LoadingReservationDetailServiceImpl extends BaseServiceImpl<LoadingReservationDetail> implements LoadingReservationDetailService {

	@Autowired
    private LoadingReservationDetailMapper loadingreservationdetailmapper;
	
	@Autowired
    private LoadingReservationService loadingReservationService;
	
	@Autowired
    private TransportOrderService transportOrderService;
    
	@Override
	protected BaseMapper<LoadingReservationDetail> getMapper() {
		return loadingreservationdetailmapper;
	}

	@Override
	protected StringBuilder checkNewRecord(LoadingReservationDetail loadingreservationdetail) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("NO", "carVin", "车架号", loadingreservationdetail.getCarVin(), 20));
        s.append(CheckUtils.checkString("YES", "remark", "备注", loadingreservationdetail.getRemark(), 200));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(LoadingReservationDetail loadingreservationdetail) {
        StringBuilder s = checkNewRecord(loadingreservationdetail);
        s.append(CheckUtils.checkPrimaryKey(loadingreservationdetail.getLoadingReservationDatailId()));
		return s;
	}
	
    @Override
    public void add(LoadingReservationDetail record) {
        log.debug("add: {}",record);
        StringBuilder s;
        try {
            s = checkNewRecord(record);
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }

            getMapper().insertSelective(record);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new RuntimeException("数据重复，请查证后重新保存！", e);
            }else if(e instanceof BusinessException){
				throw e;
			} else {
                throw new RuntimeException("保存失败！", e);
            }
        }
    }
    

    @Override
    public PageInfo<LoadingReservationDetail> queryList(Map<String, Object> map, int pageNum, int pageSize) {
        log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
        List<TransportOrderDetail> transportOrderDetails = null;
        PageInfo<LoadingReservationDetail> loadingReservationDetails = null;
		try {
			loadingReservationDetails = new PageInfo<LoadingReservationDetail>(getMapper().selectList(map));
        	if(map.get("transportOrderId") != null) {
        		transportOrderDetails = transportOrderService.queryById(Long.valueOf(map.get("transportOrderId").toString())).getDetails();
        		for (LoadingReservationDetail loadingReservationDetail : loadingReservationDetails.getList()) {
        			for (TransportOrderDetail transportOrderDetail : transportOrderDetails) {
						if(transportOrderDetail.getTransportOrderDetailId() == loadingReservationDetail.getTransportOrderDetailId()) {
							loadingReservationDetail.setContacts(transportOrderDetail.getContacts());
						}
					}
            		
    			}
        	}
		} catch (Exception e) {
            if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
        }
        return loadingReservationDetails;
    }
    
    @Override
    public List<LoadingReservationDetail> queryList(Map<String, Object> map) {
        log.debug("queryList: {}", map);
        List<TransportOrderDetail> transportOrderDetails = null;
        List<LoadingReservationDetail> loadingReservationDetails = null;
        try {
        	loadingReservationDetails = getMapper().selectList(map);
        	if(map.get("transportOrderId") != null) {
        		transportOrderDetails = transportOrderService.queryById(Long.valueOf(map.get("transportOrderId").toString())).getDetails();
        		for (LoadingReservationDetail loadingReservationDetail : loadingReservationDetails) {
        			for (TransportOrderDetail transportOrderDetail : transportOrderDetails) {
						if(transportOrderDetail.getTransportOrderDetailId().equals(loadingReservationDetail.getTransportOrderDetailId()) ) {
							loadingReservationDetail.setContacts(transportOrderDetail.getContacts());
						}
					}
            		
    			}
        	}
		} catch (Exception e) {
            if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
        }
        return loadingReservationDetails;
    }
    
	@Override
    public LoadingReservationDetail queryById(Long id) {
        log.debug("query: {}",id);
        StringBuilder s;
        LoadingReservationDetail loadingReservationDetail = null;
        try {
        	s = new StringBuilder();
            s.append(CheckUtils.checkLong("NO", "deta", "预约子表Id", id, 20));
            if (!StringUtils.isBlank(s)) {
                 throw new BusinessException("数据检查失败：" + s.toString());
            }
            loadingReservationDetail = getMapper().selectByPrimaryKey(id);
            if(loadingReservationDetail == null) {
            	throw new BusinessException("预约装车子表不存在此条记录！");
            }
		} catch (Exception e) {
            if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
        }
        return loadingReservationDetail;
    }

	@Override
	public void logicDeleteById(Long id) {
		log.debug("logicDeleteById: {}",id);
		StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "data", "预约装车子表ID", id, 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
   		    queryById(id);
   		 loadingreservationdetailmapper.logicDeleteByPrimaryKey(id);
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
		    		s.append(CheckUtils.checkInteger("NO", "data", "预约装车子表ID", id, 20));
		   		    if (!StringUtils.isBlank(s)) {
		                throw new BusinessException("数据检查失败：" + s.toString());
		            }
		   		 queryById(Long.valueOf(id));
	    		}
	        	loadingreservationdetailmapper.logicDeleteByPrimaryKeyList(ids);
        	}else {
        		throw new BusinessException("9001","预约装车子表ID");
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
	public void logicDeleteByReservationId(Long id) {
		log.debug("logicDeleteById: {}",id);
		StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "data", "预约装车主表ID", id, 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
   		 loadingReservationService.queryById(id);
   		 loadingreservationdetailmapper.logicDeleteByLoadingId(id);
		} catch (Exception e) {
            if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
        }
	}

	@Override
	public List<LoadingReservationDetail> queryList(Long loadingReservationId) {
        log.debug("queryList: {}",loadingReservationId);
        StringBuilder s;
        List<LoadingReservationDetail> loadingReservationDetails = null;
        try {
        	s = new StringBuilder();
            s.append(CheckUtils.checkLong("NO", "loadingReservationId", "预约主表Id", loadingReservationId, 20));
            if (!StringUtils.isBlank(s)) {
                 throw new BusinessException("数据检查失败：" + s.toString());
            }
            loadingReservationDetails = loadingreservationdetailmapper.selecByLoadingReservationIdtList(loadingReservationId);
		} catch (Exception e) {
            if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
        }
        return loadingReservationDetails;
    }

	@Override
	public void logicDeleteByTransportOrderIds(int[] ids) {
		log.debug("logicDeleteById: {}",ids);
		StringBuilder s;
        try {
        	if(ids.length > 0) {
        		s = new StringBuilder();
	        	for (int id : ids) {
		    		s.append(CheckUtils.checkInteger("NO", "data", "预约装车子表ID", id, 20));
		   		    if (!StringUtils.isBlank(s)) {
		                throw new BusinessException("数据检查失败：" + s.toString());
		            }
		   		 
		   		 queryById(Long.valueOf(id));
	    		}
	        	loadingreservationdetailmapper.logicDeleteByTransportOrderIdsList(ids);
        	}else {
        		throw new BusinessException("9001","预约装车子表ID");
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
	public void logicDeleteByTransportOrderId(Long id) {
		log.debug("logicDeleteByTransportOrderId: {}",id);
		StringBuilder s;
        try {
        	s = new StringBuilder();
        	s.append(CheckUtils.checkLong("NO", "data", "预约装车主表ID", id, 20));
   		    if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }
   		 loadingReservationService.queryById(id);
   		 loadingreservationdetailmapper.logicDeleteByTransportOrderId(id);
		} catch (Exception e) {
            if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
        }
	}

	@Override
	public List<LoadingReservationDetail> queryByReservationId(Long loadingReservationId) {
        log.debug("queryByReservationId: {}",loadingReservationId);
        StringBuilder s;
        List<LoadingReservationDetail> details;
        try {
        	s = new StringBuilder();
            s.append(CheckUtils.checkLong("NO", "deta", "预约主表Id", loadingReservationId, 20));
            if (!StringUtils.isBlank(s)) {
                 throw new BusinessException("数据检查失败：" + s.toString());
            }
            details = loadingreservationdetailmapper.selecByLoadingReservationIdtList(loadingReservationId);
		} catch (Exception e) {
            if(e instanceof BusinessException){
				throw e;
			}else {
				throw new RuntimeException("操作失败！", e);
			}
        }
        return details;
    }
	
	
/*
	@Override
	public LoadingReservationDetail queryByReservationId(Long id) {
        log.debug("queryByReservationId: {}",id);
        StringBuilder s;
        LoadingReservationDetail loadingReservationDetail = null;
        try {
        	s = new StringBuilder();
            s.append(CheckUtils.checkLong("NO", "deta", "预约主表Id", id, 20));
            if (!StringUtils.isBlank(s)) {
                 throw new BusinessException("数据检查失败：" + s.toString());
            }
            loadingReservationDetail = loadingreservationdetailmapper.selectByReKey(id);
		} catch (Exception e) {
			 throw new RuntimeException("查询失败！"+e.getMessage(), e);
		}
        return loadingReservationDetail;
    }*/
 
	 
}
