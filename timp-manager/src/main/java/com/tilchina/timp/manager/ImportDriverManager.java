package com.tilchina.timp.manager;

import com.tilchina.timp.model.Corp;
import com.tilchina.timp.model.Transporter;
import com.tilchina.timp.model.TransporterAndDriver;
import com.tilchina.timp.model.User;
import com.tilchina.timp.vo.ImportDriverVO;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by demon on 2018/6/11.
 */
public interface ImportDriverManager {
    void readFile(File file);

    @Transactional
    void addTransStatus(List<TransporterAndDriver> trans);

    @Transactional
    void addDriverStatus(List<TransporterAndDriver> trans);

    @Transactional
    List<TransporterAndDriver> addDriverAndTrans(Map<String, User> userMap, Map<String, Transporter> transporterMap);

    @Transactional
    Map<String,Transporter> addTransporter(List<ImportDriverVO> vos, Map<String, Long> trailMap, Map<String, User> userMap, Map<String, Corp> corpMap);

    Map<String, User> addUser(List<ImportDriverVO> vos, Map<String, Corp> corpMap);

    // 保存客户公司
    @Transactional
    Map<String,Corp> addCorp(List<ImportDriverVO> vos);
}
