package com.tilchina.timp.manager.impl;

import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.manager.ImportTransManager;
import com.tilchina.timp.mapper.FreightMapper;
import com.tilchina.timp.model.Brand;
import com.tilchina.timp.model.City;
import com.tilchina.timp.model.Freight;
import com.tilchina.timp.service.BrandService;
import com.tilchina.timp.service.CityService;
import com.tilchina.timp.vo.ImplTransVO;
import com.tilchina.timp.vo.ImplTransVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by demon on 2018/6/12.
 */
@Service
@Slf4j
public class ImportTransManagerImpl implements ImportTransManager {

    @Autowired
    private CityService cityService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private FreightMapper freightMapper;

    @Override
    @Transactional
    public void readFile(File file) {
        List<ImplTransVO> vos = readDefault(file);

        //获取城市
        Map<String, Object> cityParam = new HashMap<>();
        cityParam.put("cityType", 2 + "");
        List<City> cities = cityService.queryList(cityParam);
        Map<String, City> cityMap = new HashMap<>();
        cities.forEach(city -> cityMap.put(city.getCityName(), city));

        //获取品牌
        List<Brand> brands = brandService.queryList(null);
        Map<String, Brand> brandMap = new HashMap<>();
        brands.forEach(b -> brandMap.put(b.getBrandName(), b));
        //保存运价
        try {
            addPrice(vos, cityMap, brandMap);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void addPrice(List<ImplTransVO> vos, Map<String, City> cityMap, Map<String, Brand> brandMap) throws Exception {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        List<Freight> freights = new ArrayList<>();
        int code = 800000;
        int i = 0;
        for (ImplTransVO vo : vos) {
            if ("锦浩小板".equals(vo.getCorpName())) {
                break;
            }

            Freight f = new Freight();
            f.setFreightCode((code + i) + "");
            f.setFreightType(1);
            f.setEffectiveDate(sf.parse("2018-01-01"));
            f.setFailureDate(sf.parse("2099-01-01"));
            f.setStartPlaceId(getCityId(vo.getSendCityName(), cityMap));
            f.setArrivalPlaceId(getCityId(vo.getReceiveCityName(), cityMap));
            f.setRemark(vo.getReceiveCityName());
            if(brandMap.get(vo.getBrandName()) == null){
                System.out.println(vo.getBrandName());
                continue;
            }
            f.setBrandId(brandMap.get(vo.getBrandName()).getBrandId());
            f.setDenominatedMode(0);
            f.setUnitPrice(new BigDecimal(vo.getPrice()));
            f.setBillStatus(1);
            f.setCreator(1L);
            f.setCreateDate(new Date());
            f.setChecker(1L);
            f.setCheckDate(new Date());
            f.setCorpId(82996L);
            f.setFlag(0);
            freights.add(f);
            i++;
        }
        freightMapper.insert(freights);
    }


    private Long getCityId(String cityName, Map<String, City> cityMap) {
        if (StringUtils.isBlank(cityName)) {
            return null;
        }
        cityName = cityName.replaceAll("市-市", "");
        City city = cityMap.get(cityName + "市");
        if (city == null) {
            city = cityMap.get(cityName);
        }
        if (city == null) {
            return 0L;
        }
        return city.getCityId();
    }

    private List<ImplTransVO> readDefault(File file) {
        FileInputStream inp = null;
        List<ImplTransVO> vos = new ArrayList<>();
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
                ImplTransVO vo = new ImplTransVO();

                vo.setCorpName(readString(r, 0));
                if (StringUtils.isBlank(vo.getCorpName())) {
                    continue;
                }
                vo.setBrandName(readString(r, 1));
                vo.setSendCityName(readString(r, 2));
                vo.setReceiveCityName(readString(r, 3));
                vo.setPrice(readString(r, 4));

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

    private Date readDate(Row row, int index) {
        Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) {
            return null;
        }
        switch (cell.getCellTypeEnum()) {
            case STRING:
                String value = cell.getRichStringCellValue().getString().trim();
                if (value.length() != 10) {
                    throw new BusinessException("[" + value + "]日期格式错误，支持yyyy-MM-dd、yyyy/MM/dd。");
                }
                SimpleDateFormat sf = null;
                if (value.indexOf("-") != -1) {
                    sf = new SimpleDateFormat("yyyy-MM-dd");
                } else if (value.indexOf("/") != -1) {
                    sf = new SimpleDateFormat("yyyy/MM/dd");
                } else {
                    throw new BusinessException("[" + value + "]日期格式错误，支持yyyy-MM-dd、yyyy/MM/dd。");
                }
                try {
                    return sf.parse(value);
                } catch (ParseException e) {
                    log.error("日期格式转换错误！", e);
                    throw new BusinessException("[" + value + "]日期格式错误，支持yyyy-MM-dd、yyyy/MM/dd。");
                }
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    throw new BusinessException("[" + cell.getStringCellValue() + "]日期格式错误，支持yyyy-MM-dd、yyyy/MM/dd。");
                }
            default:
                return null;
        }
    }

    private String getCode() {
        SimpleDateFormat sf = new SimpleDateFormat("YYYYMMDDHHmmss");
        return sf.format(new Date());
    }
}
