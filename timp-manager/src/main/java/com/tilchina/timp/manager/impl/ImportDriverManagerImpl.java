package com.tilchina.timp.manager.impl;

import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.manager.ImportDriverManager;
import com.tilchina.timp.model.*;
import com.tilchina.timp.service.*;
import com.tilchina.timp.vo.ImportDriverVO;
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
public class ImportDriverManagerImpl implements ImportDriverManager {

    @Autowired
    private CorpService corpService;

    @Autowired
    private UserService userService;

    @Autowired
    private TransporterService transporterService;

    @Autowired
    private TransporterAndDriverService transporterAndDriverService;

    @Autowired
    private DriverStatusService driverStatusService;

    @Autowired
    private TransporterStatusService transporterStatusService;

    @Override
    @Transactional
    public void readFile(File file) {
        List<ImportDriverVO> vos = readDefault(file);

        //保存公司
        Map<String, Corp> corpMap = addCorp(vos);

        //保存司机
        Map<String, User> userMap = addUser(vos,corpMap);

        //获取挂车型号
        Map<String, Long> trailMap = new HashMap<>();
        trailMap.put("6", 1L);
        trailMap.put("7", 2L);
        trailMap.put("8", 3L);
        trailMap.put("10", 4L);
        trailMap.put("12", 5L);

        //保存轿运车档案
        Map<String,Transporter> transporterMap = addTransporter(vos,trailMap,userMap,corpMap);

        //保存轿运车司机关系表
        List<TransporterAndDriver> trans = addDriverAndTrans(userMap,transporterMap);

        //保存驾驶员状态
        addDriverStatus(trans);
        //保存轿运车状态
        addTransStatus(trans);

    }

    @Override
    @Transactional
    public void addTransStatus(List<TransporterAndDriver> trans){
        List<TransporterStatus> transStatus = new ArrayList<>();
        for (TransporterAndDriver tran : trans) {
            TransporterStatus status = new TransporterStatus();
            status.setTransporterId(tran.getTransporterId());
            status.setDriverId(tran.getDriverId());
            status.setTransporterStatus(0);
            status.setCreator(1L);
            status.setCreateDate(new Date());
            status.setCorpId(tran.getCorpId());
            status.setFlag(0);
            transStatus.add(status);
        }
        transporterStatusService.add(transStatus);
    }

    @Override
    @Transactional
    public void addDriverStatus(List<TransporterAndDriver> trans){
        List<DriverStatus> driverStatuses = new ArrayList<>();
        for (TransporterAndDriver tran : trans) {
            DriverStatus status = new DriverStatus();
            status.setDriverId(tran.getDriverId());
            status.setTransporterTransporterId(tran.getTransporterId());
            status.setDriverStatus(0);
            status.setCreator(1L);
            status.setCreateDate(new Date());
            status.setCorpId(tran.getCorpId());
            status.setFlag(0);
            driverStatuses.add(status);
        }
        driverStatusService.add(driverStatuses);
    }

    @Override
    @Transactional
    public List<TransporterAndDriver> addDriverAndTrans(Map<String, User> userMap, Map<String, Transporter> transporterMap){
        List<TransporterAndDriver> trans = new ArrayList<>();
        for (String s : transporterMap.keySet()) {
            User user = userMap.get(s);
            Transporter t = transporterMap.get(s);
            if(t.getContractorId() == null){
                continue;
            }
            TransporterAndDriver tran = new TransporterAndDriver();
            tran.setTransporterId(t.getTransporterId());
            tran.setDriverId(user.getUserId());
            tran.setCreator(1L);
            tran.setCreateDate(new Date());
            tran.setCorpId(user.getCorpId());
            tran.setFlag(0);
            trans.add(tran);
        }
        transporterAndDriverService.add(trans);
        return trans;
    }

