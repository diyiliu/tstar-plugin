package com.tiza.plugin.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Description: HttpUtil
 * Author: DIYILIU
 * Update: 2018-12-21 09:38
 */

@Slf4j
public class HttpUtil {

    public static String postWithJsonAndParameter(String url, String json, Map param) throws Exception {
        log.info("POST: {}, Body: {}, 参数: {}", url, json, JacksonUtil.toJson(param));

        URIBuilder builder = new URIBuilder(url);
        builder.addParameters(buildParameter(param));
        HttpPost httpPost = new HttpPost(builder.build());

        StringEntity entity = new StringEntity(json, "UTF-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);

        return askFor(httpPost);
    }

    public static String postWithJson(String url, String json) throws Exception {
        log.info("POST: {}, 参数: {}", url, json);

        HttpPost httpPost = new HttpPost(url);

        StringEntity entity = new StringEntity(json, "UTF-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);

        return askFor(httpPost);
    }

    /**
     * GET 请求
     *
     * @param url
     * @param param
     * @return
     * @throws Exception
     */
    public static String getForString(String url, Map param) throws Exception {
        log.info("GET: {}, 参数: {}", url, JacksonUtil.toJson(param));

        HttpGet httpGet;
        if (MapUtils.isEmpty(param)) {
            httpGet = new HttpGet(url);
        } else {
            URIBuilder builder = new URIBuilder(url);
            builder.addParameters(buildParameter(param));

            httpGet = new HttpGet(builder.build());
        }

        return askFor(httpGet);
    }

    /**
     * POST 请求
     *
     * @param url
     * @param param
     * @return
     * @throws Exception
     */
    public static String postForString(String url, Map param) throws Exception {
        log.info("POST: {}, 参数: {}", url, JacksonUtil.toJson(param));

        HttpPost httpPost = new HttpPost(url);
        if (MapUtils.isNotEmpty(param)) {
            httpPost.setEntity(new UrlEncodedFormEntity(buildParameter(param), "UTF-8"));
        }

        return askFor(httpPost);
    }

    private static String askFor(HttpRequestBase httpRequest) throws Exception {
        RequestConfig config = RequestConfig.custom()
                // 连接时间
                .setConnectTimeout(10 * 1000)
                // 请求超时
                .setConnectionRequestTimeout(30 * 1000)
                // 数据传输时间
                .setSocketTimeout(20 * 1000).build();

        HttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
        HttpResponse response = client.execute(httpRequest);
        HttpEntity entity = response.getEntity();

        return EntityUtils.toString(entity, "UTF-8");
    }

    private static List<NameValuePair> buildParameter(Map param) {
        List<NameValuePair> nameValuePairs = new ArrayList();
        for (Iterator iterator = param.keySet().iterator(); iterator.hasNext(); ) {
            String key = (String) iterator.next();
            String value = String.valueOf(param.get(key));

            nameValuePairs.add(new BasicNameValuePair(key, value));
        }

        return nameValuePairs;
    }
}
