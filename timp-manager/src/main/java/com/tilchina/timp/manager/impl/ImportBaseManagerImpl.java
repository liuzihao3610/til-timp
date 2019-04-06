package com.tilchina.timp.manager.impl;

import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.manager.ImportBaseManager;
import com.tilchina.timp.model.*;
import com.tilchina.timp.service.*;
import com.tilchina.timp.vo.ImportBaseVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by demon on 2018/6/11.
 */
@Service
@Slf4j
public class ImportBaseManagerImpl implements ImportBaseManager {

    @Autowired
    private CorpService corpService;

    @Autowired
    private CityService cityService;

    @Autowired
    private UnitService unitService;

//    @Autowired
//    private CustomerInfoService customerInfoService;

    @Autowired
    private UserService userService;

    @Autowired
    private ContactsOldService contactsService;

    @Override
    @Transactional
    public void readFile(File file){
        List<ImportBaseVO> vos = readDefault(file);
        //保存客户公司
        Map<String,Corp> customerMap = addCustomer(vos);

        //保存经销公司
        Map<String,Corp> unitCorpMap = addCorp(vos);

        //获取城市
        Map<String,Object> cityParam = new HashMap<>();
        cityParam.put("cityType",2+"");
        List<City> cities = cityService.queryList(cityParam);
        Map<String,City> cityMap = new HashMap<>();
        cities.forEach(city -> cityMap.put(city.getCityName(),city));

        cityParam.put("cityType",1+"");
        List<City> provinces = cityService.queryList(cityParam);
        Map<String,City> provinceMap = new HashMap<>();
        provinces.forEach(city -> provinceMap.put(city.getCityName(),city));

        //保存收发货单位
        Map<String,Unit> unitMap = addUnit(vos,unitCorpMap,provinceMap,cityMap);

        //保存客户定制信息
        addCostomerInfo(vos,customerMap,unitMap);

        //创建用户
        Map<String,User> userMap = addUser(vos,unitCorpMap);

        //保存收发货单位联系人
        addUnitUser(userMap,unitMap);
    }

    @Override
    @Transactional
    public void addUnitUser(Map<String, User> userMap, Map<String, Unit> unitMap){
        List<ContactsOld> contacts = new ArrayList<>();
        for (String s : userMap.keySet()) {
            User user = userMap.get(s);
            Unit unit = unitMap.get(s);
            ContactsOld con = new ContactsOld();
            con.setUserId(user.getUserId());
            con.setUnitId(unit.getUnitId());
            con.setCreator(1L);
            con.setCreateTime(new Date());
// TODO WSG - 隶属公司字段作废后代码逻辑需要更新
//            con.setCorpId(unit.getSuperorCorpId());
            con.setFlag(0);
            contacts.add(con);
        }
        contactsService.add(contacts);
    }

    @Override
    @Transactional
    public Map<String,User> addUser(List<ImportBaseVO> vos, Map<String, Corp> unitCorpMap){
        //处理用户
        List<User> users = new ArrayList<>();
        List<String> used = new ArrayList<>();
        Map<String,User> userMap = new HashMap<>();
        int code = 800000;
        int i = 0;
        for (ImportBaseVO vo : vos) {
            if(StringUtils.isBlank(vo.getContant())){
                continue;
            }
            String check = vo.getContant()+vo.getPhone();
            if(used.contains(check)){
                continue;
            }
            used.add(check);
            User u = new User();
            u.setUserCode((code+i)+"");
            u.setUserName(vo.getContant());
            u.setPhone(StringUtils.isBlank(vo.getPhone())?"无":vo.getPhone());
            u.setUserType(0);
            u.setDriverType(0);
            u.setSex(0);
            u.setBirthday(new Date());
            u.setBindingPhone(0);
            u.setBillStatus(1);
            u.setCreator(1L);
            u.setCreateDate(new Date());
            u.setChecker(1L);
            u.setCheckDate(new Date());
            u.setCorpId(unitCorpMap.get(vo.getUnitName()).getCorpId());
            u.setFlag(0);
            users.add(u);
            userMap.put(vo.getUnitName(),u);
            i++;
        }
        userService.add(users);
        return userMap;
    }

