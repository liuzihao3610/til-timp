package com.tilchina.timp.model;

import lombok.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.tilchina.catalyst.vo.SuperVO;

/**
* 运输计划表
*
* @version 1.1.0
* @author XiaHong
*/
@Getter
@Setter
@ToString
public class TransportPlan extends SuperVO {

    private Long transportPlanId;   //运输计划ID
    private Long transportOrderId;   //运单主表ID
    private String transportOrderCode;   //运单号
    private Integer  sequence;	//顺序号
    private Long transporterId;   //轿运车ID
    private Long driverId;   //司机ID
    private Integer carCount;   //装载数量
    private Integer handingCount;   //装卸数量 
    private Double driving;   //行驶公里数
    private Double approve;   //核准公里数
    private Long startUnitId;   //起始地
    private Double startLng;   //起始地经度
    private Double startLat;   //起始地纬度
    private Long endUnitId;   //目的地
    private Double endLng;   //目的地经度
    private Double endLat;   //目的地纬度
    private Long corpId;   //公司ID
    private Double loadWeight; //载重
    private Integer loadType; //载重类型

    private String refTransporterCode;   //轿运车名称
    private String refDriverName;   //司机名称
    private String refCorpName;   //公司名称
	private String refStartUnitName;   //起始地名称
	private String refEndUnitName;   //目的地名称
	private String refStartCityName; //起始地名称
	private String refEndCityName; //目的地名称
    private String refStartUnitAddr;	//收货单位名称
    private String refEndUnitAddr;	//收货单位名称
    private String refDriverCode;   // 驾驶员名编号
    private String refTractirPlateCode;   //车头车牌号
    
	/**
	 * 运输计划表通过距离排序
	 * @param transportPlans
	 * @return
	 */
	public static List<TransportPlan> sort (List<TransportPlan> transportPlans) {
		 Collections.sort(transportPlans, new Comparator<TransportPlan>(){
		    /*    
			 * int compare(Person p1, Person p2) 返回一个基本类型的整型，
	         * 返回负数表示：p1 小于p2，
	         * 返回0 表示：p1和p2相等，
	         * 返回正数表示：p1大于p2*/
		        public int compare(TransportPlan p1, TransportPlan p2) {
		            //按照Person的距离进行升序排列
		            if(p1.getDriving() > p2.getDriving()){
		                return 1;
		            }
		            if(p1.getDriving() == p2.getDriving()){
		                return 0;
		            }
		            return -1;
		        }
		    });
		return transportPlans;
	 }
    
}

