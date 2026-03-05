package com.utils;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 百度 AI 工具类
 */
@Slf4j
public class BaiduUtil {

    /**
     * 根据经纬度获得省市区信息
     *
     * @param lon       纬度
     * @param lat       经度
     * @param coordtype 经纬度坐标系
     * @return
     */
    public static Map<String, String> getCityByLonLat(String key, String lng, String lat) {
        String location = lat + "," + lng;
        try {
            //拼装 url
            String url = "http://api.map.baidu.com/reverse_geocoding/v3/?ak=" + key + "&output=json&coordtype=wgs84ll&location=" + location;
            String result = HttpClientUtils.doGet(url);
            JSONObject o = new JSONObject(result);
            Map<String, String> area = new HashMap<>();
            area.put("province", o.getJSONObject("result").getJSONObject("addressComponent").getString("province"));
            area.put("city", o.getJSONObject("result").getJSONObject("addressComponent").getString("city"));
            area.put("district", o.getJSONObject("result").getJSONObject("addressComponent").getString("district"));
            area.put("street", o.getJSONObject("result").getJSONObject("addressComponent").getString("street"));
            return area;
        } catch (Exception e) {
            log.error("获取地理位置信息失败！", e);
        }
        return null;
    }

    /**
     * 获取 API 访问 token
     * 该 token 有一定的有效期，需要自行管理，当失效时需重新获取.
     *
     * @param ak - 百度云官网获取的 API Key
     * @param sk - 百度云官网获取的 Securet Key
     * @return assess_token
     */
    public static String getAuth(String ak, String sk) {
        // 获取 token 地址
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                // 1. grant_type 为固定参数
                + "grant_type=client_credentials"
                // 2. 官网获取的 API Key
                + "&client_id=" + ak
                // 3. 官网获取的 Secret Key
                + "&client_secret=" + sk;
        try {
            URL realUrl = new URL(getAccessTokenUrl);
            // 打开和 URL 之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段（调试模式才打印）
            if (log.isDebugEnabled()) {
                for (String key : map.keySet()) {
                    log.debug("Header: {} -> {}", key, map.get(key));
                }
            }
            // 定义 BufferedReader 输入流来读取 URL 的响应
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            /**
             * 返回结果示例（调试模式才打印）
             */
            if (log.isDebugEnabled()) {
                log.debug("Token response: {}", result);
            }
            org.json.JSONObject jsonObject = new org.json.JSONObject(result);
            String access_token = jsonObject.getString("access_token");
            return access_token;
        } catch (Exception e) {
            log.error("获取 token 失败！", e);
        }
        return null;
    }

}
