package com.tilchina.timp.manager.impl;

import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.manager.ImportCarManager;
import com.tilchina.timp.model.Brand;
import com.tilchina.timp.model.Car;
import com.tilchina.timp.model.CarType;
import com.tilchina.timp.service.BrandService;
import com.tilchina.timp.service.CarService;
import com.tilchina.timp.service.CarTypeService;
import com.tilchina.timp.vo.ImportCarVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by demon on 2018/6/12.
 */
@Slf4j
@Service
public class ImportCarManagerImpl implements ImportCarManager {

    @Autowired
    private BrandService brandService;

    @Autowired
    private CarTypeService carTypeService;

    @Autowired
    private CarService carService;

    @Override
    @Transactional
    public void readFile(File file) {
        List<ImportCarVO> vos = readDefault(file);

        // 保存品牌
        Map<String,Brand> brandMap = addBrand(vos);

        // 保存类别
        Map<String,CarType> carTypeMap = addCarType(vos,brandMap);

        // 保存车型
        addCar(vos,carTypeMap);

    }

    @Override
    @Transactional
    public void addCar(List<ImportCarVO> vos, Map<String,CarType> carTypeMap){
        List<Car> cars = new ArrayList<>();
        Map<String,List<ImportCarVO>> map = vos.stream().collect(Collectors.groupingBy(ImportCarVO::getCarKey));
        int code = 500000000;
        int i=0;
        for (String s : map.keySet()) {
            String[] keys = s.split("@@");
            String brandName = keys[0];
            String carTypeName = keys[1];
            String carName = keys[2];
            CarType carType = carTypeMap.get(brandName+"@@"+carTypeName);

            Car car = new Car();
            car.setCarCode((code+i)+"");
            car.setCarName(carName);
            car.setBrandId(carType.getBrandId());
            car.setCarTypeId(carType.getCarTypeId());
            car.setCategoryId(1L);
            car.setBillStatus(1);
            car.setCreator(1L);
            car.setCreateTime(new Date());
            car.setChecker(1L);
            car.setCheckDate(new Date());
            car.setCorpId(1L);
            car.setFlag(0);

            cars.add(car);
            i++;
        }
        carService.add(cars);
    }

    @Override
    @Transactional
    public Map<String,CarType> addCarType(List<ImportCarVO> vos, Map<String, Brand> brandMap){
        List<CarType> carTypes = new ArrayList<>();
        Map<String,CarType> carTypeMap = new HashMap<>();
        Map<String,List<ImportCarVO>> map = vos.stream().collect(Collectors.groupingBy(ImportCarVO::getKey));
        int code = 100000;
        int i=0;
        for (String s : map.keySet()) {
            CarType carType = new CarType();
            carType.setCarTypeCode((code+i)+"");
            String[] keys = s.split("@@");
            carType.setCarTypeName(keys[1]);
            carType.setBrandId(brandMap.get(keys[0]).getBrandId());
            carType.setCreator(1L);
            carType.setCreateDate(new Date());
            carType.setFlag(0);
            carType.setCorpId(1L);
            carTypes.add(carType);
            i++;
            carTypeMap.put(s,carType);
        }
        carTypeService.add(carTypes);
        return carTypeMap;
    }

    @Override
    @Transactional
    public Map<String,Brand> addBrand(List<ImportCarVO> vos){
        List<Brand> brands = new ArrayList<>();
        Map<String,Brand> brandMap = new HashMap<>();
        Map<String,List<ImportCarVO>> map = vos.stream().collect(Collectors.groupingBy(ImportCarVO::getBrandName));
        int code = 100000;
        int i=0;
        for (String s : map.keySet()) {
            Brand brand = new Brand();
            brand.setBrandCode((code+i)+"");
            brand.setBrandName(s);
            brand.setCreator(1L);
            brand.setCreateDate(new Date());
            brand.setFlag(0);
            brands.add(brand);
            brand.setCorpId(1L);
            i++;
            brandMap.put(brand.getBrandName(),brand);
        }
        brandService.add(brands);
        return brandMap;
    }

    private List<ImportCarVO> readDefault(File file) {
        FileInputStream inp = null;
        List<ImportCarVO> vos = new ArrayList<>();

        try {
            inp = new FileInputStream(file);
            Workbook wb = WorkbookFactory.create(inp);
            Sheet sheet = wb.getSheetAt(0);

            int rowStart = sheet.getFirstRowNum();
            int rowEnd = sheet.getLastRowNum();
            for (int i = rowStart + 1; i <= rowEnd; i++) {
                Row r = sheet.getRow(i);
                if (r == null) {
                    continue;
                }
                ImportCarVO vo = new ImportCarVO();

                vo.setBrandName(readString(r, 1));
                if (StringUtils.isBlank(vo.getBrandName())) {
                    continue;
                }
                vo.setCarTypeName(readString(r, 2));
                vo.setCarName(readString(r, 3));

                vos.add(vo);
            }

        } catch (IOException e) {
            log.error("文件读取失败！", e);
            throw new BusinessException(e);
        } catch (Exception e) {
            log.error("文件解析失败！", e);
            throw new BusinessException(e);
        } finally {
            if (inp != null) {
                try {
                    inp.close();
                } catch (Exception e) {
                    log.error("FileInputStream close error!", e);
                }
            }
        }
        return vos;
    }

    private String readString(Row row, int index) {
        Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) {
            return null;
        }
        switch (cell.getCellTypeEnum()) {
            case STRING:
                if (cell.getRichStringCellValue().getString().trim().length() == 0) {
                    return null;
                } else if ("NULL".equals(cell.getRichStringCellValue().getString())) {
                    return null;
                } else {
                    return cell.getRichStringCellValue().getString().trim();
                }
            case NUMERIC:
                if (cell.getDateCellValue() != null) {
                    String value = String.valueOf(cell.getNumericCellValue());
                    if (StringUtils.isBlank(value)) {
                        return null;
                    }
                    if ("NULL".equals(value)) {
                        return null;
                    }
                    return value;
                } else {
                    throw new BusinessException("[" + cell.getStringCellValue() + "]日期格式错误，支持yyyy-MM-dd、yyyy/MM/dd。");
                }
            default:
                return null;
        }
    }

}
