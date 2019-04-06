package com.tilchina.timp.utils;

import com.tilchina.timp.expection.BusinessException;

/**
 * 校验提报状态
 * @author Xiahong
 *
 */
public class CheckReportStatus {
	
	/**
	 * 校验轿运车提报状态
	 * @param update
	 * @param original
	 * @param status 0=修改骄运车状态表 1=新增骄运车状态表
	 */
/*	public  static void checkTransporterReportStatuc(Integer update,Integer original,Integer status) {
		if(update != 0 ) {
			//骄运车状态:0=空闲,1=在途,2=到店,3=回程,4=保养,5=维修,6=在库
			if(status == 0) {
				if(update == 0 && original == update) {
		        	throw new BusinessException("该轿运车状态已为空闲状态，请勿重复提交");
		        }else if(update == 1 && original != 0 ) {
		        	throw new BusinessException("目前轿运车状态并未空闲，请先将轿运车状态提交为空闲，在进行此操作！");
		        }else if(update == 2 && original != 1 ) {
		        	throw new BusinessException("目前轿运车状态并未在途，请先将轿运车状态提交为在途，在进行此操作！");
		        }else if(update == 3 && original != 2 ) {
		        	throw new BusinessException("目前轿运车状态并未到店，请先将轿运车状态提交为到店，在进行此操作！");
		        }else if(update == 4 && original != 6 ||  update == 5 && original != 6) {
		        	throw new BusinessException("目前轿运车状态并未在库，请先将轿运车状态提交为在库，在进行此操作！");
		        }
			}else if(status == 1) {
				if(update > 0 && update != original) {
					if(update > 2 && update < 6) {
						throw new BusinessException("该轿运车目前不支持此状态提交！请先将轿运车状态提报为在库状态,在进行此操作！");
					}
		        	
		        }
			}
		}
		
	}
	
	*//**
	 *  校验司机提报状态
	 * @param update 提交状态
	 * @param original 原先状态
	 * @param status 0=修改司机状态表 1=新增司机状态表
	 *//*
	public  static void checkDriverReportStatuc(Integer update,Integer original,Integer status) {
		//司机状态:0=空闲 1=事假 2=病假 3=培训 4=学习 5=审证 6=在途 7=到店 8=待回程 9=回程
		if(update != 0 ) {
			if(status == 0) {
				if(update == 0 && original == update) {
		        	throw new BusinessException("该司机状态已为空闲状态，请勿重复提交");
		        }else if(update < 7 && original != 0 ) {
		        	throw new BusinessException("目前司机状态并未空闲，请先将司机状态提交为空闲，在进行此操作！");
		        }else if(update == 7 && original != 6 ) {
		        	throw new BusinessException("目前司机状态并未在途，请先将司机状态提交为在途，在进行此操作！");
		        }else if(update == 8 && original != 7 || update == 9 && original != 7) {
		        	throw new BusinessException("目前司机状态并未到店，请先将司机状态提交为到店，在进行此操作！");
		        }
			}else if(status == 1){
				if(update > 5 && update != original) {
		        	throw new BusinessException("该司机目前不支持此状态提交！");
		        }
			}
		}
		
	}*/
	
	
}
