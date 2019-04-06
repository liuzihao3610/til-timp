package com.tilchina.timp.service;

import com.github.pagehelper.PageInfo;
import com.tilchina.framework.service.BaseService;
import com.tilchina.timp.model.Regist;
import com.tilchina.timp.vo.RegistTreeVO;
import com.tilchina.timp.vo.RegistVO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
* 
*
* @version 1.1.0
* @author XiaHong
*/
public interface RegistService extends BaseService<Regist> {

	void logicDeleteById(Long id);

	void logicDeleteByIdList(int[]  ids);

	Map<String, Object> queryDynamicList();

    /**
     * 返回树形结构
     * @param userId
     * @return
     */
    List<Regist> queryTreeByUserId(Long userId);

    List<Regist> queryByUserId(Long userId);

    PageInfo<Regist> getRefer(Map<String,Object> data, int pageNum, int pageSize);

    /**
     * 返回树形结构
     * @param data
     * @return
     */
    List<Regist> getUpReferences(Map<String,Object> data);

    /**
     * 返回树形结构
     * @param registIds
     * @return
     */
    List<Regist> queryByRegistId(Set<Long> registIds);

    void addUrls();

    /**
     * 根据功能注册类型返回树形功能注册列表
     * @return
     */
    List<Regist> getRegist();

    List<RegistVO> getButtonRegist(Map<String,Object> data);

    Regist queryByRegistCode(String registCode);

    void test();

    List<RegistTreeVO>  allotPower(Map<String, Object> map);

}
