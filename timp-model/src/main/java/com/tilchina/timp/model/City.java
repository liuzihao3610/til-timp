package com.tilchina.timp.model;

import com.tilchina.catalyst.vo.SuperVO;
import com.tilchina.timp.annotation.EnumField;
import com.tilchina.timp.annotation.ExportField;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
* 省市区档案
*
* @version 1.1.0
* @author XiaHong
*/
@Getter
@Setter
@ToString
public class City extends SuperVO {

    private Long cityId;   //主键
    private String cityCode;   //城市编码

    @ExportField(title = "城市名称")
    private String cityName;   //城市名称

    @EnumField(title = "类型")
    private Integer cityType;   //类型(0=国家 1=省/直辖市 2=市 3=区)
    private Long upCityId;   //上级ID
    private Long corpId;   //公司ID
    private Integer flag;   //封存标志
    
    
    private List<City> lowerCity;//二级城市

    @ExportField(title = "上级城市名称")
    private String refUpCityName;//上级城市名称
    private String refCorpName;//公司名称

}

