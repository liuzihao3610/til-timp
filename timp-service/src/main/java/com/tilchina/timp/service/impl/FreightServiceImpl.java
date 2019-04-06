package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.enums.OfferType;
import com.tilchina.timp.enums.ValuationMode;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.FreightMapper;
import com.tilchina.timp.model.*;
import com.tilchina.timp.service.*;
import com.tilchina.timp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 运价管理
 *
 * @author LiuShuqi
 * @version 1.0.0
 */
@Service
@Slf4j
public class FreightServiceImpl extends BaseServiceImpl<Freight> implements FreightService {

	@Autowired
	private FreightMapper freightmapper;

	@Autowired
	private BrandService brandService;

	@Autowired
	private CarTypeService carTypeService;

	@Autowired
	private FreightService freightService;

	@Autowired
    private CityService cityService;

	@Autowired
	private UnitService unitService;

    @Autowired
    private TransportOrderService transportOrderService;

	@Autowired
	private CorpService corpService;

	@Autowired
	private TransportOrderDetailService transportOrderDetailService;


	@Override
	protected BaseMapper<Freight> getMapper() {
		return freightmapper;
	}

	/**
     * 获取运单标准运价的合计价格
     *
     * @since 1.0.0
     * @param order     运单，需携带明细数据
     * @return java.math.BigDecimal
     */
	@Transactional
    @Override
	public BigDecimal getAllPrice(TransportOrder order){
		getPrice(order);
		List<TransportOrderDetail> details = order.getDetails();
		BigDecimal price = BigDecimal.ZERO;
		for (TransportOrderDetail detail : details) {
			price = price.add(detail.getFreight().getFinalPrice());
		}
		return price;
	}

	/**
	 * 获取运单标准运价的合计价格
	 *
	 * @since 1.0.0
	 * @param details     运单明细
	 * @return java.math.BigDecimal
	 */
	@Transactional
	@Override
	public BigDecimal getAllPrice(List<TransportOrderDetail> details){
		getPrice(details);
		BigDecimal price = BigDecimal.ZERO;
		for (TransportOrderDetail detail : details) {
			price = price.add(detail.getFreight().getFinalPrice());
		}
		return price;
	}

	public void getPrice(List<TransportOrderDetail> details){
		/*if(order == null){
			throw new BusinessException("运单为空！");
		}
		if(CollectionUtils.isEmpty(order.getDetails())){
			throw new BusinessException("运单明细为空！");
		}
		List<TransportOrderDetail> details = order.getDetails();*/
		// 获取实际结算城市
		Set<Long> accountCitys = new HashSet<>();
		String sendCity = null;
		Long sendCityId = null;
		for (TransportOrderDetail detail : details) {
			accountCitys.add(detail.getAccountCityId());
			if(sendCity == null){
				sendCity = detail.getRefSendUnitCity();
				sendCityId = detail.getSendCityId();
			}
		}
		Double distance = 0D;
		Long accountCity = null;
		for (Long cityId : accountCitys) {
			City city = cityService.queryById(cityId);
			Double d = GaodeApi.getDistanceByAddress(sendCity,city.getCityName());
			if( distance < d ){
				distance = d;
				accountCity = cityId;
			}
		}

		for (TransportOrderDetail detail : details) {
            TransportOrder transportOrder = transportOrderService.queryById(detail.getTransportOrderId());
            Optional.ofNullable(transportOrder)
                    .orElseThrow(() ->  new BusinessException("运单为空！"));
            Freight f = getFreight(sendCityId,accountCity,detail.getRefBrandId(), detail.getRefCarTypeId(),transportOrder.getReceivingDate());
			if(f==null){
				throw new BusinessException("商品车品牌："+detail.getRefBrandName()+"，商品车类型："+detail.getRefCategoryName()+",发货城市："+detail.getRefSendUnitCity()+"，目的地城市："+detail.getRefReceiveUnitCity()+"，没有可用的运价！");

			}
			detail.setFreight(f);
			detail.setFinalPrice(f.getFinalPrice());
			detail.setFreightId(f.getFreightId());
		}
		transportOrderDetailService.updateSelective(details);
	}


