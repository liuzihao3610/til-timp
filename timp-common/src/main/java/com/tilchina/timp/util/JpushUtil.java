package com.tilchina.timp.util;

import cn.jiguang.common.utils.Base64;
import com.alibaba.fastjson.JSONObject;
import com.tilchina.catalyst.spring.PropertiesUtils;
import com.tilchina.timp.enums.PlatformType;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSONArray;

/**
     * java后台极光推送方式一：使用Http API
     * 此种方式需要自定义http请求发送客户端:HttpClient
     */
    @SuppressWarnings({ "deprecation", "restriction" })
    public class JpushUtil {
        private static final Logger log = LoggerFactory.getLogger(JpushUtil.class);
        private static String appKey = "e1c4814e0cd6be497a127a74";
        private static String masterSecret = "4e540169898044ec6255b9da";

        // 设置车载的app_key和masterSecret
        private static String carAppKey = PropertiesUtils.getString("carjgappkey");
        private static String carMasterSecret = PropertiesUtils.getString("carjgmastersecret");

        // 设置司机的app_key和masterSecret
        private static String driverAppKey = PropertiesUtils.getString("driverjgappkey");
        private static String driverMasterSecret = PropertiesUtils.getString("driverjgmastersecret");

        private static String pushUrl = "https://api.jpush.cn/v3/push";
        private static boolean apns_production = false;
        private static int time_to_live = 86400;


        /**
         * 极光推送
         */
        public static void jiguangPush(int platformType,String tag,String alert,String title){
            try{
                String result = push(pushUrl,platformType,tag,alert,title,appKey,masterSecret,apns_production,time_to_live);
                 JSONObject resData = JSONObject.parseObject( result);
                if(resData.toString().indexOf("error") != -1){
                    log.info("针对标签为" + tag + "的信息推送失败！");
                    JSONObject error = JSONObject.parseObject((resData.get("error").toString()));
                    log.info("错误信息为：" + error.get("message").toString());
                }else {
                    log.info("针对标签为" + tag + "的信息推送成功！");
                }

            }catch(Exception e){
                log.error("针对标签为" + tag + "的信息推送失败！",e);
            }
        }

        /**
         * 组装极光推送专用json串
         * @param platformType
         * @param audience
         * @param alert
         * @param title
         * @param apns_production
         * @param time_to_live
         * @return
         */
        public static JSONObject generateJson(int platformType,String audience,String alert,String title,boolean apns_production,int time_to_live){
            JSONObject json = new JSONObject();
            JSONArray platform = new JSONArray();//平台
            json.put("audience", audience);//推送目标
            JSONObject notification = new JSONObject();//通知内容
            JSONObject options = new JSONObject();//设置参数
            JSONObject message = new JSONObject();//设置自定义消息
            message.put("msg_content",alert);
            message.put("title",title);
            json.put("message",message);
            if (platformType == PlatformType.ANDROID.getIndex() || platformType == PlatformType.ALL.getIndex()){
                platform.add("android");
                JSONObject android = new JSONObject();//android通知内容
                android.put("alert", alert);
                android.put("title", title);
                 JSONObject android_extras = new JSONObject();//android额外参数
                android_extras.put("type", "infomation");
                android.put("extras", android_extras);
                notification.put("android", android);
                options.put("sendno","1");
            }
            if (platformType == PlatformType.IOS.getIndex()|| platformType == PlatformType.ALL.getIndex()){
                platform.add("ios");
                JSONObject ios = new JSONObject();//ios通知内容
                ios.put("alert", alert);
                ios.put("sound", "default");
                ios.put("badge", "+1");
                JSONObject ios_extras = new JSONObject();//ios额外参数
                ios_extras.put("type", "infomation");
                ios.put("extras", ios_extras);
                notification.put("ios", ios);
            }

            options.put("time_to_live", Integer.valueOf(time_to_live));
            options.put("apns_production", apns_production);
           /* options.put("apns_production", "false");*/
            json.put("platform", platform);

            json.put("notification", notification);
            json.put("options", options);
            return json;

        }



        /**
         * 推送方法-调用极光API
         * @param reqUrl
         * @param tag
         * @param alert
         * @return result
         */
        public static String push(String reqUrl,int platformType,String tag,String alert,String title,String appKey,String masterSecret,boolean apns_production,int time_to_live){
            String base64_auth_string = encryptBASE64( appKey + ":" + masterSecret);
            String authorization = "Basic " + base64_auth_string;
            String x = generateJson(platformType, tag, alert, title, apns_production, time_to_live).toString().replaceAll("\\\\","");
            System.out.println(x);
            System.out.println(x.replaceAll("\"\\{\"tag","{\"tag").replaceAll("\"\\]\\}\"","\"]}"));
            x = x.replaceAll("\"\\{\"tag","{\"tag").replaceAll("\"\\]\\}\"","\"]}");
            return sendPostRequest(reqUrl,x ,"UTF-8",authorization);
        }

        /**
         * 发送Post请求（json格式）
         * @param reqURL
         * @param data
         * @param encodeCharset
         * @param authorization
         * @return result
         */
        @SuppressWarnings({ "resource" })
        public static String sendPostRequest(String reqURL, String data, String encodeCharset,String authorization){
            HttpPost httpPost = new HttpPost(reqURL);
            HttpClient client = new DefaultHttpClient();
            HttpResponse response = null;
            String result = "";
            try {
             StringEntity entity = new StringEntity(data, encodeCharset);
                entity.setContentType("application/json");
                httpPost.setEntity(entity);
                httpPost.setHeader("Authorization",authorization);
                response = client.execute(httpPost);
                result = EntityUtils.toString(response.getEntity(), encodeCharset);
     /*           Request.Post(reqURL)
                        .addHeader("Authorization", authorization)
                        .bodyString(data, ContentType.APPLICATION_JSON).execute();*/
            } catch (Exception e) {
                log.error("请求通信[" + reqURL + "]时偶遇异常,堆栈轨迹如下", e);
            }finally{
                client.getConnectionManager().shutdown();
            }
            return result;
        }
        /**
         　　　　* BASE64加密工具
         　　　　*/
        public static String encryptBASE64(String str) {
            char[] encode = Base64.encode(str.getBytes());
            String key = String.valueOf(encode);
            return key;
        }

        /**
         * 组装极光推送专用json串
         * @param tag
         * @param alert
         * @return json
         */
        public static JSONObject generateJson(String tag,String alert,String title,boolean apns_production,int time_to_live){
            JSONObject json = new JSONObject();
            JSONArray platform = new JSONArray();//平台
            platform.add("android");

            JSONObject audience = new JSONObject();//推送目标
            JSONArray tag1 = new JSONArray();
            if (!"all".equals(tag)){
                tag1.add(tag);
                audience.put("tag", tag1);
                json.put("audience", audience);
            }else{
                json.put("audience", "all");
            }
            JSONObject notification = new JSONObject();//通知内容
            JSONObject android = new JSONObject();//android通知内容
            android.put("alert", alert);
            android.put("title", title);
/*            JSONObject android_extras = new JSONObject();//android额外参数
            android_extras.put("type", "infomation");
            android.put("extras", android_extras);*/
            notification.put("android", android);

/*
            platform.add("ios");
            JSONObject ios = new JSONObject();//ios通知内容
            ios.put("alert", alert);
            ios.put("sound", "default");
            ios.put("badge", "+1");
            JSONObject ios_extras = new JSONObject();//ios额外参数
            ios_extras.put("type", "infomation");
            ios.put("extras", ios_extras);
            notification.put("ios", ios);
*/

            JSONObject options = new JSONObject();//设置参数
            options.put("time_to_live", Integer.valueOf(time_to_live));
            options.put("apns_production", apns_production);
            /*       options.put("apns_production", "false");*/
            json.put("platform", platform);

            json.put("notification", notification);
            json.put("options", options);
            return json;

        }

    }
