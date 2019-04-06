package com.tilchina.timp.manager;

import com.tilchina.timp.model.Brand;
import com.tilchina.timp.model.City;
import com.tilchina.timp.vo.ImplTransVO;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by demon on 2018/6/12.
 */
public interface ImportTransManager {
    @Transactional
    void readFile(File file);

    void addPrice(List<ImplTransVO> vos, Map<String, City> cityMap, Map<String, Brand> brandMap) throws Exception;
}