    @Override
    @Transactional
    public void addCostomerInfo(List<ImportBaseVO> vos, Map<String, Corp> customerMap, Map<String,Unit> unitMap){
// TODO WSG - 默认运输公司作废后代码逻辑需要更新
//        List<CustomerInfo> customerInfos = new ArrayList<>();
//        List<String> used = new ArrayList<>();
//        for (ImportBaseVO vo : vos) {
//            if(StringUtils.isBlank(vo.getUnitCode())){
//                continue;
//            }
//            if(used.contains(vo.getUnitName())){
//                continue;
//            }
//            used.add(vo.getUnitName());
//            CustomerInfo info = new CustomerInfo();
//            info.setCustomerCorpId(customerMap.get(vo.getCustomName()).getCorpId());
//            info.setDealerNumber(vo.getUnitCode());
//            info.setRecvSendUnitId(unitMap.get(vo.getUnitName()).getUnitId());
//            info.setCreator(1L);
//            info.setCorpId(1L);
//            info.setFlag(0);
//            info.setCreateDate(new Date());
//            customerInfos.add(info);
//        }
//
//         customerInfoService.add(customerInfos);
    }

    @Override
    @Transactional
    public Map<String,Corp> addCorp(List<ImportBaseVO> vos){
        List<Corp> corps = new ArrayList<>();
        int i=0;
        int code = 300000;
        Map<String,List<ImportBaseVO>> unitMap = vos.stream().collect(Collectors.groupingBy(ImportBaseVO::getUnitName));
        for (String s : unitMap.keySet()) {
            Corp corp = new Corp();
            corp.setCorpCode((code+i)+"");
            corp.setCorpName(s);
            corp.setCorpTypeId(3L);
            corp.setCreator(1L);
            corp.setCreateDate(new Date());
            corp.setFlag(0);
            // corp.setDel(0L);
            corps.add(corp);
            i++;
        }
        corpService.add(corps);
        Map<String,Corp> corpMap = new HashMap<>();
        corps.forEach( corp -> corpMap.put(corp.getCorpName(),corp));
        return corpMap;
    }

    @Override
    @Transactional
    public Map<String, Unit> addUnit(List<ImportBaseVO> vos, Map<String, Corp> customerMap,
                                     Map<String, City> provinceMap, Map<String, City> cityMap){
        List<Unit> units = new ArrayList<>();
        List<String> used = new ArrayList<>();
        int code = 200000000;
        for (int i = 0; i < vos.size(); i++) {
            ImportBaseVO vo = vos.get(i);
            if(used.contains(vo.getUnitName())){
                continue;
            }
            used.add(vo.getUnitName());
            Unit unit = new Unit();
            unit.setUnitCode((code+i)+"");
            unit.setUnitName(vo.getUnitName());
            unit.setUnitType(0);
            unit.setDeliver(0);
            unit.setSpecial(0);
            unit.setAddress(vo.getAddress());
            unit.setTelephone(vo.getPhone()+","+vo.getTel());
            unit.setCreator(1L);
            unit.setCreateTime(new Date());
            unit.setCorpId(1L);
            unit.setFlag(0);
            unit.setDel(0L);
            unit.setLng(vo.getLng() == null?null:Double.valueOf(vo.getLng()));
            unit.setLat(vo.getLat() == null?null:Double.valueOf(vo.getLat()));
// TODO WSG - 隶属公司字段作废后代码逻辑需要更新
//            unit.setSuperorCorpId(customerMap.get(vo.getUnitName()).getCorpId());
            // 补充城市
            unit.setAccountCityId(getCityId(vo.getSettlementCity(),cityMap));
            unit.setProvinceId(getProvinceId(vo.getProvince(),provinceMap));
            unit.setCityId(getCityId(vo.getCity(),cityMap));
            unit.setEnName(vo.getArea());
            units.add(unit);
        }
        unitService.add(units);
        Map<String,Unit> unitMap = new HashMap<>();
        units.forEach( u -> unitMap.put(u.getUnitName(),u));
        return unitMap;
    }

