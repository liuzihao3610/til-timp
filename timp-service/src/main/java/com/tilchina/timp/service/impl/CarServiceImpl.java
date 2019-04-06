package com.tilchina.timp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tilchina.catalyst.utils.CheckUtils;
import com.tilchina.framework.mapper.BaseMapper;
import com.tilchina.framework.service.BaseServiceImpl;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.enums.CarLevel;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.mapper.CarMapper;
import com.tilchina.timp.model.Car;
import com.tilchina.timp.service.BrandService;
import com.tilchina.timp.service.CarService;
import com.tilchina.timp.service.CarTypeService;
import com.tilchina.timp.service.CategoryService;
import com.tilchina.timp.util.DateUtil;
import com.tilchina.timp.util.ExcelUtil;
import com.tilchina.timp.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author XiaHong
 * @version 1.1.0
 */
@Service
@Slf4j
public class CarServiceImpl extends BaseServiceImpl<Car> implements CarService {

    @Autowired
    private CarMapper carmapper;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CarTypeService carTypeService;

    @Autowired
    private CategoryService categoryService;

    @Override
    protected BaseMapper<Car> getMapper() {
        return carmapper;
    }

    private static boolean isNullAble(String nullable) {
        return "YES".equals(nullable.toUpperCase()) || "Y".equals(nullable.toUpperCase());
    }

    public static String checkBigDecimal(String nullable, String attName, String comment, BigDecimal decimal, int length, int scale) {
        String desc = comment + "[" + attName + "]";
        if (decimal == null) {
            if (!isNullAble(nullable)) {
                return desc + " can not be null! ";
            } else {
                return "";
            }
        }
        if (decimal.toString().length() > length) {
            return desc + " value " + decimal + " out of range [" + length + "].";
        }

        String scaleStr = decimal.toString();
        if (scale > 0 && scaleStr.contains(".") && scaleStr.split("\\.")[1].length() > scale) {
            return desc + " value " + decimal + " scale out of range [" + scale + "].";
        }
        return "";
    }

    public static String checkDouble(String nullable, String attName, String comment, Double dou, int length, int scale) {
        String desc = comment + "[" + attName + "]";
        if (dou == null) {
            if (!isNullAble(nullable)) {
                return desc + " can not be null! ";
            } else {
                return "";
            }
        }
        if (dou.toString().length() > length) {
            return desc + " value " + dou + " out of range [" + length + "].";
        }

        String scaleStr = dou.toString();
        if (scale > 0 && scaleStr.contains(".") && scaleStr.split("\\.")[1].length() > scale) {
            return desc + " value " + dou + " scale out of range [" + scale + "].";
        }
        return "";
    }

    @Override
    protected StringBuilder checkNewRecord(Car car) {
        StringBuilder s = new StringBuilder();
        s.append(CheckUtils.checkString("YES", "carCode", "商品车编码", car.getCarCode(), 50));
        s.append(CheckUtils.checkString("NO", "carName", "商品车名称", car.getCarName(), 40));
        s.append(CheckUtils.checkLong("YES", "carTypeId", "商品车类型ID", car.getCarTypeId(), 20));
        s.append(CheckUtils.checkLong("NO", "brandId", "品牌ID", car.getBrandId(), 20));
        s.append(CheckUtils.checkLong("NO", "categoryId", "类别ID", car.getCategoryId(), 20));
        s.append(CheckUtils.checkInteger("YES", "carLevel", "汽车等级(0=未指定", car.getCarLevel(), 10));
        s.append(CheckUtils.checkInteger("YES", "suv", "SUV", car.getSuv(), 10));
        s.append(CheckUtils.checkString("YES", "description", "描述", car.getDescription(), 100));
        s.append(CheckUtils.checkInteger("YES", "dataLevel", "数据类别(0-N)", car.getDataLevel(), 10));
        s.append(CheckUtils.checkInteger("YES", "carLong", "长(毫米)", car.getCarLong(), 10));
        s.append(CheckUtils.checkInteger("YES", "carWidth", "宽(毫米)", car.getCarWidth(), 10));
        s.append(CheckUtils.checkInteger("YES", "carHigh", "高(毫米)", car.getCarHigh(), 10));
        s.append(CheckUtils.checkInteger("YES", "wheelBase", "轴距(毫米)", car.getWheelBase(), 10));
        s.append(CheckUtils.checkInteger("YES", "wheelRadius", "车轮半径(毫米)", car.getWheelRadius(), 10));
        s.append(CheckUtils.checkInteger("YES", "frontSuspension", "前悬(毫米)", car.getFrontSuspension(), 10));
        s.append(CheckUtils.checkInteger("YES", "rearSuspension", "后悬(毫米)", car.getRearSuspension(), 10));
        s.append(CheckUtils.checkInteger("YES", "rideHeight", "底盘高度(毫米)", car.getRideHeight(), 10));
        s.append(checkBigDecimal("YES", "price", "价格(元)", car.getPrice(), 10, 2));
        s.append(CheckUtils.checkString("YES", "origin", "产地", car.getOrigin(), 200));
        s.append(checkDouble("YES", "handleChangeMultiple", "装卸费倍数", car.getHandleChangeMultiple(), 10, 3));
        s.append(CheckUtils.checkInteger("YES", "billStatus", "状态(0=制单", car.getBillStatus(), 10));
        car.setCreateTime(new Date());
        s.append(CheckUtils.checkDate("YES", "createTime", "创建时间", car.getCreateTime()));
        s.append(CheckUtils.checkDate("YES", "checkDate", "审核时间", car.getCheckDate()));
        s.append(CheckUtils.checkInteger("YES", "flag", "封存标志", car.getFlag(), 10));
        return s;
    }

