package com.tilchina.timp.service;

import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.Contacts;
import com.tilchina.timp.vo.ContactsVO;

import java.util.List;

/**
* 收发货单位联系人
*
* @version 1.0.0
* @author LiuShuqi
*/
public interface ContactsService extends BaseService<Contacts> {
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
     * app运单:收发货联系人信息
     * @param startUnitId
     * @param EndUnitId
     * @return
     */
    ContactsVO appQueryByUnitId(Long startUnitId, Long EndUnitId);

    /**
     * app运单:发货联系人信息
     * @param startUnitId
     * @return
     */
    List<ContactsVO> queryByStartUnitId(Long startUnitId);

    /**
     * app运单:收货联系人信息
     * @param endUnitId
     * @return
     */
    List<ContactsVO> queryByEndUnitId(Long endUnitId);

    /**
     * 通过收发货单位联系人名称查询
     * @param contactsName
     * @return
     */
    Contacts queryByName(String contactsName);

    List<Contacts> queryByUnitIds(List<Long> unitIds);

    /**
     * 通过收发货单位和手机号码查询
     * @param unitId
     * @param phone
     * @return
     */
    Contacts getByUnitPhone(Long unitId, String phone);
}
