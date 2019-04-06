package com.tilchina.timp.manager;

import com.tilchina.timp.model.City;
import com.tilchina.timp.model.Quotation;
import com.tilchina.timp.vo.BaoshijiePriceVO;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by demon on 2018/6/12.
 */
public interface ImportPriceManager {
    @Transactional
    void readFile(File file);

    void addOthers(Quotation vo, Long customerId, Long carrierId);

    @Transactional
    Quotation addPrice(List<BaoshijiePriceVO> vos, Map<String, City> cityMap, Map<String, Long> carTypeMap) throws Exception;
}
