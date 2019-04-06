package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
* 收发货单位
*
* @version 1.1.0
* @author XiaHong
*/
@Getter
@Setter
@ToString
public class Unit extends SuperVO {

    private Long unitId;   //主键
    private String unitCode;   //单位编码
    private String unitName;   //单位名称
    private String enName;   //英文名称
    private Integer unitType;   //单位类型(0=经销店 1=仓库 2=展馆)
/*    private Integer deliver;   //交车店(0=否 1=是)*/
    private Integer special;   //特殊经销店(0=否 1=是)
    private Long accountCityId;   //结算隶属点(城市ID) 以弃用
    private String unitCityCode;   //城市编号(客户提供)
    private Long provinceId;   //省(city_id)
    private Long cityId;   //市(city_id)
    private Long areaId;   //区(city_id)
    private String address;   //单位地址
    private String telephone;   //固定电话
    private String fax;   //传真
    private Double lng;   //经度
    private Double lat;   //纬度
   /* private Long superorCorpId;   //隶属公司(公司ID)*/
    private Long creator;   //创建人
    private Date createTime;   //创建时间
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志
    private Long del; //删除标志

    private Long customerCorpId;   //所属客户
    private Integer deliver;   //交车方式:0=无限制 1=必须交车
    private String deliverDescription;   //交车方式描述
    private BigDecimal deliverWash;   //交车洗车费用
    private BigDecimal swingWash;   //甩车洗车费
    private Integer washCar;   //洗车费结算方式:0=都支持 1=自付 2=公司月结
    private Integer level;   //级别:0=普通 1=重要 2=vip 3=散户
    private Integer receive;   //接车方式:0=本店 1=其他接车点
    private String dealerCode;   //经销商代码
    private String citySettlement;   //同城结算点:用于同城业务识别结算路线和报价

    private String refAccountCity; //结算隶属点
    private String refCustomerCorp; //归属客户
    private String refCorpName;//公司名称
    private String refProvinceName; //省
    private String refCityName; //市
    private String refAreaName; //区
    private String refCreateName; //创建人姓名

    private  String refUnitContacts;    // 联系人信息：姓名：手机号码；
    // APP收发货联系人姓名、电话 Created by XueYuSong
/*    private String refUserName;
    private String refPhone;*/

}

