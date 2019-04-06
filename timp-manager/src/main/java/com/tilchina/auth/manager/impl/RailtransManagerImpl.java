package com.tilchina.auth.manager.impl;

import com.tilchina.auth.manager.RailtransOrderManager;
import com.tilchina.timp.common.Environment;
import com.tilchina.timp.expection.BusinessException;
import com.tilchina.timp.model.*;
import com.tilchina.timp.service.RailtransOrderDetailService;
import com.tilchina.timp.service.RailtransOrderService;
import com.tilchina.timp.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RailtransManagerImpl implements RailtransOrderManager {

    @Autowired
    private RailtransOrderService railtransOrderService;

    @Autowired
    private RailtransOrderDetailService railtransOrderDetailService;

    @Override
    public String importText(File file) throws Exception {

        List<RailtransParsedTextModel> models = new LinkedList<>();

        // if throws exception using getCanonicalPath instead of getAbsolutePath
        Path path = Paths.get(file.getAbsolutePath());

        try {
            String gbkText = new String(Files.readAllBytes(path), "GBK");
            // String uniText = new String(gbkText.getBytes(), "UTF-8");
            // System.out.println(uniText);

            String[] lines = gbkText.split("\r\n");

            for (String line : lines) {
                String[] segments = line.split("[，]|[,]");

                //System.out.println(String.format("%s", segments[1]));
                models.add(new RailtransParsedTextModel() {{
                    setCarVin(check(segments[1]));
                    setCabinNumber(check(segments[2]));
                    setTerminalStation(check(segments[0]));
                }});
            }
            return railtransOrderDetailService.updateRangeByCarVin(models);
        } catch (IOException e) {
            throw e;
        }
    }

    private String check(String str) {
        return StringUtils.isBlank(str) ? null : str.trim();
    }

    @Override
    @Transactional
    public void importExcel(File file) {

        Environment env = Environment.getEnv();
        RailtransOrder order = new RailtransOrder();

        List<RailtransOrderDetail> details = new LinkedList<>();
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");

        try (InputStream inputStream = new FileInputStream(file)) {

            HSSFWorkbook workbook = new HSSFWorkbook(inputStream);

            // Get first or default worksheet
            HSSFSheet sheet = workbook.getSheetAt(0);

            int rowNumber = sheet.getLastRowNum();
            int colNumber = sheet.getRow(0).getLastCellNum();


            HSSFRow row;
            for (int i = 1; i <= rowNumber; i++) {

                row = sheet.getRow(i);
                HSSFCell[] cells = new HSSFCell[colNumber];

                try {

                    for (int j = 0; j < colNumber; j++) {

                        cells[j] = row.getCell(j);

                        // cells[j].setCellType(CellType.STRING);
                        if (cells[j] == null) {
                            // System.out.println(String.format("%d: %s", j, cells[j]));
                        } else if (j >= 9 && j <= 13) {
                            cells[j].setCellType(CellType.NUMERIC);
                            // System.out.println(String.format("%d: %s", j, parser.format(HSSFDateUtil.getJavaDate(cells[j].getNumericCellValue()))));
                        } else {
                            cells[j].setCellType(CellType.STRING);
                            // System.out.println(String.format("%d: %s", j, cells[j].getStringCellValue().replace("\n", "")));
                        }

                        // System.out.println(String.format("%d: %s ", j, cells[j].getCellTypeEnum()));
                    }

                } catch (Exception e) {
                    // Ignore
                    e.printStackTrace();
                }

                // 0:   No.序号
                // 1:   Trans. Order运输指令
                // 2:   Carrier运输商
                // 3:   Hub LocationHub位置
                // 4:   Dealer No.经销商编号
                // 5:   Dealer Name经销商名称
                // 6:   VIN底盘编号
                // 7:   MODEL车型
                // 8:   批次
                // 9:   TO Date运输指令下达日
                // 10:  Planned Dispatch Date计划发运日期
                // 11:  Actual Pick-Up Date实际提车日期
                // 12:  Actual Dispatch Date实际发运日期
                // 13:  Estimated To Hub Date预计达到Hub仓库日期
                // 14:  Due Date交货期限
                // 15:  Zone位置
                // 16:  Remark备注

                // cells[1]:   transOrder
                // cells[4]:   dealerNo
                // cells[5]:   dealerName
                // cells[6]:   carVin
                // cells[7]:   carModel
                // cells[9]:   toDate
                // cells[10]:  plannedDispatchedDate
                // cells[11]:  actualPickupDate
                // cells[12]:  actualDispatchedDate
                // cells[13]:  estTohubDate
                // cells[14]:  dueDate
                // cells[15]:  zone
                // cells[16]:  remark

                RailtransOrderDetail detail = new RailtransOrderDetail();

                // if (cells[1]  == null) { model.setTransOrder(null); }
                // if (cells[2]  == null) {  }
                // if (cells[3]  == null) {  }
                // if (cells[4]  == null) { model.setDealerNo(null); }
                // if (cells[5]  == null) { model.setDealerName(null); }
                // if (cells[6]  == null) { model.setCarVin(null); }
                // if (cells[7]  == null) { model.setCarModel(null); }
                // if (cells[8]  == null) {  }
                // if (cells[9]  == null) { model.setToDate(null); }
                // if (cells[10] == null) { model.setPlannedDispatchedDate(null); }
                // if (cells[11] == null) { model.setActualPickupDate(null); }
                // if (cells[12] == null) { model.setActualDispatchedDate(null); }
                // if (cells[13] == null) { model.setEstToh ubDate(null); }
                // if (cells[14] == null) { model.setDueDate(null); }
                // if (cells[15] == null) { model.setZone(null); }
                // if (cells[16] == null) { model.setRemark(null); }

                if (cells[1] != null) {
                    detail.setTransOrder(cells[1].getStringCellValue());
                }
                if (cells[2] != null) {
                    order.setCarrier(cells[2].getStringCellValue());
                }
                if (cells[3] != null) {
                    order.setHubLocation(cells[3].getStringCellValue());
                }
                if (cells[4] != null) {
                    detail.setDealerNo(cells[4].getStringCellValue());
                }
                if (cells[5] != null) {
                    detail.setDealerName(cells[5].getStringCellValue());
                }
                if (cells[6] != null) {
                    detail.setCarVin(cells[6].getStringCellValue());
                }
                if (cells[7] != null) {
                    detail.setCarModel(cells[7].getStringCellValue());
                }
                if (cells[8] != null) {
                    order.setCarBatch(cells[8].getStringCellValue());
                }
                if (cells[9] != null) {
                    detail.setToDate(HSSFDateUtil.getJavaDate(cells[9].getNumericCellValue()));
                }
                if (cells[10] != null) {
                    detail.setPlannedDispatchedDate(HSSFDateUtil.getJavaDate(cells[10].getNumericCellValue()));
                }
                if (cells[11] != null) {
                    detail.setActualPickupDate(HSSFDateUtil.getJavaDate(cells[11].getNumericCellValue()));
                }
                if (cells[12] != null) {
                    detail.setActualDispatchedDate(HSSFDateUtil.getJavaDate(cells[12].getNumericCellValue()));
                }
                if (cells[13] != null) {
                    detail.setEstTohubDate(HSSFDateUtil.getJavaDate(cells[13].getNumericCellValue()));
                }
                if (cells[14] != null) {
                    detail.setDueDate(HSSFDateUtil.getJavaDate(cells[14].getNumericCellValue()));
                }
                if (cells[15] != null) {
                    detail.setZone(cells[15].getStringCellValue());
                }
                if (cells[16] != null) {
                    detail.setRemark(cells[16].getStringCellValue());
                }
                detail.setStartingStation("华晨宝马主机厂");
                details.add(detail);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        order.setTransOrderCode(DateUtil.dateToStringCode(new Date()));
        order.setClient("Toyota");

        order.setCreator(env.getUser().getUserId());
        order.setChecker(order.getCreator());
        order.setCorpId(env.getCorp().getCorpId());
        order.setBillStatus(1);

        order.setCreateDate(new Date());
        order.setCheckDate(order.getCreateDate());
        railtransOrderService.add(order);

        details.forEach(detail -> {
            detail.setTransOrderId(order.getTransOrderId());
            detail.setCreateDate(new Date());
            detail.setCreator(env.getUser().getUserId());
            detail.setCorpId(env.getCorp().getCorpId());
        });

        railtransOrderDetailService.add(details);
    }

    @Override
    public File exportExcel() {
        return null;
    }

    @Override
    public void getLatestCabinStatus() {

        List<RailtransCabinStatus> models = new ArrayList<>();
        railtransOrderDetailService
                .selectDistinctCabinNumber()
                .forEach(map -> {
                            Object cabin = ((Map) map).get("CABIN_NUMBER");
                            if (cabin != null && !StringUtils.isBlank(cabin.toString())) {
                                models.add(parseHtml((Map) map));
                            }
                        }
                );

        railtransOrderDetailService.updateRangeCabinStatusByCabinNumber(models);
    }

    @Override
    public void getSpecifiedCabinStatus(Map<String, String> params) {

        String cabinNumber = params.get("CABIN_NUMBER");
        String pattern = "\\D";
        Pattern p = Pattern.compile(pattern);
        Matcher results = p.matcher(cabinNumber);
        if (results.find()) {
            throw new BusinessException("车厢号应为6位纯数字，请检查后重试。");
        }

        List result = railtransOrderDetailService.getByCabinNumber(new ArrayList<String>() {{
            add(cabinNumber);
        }});
        if (result.size() > 0) {

            RailtransOrderDetail railtransOrderDetail = (RailtransOrderDetail) result.stream().findFirst().get();

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long diff = Calendar.getInstance().getTimeInMillis() - railtransOrderDetail.getLatestUpdateTime().getTime();
            long mins = TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS);
            if (mins > 15) {
                RailtransCabinStatus status = parseHtml(params);
                railtransOrderDetailService.updateCabinStatusByCabinNumber(status);
            } else {
                throw new BusinessException("请求过于频繁，请稍后再试。");
            }
        } else {
            throw new BusinessException("该车厢号未查询到任何车辆记录，请检查后重试。");
        }
    }

    @Override
    public String getHtmlFromCrscsc(String cabinNumber) {
        try {
            HttpGet httpGet = new HttpGet(String.format("http://www.crscsc.com.cn/trackQuery/ch/?ch=%s", URLEncoder.encode(cabinNumber,"UTF-8")));
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            String html = EntityUtils.toString(httpResponse.getEntity());
            httpResponse.close();
            httpClient.close();

            Document doc = Jsoup.parse(html);
            Elements result = doc.select("#resultBox");

            return result.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private RailtransCabinStatus parseHtml(Map params) {

        String cabinNumber = params.get("CABIN_NUMBER").toString();
        String terminalStation = params.get("TERMINAL_STATION").toString();


        try {
            HttpGet httpGet = new HttpGet(String.format("http://www.crscsc.com.cn/trackQuery/ch/?ch=%s", URLEncoder.encode(cabinNumber,"UTF-8")));

            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            String html = EntityUtils.toString(httpResponse.getEntity());

            httpResponse.close();
            httpClient.close();

            Document doc = Jsoup.parse(html);
            Elements ul = doc.select("#result");
            Elements lis = ul.select("li");

            List<TrainStatus> statuses = new LinkedList<>();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for (Element li : lis) {
                if (!li.hasClass("togglable toggled")) {

                    try {

                        Date dateTime = format.parse(li.child(0).text());
                        String[] segments = li.child(2).text().replace("\n", "").split(" ");

                        String province = segments[0];
                        String city = segments[1];
                        String station = segments[2];
                        String status = segments[3];
                        String other = "";
                        StringBuilder sb = new StringBuilder();
                        for (int i = 4; i < segments.length; i++) {

                            if (i == segments.length - 1) {
                                sb.append(String.format("$s ", segments[i]));
                            } else {
                                sb.append(String.format("$s", segments[i]));
                            }
                        }

                        statuses.add(
                                new TrainStatus() {{
                                    setDateTime(dateTime);
                                    setProvince(province);
                                    setCity(city);
                                    setStation(station);
                                    setStatus(status);
                                    setOther(sb.toString());
                                }}
                        );
                    } catch (ParseException e) {
                        // e.printStackTrace();
                    }
                }
            }

            RailtransCabinStatus model = new RailtransCabinStatus();
            model.setLatestUpdateTime(new Date());

            // System.out.println(String.format("Count: %d", statuses.size()));

            if (statuses.size() == 0) return null;

            TrainStatus latestStatus = ((LinkedList<TrainStatus>) statuses).getFirst();
            model.setCabinNumber(cabinNumber);
            model.setLatestProvince(latestStatus.getProvince());
            model.setLatestCity(latestStatus.getCity());
            model.setLatestStation(latestStatus.getStation());
            model.setLatestStatus(latestStatus.getStatus());
            model.setIsArrived(0);
            model.setRemark(latestStatus.getOther());

            statuses.forEach(trainStatus -> {
                if (trainStatus.getStation().contains(terminalStation)) {
                    model.setCabinNumber(cabinNumber);
                    model.setLatestProvince(trainStatus.getProvince());
                    model.setLatestCity(trainStatus.getCity());
                    model.setLatestStation(trainStatus.getStation());
                    model.setLatestStatus(trainStatus.getStatus());
                    model.setIsArrived(1);
                    model.setZone("已到达成都DDA");
                    model.setRemark(trainStatus.getOther());
                    model.setLatestUpdateTime(new Date());
                    return;
                }
            });

            return model;

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }
}
