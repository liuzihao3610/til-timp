package com.tilchina.timp.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by demon on 2018/6/9.
 */
@Data
public class AssemblyParam implements Serializable{

    private Long transportCorpId; // 运输公司
    private Long sendCityId; // 起运地城市
    private Integer operationType; // 作业类型
    private Integer assemblyType; // 配板方案
    private Integer transporterType; // 运力获取方式
    private List<TransporterCountVO> counts; // 自定义运力

    private Integer trail1;// 8位板
    private Integer trail2;// 7位板
}
