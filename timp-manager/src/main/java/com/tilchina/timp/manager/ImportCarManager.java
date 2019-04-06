package com.tilchina.timp.manager;

import com.tilchina.timp.model.Brand;
import com.tilchina.timp.model.CarType;
import com.tilchina.timp.vo.ImportCarVO;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by demon on 2018/6/12.
 */
public interface ImportCarManager {
    @Transactional
    void readFile(File file);

    @Transactional
    void addCar(List<ImportCarVO> vos, Map<String, CarType> carTypeMap);

    @Transactional
    Map<String,CarType> addCarType(List<ImportCarVO> vos, Map<String, Brand> brandMap);

    @Transactional
    Map<String,Brand> addBrand(List<ImportCarVO> vos);
}
