package com.tilchina.timp.manager.impl;

import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.manager.ImportPriceManager;
import com.tilchina.timp.mapper.*;
import com.tilchina.timp.model.*;
import com.tilchina.timp.service.CityService;
import com.tilchina.timp.service.QuotationDetailService;
import com.tilchina.timp.service.QuotationPriceService;
import com.tilchina.timp.service.QuotationService;
import com.tilchina.timp.vo.BaoshijiePriceVO;
import com.tilchina.timp.vo.BaoshijiePriceVO;
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
import java.util.stream.Collectors;

/**
 * Created by demon on 2018/6/12.
 */
@Service
@Slf4j
public class ImportPriceManagerImpl implements ImportPriceManager {

    @Autowired
    private QuotationMapper quotationMapper;

    @Autowired
    private QuotationDetailMapper quotationDetailMapper;

    @Autowired
    private QuotationPriceMapper quotationPriceMapper;

    @Autowired
    private CorpMapper corpMapper;

    @Autowired
    private CityService cityService;

    @Autowired
    private BrandMapper brandMapper;

//    private static Long customerId = 83255L;
//    private static Long brandId = 1184L;
    private static Long customerId = 83073L;
    private static Long brandId = 1240L;
    private static Long carTypeId = 2200L;

    @Override
    @Transactional
    public void readFile(File file) {
        List<BaoshijiePriceVO> vos = readDefault(file);

        Map<String,List<BaoshijiePriceVO>> vosMap = vos.stream().collect(Collectors.groupingBy(BaoshijiePriceVO::getCustomerName));
        List<String> corpNames = new ArrayList<>(vosMap.keySet());
        // 获取公司ID
        List<Corp> corps = corpMapper.selectByCorpName(corpNames);
        Map<String,Corp> corpMap = new HashMap<>();
        corps.forEach( corp -> corpMap.put(corp.getCorpName(),corp));

        // 获取品牌
        Map<String,List<BaoshijiePriceVO>> vosBrandMap = vos.stream().collect(Collectors.groupingBy(BaoshijiePriceVO::getCustomerName));
//        List<Brand> brands = brandMapper.selectByBrandName();
        // 车型类别Id
        Map<String,Long> carTypeMap = new HashMap<>();
        carTypeMap.put("A",2165L);
        carTypeMap.put("奔驰",2166L);
        carTypeMap.put("C",2168L);
        carTypeMap.put("D",2169L);
        carTypeMap.put("特殊",2311L);
        carTypeMap.put("斯宾特",2315L);

        //获取城市
        Map<String,Object> cityParam = new HashMap<>();
        cityParam.put("cityType",2+"");
        List<City> cities = cityService.queryList(cityParam);
        Map<String,City> cityMap = new HashMap<>();
        cities.forEach(city -> cityMap.put(city.getCityName(),city));
        // 保存报价
        try {
            Quotation vo = addPrice(vos, cityMap, carTypeMap);
            Thread.sleep(1000);
            // 保存东泽-锦浩共源
            addOthers(vo,1L,2L);
            // 保存锦浩-大板
            Thread.sleep(1000);
            addOthers(vo,2L,82996L);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addOthers(Quotation vo, Long customerId, Long carrierId){

        vo.setQuotationNumber("INIT"+getCode());
        vo.setCustomerId(customerId);
        vo.setVendorCorpId(carrierId);
        vo.setCorpId(carrierId);
        quotationMapper.insertSelective(vo);

        for (QuotationDetail detail : vo.getDetails()) {
            detail.setQuotationId(vo.getQuotationId());
        }
        quotationDetailMapper.insert(vo.getDetails());

        List<QuotationPrice> prices = new ArrayList<>();
        for (QuotationDetail detail : vo.getDetails()) {
            for (QuotationPrice quotationPrice : detail.getPrices()) {
                quotationPrice.setQuotationId(detail.getQuotationId());
                quotationPrice.setQuotationDetailId(detail.getQuotationDetailId());
            }
            prices.addAll(detail.getPrices());
        }

        quotationPriceMapper.insert(prices);
    }

    @Override
    @Transactional
    public Quotation addPrice(List<BaoshijiePriceVO> vos, Map<String, City> cityMap, Map<String, Long> carTypeMap) throws Exception{
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Quotation vo = new Quotation();
        vo.setQuotationNumber("INIT"+getCode());
        vo.setQuotationType(0);
        vo.setCustomerId(customerId);
        vo.setVendorCorpId(1L);
        vo.setEffectiveDate(sf.parse("2018-05-01"));
        vo.setCreator(1L);
        vo.setCreateDate(new Date());
        vo.setChecker(1L);
        vo.setCheckDate(new Date());
        vo.setCorpId(1L);
        vo.setBillStatus(1);
        vo.setFlag(0);

        quotationMapper.insertSelective(vo);

        for (BaoshijiePriceVO baoshijiePriceVO : vos) {
            QuotationDetail detail = new QuotationDetail();
            vo.addDetail(detail);

            detail.setQuotationId(vo.getQuotationId());
            detail.setQuotationPlan(0);
            detail.setSendCityId(getCityId(baoshijiePriceVO.getSendCityName(),cityMap));
            detail.setRecvCityId(getCityId(baoshijiePriceVO.getReceiveCityName(),cityMap));
            detail.setCarTypeId(carTypeMap.get(baoshijiePriceVO.getCarTypeName()));
            detail.setJobType(0);
            detail.setLeadTime(3);
            detail.setTotalPrice(new BigDecimal(baoshijiePriceVO.getPrice()));
            detail.setCarBrandId(brandId);
            detail.setCorpId(vo.getVendorCorpId());
            detail.setFlag(0);
            detail.setPriceDesc(baoshijiePriceVO.getReceiveCityName());

            QuotationPrice price = new QuotationPrice();
            detail.addPrice(price);

            price.setQuotationAmount(1);
            price.setQuotationPrice(new BigDecimal(baoshijiePriceVO.getPrice()));
            price.setCorpId(vo.getVendorCorpId());
            price.setFlag(0);

        }
        quotationDetailMapper.insert(vo.getDetails());

        List<QuotationPrice> prices = new ArrayList<>();
        for (QuotationDetail detail : vo.getDetails()) {
            for (QuotationPrice quotationPrice : detail.getPrices()) {
                quotationPrice.setQuotationId(detail.getQuotationId());
                quotationPrice.setQuotationDetailId(detail.getQuotationDetailId());
            }
            prices.addAll(detail.getPrices());
        }

        quotationPriceMapper.insert(prices);

        return vo;
    }

    private List<BaoshijiePriceVO> readDefault(File file) {
        FileInputStream inp = null;
        List<BaoshijiePriceVO> vos = new ArrayList<>();

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
                BaoshijiePriceVO vo = new BaoshijiePriceVO();

                vo.setCustomerName(readString(r, 0));
                if (StringUtils.isBlank(vo.getCustomerName())) {
                    continue;
                }
                vo.setSendCityName(readString(r, 1));
                vo.setReceiveCityName(readString(r, 2));
                vo.setCarTypeName(readString(r, 3));
                vo.setPrice(readString(r, 5));
                vo.setStartDate(readDate(r,6));
                vo.setEndDate(readDate(r,7));

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

    private Long getCityId(String cityName,Map<String,City> cityMap){
        if(StringUtils.isBlank(cityName)){
            return null;
        }
        cityName = cityName.replaceAll("市-市","");
        City city = cityMap.get(cityName+"市");
        if(city == null){
            city = cityMap.get(cityName);
        }
        if(city == null){
            return 0L;
        }
        return city.getCityId();
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
                    throw new BusinessException("[" + cell.getDateCellValue()+ "]日期格式错误，支持yyyy-MM-dd、yyyy/MM/dd。");
                }
            default:
                return null;
        }
    }

    private Date readDate(Row row, int index){
        Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if(cell == null){
            return null;
        }
        switch (cell.getCellTypeEnum()) {
            case STRING:
                String value = cell.getRichStringCellValue().getString().trim();
                if(value.length() != 10){
                    throw new BusinessException("["+value+"]日期格式错误，支持yyyy-MM-dd、yyyy/MM/dd。");
                }
                SimpleDateFormat sf = null;
                if(value.indexOf("-") != -1){
                    sf = new SimpleDateFormat("yyyy-MM-dd");
                }else if(value.indexOf("/") != -1){
                    sf = new SimpleDateFormat("yyyy/MM/dd");
                }else{
                    throw new BusinessException("["+value+"]日期格式错误，支持yyyy-MM-dd、yyyy/MM/dd。");
                }
                try {
                    return sf.parse(value);
                }catch (ParseException e){
                    log.error("日期格式转换错误！",e);
                    throw new BusinessException("["+value+"]日期格式错误，支持yyyy-MM-dd、yyyy/MM/dd。");
                }
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    throw new BusinessException("["+cell.getStringCellValue()+"]日期格式错误，支持yyyy-MM-dd、yyyy/MM/dd。");
                }
            default:
                return null;
        }
    }

    private String getCode(){
        SimpleDateFormat sf = new SimpleDateFormat("YYYYMMDDHHmmss");
        return sf.format(new Date());
    }
}
