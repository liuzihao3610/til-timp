package com.tilchina.timp.service;

import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.Unit;
import com.tilchina.timp.vo.UnitVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
* 收发货单位
*
* @version 1.1.0
* @author XiaHong
*/
public interface UnitService/* extends BaseService<Unit>*/ {

    UnitVO add(Unit unit);

    List<UnitVO> add(List<Unit> units);

    Map<Long,Unit> queryMap();

    Map<Long,Unit> queryMapByIds(List<Long> unitIds);

    List<Unit> queryByIds(List<Long> unitIds);

/*    List<Unit> queryByIds(Map<String, Object> map, int pageNum, int pageSize);*/

    void deleteList(int[] data);

	PageInfo<Unit> getReferenceList(Map<String, Object> map, int pageNum, int pageSize);

	Unit getSingleUnit(Map<String, Object> map);

	Unit getContactInfoById(Long unitId);

	Unit getByName(String startPlace);

	List<Unit> queryByName(String unitName);

    List<Unit> queryByNames(Set<String> names);

    List<Unit> queryListByNames(List<String> names);

    List<Long> queryByUnitName(String unitName);

    public Unit queryById(Long id);

    public PageInfo<Unit> queryList(Map<String, Object> map, int pageNum, int pageSize);

    public List<Unit> queryList(Map<String, Object> map);

    void updateSelective(Unit record);

    public void update(Unit record);

    public void update(List<Unit> records);

    public void deleteById(Long id);

    void updateBlock(Unit data);

    UnitVO updatePasswords(Unit data);

    String importContacts(Map<String, Object> contacts);

    void importExcel(MultipartFile file) throws Exception;

}
