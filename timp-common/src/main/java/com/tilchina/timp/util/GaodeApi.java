package com.tilchina.timp.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.DecimalFormat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tilchina.catalyst.spring.PropertiesUtils;
import com.tilchina.timp.expection.BusinessException;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EncodingUtils;

public class GaodeApi{

	/**
	 * 规范经纬度长度
	 * @param lon
	 * @param lat
	 * @return
	 */
	   public static String doubleChangeString(double lon, double lat) {
		   String lntlat;
		   DecimalFormat df = new DecimalFormat("#.000000");
		   lntlat = df.format(lon)+","+df.format(lat);
		return lntlat;
	   }

    public static void main(String[] args){
        String start = "成都市";
        String end = "深圳市";
        String startLonLat = getLonLat(start);
        String endLonLat = getLonLat(end);
        System.out.println(startLonLat);
        System.out.println(endLonLat);
        Double dis = getDistance(startLonLat,endLonLat);
        System.out.println(dis);

//    	System.out.println(doubleChangeString(105.170000000000000000000000000000,121.290000000000000000000000000000));
//    	System.out.println(doubleChangeString(113.495406000000000000000000000000,22.625025000000000000000000000000));
//        TestGetDistance(doubleChangeString(121.473701,31.230416),
//        		doubleChangeString(121.473701,31.230416));

    }
	// 设置好账号的app_key和masterSecret
	private static String key = PropertiesUtils.getString("gdkey");
	private static String url = PropertiesUtils.getString("gdurl");
/*	private static String key = "00a54daf032c27ca9eef3e6d4dde47de";
	private static String url = "http://restapi.amap.com/v3";*/
   /**
    * 获取地址的经纬度
    * @param address
    * @return
    */
    public static String getLonLat(String address){
        //返回输入地址address的经纬度信息, 格式是 经度,纬度
        String queryUrl = url+"/geocode/geo?key="+key+"&address="+address;
        String queryResult = getResponse(queryUrl);  //高德接口返回的是JSON格式的字符串
        JSONObject jo = JSON.parseObject(queryResult);
        JSONArray ja = jo.getJSONArray("geocodes");
        if(!"OK".equals(jo.get("info"))) {
            throw new BusinessException("GaodeApi:获取地址的经纬度错误提示"+jo.get("info").toString()+",错误编码："+jo.get("infocode").toString());
        }
        return JSON.parseObject(ja.getString(0)).get("location").toString();
    }

    /**
     * 获取起止地距离
     *
     * @since 1.0.0
     * @param startAdd	起始地
     * @param endAdd	目的地
     * @return java.lang.Double
     */
    public static Double getDistanceByAddress(String startAdd, String endAdd){
        try {
            String strat = getLonLat(startAdd);
            String end = getLonLat(endAdd);
            return getDistance(strat,end);
        }catch (Exception e) {
            if(e instanceof BusinessException){
                throw e;
            }else {
                throw new RuntimeException("操作失败：获取获取两地之间的距离失败！", e);
            }
        }
    }

    /**
     * 获取两地之间的距离,单位：km
     * @param startLonLat
     * @param endLonLat
     * @return
     */
    public static Double TestGetDistance(String startLonLat, String endLonLat){
        //返回起始地startAddr与目的地endAddr之间的距离，单位：km
        Double result = new Double(0.00),dist = new Double(0.00);
        try {
            if(startLonLat.contains("null")) {
                throw new BusinessException("数据检查失败：startLonLat:" + startLonLat);
            }else if(endLonLat.contains("null")){
                throw new BusinessException("数据检查失败：endLonLat:" + endLonLat);
            }else {
                String queryUrl = "http://restapi.amap.com/v3/distance?"
                        //出发点
                        + "origins="+startLonLat
                        //目的地
                        + "&destination="+endLonLat
                        //返回数据格式类型:可选值：JSON，XML
                        + "&output=JSON"
                        //驾车导航距离
                        + "&type=1"
                        + "&key=00a54daf032c27ca9eef3e6d4dde47de";
                String queryResult = getResponse(queryUrl);
                JSONObject jo = JSON.parseObject(queryResult);
                if(!"OK".equals(jo.get("info"))) {
                    throw new BusinessException(jo.get("info").toString());
                }
                JSONArray ja = jo.getJSONArray("results");
                if(JSON.parseObject(ja.getString(0)).get("info") != null){
                    throw new BusinessException("GaodeApi:错误提示"+jo.get("info").toString()+",错误编码："+jo.get("infocode").toString());
                }
                dist = Double.parseDouble((JSON.parseObject(ja.getString(0)).get("distance").toString()));
                result = Math.round(dist/100d)/10d;
            }
        } catch (Exception e) {
            if(e instanceof BusinessException){
                throw e;
            }else {
                throw new RuntimeException("GaodeApi:操作失败：获取获取两地之间的距离失败！", e);
            }
        }

        return result;
    }

    /**
     * 获取两地之间的距离,单位：km
     * @param startLonLat
     * @param endLonLat
     * @return
     */
    public static Double getDistance(String startLonLat, String endLonLat){
        //返回起始地startAddr与目的地endAddr之间的距离，单位：km
        Double result = new Double(0.00),dist = new Double(0.00);
        try {
            if(startLonLat.contains("null")) {
                throw new BusinessException("数据检查失败：startLonLat:" + startLonLat);
            }else if(endLonLat.contains("null")){
                throw new BusinessException("数据检查失败：endLonLat:" + endLonLat);
            }else {
                String queryUrl =
                        url+"/distance?"
                                //出发点
                                + "origins="+startLonLat
                                //目的地
                                + "&destination="+endLonLat
                                //返回数据格式类型:可选值：JSON，XML
                                + "&output=JSON"
                                //驾车导航距离
                                + "&type=1"
                                + "&key="+key;
                String queryResult = getResponse(queryUrl);
                JSONObject jo = JSON.parseObject(queryResult);
                if(!"OK".equals(jo.get("info"))) {
                    throw new BusinessException("GaodeApi:错误提示"+jo.get("info").toString()+",错误编码："+jo.get("infocode").toString());
                }
                JSONArray ja = jo.getJSONArray("results");
                if(JSON.parseObject(ja.getString(0)).get("info") != null){
                    throw new BusinessException("GaodeApi:起始地经纬度:"+startLonLat+"目的地经纬度:"+endLonLat+"error:"+JSON.parseObject(ja.getString(0)).get("info").toString());
                }
                dist = Double.parseDouble((JSON.parseObject(ja.getString(0)).get("distance").toString()));
                result = Math.round(dist/100d)/10d;
            }
        } catch (Exception e) {
            if(e instanceof BusinessException){
                throw e;
            }else {
                throw new RuntimeException("GaodeApi:操作失败：获取获取两地之间的距离失败！", e);
            }
        }

        return result;
    }

    private static String getResponse(String serverUrl){
        //用JAVA发起http请求，并返回json格式的结果
        StringBuffer result = new StringBuffer();
        try {
            String json = Request.Get(serverUrl).execute().returnContent().asString(Charset.forName("UTF8"));
          /*  URL url = new URL(serverUrl);
            URLConnection conn = url.openConnection();*/
      /*      BufferedReader in = new BufferedReader(new InputStreamReader(content));
            String line;
            while((line = in.readLine()) != null){
                line =  EncodingUtils.getString(line.getBytes(), "GBK");
                line =  EncodingUtils.getString(line.getBytes(), "UTF8");
                result.append(line);  
            }  
            in.close();  */
            return json;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

}  