package com.tilchina.timp.manager;

import com.tilchina.timp.model.City;
import com.tilchina.timp.model.Corp;
import com.tilchina.timp.model.Unit;
import com.tilchina.timp.model.User;
import com.tilchina.timp.vo.ImportBaseVO;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by demon on 2018/6/11.
 */
public interface ImportBaseManager {
    @Transactional
    void readFile(File file);

    void addUnitUser(Map<String, User> userMap, Map<String, Unit> unitMap);

    @Transactional
    Map<String,User> addUser(List<ImportBaseVO> vos, Map<String, Corp> unitCorpMap);

    @Transactional
    void addCostomerInfo(List<ImportBaseVO> vos, Map<String, Corp> customerMap, Map<String, Unit> unitMap);

    @Transactional
    Map<String,Corp> addCorp(List<ImportBaseVO> vos);

    @Transactional
    Map<String, Unit> addUnit(List<ImportBaseVO> vos, Map<String, Corp> customerMap,
                 Map<String, City> provinceMap, Map<String, City> cityMap);

    // 保存客户公司
    Map<String,Corp> addCustomer(List<ImportBaseVO> vos);
}
