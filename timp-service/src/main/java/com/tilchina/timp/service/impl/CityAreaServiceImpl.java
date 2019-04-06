package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.framework.enmus.RowStatus;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.CityAreaDetailMapper;
import com.tilchina.timp.model.CityAreaDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.timp.model.CityArea;
import com.tilchina.timp.service.CityAreaService;
import com.tilchina.timp.mapper.CityAreaMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 虚拟城市区域
 *
 * @author WangShengguang
 * @version 1.0.0
 */
@Service
@Slf4j
public class CityAreaServiceImpl implements CityAreaService {

    @Autowired
    private CityAreaMapper cityAreaMapper;

    @Autowired
    private CityAreaDetailMapper cityAreaDetailMapper;

    @Override
    public CityArea queryById(Long id) {
        if (id == null) {
            throw new BusinessException("9003");
        }
        return cityAreaMapper.selectByPrimaryKey(id);
    }

    @Override
    public PageInfo<CityArea> queryList(Map<String, Object> map, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo(queryList(map));
    }

    @Override
    public List<CityAreaDetail> queryDetails(Long cityAreaId){
        if(cityAreaId == null){
            throw new BusinessException("9003");
        }
        return cityAreaDetailMapper.selectByCityAreaId(cityAreaId);
    }

    @Override
    public List<CityArea> queryList(Map<String, Object> map) {
        Environment env = Environment.getEnv();
        if (map == null) {
            map = new HashMap<>();
        }
        map.put("corpId", env.getCorp().getCorpId());
        return cityAreaMapper.selectList(map);
    }

    @Override
    @Transactional
    public void add(CityArea record) {
        if (record == null) {
            throw new BusinessException("9003");
        }
        StringBuilder s = checkNewRecord(record);
        if (s.length() > 0) {
            throw new BusinessException(s.toString());
        }

        List<CityAreaDetail> details = record.getDetails();
        if (CollectionUtils.isEmpty(details)) {
            throw new BusinessException("9011");
        }
        StringBuilder ds = checkNewDetails(details);
        if (ds.length() > 0) {
		    throw new BusinessException(ds.toString());
        }

        Environment env = Environment.getEnv();
        record.setCorpId(env.getCorp().getCorpId());
        record.setCreateDate(new Date());
        record.setCreator(env.getUser().getUserId());
        try{
            cityAreaMapper.insert(record);
        }catch(Exception e){
            if(e.getMessage().indexOf("IDX_") != -1){
                throw new BusinessException("编码或名称已存在！");
            }else{
                throw e;
            }
        }
        for (CityAreaDetail detail : details) {
            detail.setCityAreaId(record.getCityAreaId());
            detail.setCorpId(env.getCorp().getCorpId());
        }
        try{
            cityAreaDetailMapper.insert(details);
        }catch(Exception e){
            if(e.getMessage().indexOf("IDX_") != -1){
                throw new BusinessException("明细数据收发货单位重复！");
            }else{
                throw e;
            }
        }
    }

    @Override
    public void add(List<CityArea> records) {
        throw new BusinessException("暂未实现");
    }

    @Override
    @Transactional
    public void updateSelective(CityArea record) {
        if (record == null || record.getCityAreaId() == null) {
            throw new BusinessException("9003");
        }

        StringBuilder s = checkNewRecord(record);
        if (s.length() > 0) {
            throw new BusinessException(s.toString());
        }

        List<CityAreaDetail> details = record.getDetails();
        if (CollectionUtils.isEmpty(details)) {
            throw new BusinessException("9011");
        }

        Environment env = Environment.getEnv();
        StringBuilder ds = new StringBuilder();
        List<CityAreaDetail> adds = new ArrayList<>();
        List<CityAreaDetail> updates = new ArrayList<>();
        List<CityAreaDetail> dels = new ArrayList<>();
        for (int i = 0; i < details.size(); i++) {
            CityAreaDetail detail = details.get(i);
            if(detail.getUnitId() == null){
                ds.append("明细数据第" + (i + 1) + "行，收发货单位不能为空！");
                continue;
            }
            switch (RowStatus.get(detail.getRowStatus())){
                case NEW:
                    detail.setCorpId(env.getCorp().getCorpId());
                    detail.setCityAreaId(record.getCityAreaId());
                    adds.add(detail);
                    break;
                case EDIT:
                    if(detail.getCityAreaDetailId()==null){
                        ds.append("明细数据第" + (i + 1) + "行，参数传递错误！");
                        break;
                    }
                    updates.add(detail);
                    break;
                case DELETE:
                    if(detail.getCityAreaDetailId()==null){
                        ds.append("明细数据第" + (i + 1) + "行，参数传递错误！");
                        break;
                    }
                    dels.add(detail);
                    break;
                default: break;
            }
        }

        if (ds.length() > 0) {
            throw new BusinessException(ds.toString());
        }

        int r = cityAreaMapper.updateByPrimaryKeySelective(record);
        if(r == 0){
            throw new BusinessException("不存在或已删除！");
        }
        try {
            if (CollectionUtils.isNotEmpty(adds)) {
                cityAreaDetailMapper.insert(adds);
            }
            if (CollectionUtils.isNotEmpty(updates)) {
                cityAreaDetailMapper.updateByPrimaryKeySelective(updates);
            }
        }catch(Exception e){
            if(e.getMessage().indexOf("IDX_") != -1){
                throw new BusinessException("明细数据收发货单位重复！");
            }else{
                throw e;
            }
        }
        if(CollectionUtils.isNotEmpty(dels)){
            cityAreaDetailMapper.deleteByPrimaryKey(dels);
        }

    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if(id == null){
            throw new BusinessException("9003");
        }
        cityAreaMapper.deleteByPrimaryKey(id);
        cityAreaDetailMapper.deleteDetailsByCityAreaId(id);
    }

    private StringBuilder checkNewRecord(CityArea cityarea) {
        StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("NO", "", "虚拟区域编码", cityarea.getCaCode(), 12));
        s.append(CheckUtils.checkString("NO", "", "虚拟区域名称", cityarea.getCaName(), 20));
        return s;
    }

    private StringBuilder checkNewDetails(List<CityAreaDetail> details) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < details.size(); i++) {
            StringBuilder sub = new StringBuilder();
            CityAreaDetail detail = details.get(i);
            sub.append(CheckUtils.checkLong("NO", "", "收发货单位", detail.getUnitId(), 20));
            if (sub.length() > 0) {
                s.append("明细数据第" + (i + 1) + "行，收发货单位不能为空！");
            }
        }
        return s;
    }

    private StringBuilder checkUpdate(CityArea cityarea) {
        StringBuilder s = checkNewRecord(cityarea);
        s.append(CheckUtils.checkPrimaryKey(cityarea.getCityAreaId()));
        return s;
    }
}