	/**
     * 运单明细获取运价信息，填充到明细对象中的Freight属性
     *
     * @since 1.0.0
     * @param order     运单，需携带明细数据
     * @return void
     */
    @Override
	public void getPrice(TransportOrder order){
		if(order == null){
			throw new BusinessException("运单为空！");
		}
		if(CollectionUtils.isEmpty(order.getDetails())){
			throw new BusinessException("运单明细为空！");
		}
		List<TransportOrderDetail> details = order.getDetails();
		// 获取实际结算城市
        Set<Long> accountCitys = new HashSet<>();
        String sendCity = null;
        Long sendCityId = null;
        for (TransportOrderDetail detail : details) {
            accountCitys.add(detail.getAccountCityId());
            if(sendCity == null){
                sendCity = detail.getRefSendUnitCity();
                sendCityId = detail.getSendCityId();
            }
        }
        Double distance = 0D;
        Long accountCity = null;
        for (Long cityId : accountCitys) {
            City city = cityService.queryById(cityId);
            Double d = GaodeApi.getDistanceByAddress(sendCity,city.getCityName());
            if( distance < d ){
                distance = d;
                accountCity = cityId;
            }
        }

		for (TransportOrderDetail detail : details) {
			Freight f = getFreight(sendCityId,accountCity,detail.getRefBrandId(), detail.getRefCarTypeId(),order.getReceivingDate());
			if(f==null){
				throw new BusinessException("商品车品牌："+detail.getRefBrandName()+"，商品车类型："+detail.getRefCategoryName()+",发货城市："+detail.getRefSendUnitCity()+"，目的地城市："+detail.getRefReceiveUnitCity()+"，没有可用的运价！");

			}
			detail.setFreight(f);
			detail.setFinalPrice(f.getFinalPrice());
			detail.setFreightId(f.getFreightId());
		}
		transportOrderDetailService.updateSelective(details);
	}

	/**
     *
     * 根据启运地、目的地、品牌、类别、单据日期获取运价的实际价格
     *
     * @since 1.0.0
     * @param startPlaceId      起运地城市
     * @param arrivalPlaceId    目的地城市
     * @param brandId           品牌ID
     * @param carTypeId         车型类别ID
     * @param billDate          单据日期
     * @return java.math.BigDecimal
     */
    @Override
	public BigDecimal getPrice(Long startPlaceId, Long arrivalPlaceId, Long brandId, Long carTypeId, Date billDate){
		Freight f = getFreight(startPlaceId, arrivalPlaceId, brandId, carTypeId, billDate);
		if(f != null) {
			return f.getFinalPrice();
		}
		return null;
	}

    /**
     *
     * 根据启运地、目的地、品牌、类别、单据日期获取运价对象
     *
     * @since 1.0.0
     * @param startPlaceId      起运地城市
     * @param arrivalPlaceId    目的地城市
     * @param brandId           品牌ID
     * @param carTypeId         车型类别ID
     * @param billDate          单据日期
     * @return java.math.BigDecimal
     */
	@Override
	public Freight getFreight(Long startPlaceId, Long arrivalPlaceId, Long brandId, Long carTypeId, Date billDate){
		Map<String,Object> map = new HashMap<>();
		map.put("startPlaceId",startPlaceId);
		map.put("arrivalPlaceId",arrivalPlaceId);
		map.put("brandId",brandId);
		map.put("carTypeId",carTypeId);
		map.put("effectiveDate",billDate);
		Freight f = freightmapper.getOneFreight(map);
		if(f != null) {
			f.setFinalPrice(f.getUnitPrice());
		}
		return f;
	}

