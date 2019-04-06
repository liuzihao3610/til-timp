package com.tilchina.timp.manager;

import com.tilchina.timp.vo.AssemblyParam;
import com.tilchina.timp.vo.BestAssemblyResultVO;
import com.tilchina.timp.vo.TransporterCountVO;

import java.util.List;

/**
 * 
 * 
 * @version 1.0.0 2018/5/7
 * @author WangShengguang   
 */ 
public interface AutoAssemblyManager {

    void assembly(AssemblyParam param);

    void assembly(Long transportUnitId, Long sendCityId, Integer operationType, Integer assemblyType, Integer transporterType, List<TransporterCountVO> counts);

    void saveResult(List<BestAssemblyResultVO> assemblies);
}
