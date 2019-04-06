package com.tilchina.timp.manager.impl;

import com.tilchina.timp.common.Environment;
import com.tilchina.timp.enums.OperationType;
import com.tilchina.timp.enums.OrderType;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.manager.OrderManager;
import com.tilchina.timp.model.*;
import com.tilchina.timp.service.BrandService;
import com.tilchina.timp.service.CarService;
import com.tilchina.timp.service.OrderService;
import com.tilchina.timp.service.UnitService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author WangShengguang
 * @version 1.0.0 2018/5/21
 */
@Service
@Slf4j
public class OrderManagerImpl implements OrderManager {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UnitService unitService;

    @Autowired
    private CarService carService;

    @Autowired
    private BrandService brandService;

    @Override
    public void add(Order order) {
        switch (OrderType.get(order.getOrderType())) {
            case In:
                orderService.add(order);
                break;
            case Out:
                orderService.add(order);
                break;
            case Section:
                orderService.addSection(order);
                break;
            default:
                throw new BusinessException("订单类型错误！");
        }
    }

    @Override
    @Transactional
    public void importFile(Integer fileType, String orderDate, Long corpCustomerId, Long sendUnitId, File file, boolean auto) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Environment env = Environment.getEnv();

        // 构造订单主表信息
        Order order = new Order();
        try {
            order.setOrderDate(sf.parse(orderDate));
        } catch (Exception e) {
            throw new RuntimeException("订单日期格式错误！");
        }
        order.setOrderType(OrderType.In.getIndex());
        order.setCorpCustomerId(corpCustomerId);
        order.setCorpCarrierId(env.getCorp().getCorpId());
        order.setCreator(env.getUser().getUserId());
        order.setWorkType(OperationType.LongHaul.getIndex());// 作业类型默认长途
        order.setBillStatus(0);

        switch (fileType) {
            case 0:
                readDefault(order,file);
                String s = checkDefault(order,sendUnitId);
                if(!StringUtils.isBlank(s)){
                    throw new BusinessException(s);
                }
                break; //通用模板
            default:
                throw new BusinessException("暂不支持所选模板类型！");
        }

        orderService.add(order);
    }

    @Override
    public String checkDefault(Order order, Long sendUnitId){
        List<OrderDetail> details = order.getOds();
        if(CollectionUtils.isEmpty(details)){
            throw new BusinessException("导入数据为空！");
        }

        Set<String> unitNames = new HashSet<>();
        Set<String> carNames = new HashSet<>();
        Set<String> brandNames = new HashSet<>();
        for (int i = 0; i < details.size(); i++) {
            OrderDetail detail = details.get(i);
            if(StringUtils.isBlank(detail.getCarVin())){
                continue;
            }
            unitNames.add(detail.getRefReceiveUnitName());
            carNames.add(detail.getRefCarName());
            brandNames.add(detail.getRefBrandName());
        }

        // 获取数据库对象
        List<Unit> units = unitService.queryByNames(unitNames);
        Map<String,Unit> unitMap = new HashMap<>();
        for (Unit unit : units) {
            unitMap.put(unit.getUnitName(),unit);
        }

        List<Car> cars = carService.queryByCarNames(carNames);
        Map<String,Car> carMap = new HashMap<>();
        for (Car car : cars) {
            carMap.put(car.getCarName(),car);
        }

        List<Brand> brands = brandService.queryByNames(brandNames);
        Map<String,Brand> brandMap = new HashMap<>();
        for (Brand brand : brands) {
            brandMap.put(brand.getBrandName(),brand);
        }

        StringBuilder s = new StringBuilder();
        for (int i = 0; i < details.size(); i++) {
            StringBuilder sb = new StringBuilder();
            OrderDetail detail = details.get(i);
            if(StringUtils.isBlank(detail.getCarVin())){
                continue;
            }
            Unit unit = unitMap.get(detail.getRefReceiveUnitName());
            if(unit == null){
                sb.append("目的地["+detail.getRefReceiveUnitName()+"]不存在。");
            }else{
                detail.setReceiveUnitId(unit.getUnitId());
            }

            Car car = carMap.get(detail.getRefCarName());
            if(car == null){
                sb.append("车型["+detail.getRefCarName()+"]不存在。");
            }else{
                detail.setCarId(car.getCarId());
                detail.setBrandId(car.getBrandId());
                detail.setCarTypeId(car.getCarTypeId());
            }
            detail.setSendUnitId(sendUnitId);
            if(sb.length() > 0){
                s.append("导入文件第"+(i+2)+"行，").append(sb);
            }
        }
        return s.toString();
    }

    private Order readDefault(Order order, File file) {
        FileInputStream inp = null;
        StringBuilder s = new StringBuilder();
        try {
            inp = new FileInputStream(file);
            Workbook wb = WorkbookFactory.create(inp);
            Sheet sheet = wb.getSheetAt(0);

            List<OrderDetail> details = new ArrayList<>();
            order.setOds(details);
            int rowStart = sheet.getFirstRowNum();
            int rowEnd = sheet.getLastRowNum();
            for (int i = rowStart + 1; i <= rowEnd; i++) {
                Row r = sheet.getRow(i);
                if (r == null) {
                    continue;
                }

                OrderDetail detail = new OrderDetail();
                try {
                    detail.setCarVin(readString(r, 0));
                    if(StringUtils.isBlank(detail.getCarVin())){
                        continue;
                    }
                    detail.setRefReceiveUnitName(readString(r, 1));
                    detail.setRefReceiveCityName(readString(r, 2));
                    detail.setRefCarName(readString(r, 3));
                    detail.setRefBrandName(readString(r, 4));
                    detail.setClaimLoadDate(readDate(r, 5));
                    detail.setRemark(readString(r, 6));
                    detail.setEta(readDate(r, 8));
                    detail.setProductNumber(readString(r, 9));
                    detail.setInstructionNumber(readString(r, 10));
                    details.add(detail);
                }catch(Exception ce){
                    s.append("导入文件第"+(i+1)+"行："+ce.getMessage());
                }
            }
            if(s.length() > 0){
                throw new BusinessException(s.toString());
            }
        } catch (IOException e) {
            log.error("文件读取失败！", e);
            throw new BusinessException(e);
        } catch (Exception e) {
            log.error("文件解析失败！", e);
            throw new BusinessException(e);
        }
        return order;
    }

    private String readString(Row row, int index){
        Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if(cell == null){
            return null;
        }
        switch (cell.getCellTypeEnum()) {
            case STRING:
                if(cell.getRichStringCellValue().getString().trim().length() == 0){
                    return null;
                }else {
                    return cell.getRichStringCellValue().getString().trim();
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


}
