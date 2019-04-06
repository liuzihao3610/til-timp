package com.tilchina.timp.vo;/*
 * @author Xiahong
 * @date 2018-07-13 10:39
 */

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class UnitExcelVO {
		//	收发货单位
	private String unitName;   //单位名称
	private String enName;   //英文名称
	private String unitType;   //单位类型(0=经销店 1=仓库 2=展馆)
	private String special;   //特殊经销店(0=否 1=是)
	private String accountCityName;   //结算隶属点(城市ID)
	private String unitCityCode;   //城市编号(客户提供)
	private String provinceName;   //省(city_id)
	private String cityName;   //市(city_id)
	private String areaName;   //区(city_id)
	private String address;   //单位地址
	private String telephone;   //固定电话
	private String fax;   //传真
	private Double lng;   //经度
	private Double lat;   //纬度
	private String customerCorpName;   //所属客户
	private String deliver;   //交车方式:0=无限制 1=必须交车
	private String deliverDescription;   //交车方式描述
	private BigDecimal deliverWash;   //交车洗车费用
	private BigDecimal swingWash;   //甩车洗车费
	private String washCar;   //洗车费结算方式:0=都支持 1=自付 2=公司月结
	private String level;   //级别:0=普通 1=重要 2=vip 3=散户
	private String receive;   //接车方式:0=本店 1=其他接车点
	private String dealerCode;   //经销商代码
	private String citySettlement;   //同城结算点:用于同城业务识别结算路线和报价
		//	收发货单位联系人
	private String contactsName;   //联系人姓名
	private String phone;   //手机
	private String fix;   //固定电话
	private String email;   //邮箱
	private Integer qq;   //QQ
	private String wechat;   //微信号
		//	接车点信息
	private String unitStopAdress;   //停靠点地址
	private String continueTransport;   //是否需要继续运输(0= 否 1=是)
	private String smallCartPay;   //小板费支付方式(0=司机自付 1=公司支付 2=经销店垫付)
	private Double unitStopLng;   //接车点经度
	private Double unitStopLat;   //接车点纬度


}
