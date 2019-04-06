package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.SettleRoute;
import org.springframework.web.multipart.MultipartFile;

/**
* 结算路线主表
*
* @version 1.0.0
* @author LiuShuqi
*/
public interface SettleRouteService extends BaseService<SettleRoute> {
    /**
     * 封存
     * @param data
     */
    void sequestration(Long data);

    /**
     * 取消封存
     * @param data
     */
    void unsequestration(Long data);

    /**
     * 导入结算路线
     * @param file
     */
    void importExcel(MultipartFile file);
}
