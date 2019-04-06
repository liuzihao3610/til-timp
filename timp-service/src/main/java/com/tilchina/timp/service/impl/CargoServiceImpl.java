package com.tilchina.timp.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.model.Brand;
import com.tilchina.timp.model.Cargo;
import com.tilchina.timp.service.CargoService;
import com.tilchina.timp.mapper.CargoMapper;

import java.util.List;
import java.util.Map;

/**
* 货物装载信息表
*
* @version 1.0.0
* @author WangShengguang
*/
@Service
@Slf4j
public class CargoServiceImpl extends BaseServiceImpl<Cargo> implements CargoService {

	@Autowired
    private CargoMapper cargomapper;
	
	@Override
	protected BaseMapper<Cargo> getMapper() {
		return cargomapper;
	}

	@Override
	protected StringBuilder checkNewRecord(Cargo cargo) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkInteger("NO", "cargoType", "货物类型", cargo.getCargoType(), 10));
        s.append(CheckUtils.checkInteger("NO", "floor", "所在层", cargo.getFloor(), 10));
        s.append(CheckUtils.checkInteger("NO", "place", "位置", cargo.getPlace(), 10));
        s.append(CheckUtils.checkInteger("NO", "carryOver", "是否可以结转", cargo.getCarryOver(), 10));
        s.append(CheckUtils.checkInteger("NO", "cargoLong", "长（毫米）", cargo.getCargoLong(), 10));
        s.append(CheckUtils.checkInteger("NO", "cargoWidth", "宽（毫米）", cargo.getCargoWidth(), 10));
        s.append(CheckUtils.checkInteger("YES", "cargoHigh", "高（毫米）", cargo.getCargoHigh(), 10));
        s.append(CheckUtils.checkInteger("YES", "cargoWeight", "限重（kg）", cargo.getCargoWeight(), 10));
        s.append(CheckUtils.checkDate("NO", "createDate", "创建时间", cargo.getCreateDate()));
        s.append(CheckUtils.checkInteger("NO", "flag", "封存标志", cargo.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(Cargo cargo) {
        StringBuilder s = checkNewRecord(cargo);
        s.append(CheckUtils.checkPrimaryKey(cargo.getCargoId()));
		return s;
	}

	@Override
	@Transactional
	public List<Cargo> queryByTrailers(List<Long> trailerIds){
		return cargomapper.selectByTrailers(trailerIds);
	}
	
	/**
     * 查询结果按创建时间排序
     */
	@Override
	@Transactional
	public PageInfo<Cargo> getList(Map<String, Object> map, int pageNum, int pageSize) {
		log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
        
		try {
			PageHelper.startPage(pageNum, pageSize);
			return new PageInfo<Cargo>(cargomapper.getList(map));
		} catch (Exception e) {
			throw e;
		}
	}
}
