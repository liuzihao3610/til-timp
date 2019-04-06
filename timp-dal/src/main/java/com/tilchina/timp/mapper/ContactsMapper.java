package com.tilchina.timp.mapper;

import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.timp.model.Contacts;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* 收发货单位联系人
*
* @version 1.0.0
* @author LiuShuqi
*/
@Repository
public interface ContactsMapper extends BaseMapper<Contacts> {
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
     * 批量删除
     * @param data
     */
    void deleteList(int[] data);

    /**
     * 通过收发货单位ID查询联系人
     * @param unitId
     * @return
     */
    List<Contacts> queryByUnitId(Long unitId);

    /**
     * 通过收发货单位联系人名称查询
     * @param contactsName
     * @return
     */
    Contacts queryByName(String contactsName);

    List<Contacts> queryByUnitIds(@Param("unitIds") List<Long> unitIds);
    /**
     * 通过收发货单位和手机号码查询
     * @param unitId
     * @param phone
     * @return
     */
    Contacts getByUnitPhone(Long unitId, String phone);
}