	public static String checkBigDecimal(String nullable, String attName, String comment, BigDecimal dou, int length, int scale) {
		String desc = comment + "[" + attName + "]";
		String str = dou + "";
		if ("".equals(str.trim())) {
			if (!isNullAble(nullable)) {
				return desc + " can not be null! ";
			} else {
				return "";
			}
		}
        if ((str.substring(0, str.indexOf("."))).length() > length) {
            return desc + ":" + dou + "  小数点只能精确到小数点后" + length + "位; ";
        } else if ((str.substring(str.indexOf(".")+1, str.length())).length() > scale) {
            return desc + ":" + dou + " 小数点只能精确到小数点后" + scale + "位; ";
        }
		return "";
	}

	public static String checkDouble(String nullable, String attName, String comment, Double dou, int length, int scale) {
		String desc = comment + "[" + attName + "]";
		String str = dou + "";
		if ("".equals(str.trim())) {
			if (!isNullAble(nullable)) {
				return desc + " can not be null! ";
			} else {
				return "";
			}
		}
		if ((str.substring(0, str.indexOf("."))).length() > length) {
			return desc + ":" + dou + "  小数点只能精确到小数点后" + length + "位; ";
		} else if ((str.substring(str.indexOf(".")+1, str.length())).length() > scale) {
			return desc + ":" + dou + " 小数点只能精确到小数点后" + scale + "位; ";
		}
		return "";
	}

	private static boolean isNullAble(String nullable) {
		return "YES".equals(nullable.toUpperCase()) || "Y".equals(nullable.toUpperCase());
	}

	@Override
	protected StringBuilder checkNewRecord(Freight freight) {
		StringBuilder s = new StringBuilder();
		Double aDouble = Optional.ofNullable(freight.getKilometer()).orElse(0.00);
		freight.setKilometer(aDouble);
		freight.setFreightCode(DateUtil.dateToStringCode(new Date()));
		s.append(CheckUtils.checkString("YES", "freightCode", "报价编号", freight.getFreightCode(), 20));
		s.append(CheckUtils.checkInteger("NO", "freightType", "报价类型(0=标准价", freight.getFreightType(), 10));
		s.append(CheckUtils.checkDate("NO", "effectiveDate", "生效日期", freight.getEffectiveDate()));
		s.append(CheckUtils.checkDate("NO", "failureDate", "失效日期", freight.getFailureDate()));
		s.append(CheckUtils.checkInteger("NO", "denominatedMode", "计价方式(0=统一价格", freight.getDenominatedMode(), 10));
		s.append(checkBigDecimal("NO", "unitPrice", "单价", freight.getUnitPrice(), 10, 2));
		s.append(CheckUtils.checkLong("YES", "carTypeId", "车型类别", freight.getCarTypeId(), 20));

		freight.setFreightRate(Optional.ofNullable(freight.getFreightRate()).orElse(new BigDecimal("0.00")));
		s.append(checkBigDecimal("YES", "freightRate", "起运价", freight.getFreightRate(), 10, 2));

		freight.setPerKilometerPrice(Optional.ofNullable(freight.getPerKilometerPrice()).orElse(new BigDecimal("0.00")));
		s.append(checkBigDecimal("YES", "perKilometerPrice", "每公里单价", freight.getPerKilometerPrice(), 10, 2));

		s.append(checkDouble("YES", "kilometer", "公里数",freight.getKilometer() , 10, 2));

		freight.setHandleCharge(Optional.ofNullable(freight.getHandleCharge()).orElse(new BigDecimal("0.00")));
		s.append(checkBigDecimal("YES", "handleCharge", "装卸费", freight.getHandleCharge(), 10, 2));
		s.append(CheckUtils.checkString("YES", "remark", "描述", freight.getRemark(), 200));
		s.append(CheckUtils.checkInteger("YES", "billStatus", "单据状态(0=制单", freight.getBillStatus(), 10));
		s.append(CheckUtils.checkLong("NO", "sendUnitId", "发货单位ID", freight.getSendUnitId(), 20));
		s.append(CheckUtils.checkLong("NO", "receiveUnitId", "收货单位ID", freight.getReceiveUnitId(), 20));
		s.append(CheckUtils.checkLong("NO", "settlementCorpId", "结算单位ID", freight.getSettlementCorpId(), 20));
		freight.setCreateDate(new Date());
		s.append(CheckUtils.checkDate("YES", "createDate", "创建时间", freight.getCreateDate()));
		s.append(CheckUtils.checkDate("YES", "checkDate", "审核时间", freight.getCheckDate()));
		s.append(CheckUtils.checkInteger("YES", "flag", "封存标志(0=否", freight.getFlag(), 10));
		return s;
	}

