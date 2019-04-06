package com.tilchina.timp.model;

import lombok.*;
import java.util.List;
import com.tilchina.catalyst.vo.SuperVO;
import com.tilchina.timp.vo.ContactsVO;

/**
* 
*
* @version 1.0.0
* @author Xiahong
*/
@Getter
@Setter
@ToString
public class LoadingReservationDetail extends SuperVO {

    private Long loadingReservationDatailId;   //预约装车子表ID
    private Long loadingReservationId;   //预约单主表ID
    private Long transportOrderId;   //运单ID
    private Long transportOrderDetailId;   //运单子表ID
    private Long carId;   //商品车ID
    private String carVin;   //车架号
    private String remark;   //备注
    private Long corpId;   //公司ID
    
    private String refLoadingReservationCode;	//预约主表编号
    private String refTransportOrderCode;	//运单主表编号
    private String refCarName;	//商品车名称
    private String refCorpName;	//公司名称
    private String refBrandName;	//商品车品牌
    private String refCategoryName;	//商品车类别
    private List<Car> cars;	//商品车信息
    private ContactsVO contacts;	//收发货单位联系人
}

