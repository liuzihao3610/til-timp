package com.tilchina.timp.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.tilchina.catalyst.vo.SuperVO;
import com.tilchina.timp.vo.ContactsVO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
* 运单子表
*
* @version 1.1.0
* @author XiaHong
*/
@Getter
@Setter
@ToString
public class TransportOrderDetail extends SuperVO {

    private Long transportOrderDetailId;   //运单子表ID
    private Long transportOrderId;   //运单主表ID
    private String transportOrderCode;   //运单号
    private Long carId;   //商品车id
    private String carVin;   //车架号
    private Long sendUnitId;   //发货单位ID
    private Long receiveUnitId;   //收货单位ID
    private Integer examination;   //车检标志:0=否 1=是
    private Integer urgent;		//加急标志：0=否 1=是
    private Date claimLoadingDate;   //指定装车日期
    private Date realityLoadingDate;   //实际装车日期
    private Date claimDeliveryDate;   //指定交车日期
    private Date realityDeliveryDate;   //实际交车日期
    private Date arriveDateBesides;   //到店时间（外）
    private Date signDate;   //签收时间
    private Long driverId;   //司机ID
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志
    private Long examinationLocationId;   //车检地址:检查站ID
    private Integer carStatus;//车辆状态
    
    private String refCarName;   //商品车名称
    private String refSendUnitName;   //发货单位名称
    private String refReceiveUnitName;   //收货单位名称
    private String refReceiveUnitCity;	//收货单位城市
    private String refReceiveUnitAddr;	//收货单位名称
    private String refDriverName;   //司机名称
    private String refExaminationLocation;   //车检地址检查站名称
    private String refCorpName;   //公司名称
    private Long refBrandId;
    private String refBrandName; //品牌
    private Long refCarTypeId;
    private String refCategoryName; //类别
    private String refSendUnitCity;	//发货单位城市
    private String refSendUnitAddr;	//发货单位名称
    private Integer refReceiveUnitSpecial;	//是否经销店
    private String refCarCode; //商品车型号
    private String refDriverCode;   // 驾驶员名编号
    private Integer refTransferOrderBillStatus;	//交接单状态 sr_transfer_order(客户交接单)

  /*  private List<ContactsVO> contactVOs;	//收发货单位联系人*/
    private ContactsVO contacts;	//收发货单位联系人

    private String refTractirPlateCode;   //车头车牌号
    private String refCustomerName;	//客户名称

    private List<ContactsVO> startContactVOs;	//收发货单位联系人
    private List<ContactsVO> endContactVOs;	//收发货单位联系人

    private Long sendCityId;    //发货城市
    private Long receiveCityId; //收货城市
    private Long accountCityId; // 结算城市

    @JSONField(serialize = false)
    private Freight freight;

    @JSONField(serialize = false)
    private Long freightId; //结算价格id

    private String freightCode;   //运价编号
    private Integer freightType;   //运价类型(0=标准价 1=结算价)

   /* @JSONField(serialize = false)*/
    private BigDecimal finalPrice;   //最终价格


    @JSONField(serialize = false)
    private BigDecimal customerPrice; //客户报价
    
    /*private Long refStockCarId;   //商品车库存表id
*/    
}