	@Override
	protected StringBuilder checkUpdate(Freight freight) {
		StringBuilder s = checkNewRecord(freight);
		s.append(CheckUtils.checkPrimaryKey(freight.getFreightId()));
		return s;
	}

	@Override
	@Transactional
	public Freight queryById(Long id) {
		log.debug("query: {}", id);
		return freightmapper.selectByPrimaryKey(id);
	}

	@Override
	@Transactional
	public PageInfo<Freight> queryList(Map<String, Object> map, int pageNum, int pageSize) {
		log.debug("queryList: {}, page: {},{}", map, pageNum, pageSize);
		PageHelper.startPage(pageNum, pageSize);
		return new PageInfo<Freight>(freightmapper.selectList(map));
	}

	@Override
	@Transactional
	public List<Freight> queryList(Map<String, Object> map) {
		log.debug("queryList: {}", map);
		return freightmapper.selectList(map);
	}

	@Override
	@Transactional
	public void add(Freight record) {
		log.debug("add: {}", record);
		StringBuilder s;
		try {
			s = checkNewRecord(record);
			if (!StringUtils.isBlank(s)) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}
			City city = cityService.queryById(record.getStartPlaceId());
			if (null == city) {
				throw new BusinessException("起运地城市不存在");
			}
			City c = cityService.queryById(record.getArrivalPlaceId());
			if (null == c) {
				throw new BusinessException("目的地城市不存在");
			}
			Brand brand = brandService.queryById(record.getBrandId());
			if (null == brand) {
				throw new BusinessException("品牌不存在");
			}
			Environment environment = Environment.getEnv();
			record.setCreator(environment.getUser().getUserId());
			if (record.getCorpId()==null){
				record.setCorpId(environment.getCorp().getCorpId());
			}
			freightmapper.insertSelective(record);
		} catch (Exception e) {
			if (e.getMessage().indexOf("IDX_") != -1) {
				throw new BusinessException("数据重复，请查证后重新提交！", e);
			} else if (e instanceof BusinessException) {
				throw e;
			} else {
				throw e;
			}
		}
	}

	@Override
	@Transactional
	public void add(List<Freight> records) {
		log.debug("addBatch: {}", records);
		StringBuilder s = new StringBuilder();
		boolean checkResult = true;
		try {
			for (int i = 0; i < records.size(); i++) {
				Freight record = records.get(i);
				StringBuilder check = checkNewRecord(record);
				if (!StringUtils.isBlank(check)) {
					checkResult = false;
					s.append("第" + (i + 1) + "行，" + check + "/r/n");
				}
			}
			if (!checkResult) {
				throw new RuntimeException("数据检查失败：" + s.toString());
			}

			freightmapper.insert(records);
		} catch (Exception e) {
			if (e.getMessage().indexOf("IDX_") != -1) {
				throw new RuntimeException("数据重复，请查证后重新保存！", e);
			} else {
				throw new RuntimeException("批量保存失败！", e);
			}
		}
	}

	@Override
	@Transactional
	public void updateSelective(Freight record) {
		log.debug("updateSelective: {}", record);
		StringBuilder s=new StringBuilder();
		try {
			s.append(CheckUtils.checkLong("NO", "freightId", "运价管理ID", record.getFreightId(), 20));
			if (!StringUtils.isBlank(s)) {
				throw new BusinessException("数据检查失败：" + s.toString());
			}
			City city = cityService.queryById(record.getStartPlaceId());
			if (null == city) {
				throw new BusinessException("起运地城市不存在");
			}
			City c = cityService.queryById(record.getArrivalPlaceId());
			if (null == c) {
				throw new BusinessException("目的地城市不存在");
			}
			Brand brand = brandService.queryById(record.getBrandId());
			if (null == brand) {
				throw new BusinessException("品牌不存在");
			}
			freightmapper.updateByPrimaryKeySelective(record);


		} catch (Exception e) {
			if (e.getMessage().indexOf("IDX_") != -1) {
				throw new BusinessException("数据重复，请查证后重新提交！", e);
			} else if (e instanceof BusinessException) {
				throw e;
			} else {
				throw new RuntimeException("操作失败");
			}
		}
	}

	@Override
	@Transactional
	public void update(Freight record) {
		log.debug("update: {}", record);
		StringBuilder s;
		try {
			s = checkUpdate(record);
			if (!StringUtils.isBlank(s)) {
				throw new RuntimeException("数据检查失败：" + s.toString());
			}

			freightmapper.updateByPrimaryKey(record);
		} catch (Exception e) {
			if (e.getMessage().indexOf("IDX_") != -1) {
				throw new RuntimeException("数据重复，请查证后重新提交！", e);
			} else {
				throw new RuntimeException("更新失败！", e);
			}
		}
	}

	@Override
	@Transactional
	public void update(List<Freight> records) {
		log.debug("updateBatch: {}", records);
		StringBuilder s = new StringBuilder();
		boolean checkResult = true;
		try {
			for (int i = 0; i < records.size(); i++) {
				Freight record = records.get(i);
				StringBuilder check = checkUpdate(record);
				if (!StringUtils.isBlank(check)) {
					checkResult = false;
					s.append("第" + (i + 1) + "行，" + check + "/r/n");
				}
			}
			if (!checkResult) {
				throw new RuntimeException("数据检查失败：" + s.toString());
			}

			freightmapper.update(records);
		} catch (Exception e) {
			if (e.getMessage().indexOf("IDX_") != -1) {
				throw new RuntimeException("数据重复，请查证后重新提交！", e);
			} else {
				throw new RuntimeException("批量更新失败！", e);
			}
		}

	}

	@Override
	@Transactional
	public void deleteById(Long id) {
		log.debug("delete: {}", id);
		try {
			Freight freight = freightmapper.selectByPrimaryKey(id);
			if (freight == null) {
				throw new BusinessException("这条记录不存在");
			}
			freightmapper.deleteByPrimaryKey(id);
		} catch (Exception e) {
			throw e;
		}

	}

	@Override
	@Transactional
	public void deleteList(int[] freightId) {
		try {
			for (int i = 0; i < freightId.length; i++) {
				Freight freight = freightmapper.selectByPrimaryKey((long) freightId[i]);
				if (freight == null) {
					throw new BusinessException("这条记录不存在");
				}
			}
			freightmapper.deleteList(freightId);
		} catch (Exception e) {
			throw e;
		}

	}

	@Override
	@Transactional
	public void audit(Long freightId) {
		try {
			Freight freight = freightmapper.selectByPrimaryKey(freightId);
			if (freight == null) {
				throw new BusinessException("这条记录不存在");
			}
			if (freight.getBillStatus() == 1) {
				throw new BusinessException("已经是审核状态,无需审核");
			}
			Environment environment = Environment.getEnv();
			freight.setChecker(environment.getUser().getUserId());
			freight.setCheckDate(new Date());
			freight.setBillStatus(1);
			freightmapper.updateByPrimaryKeySelective(freight);
			//	维护运单子表里的结算价格
			transportOrderDetailService.maintainFreighPrice(freight);
		} catch (Exception e) {
			throw e;
		}

	}

	@Override
	@Transactional
	public void unaudit(Long freightId) {
		try {
			Freight freight = freightmapper.selectByPrimaryKey(freightId);
			if (freight == null) {
				throw new BusinessException("这条记录不存在");
			}
			if (freight.getBillStatus() == 0) {
				throw new BusinessException("已经是制单状态,无需取消");
			}
			freight.setChecker(null);
			freight.setCheckDate(null);
			freightService.updateCheckDate(freight);
		} catch (Exception e) {
			throw e;
		}

	}

	@Override
	@Transactional
	public void updateCheckDate(Freight freight) {
		Freight f = freightmapper.selectByPrimaryKey(freight.getFreightId());
		if (f == null) {
			throw new BusinessException("记录不存在");
		}
		freightmapper.updateCheckDate(freight.getFreightId());

	}

	@Override
	@Transactional
	public List<Freight> getReferenceList(Map<String, Object> map) {
		try {
			return freightmapper.getReferenceList(map);
		} catch (Exception e) {
			throw e;
		}

	}

	@Override
	@Transactional
	public Freight getFreight(Date orderDate,Long SeCityId, Long ReCityId, Long brandId, Long carTypeId) {
		try {
			if (SeCityId == null || ReCityId == null || brandId == null || carTypeId == null) {
				throw new BusinessException("发货城市或收货城市或品牌或商品车类型不能为空");
			}
			City city = cityService.queryById(SeCityId);
			if (city == null) {
				throw new BusinessException("发货城市不存在");
			}
			City c = cityService.queryById(ReCityId);
			if (c == null) {
				throw new BusinessException("收货城市不存在");
			}
			Brand brand = brandService.queryById(brandId);
			if (brand == null) {
				throw new BusinessException("品牌不存在");
			}
			CarType carType = carTypeService.queryById(carTypeId);
			if (carType == null) {
				throw new BusinessException("商品车类型不存在");
			}

			Freight freight = freightmapper.getFreight(orderDate,SeCityId, ReCityId, brandId, carTypeId);
			if (freight == null) {
				throw new BusinessException("运价信息不存在,请前往运价管理页面新增");
			}
			return freight;
		} catch (Exception e) {
			if (e instanceof BusinessException) {
				throw e;
			} else {
				throw e;
			}
		}

	}