    @Override
    protected StringBuilder checkUpdate(Car car) {
        StringBuilder s = checkNewRecord(car);
        s.append(CheckUtils.checkPrimaryKey(car.getCarId()));
        return s;
    }

    /**
     * 批量删除
     * @param data
     */
    @Override
    @Transactional
    public void deleteList(int[] data) {
        try {
            if (null == data || 0 == data.length) {
                throw new BusinessException("参数为空,请检查");
            }
            carmapper.deleteList(data);
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                throw e;
            } else {
                throw new RuntimeException("操作失败");
            }
        }


    }

    /**
     * 批量更新
     * @param records
     */
    @Override
    @Transactional
    public void update(List<Car> records) {
        log.debug("updateBatch: {}", records);
        StringBuilder s = new StringBuilder();
        boolean checkResult = true;
        try {
            if (null == records || 0 == records.size()) {
                throw new BusinessException("参数为空,请检查!");
            }
            for (int i = 0; i < records.size(); i++) {
                Car record = records.get(i);
                StringBuilder check = checkUpdate(record);
                if (!StringUtils.isBlank(check)) {
                    checkResult = false;
                    s.append("第" + (i + 1) + "行，" + check + "/r/n");
                }
                carmapper.updateByPrimaryKeySelective(record);
            }
            if (!checkResult) {
                throw new RuntimeException("数据检查失败：" + s.toString());
            }

        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新提交！", e);
            } else if (e instanceof BusinessException) {
                throw e;
            }
        }

    }

    /**
     * 审核
     * @param carId
     */
    @Override
    @Transactional
    public void audit(Long carId) {
        if (null == carId) {
            throw new BusinessException("参数为空");
        }
        Car car = carmapper.selectByPrimaryKey(carId);
        if (car == null) {
            throw new BusinessException("商品车不存在");
        }
        Environment environment=Environment.getEnv();
        car.setChecker(environment.getUser().getUserId());
        car.setCheckDate(new Date());
        car.setBillStatus(1);
        carmapper.updateByPrimaryKeySelective(car);
    }

    /**
     * 取消审核
     * @param carId
     */
    @Override
    @Transactional
    public void unaudit(Long carId) {
        if (null == carId) {
            throw new BusinessException("参数为空");
        }
        Car car = carmapper.selectByPrimaryKey(carId);
        if (car == null) {
            throw new BusinessException("商品车不存在");
        }
        carmapper.unaudit(car);
    }

    /**
     * 参照
     * @param map
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    @Transactional
    public PageInfo<Car> getReferenceList(Map<String, Object> map, int pageNum, int pageSize) {
        log.debug("getReferenceList: {}, page: {},{}", pageNum, pageSize);
        PageHelper.startPage(pageNum, pageSize);
        List<Car> list = carmapper.getReferenceList(map);
        return new PageInfo<Car>(list);
    }

    /**
     * 新增商品车
     * @param record
     */
    @Override
    @Transactional
    public void add(Car record) {
        log.debug("add: {}", record);
        StringBuilder s;
        try {
            s = checkNewRecord(record);
            if (!StringUtils.isBlank(s)) {
                throw new BusinessException("数据检查失败：" + s.toString());
            }

            Environment env = Environment.getEnv();
            record.setCarCode(DateUtil.dateToStringCode(new Date()));
            record.setCreator(env.getUser().getUserId());
            record.setCreateTime(new Date());
            record.setBillStatus(0);
            record.setCarId(null);
            record.setChecker(null);
            record.setCheckDate(null);
            record.setCorpId(env.getCorp().getCorpId());
            carmapper.insertSelective(record);
        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新提交！", e);
            } else if (e instanceof BusinessException) {
                throw e;
            }else{
                throw new BusinessException("操作失败");
            }
        }
    }


    /**
     * 修改
     * @param record
     */
    @Override
    @Transactional
    public void updateSelective(Car record) {
        log.debug("updateSelective: {}", record);
        try {
            StringBuilder s = new StringBuilder();
            try {
                s = s.append(CheckUtils.checkLong("NO", "carId", "商品车ID", record.getCarId(), 20));
                if (!StringUtils.isBlank(s)) {
                    throw new RuntimeException("数据检查失败：" + s.toString());
                }
                Car car = carmapper.selectByPrimaryKey(record.getCarId());
                if (car == null) {
                    throw new BusinessException("这条记录不存在!");
                }
                car.setCarCode(null);
                carmapper.updateSelective(record);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }

        } catch (Exception e) {
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新提交！", e);
            } else if (e instanceof BusinessException) {
                throw e;
            }
        }
    }

    /**
     * 通过ID查询
     * @param id
     * @return
     */
    @Override
    @Transactional
    public Car queryById(Long id) {
        log.debug("query: {}", id);
        StringBuilder s = new StringBuilder();
        try {
            s = s.append(CheckUtils.checkLong("NO", "carId", "商品车ID", id, 20));
            if (!StringUtils.isBlank(s)) {
                throw new RuntimeException("数据检查失败：" + s.toString());
            }

            return carmapper.selectByPrimaryKey(id);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 通过ID删除
     * @param id
     */
    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("delete: {}", id);
        StringBuilder s = new StringBuilder();
        try {
            s = s.append(CheckUtils.checkLong("NO", "carId", "商品车ID", id, 20));
            if (!StringUtils.isBlank(s)) {
                throw new RuntimeException("数据检查失败：" + s.toString());
            }

            carmapper.deleteByPrimaryKey(id);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    /**
     * 通过商品车名称查询
     * @param model
     * @return
     */
    @Override
    @Transactional
    public Car getByCarName(String model) {
        try {

            return carmapper.getByCarName(model);
        } catch (Exception e) {
            throw e;
        }

    }

    /**
     * 查询所有商品车返回Map参照
     * @return
     */
    @Override
    @Transactional
    public Map<String, Car> queryMap() {
        List<Car> cars = queryList(null);
        Map<String, Car> carMap = new HashMap<>();
        for (Car car : cars) {
            carMap.put(car.getBrandId() + "_" + car.getCarId(), car);
        }
        return carMap;
    }

    /**
     * 通过删除名称批量查询
     * @param codes
     * @return
     */
    @Override
    @Transactional
    public List<Car> queryByCarNames(Set<String> codes){
        if(CollectionUtils.isEmpty(codes)){
            return null;
        }
        return carmapper.selectByCarNames(codes);
    }

    /**
     * 通过删除ID批量查询
     * @param carIds
     * @return
     */
    @Override
    @Transactional
    public Map<Long,Car> queryMapByCarIds(Set<Long> carIds){
        List<Car> cars = queryByCarIds(carIds);
        Map<Long, Car> carMap = new HashMap<>();
        for (Car car : cars) {
            carMap.put(car.getCarId(), car);
        }
        return carMap;
    }

    /**
     * 通过删除ID批量查询
     * @param ids
     * @return
     */
    @Override
    @Transactional
    public List<Car> queryByCarIds(Set<Long> ids) {
        if(CollectionUtils.isEmpty(ids)){
            throw new BusinessException("查询【商品车信息】请求参数错误！");
        }
        return carmapper.selectByCarIds(ids);
    }

    /**
     * 查询结果按创建时间排序
     */
	@Override
	@Transactional
	public PageInfo<Car> getList(Map<String, Object> map, int pageNum, int pageSize) {
		log.debug("queryList: {}, page: {},{}", map,pageNum,pageSize);
        
		try {
			PageHelper.startPage(pageNum, pageSize);
			return new PageInfo<Car>(carmapper.getList(map));
		} catch (Exception e) {
			throw e;
		}
	}

    /**
     * 通过商品车名称查询ID
     * @param modelName
     * @return
     */
    @Override
    @Transactional
    public Long queryIdByName(String modelName) {
        try {
            Long carModelId = carmapper.queryIdByName(modelName);
            return carModelId;
        } catch (Exception e) {
            log.error("modelName: {}", e);
            throw e;
        }
    }

    /**
     * 分页查询
     * @param map
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    @Transactional
    public PageInfo<Car> queryList(Map<String, Object> map, int pageNum, int pageSize) {
        log.debug("queryList: {}, page: {},{}", new Object[]{map, pageNum, pageSize});
        PageHelper.startPage(pageNum, pageSize);
        try {
            if (map!=null){
                DateUtil.addTime(map);
            }
            return new PageInfo(carmapper.selectList(map));
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * 封存
     * @param carId
     * @return
     */
    @Transactional
    public void sequestration(Long carId) {
        try {
            Car car = carmapper.selectByPrimaryKey(carId);
            if (car==null){
                throw new BusinessException("商品车不存在");
            }
            car.setFlag(1);
            carmapper.updateSelective(car);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 取消封存
     * @param carId
     * @return
     */
    @Transactional
    public void unsequestration(Long carId) {
        try {
            Car car = carmapper.selectByPrimaryKey(carId);
            if (car==null){
                throw new BusinessException("商品车不存在");
            }
            car.setFlag(0);
            carmapper.updateSelective(car);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 导入Excel
     * @param file
     */
    @Override
    @Transactional
    public void importExcel(MultipartFile file) {
        String filePath = null;
        Workbook workbook = null;
        String s = null;
        Map<String, Boolean> nullableMap = new HashMap<>();
        try{
            filePath=FileUtil.uploadFile(file,"category");
            workbook=ExcelUtil.getWorkbook(filePath);
            nullableMap.put("商品车名称",false);
            nullableMap.put("商品车类型名称",false);
            nullableMap.put("商品车类别名称",false);
            s=ExcelUtil.validateWorkbook(workbook,nullableMap);
            List<Car> cars = ExcelUtil.getModelsFromWorkbook(workbook,Car.class);
            for (int i = 0; i <cars.size() ; i++) {
                Long brandId=brandService.queryIdByName(cars.get(i).getRefBrandName());
                Long carTypeId=carTypeService.queryIdByName(cars.get(i).getRefCarTypeName());
                Long categoryId=categoryService.queryIdByName(cars.get(i).getRefCategoryName());
                cars.get(i).setBrandId(brandId);
                cars.get(i).setCarTypeId(carTypeId);
                cars.get(i).setCategoryId(categoryId);
//                cars.get(i).setCarLevel(CarLevel.get(cars.get(i).getRefCarLevel()).getIndex());
//                cars.get(i).setSuv(SUV.get(cars.get(i).getRefSuv()).getIndex());
                add(cars.get(i));
            }

        }catch (Exception e){
            if (e.getMessage().indexOf("IDX_") != -1) {
                throw new BusinessException("数据重复，请查证后重新提交！",e);
            } else if(e instanceof BusinessException){
                throw new BusinessException(e.getMessage());
            }else {
                throw new RuntimeException("导入商品车型号失败");
            }
        }

    }

    /**
     * 导出Excel
     * @return
     */
    @Override
    @Transactional
    public Workbook exportExcel() throws Exception {
        try {
            List<Car> cars = queryList(new HashMap<>());
            cars = conversion(cars);
            Workbook workbook = ExcelUtil.getWorkbookFromModels(cars);
            return workbook;
        } catch (Exception e) {
            log.error("{}", e);
            throw e;
        }
    }

    /**
     * 数据转换
     * @param cars
     * @return
     */
    public List<Car> conversion(List<Car> cars){
        try {
            for (int i = 0; i <cars.size() ; i++) {
                Integer SUV=cars.get(i).getSuv();
                if (SUV==0){
                    cars.get(i).setRefSuv("否");
                }else{
                    cars.get(i).setRefSuv("是");
                }
                cars.get(i).setRefCarLevel(CarLevel.get(cars.get(i).getCarLevel()).getName());
            }
            return cars;
        } catch (Exception e) {
            log.error("{}", e);
            throw e;
        }
    }

}