    @Override
    @Transactional
    public Map<String,Transporter> addTransporter(List<ImportDriverVO> vos, Map<String, Long> trailMap, Map<String, User> userMap, Map<String, Corp> corpMap) {
        List<Transporter> transporters = new ArrayList<>();
        Map<String,Transporter> transporterMap = new HashMap<>();
        int code = 600000;
        int i = 0;
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        for (ImportDriverVO vo : vos) {
            Transporter t = new Transporter();
            t.setTransporterCode((code + i) + "");
            t.setTractorId(1L);
            t.setTractirPlateCode(vo.getTractorPlate());
            t.setTractirVin(vo.getGps());
            t.setEngineCode(vo.getQrCode());
            t.setTrailerId(trailMap.get(vo.getMax()));
            t.setTrailerPlateCode(vo.getTrailerPlate());
            t.setContractorId(userMap.get(vo.getDriverName())==null?null:userMap.get(vo.getDriverName()).getUserId());
            try {
                t.setContractorEndDate(sf.parse("2099-01-01"));
            }catch(Exception e){
                e.printStackTrace();
            }
            t.setBillStatus(1);
            t.setCreator(1L);
            t.setCreateDate(new Date());
            t.setChecker(1L);
            t.setCheckDate(new Date());
            t.setCorpId(corpMap.get(vo.getCorpName()).getCorpId());
            t.setFlag(0);
            t.setTrucksType(0);
            transporters.add(t);
            i++;
            if(vo.getDriverName() == null){
                continue;
            }
            transporterMap.put(vo.getDriverName(),t);
        }
        transporterService.add(transporters);
        return transporterMap;
    }

    @Override
    @Transactional
    public Map<String, User> addUser(List<ImportDriverVO> vos, Map<String, Corp> corpMap) {
        List<User> users = new ArrayList<>();
        Map<String, User> userMap = new HashMap<>();
        List<String> used = new ArrayList<>();
        int code = 600000;
        int i = 0;
        for (ImportDriverVO vo : vos) {
            if(StringUtils.isBlank(vo.getDriverName())){
                continue;
            }
            if(used.contains(vo.getDriverName())){
                continue;
            }
            used.add(vo.getDriverName());
            User u = new User();
            u.setUserCode((code + i) + "");
            u.setUserName(vo.getDriverName());
            u.setPhone("无");
            u.setUserType(1);
            u.setDriverType("承包".equals(vo.getSettlement())?1:2);
            u.setSex(1);
            u.setBirthday(new Date());
            u.setBindingPhone(0);
            u.setBillStatus(1);
            u.setCreator(1L);
            u.setCreateDate(new Date());
            u.setChecker(1L);
            u.setCheckDate(new Date());
            u.setCorpId(corpMap.get(vo.getCorpName()).getCorpId());
            u.setFlag(0);
            users.add(u);
            userMap.put(vo.getDriverName(), u);
            i++;
        }
        userService.add(users);
        return userMap;
    }

    // 保存客户公司
    @Override
    @Transactional
    public Map<String, Corp> addCorp(List<ImportDriverVO> vos) {
        Map<String, List<ImportDriverVO>> map = vos.stream().collect(Collectors.groupingBy(ImportDriverVO::getCorpName));
        List<Corp> corps = new ArrayList<>();
        int i = 0;
        int code = 110000;
        for (String s : map.keySet()) {
            Corp corp = new Corp();
            corp.setCorpCode((code + i) + "");
            corp.setCorpName(s);
            corp.setCorpTypeId(1L);
            corp.setCreator(1L);
            corp.setCreateDate(new Date());
            corp.setFlag(0);
            // corp.setDel(0L);
            corps.add(corp);
            i++;
        }
        corpService.add(corps);
        Map<String, Corp> corpMap = new HashMap<>();
        corps.forEach(corp -> corpMap.put(corp.getCorpName(), corp));
        return corpMap;
    }

    private List<ImportDriverVO> readDefault(File file) {
        FileInputStream inp = null;
        List<ImportDriverVO> vos = new ArrayList<>();

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
                ImportDriverVO vo = new ImportDriverVO();
                vo.setCorpName(readString(r, 0));
                if (StringUtils.isBlank(vo.getCorpName())) {
                    continue;
                }
                vo.setDriverName(readString(r, 1));
                vo.setTractorPlate(readString(r, 2));
                if (StringUtils.isBlank(vo.getTractorPlate())) {
                    continue;
                }
                vo.setTrailerPlate(readString(r, 3));
                vo.setMax(readString(r, 4));
                vo.setCdCard(readString(r, 5));
                vo.setQrCode(readString(r, 9));
                vo.setGps(readString(r, 10));
                vo.setSettlement(readString(r, 14));
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
