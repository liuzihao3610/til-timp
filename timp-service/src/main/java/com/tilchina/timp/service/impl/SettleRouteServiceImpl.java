package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.SettleRouteMapper;
import com.tilchina.timp.model.SettleRoute;
import com.tilchina.timp.model.SettleRouteB;
import com.tilchina.timp.service.SettleRouteBService;
import com.tilchina.timp.service.SettleRouteService;
import com.tilchina.timp.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* 结算路线主表
*
* @version 1.0.0
* @author LiuShuqi
*/
@Service
@Slf4j
public class SettleRouteServiceImpl extends BaseServiceImpl<SettleRoute> implements SettleRouteService {

	@Autowired
    private SettleRouteMapper settleroutemapper;

	@Autowired
	private SettleRouteBService settleRouteBService;
	
	@Override
	protected BaseMapper<SettleRoute> getMapper() {
		return settleroutemapper;
	}

	@Override
	protected StringBuilder checkNewRecord(SettleRoute settleroute) {
		StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("YES", "settleRouteCode", "路线编号", settleroute.getSettleRouteCode(), 20));
        s.append(CheckUtils.checkString("NO", "settleRouteName", "路线名称", settleroute.getSettleRouteName(), 20));
        s.append(CheckUtils.checkString("YES", "description", "描述", settleroute.getDescription(), 200));
        s.append(CheckUtils.checkDate("NO", "createDate", "创建时间", settleroute.getCreateDate()));
        s.append(CheckUtils.checkInteger("NO", "flag", "封存标志(0=否", settleroute.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(SettleRoute settleroute) {
        StringBuilder s = checkNewRecord(settleroute);
        s.append(CheckUtils.checkPrimaryKey(settleroute.getSettleRouteId()));
		return s;
	}

	/**
	 * 通过结算路线ID获取信息
	 * @param id
	 * @return
	 */
	@Override
	@Transactional
	public SettleRoute queryById(Long id) {
		log.debug("query: {}", id);
		return settleroutemapper.selectByPrimaryKey(id);
	}

	/**
	 * 分页查询所有结算路线
	 * @param map
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@Override
	@Transactional
	public PageInfo<SettleRoute> queryList(Map<String, Object> map, int pageNum, int pageSize) {
		log.debug("queryList: {}, page: {},{}", new Object[]{map, pageNum, pageSize});
		PageHelper.startPage(pageNum, pageSize);
		if (map!=null){
			DateUtil.addTime(map);
		}
		return new PageInfo(settleroutemapper.selectList(map));
	}

	/**
	 * 查询所有结算路线
	 * @param map
	 * @return
	 */
	@Override
	@Transactional
	public List<SettleRoute> queryList(Map<String, Object> map) {
		log.debug("queryList: {}", map);
		if (map!=null){
			DateUtil.addTime(map);
		}
		return settleroutemapper.selectList(map);
	}

	/**
	 * 新增结算路线
	 * @param record
	 */
	@Override
	@Transactional
	public void add(SettleRoute record) {
		log.debug("add: {}", record);

		try {
			StringBuilder s = this.checkNewRecord(record);
			if (!StringUtils.isBlank(s)) {
				throw new BusinessException("数据检查失败：" + s.toString());
			} else {
				Environment environment=Environment.getEnv();
				record.setCreator(environment.getUser().getUserId());
				record.setCreateDate(new Date());
				record.setCorpId(environment.getCorp().getCorpId());
				record.setSettleRouteCode(DateUtil.dateToStringCode(new Date()));
				settleroutemapper.insertSelective(record);
				/**
				 * 获取结算路线并按序号排序
				 */
				List<SettleRouteB> settleRouteBS=record.getSettleRouteBS();
				if (CollectionUtils.isEmpty(settleRouteBS)){
					throw new BusinessException("至少添加一条子路线");
				}
				List<SettleRouteB> bList =settleRouteBS.stream().sorted(Comparator.comparing(SettleRouteB::getSequence)).collect(Collectors.toList());
				String description=new String();
				for (int i = 0; i <bList.size() ; i++) {
					bList.get(i).setSettleRouteId(record.getSettleRouteId());
					settleRouteBService.add(bList.get(i));
					if (i==bList.size()-1){
						description +=bList.get(i).getCityName();
					}else{
						description +=bList.get(i).getCityName()+"-";
					}
				}
				/**
				 * 更新结算路线描述
				 */
				SettleRoute settleRoute=settleroutemapper.selectByPrimaryKey(record.getSettleRouteId());
				settleRoute.setDescription(description);
				settleroutemapper.updateByPrimaryKeySelective(settleRoute);


			}
		} catch (Exception var4) {
			if (var4.getMessage().indexOf("IDX_") != -1) {
				throw new BusinessException("数据重复，请查证后重新保存！", var4);
			} else if (var4 instanceof BusinessException){
				throw var4;
			}else{
				throw new BusinessException("操作失败");
			}
		}
	}

	/**
	 * 修改结算路线
	 * @param record
	 */
	@Override
	@Transactional
	public void updateSelective(SettleRoute record) {
		log.debug("updateSelective: {}", record);

		try {
			SettleRoute settleRoute=settleroutemapper.selectByPrimaryKey(record.getSettleRouteId());
			if (settleRoute==null){
				throw new BusinessException("结算路线不存在");
			}
			Environment environment=Environment.getEnv();
			settleroutemapper.updateByPrimaryKeySelective(record);
			//删除子路线,重新添加
			List<SettleRouteB> bList=settleRouteBService.getBySettleRouteId(record.getSettleRouteId());
			for (SettleRouteB settleRouteB : bList){
				settleRouteBService.deleteById(settleRouteB.getSettleRouteBId());
			}
			List<SettleRouteB> settleRouteBS=record.getSettleRouteBS();
			if (CollectionUtils.isEmpty(settleRouteBS)){
				throw new BusinessException("至少添加一条子路线");
			}
			List<SettleRouteB> newList =settleRouteBS.stream().sorted(Comparator.comparing(SettleRouteB::getSequence)).collect(Collectors.toList());
			String description=new String();
			for (int i = 0; i <newList.size() ; i++) {
				newList.get(i).setSettleRouteId(settleRoute.getSettleRouteId());
				settleRouteBService.add(newList.get(i));
				if (i==newList.size()-1){
					description =description + newList.get(i).getCityName();
				}else{
					description =description + newList.get(i).getCityName()+"-";
				}
			}
			/**
			 * 更新结算路线描述
			 */
			record.setDescription(description);
			settleroutemapper.updateByPrimaryKeySelective(record);

		} catch (Exception var3) {
			if (var3.getMessage().indexOf("IDX_") != -1) {
				throw new RuntimeException("数据重复，请查证后重新提交！", var3);
			} else if (var3 instanceof BusinessException){
				throw var3;
			}else {
				throw new RuntimeException("操作失败");
			}
		}
	}

	/**
	 * 通过ID 删除结算路线
	 * @param id
	 */
	@Override
	@Transactional
	public void deleteById(Long id) {
		log.debug("delete: {}", id);
		SettleRoute settleRoute=settleroutemapper.selectByPrimaryKey(id);
		if (settleRoute==null){
			throw new BusinessException("结算路线不存在");
		}
		settleroutemapper.deleteByPrimaryKey(id);
		List<SettleRouteB> settleRouteBS=settleRouteBService.getBySettleRouteId(settleRoute.getSettleRouteId());
		for (SettleRouteB settleRouteB: settleRouteBS){
			settleRouteBService.deleteById(settleRouteB.getSettleRouteBId());
		}
	}

	/**
	 * 封存
	 * @param settleRouteId
	 */
	@Override
	@Transactional
	public void sequestration(Long settleRouteId) {
		SettleRoute settleRoute=settleroutemapper.selectByPrimaryKey(settleRouteId);
		if (settleRoute==null){
			throw new BusinessException("结算路线不存在");
		}
		settleRoute.setFlag(1);
		settleroutemapper.updateByPrimaryKeySelective(settleRoute);
	}

	/**
	 * 取消封存
	 * @param settleRouteId
	 */
	@Override
	@Transactional
	public void unsequestration(Long settleRouteId) {
		SettleRoute settleRoute=settleroutemapper.selectByPrimaryKey(settleRouteId);
		if (settleRoute==null){
			throw new BusinessException("结算路线不存在");
		}
		settleRoute.setFlag(0);
		settleroutemapper.updateByPrimaryKeySelective(settleRoute);
	}

	/**
	 * 导入结算路线
	 * @param file
	 */
	@Override
	public void importExcel(MultipartFile file) {

	}

}
