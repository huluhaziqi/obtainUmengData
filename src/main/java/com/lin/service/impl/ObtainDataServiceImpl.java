package com.lin.service.impl;

import com.lin.model.entity.ActityUserSummaryEntity;
import com.lin.model.entity.StatDailyUseLengthTimeEntity;
import com.lin.service.ObtainDataService;
import com.lin.util.JsonUtils;
import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ObtainDataServiceImpl implements ObtainDataService {

    private Logger logger = LoggerFactory.getLogger(ObtainDataServiceImpl.class);


    public static CloseableHttpClient client;
    public static final String PARAMTER_ENCODING = "UTF-8";

    public final String LOGINPAGE_URL = "http://i.umeng.com/";
    public final String LOGIN_URL = "http://i.umeng.com/login/ajax_do";

    private final String UMENG_ANDROID_ID = "7d8100e975a0b72bda302585";
    private final String UMENG_IOS_ID = "0f420015d6b58ea2fc302585";

    public final String UMENG_REPORT_URL = "http://mobile.umeng.com/apps/%s/reports/active_user";

    public final String UMENG_ACTIVE_USER_URL = "http://mobile.umeng.com/apps/%s/reports/get_active_user_summary";

    public final String UMENG_ACTIVE_USER_BETWEEN_DATE_URL = "http://mobile.umeng.com/apps/%s/reports/load_table_data?page=%s&per_page=%s&start_date=%s&end_date=%s" +
            "&time_unit=daily&stats=active_users";
    public final String UMENG_DURATION_URL = "http://mobile.umeng.com/apps/%s/reports/load_table_data?page=%s&per_page=%s" +
            "&start_date=%s&end_date=%s&time_unit=daily&stats=duration&stat_type=daily_per_launch";

    public final String UMENG_DAILY_USER_LENGTH_TIME_URL = "http://mobile.umeng.com/apps/%s/reports/load_table_data?page=%s&per_page=%s" +
            "&start_date=%s&end_date=%s&time_unit=daily&stats=duration&stat_type=daily";

    public final String UMENG_DAILY_USE_COUNT_URL = "http://mobile.umeng.com/apps/%s/reports/load_table_data?page=%s&per_page=%s" +
            "&start_date=%s&end_date=%s&time_unit=daily&stats=frequency&stat_type=daily";

    static {
        client = HttpClients.createDefault();
    }
    private Map<String,String> cookieMapGlobal = new HashMap<>();

    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public boolean isLoginSuccess() {
        try {
            HttpResponse httpResponse = getLoginResponse();
            return  dealWithLoginHttpResponse(httpResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public HttpResponse getLoginResponse() {
        Map<String, String> map = new HashMap<>();
        setUserAgentMap(map);

        HttpResponse httpResponse = get(LOGINPAGE_URL, null, map, null, PARAMTER_ENCODING);
        Map<String,String> cookieMap =  getCookie(httpResponse);
        System.out.println("cookieMap : " + JsonUtils.obj2JsonStr(cookieMap));
        //添加所有的cookies；
        cookieMapGlobal.putAll(cookieMap);
        String token = getLoginPageToken(httpResponse);
        //header参数
        Map<String,String> loginHeaderMap = new HashMap<>();
        setUserAgentMap(loginHeaderMap);
        setLoginHeaderMap(loginHeaderMap);
        //登录路径参数
        Map<String,String > paramMap = new HashMap<>();
        setLoginParam(paramMap);
        paramMap.put("token",token);
        HttpResponse result = post(LOGIN_URL,paramMap,loginHeaderMap,convertCookies(cookieMap),PARAMTER_ENCODING);
//        logger.info("HttpResponse reslut : {}",JsonUtils.obj2JsonStr(result));
        return result;
    }

    @Override
    public ActityUserSummaryEntity getAndroidReport() {
        String string = getReport(String.format(UMENG_REPORT_URL,UMENG_ANDROID_ID),String.format(UMENG_ACTIVE_USER_URL,UMENG_ANDROID_ID));
        return JsonUtils.jsonStr2Obj(string,ActityUserSummaryEntity.class);
    }

    @Override
    public ActityUserSummaryEntity getIosReport() {
        String string = getReport(String.format(UMENG_REPORT_URL,UMENG_IOS_ID),String.format(UMENG_ACTIVE_USER_URL,UMENG_IOS_ID));
        return JsonUtils.jsonStr2Obj(string,ActityUserSummaryEntity.class);
    }

    @Override
    public StatDailyUseLengthTimeEntity getAndroidStatDailyUserLengthTimeReport() {
        Date date = new Date();
        String startDateKey = simpleDateFormat.format(date);
        String endDateKey = simpleDateFormat.format(date);
        logger.info("startDateKey {}  endDateKey {}",startDateKey,endDateKey);
        String string = getReport(String.format(UMENG_REPORT_URL,UMENG_ANDROID_ID),String.format(UMENG_DURATION_URL,UMENG_ANDROID_ID,1,30,startDateKey,endDateKey));
        return JsonUtils.jsonStr2Obj(string,StatDailyUseLengthTimeEntity.class);
    }

    @Override
    public StatDailyUseLengthTimeEntity getIosStatDailyUserLengthTimeReport() {
        Date date = new Date();
        String startDateKey = simpleDateFormat.format(date);
        String endDateKey = simpleDateFormat.format(date);
        String string = getReport(String.format(UMENG_REPORT_URL,UMENG_IOS_ID),String.format(UMENG_DURATION_URL,UMENG_IOS_ID,1,30,startDateKey,endDateKey));
        return JsonUtils.jsonStr2Obj(string,StatDailyUseLengthTimeEntity.class);
    }

    public void setUserAgentMap(Map<String, String> headerMap) {
        headerMap.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36");
    }

    public void setLoginHeaderMap(Map<String,String> headerMap){
        headerMap.put("x-requested-with","XMLHttpRequest");
        headerMap.put("origin","https://i.umeng.com");
        headerMap.put("Referer","https://i.umeng.com/");
        headerMap.put("content-type","application/x-www-form-urlencoded");

    }

    public boolean dealWithLoginHttpResponse(HttpResponse httpResponse){
        String loginResult = getResult(httpResponse);
        logger.info("loginResult {}",loginResult);
        Map<?,?> map  = JsonUtils.jsonStr2Map(loginResult);

        if(!map.containsKey("ret")){
            return false;
        }
        Integer retCode = (Integer) ( map.get("ret"));
        if(retCode != 200){
            return false;
        }
        Map<String,String> loginMap = getCookie(httpResponse);
        cookieMapGlobal.putAll(loginMap);
        return  true;
    }

    private HttpResponse get(String url, Map<String, String> paramMap, Map<String, String> headerMap, String cookie, String encoding) {
        if (paramMap != null && !paramMap.isEmpty()) {
            List<NameValuePair> params = new ArrayList<>();
            Set<Map.Entry<String, String>> set = paramMap.entrySet();
            set.forEach(s -> {
                params.add(new BasicNameValuePair(s.getKey(), paramMap.get(s.getKey())));
            });
            String queryString = URLEncodedUtils.format(params, PARAMTER_ENCODING);
            System.out.println("params :" + queryString);
            if (url.indexOf("?") > -1) {
                url += "&" + queryString;
            } else {
                url += "?" + queryString;
            }
        }
        HttpGet httpGet = new HttpGet(url);
        System.out.println("cookies : " + cookie);
        if (cookie != null) {
            httpGet.addHeader("Cookie", cookie);
        }
        //设置header
        if (headerMap != null && !headerMap.isEmpty()) {
            Set<String> set = headerMap.keySet();
            set.forEach(s -> {
                httpGet.addHeader(s, headerMap.get(s));
            });
        }
        HttpResponse closeableHttpResponse = null;
        try {
            closeableHttpResponse = client.execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return closeableHttpResponse;
    }

    private HttpResponse post(String url, Map<String, String> paramMap, Map<String, String> headerMap, String cookie, String encoding){
        List<NameValuePair> list = new ArrayList<>(); ;
        if(paramMap != null && !paramMap.isEmpty()){
            Set<Map.Entry<String,String>> set = paramMap.entrySet();
            set.forEach(s->{
                list.add(new BasicNameValuePair(s.getKey(),s.getValue()));
            });
        }
        HttpPost httpPost = new HttpPost(url);
        if(cookie != null) {
            httpPost.addHeader("Cookie", cookie);
        }
        if(headerMap != null && !headerMap.isEmpty()){
            Set<String> set = headerMap.keySet();
            set.forEach(s->{
                httpPost.addHeader(s,headerMap.get(s));
            });
        }

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(list,encoding));
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpResponse httpResponse = null;

        try {
            httpResponse = client.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return httpResponse;
    }

    public Map<String, String> getCookie(HttpResponse response) {
        Map<String, String> cookieMap = new HashMap<>();
        Header[] headers = response.getHeaders("Set-Cookie");
        for (Header header : headers) {
            System.out.println("cookie :" + header);
            System.out.println(header.getName() + " ," + header.getValue());
            String cookieValue = header.getValue();
            String[] pair = cookieValue.split(";");
            System.out.println(Arrays.asList(pair).toString());
            String[] elements = pair[0].split("=");
            if(elements!= null && elements.length == 2){
                cookieMap.put(elements[0],elements[1]);
            }
        }
        return cookieMap;
    }

    public String getLoginPageToken(HttpResponse response){
        String result = null;
        if(response == null){
            return null;
        }
        HttpEntity entity = response.getEntity();
        if(entity != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
            try {
                result = EntityUtils.toString(entity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            return  null;
        }
        String tokenPattern = "token:";
        logger.info("getLoginPageToken result {}",result);
        int indexOfToken = result.indexOf(tokenPattern);
        String rawToken = result.substring(indexOfToken,indexOfToken + 50);
        System.out.println(rawToken);
        String token = rawToken.split("'")[1];
        System.out.println(token);
        return token;
    }

    public String getReportPageToken(HttpResponse httpResponse){
        String result = null;
        HttpEntity httpEntity = httpResponse.getEntity();
        if(httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
            return result;
        }else {
            try {
                result = EntityUtils.toString(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String pattern = "csrf-token";
        int indexOfToken = result.indexOf(pattern);
        String rawToken = result.substring(indexOfToken,indexOfToken + 50);
        String token = rawToken.split("\"")[2];
        logger.info("getReportPageToken token {}",token);
        return token;
    }

    public void setLoginParam(Map<String,String> paramMap){
        paramMap.put("username","xxxx");//此处输入账号密码
        paramMap.put("password","xxxxxxx");
        paramMap.put("sig","");
        paramMap.put("sessionid","");
        paramMap.put("website","umengplus");
        paramMap.put("app_id","");
        paramMap.put("url","");
    }

    private String convertCookies(Map<String,String> cookieMap){
        if(cookieMap == null || cookieMap.isEmpty()){
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        Set<String> set = cookieMap.keySet();
        set.forEach(s->{
            stringBuilder.append(s + "=" + cookieMap.get(s) + ";");
        });
        return stringBuilder.toString();
    }

    @Scheduled(cron = "30 * * * * ?")
    public void testScheduled(){
        System.out.println("test of scheduled");
    }

    public void setReportHeaderMap(String url,String token,Map<String,String> reportHeaderMap){
        reportHeaderMap.put("X-CSRF-Token",token);
        reportHeaderMap.put("Referer",url);
        reportHeaderMap.put("X-Requested-With","XMLHttpRequest");
        reportHeaderMap.put("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36");
        reportHeaderMap.put("Accept","*/*");
        reportHeaderMap.put("Accept-Encoding","gzip, deflate");
    }

    public String getResult(HttpResponse httpResponse){
        String result = null;
        StatusLine statusLine = httpResponse.getStatusLine();
        if(statusLine.getStatusCode() == HttpStatus.SC_OK){
            try {
                result = EntityUtils.toString(httpResponse.getEntity());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    public String getReport(String reportUrl,String activeUserUrl){
        String resultStr = null;
        Map<String,String> headerMap = new HashMap<>();
        headerMap.put("Referer","http://mobile.umeng.com/apps/authorised_apps");
        String cookies = convertCookies(cookieMapGlobal);
        HttpResponse httpResponse = get(reportUrl,null,headerMap,cookies,PARAMTER_ENCODING);
        StatusLine statusLine = httpResponse.getStatusLine();
        if(statusLine.getStatusCode() != HttpStatus.SC_OK){
            return resultStr;
        }
        String token = getReportPageToken(httpResponse);
        Map<String,String> reportHeaderMap = new HashMap<>();
        setReportHeaderMap(reportUrl,token,reportHeaderMap);
        Map<String,String> cookieReportMap = new HashMap<>();
        cookieReportMap = getCookie(httpResponse);
        cookieMapGlobal.putAll(cookieReportMap);
        HttpResponse httpResponseReport = get(activeUserUrl,null,reportHeaderMap,convertCookies(cookieMapGlobal),PARAMTER_ENCODING);
        if(httpResponseReport.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
            return resultStr;
        }
        resultStr = getResult(httpResponseReport);
        return resultStr;
    }



}