//	/**
//	 * 导入结算价格
//	 * @param file
//	 * @throws Exception
//	 */
//	@Override
//	@Transactional
//	public void importFile(MultipartFile file) throws Exception {
//		log.info("File: {}", file.getOriginalFilename());
//		String filePath=null;
//		Workbook workbook=null;
//		String validateWorkbook=null;
//		try{
//			filePath=FileUtil.uploadFile(file,"freight");
//			workbook=ExcelUtil.getWorkbook(filePath);
//			Map<String, Boolean> nullableMap=new HashMap<String, Boolean>();
//			nullableMap.put("报价类型",false);
//			nullableMap.put("生效日期",false);
//			nullableMap.put("失效日期",false);
//			nullableMap.put("计价方式",false);
//			nullableMap.put("单价",false);
//			nullableMap.put("收货单位",false);
//			nullableMap.put("发货单位",false);
//			nullableMap.put("结算单位",false);
//			validateWorkbook=ExcelUtil.validateWorkbook(workbook,nullableMap);
//			List<Freight> freights=ExcelUtil.getModelsFromWorkbook(workbook,Freight.class);
//			Environment env=Environment.getEnv();
//            for (Freight freight : freights) {
//                add(freight);
//            }
//		}catch (Exception e){
//			log.error("{}", e);
//			if (e instanceof BusinessException){
//				throw e;
//			}else{
//				throw new BusinessException("导入失败");
//			}
//		}
//	}
	/**
	 * 导入EXCEL
	 * @throws Exception
	 */
	@Override
	@Transactional
	public void importFile(MultipartFile file) throws Exception {
		log.debug("File: {}", file.getOriginalFilename());
		String filePath=null;
		try {
			 filePath= FileUtil.uploadFile(file, "freight");
			 Workbook workbook = ExcelUtil.getWorkbook(filePath);
			//写入数据库
			readExcel(workbook);
		} catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	/**
	 * 读取excel
	 * @param workbook
	 * @return
	 */
	@Transactional
	public void readExcel(Workbook workbook) {
		try {
			Environment environment=Environment.getEnv();
			List<ImportFreight> importFreights=ImportExeclUtil.readDateListT(workbook,new ImportFreight(),2,0);
			//所有的城市Map
			List<String> citys=new ArrayList<>();
			Map<String,Long> cityMap=new HashMap<>();
			//所有的品牌Map
			List<String> brands=new ArrayList<>();
			Map<String,Long> brandMap=new HashMap<>();
			//所有的类型Map
			List<String> carTypes=new ArrayList<>();
			Map<String,Long> carTypeMap=new HashMap<>();
			//所有收发货单位Map
			List<String> units=new ArrayList<>();
			Map<String,Long> unitMap=new HashMap<>();
			//所有收结算单位(公司)Map
			List<String> corps=new ArrayList<>();
			Map<String,Long> corpMap=new HashMap<>();
			for (ImportFreight impFreight:importFreights){
					//起运地
					if (impFreight.getStartPlaceName().trim().length()!=0){
						citys.add(impFreight.getStartPlaceName().trim());
					}
					//目的地
					if (impFreight.getArrivalPlaceName().trim().length()!=0){
						citys.add(impFreight.getArrivalPlaceName().trim());
					}
					//所有品牌
					if (impFreight.getBrandName().trim().length()!=0){
						brands.add(impFreight.getBrandName().trim());
					}
					//所有车类型
					if (impFreight.getCarTypeName().trim().length()!=0){
						carTypes.add(impFreight.getCarTypeName().trim());
					}
					//收发货单位
					if (impFreight.getSendUnitName().trim().length()!=0){
						units.add(impFreight.getSendUnitName().trim());
					}
					if (impFreight.getReceiveUnitName().trim().length()!=0){
						units.add(impFreight.getReceiveUnitName().trim());
					}
					//结算单位
					if (impFreight.getSettlementCorpName().trim().length()!=0){
						corps.add(impFreight.getSettlementCorpName().trim());
					}
			}
			//对城市去重
			citys=citys.stream().distinct().collect(Collectors.toList());
			for (int i = 0; i <citys.size() ; i++) {
				if (cityService.queryIdByName(citys.get(i))!=null){
					cityMap.put(citys.get(i),cityService.queryIdByName(citys.get(i)));
				}else{
					throw new BusinessException(citys.get(i)+"不存在");
				}
			}
			//对品牌去重
			brands=brands.stream().distinct().collect(Collectors.toList());
			for (int i = 0; i <brands.size() ; i++) {
				if (brandService.queryIdByName(brands.get(i))!=null){
					brandMap.put(brands.get(i),brandService.queryIdByName(brands.get(i)));
				}else{
					throw new BusinessException(brands.get(i)+"不存在");
				}
			}
			//对车类型去重
			carTypes=carTypes.stream().distinct().collect(Collectors.toList());
			for (int i = 0; i <carTypes.size() ; i++) {
				if (carTypeService.queryIdByName(carTypes.get(i))!=null){
					carTypeMap.put(carTypes.get(i),carTypeService.queryIdByName(carTypes.get(i)));
				}else{
					throw new BusinessException(carTypes.get(i)+"不存在");
				}
			}
			//对收发货单位去重
			units=units.stream().distinct().collect(Collectors.toList());
			for (int i = 0; i <units.size() ; i++) {
				if (unitService.getByName(units.get(i))!=null){
					unitMap.put(units.get(i),unitService.getByName(units.get(i)).getUnitId());
				}else{
					throw new BusinessException(units.get(i)+"不存在");
				}
			}
			//对结算单位去重
			corps=corps.stream().distinct().collect(Collectors.toList());
			for (int i = 0; i <corps.size() ; i++) {
				if (corpService.getByCorpName(corps.get(i))!=null){
					corpMap.put(corps.get(i),corpService.getByCorpName(corps.get(i)).getCorpId());
				}else{
					throw new BusinessException(corps.get(i)+"不存在");
				}
			}
			for (ImportFreight importFreight:importFreights
				 ) {
				Freight freight=new Freight();
				freight.setFreightCode(DateUtil.dateToStringCode(new Date()));
				freight.setFreightType(OfferType.get(importFreight.getFreightType()).getIndex());
				freight.setEffectiveDate(importFreight.getEffectiveDate());
				freight.setFailureDate(importFreight.getFailureDate());
				freight.setStartPlaceId(cityMap.get(importFreight.getStartPlaceName().trim()));
				freight.setArrivalPlaceId(cityMap.get(importFreight.getArrivalPlaceName().trim()));
				freight.setBrandId(brandMap.get(importFreight.getBrandName().trim()));
				freight.setCarTypeId(carTypeMap.get(importFreight.getCarTypeName().trim()));
				freight.setDenominatedMode(ValuationMode.get(importFreight.getDenominatedMode()).getIndex());
				freight.setUnitPrice(importFreight.getUnitPrice());
				freight.setFreightRate(importFreight.getFreightRate());
				freight.setPerKilometerPrice(importFreight.getPerKilometerPrice());
				BigDecimal bigDecimal=Optional.ofNullable(importFreight.getKilometer()).orElse(new BigDecimal("0.00"));
				freight.setKilometer(bigDecimal.doubleValue());
				freight.setHandleCharge(importFreight.getHandleCharge());
				freight.setRemark(importFreight.getRemark());
				freight.setBillStatus(0);
				freight.setSendUnitId(unitMap.get(importFreight.getSendUnitName().trim()));
				freight.setReceiveUnitId(unitMap.get(importFreight.getReceiveUnitName().trim()));
				freight.setSettlementCorpId(corpMap.get(importFreight.getSettlementCorpName().trim()));
				freight.setCreator(environment.getUser().getUserId());
				freight.setCreateDate(new Date());
				freight.setChecker(null);
				freight.setCheckDate(null);
				freight.setCorpId(environment.getCorp().getCorpId());
				freight.setFlag(0);
				freightService.add(freight);
			}
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw new BusinessException(e.getMessage());
			}else{
				throw new BusinessException("导入失败");
			}
		}
	}

	/**
	 * 获取结算价格
	 * @param map
	 * @return
	 */
	@Override
	@Transactional
	public Freight getFreight(Map<String, Object> map) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy:MM:dd");
		Date orderDate=new Date();
		try {
			orderDate=sdf.parse(map.get("orderDate").toString());
			Long seCityId=Long.parseLong(map.get("seCityId").toString());
			Long reCityId=Long.parseLong(map.get("reCityId").toString());
			Long brandId=Long.parseLong(map.get("brandId").toString());
			Long carTypeId=Long.parseLong(map.get("carTypeId").toString());
			Freight freight=getFreight(orderDate, seCityId, reCityId, brandId, carTypeId);
			return freight;
		} catch (ParseException e) {
			throw new BusinessException(e);
		}
		
	}
	
	/**
     * 查询结果按创建时间倒序排序
     */
	@Override
	@Transactional
	public PageInfo<Freight> getList(Map<String, Object> map, int pageNum, int pageSize) {
		log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
        
		try {
			PageHelper.startPage(pageNum, pageSize);
			return new PageInfo<Freight>(freightmapper.getList(map));
		} catch (Exception e) {
			throw e;
		}
	}


	
}