    private Long getProvinceId(String provinceName, Map<String,City> provinceMap){
        if(StringUtils.isBlank(provinceName)){
            return null;
        }
        City city = provinceMap.get(provinceName);
        if(city == null){
            city = provinceMap.get(provinceName.substring(0,provinceName.length()-1));
        }
        if(city == null){
            return 0L;
        }
        return city.getCityId();
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

    // 保存客户公司
    @Override
    @Transactional
    public Map<String,Corp> addCustomer(List<ImportBaseVO> vos){
        Map<String,List<ImportBaseVO>> map = vos.stream().collect(Collectors.groupingBy(ImportBaseVO::getCustomName));
        List<Corp> corps = new ArrayList<>();
        int i=0;
        int code = 100000;
        for (String s : map.keySet()) {
            Corp corp = new Corp();
            corp.setCorpCode((code+i)+"");
            corp.setCorpName(s);
            corp.setCorpTypeId(2L);
            corp.setCreator(1L);
            corp.setCreateDate(new Date());
            corp.setFlag(0);
            // corp.setDel(0L);
            corps.add(corp);
            i++;
        }
        corpService.add(corps);
        Map<String,Corp> corpMap = new HashMap<>();
        corps.forEach( corp -> corpMap.put(corp.getCorpName(),corp));
        return corpMap;
    }

    private List<ImportBaseVO> readDefault(File file) {
        FileInputStream inp = null;
        List<ImportBaseVO> vos = new ArrayList<>();

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
                ImportBaseVO vo = new ImportBaseVO();
                vo.setCustomName(readString(r,0));
                if(StringUtils.isBlank(vo.getCustomName())){
                    continue;
                }
                vo.setUnitName(readString(r,1));
                if(StringUtils.isBlank(vo.getUnitName())){
                    continue;
                }
                vo.setAddress(readString(r,2));
                vo.setUnitCode(readString(r,3));
                vo.setContant(readString(r,5));
                vo.setPhone(readString(r,6));
                vo.setTel(readString(r,7));
                vo.setProvince(readString(r,10));
                vo.setCity(readString(r,11));
                vo.setArea(readString(r,12));
                vo.setLng(readString(r,14));
                vo.setLat(readString(r,15));
                vo.setSettlementCity(readString(r,16));
                vos.add(vo);
            }

        } catch (IOException e) {
            log.error("文件读取失败！", e);
            throw new BusinessException(e);
        } catch (Exception e) {
            log.error("文件解析失败！", e);
            throw new BusinessException(e);
        }finally {
            if(inp!=null){
                try {
                    inp.close();
                }catch(Exception e){
                    log.error("FileInputStream close error!",e);
                }
            }
        }
        return vos;
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
                }else if("NULL".equals(cell.getRichStringCellValue().getString())){
                    return null;
                }else{
                    if(index == 3){
                        System.out.println(cell.getRichStringCellValue().getString().trim());
                    }
                    return cell.getRichStringCellValue().getString().trim();
                }
            case NUMERIC:
                if (cell.getDateCellValue() !=null ) {
                    String value = String.valueOf(cell.getNumericCellValue());
                    if(StringUtils.isBlank(value)){
                        return null;
                    }
                    if("NULL".equals(value) ){
                        return null;
                    }
                    if(index == 3){
                        System.out.println(value);
                    }
                    return value;
                } else {
                    throw new BusinessException("["+cell.getStringCellValue()+"]日期格式错误，支持yyyy-MM-dd、yyyy/MM/dd。");
                }
            default:
                return null;
        }
    }

    private String getOrderCode(){
        SimpleDateFormat sf = new SimpleDateFormat("YYYYMMDDHHmmss");
        return sf.format(new Date());
    }
}
