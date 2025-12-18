package com.blog.analytics.controller;

import com.blog.analytics.dto.HoppinResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

/**
 * 第三方API代理控制器
 *
 * @author hoppinzq
 * @since 2025-12-17
 */
@RestController
@RequestMapping("/api/proxy")
@RequiredArgsConstructor
@Slf4j
public class ProxyController {

    private final OkHttpClient okHttpClient;

    /**
     * 获取新闻热搜代理接口
     */
    @GetMapping("/news/hot-list")
    public HoppinResponse<Object> getNewsHotList(@RequestParam(required = false) Map<String, String> params) {
        try {
            // 构建请求URL
            StringBuilder urlBuilder = new StringBuilder("https://soso-b-api.cqttech.com/api/v1/hot_list");
            if (params != null && !params.isEmpty()) {
                urlBuilder.append("?");
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    urlBuilder.append(entry.getKey()).append("=")
                            .append(java.net.URLEncoder.encode(entry.getValue(), "UTF-8")).append("&");
                }
                urlBuilder.deleteCharAt(urlBuilder.length() - 1);
            }

            // 创建请求
            Request request = new Request.Builder()
                    .url(urlBuilder.toString())
                    .addHeader("X-ZQ-Ignore", "1")
                    .build();

            // 发送请求
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                // 直接返回响应内容
                Object result = com.alibaba.fastjson.JSON.parse(responseBody);
                return HoppinResponse.success(result);
            } else {
                return HoppinResponse.fail("获取新闻热搜失败: " + response.message());
            }
        } catch (IOException e) {
            log.error("获取新闻热搜失败", e);
            return HoppinResponse.fail("获取新闻热搜失败: " + e.getMessage());
        }
    }

    /**
     * 获取天气代理接口
     */
    @GetMapping("/weather")
    public HoppinResponse<Object> getWeather(@RequestParam(required = false) Map<String, String> params) {
        try {
            // 构建请求URL
            StringBuilder urlBuilder = new StringBuilder("https://userweatherapi-newtabpro.newtabpro.cn/api/GetAssistData");
            if (params != null && !params.isEmpty()) {
                urlBuilder.append("?");
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    urlBuilder.append(entry.getKey()).append("=")
                            .append(java.net.URLEncoder.encode(entry.getValue(), "UTF-8")).append("&");
                }
                urlBuilder.deleteCharAt(urlBuilder.length() - 1);
            }

            // 创建请求
            Request request = new Request.Builder()
                    .url(urlBuilder.toString())
                    .addHeader("X-ZQ-Ignore", "1")
                    .build();

            // 发送请求
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                // 直接返回响应内容
                Object result = com.alibaba.fastjson.JSON.parse(responseBody);
                return HoppinResponse.success(result);
            } else {
                return HoppinResponse.fail("获取天气失败: " + response.message());
            }
        } catch (IOException e) {
            log.error("获取天气失败", e);
            return HoppinResponse.fail("获取天气失败: " + e.getMessage());
        }
    }

    /**
     * 获取星座运势代理接口
     */
    @GetMapping("/horoscope")
    public HoppinResponse<Object> getHoroscope(@RequestParam(required = false) Map<String, String> params) {
        try {
            // 构建请求URL
            StringBuilder urlBuilder = new StringBuilder("https://userapi-newtabpro.newtabpro.cn/getxzinfo");
            if (params != null && !params.isEmpty()) {
                urlBuilder.append("?");
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    urlBuilder.append(entry.getKey()).append("=")
                            .append(java.net.URLEncoder.encode(entry.getValue(), "UTF-8")).append("&");
                }
                urlBuilder.deleteCharAt(urlBuilder.length() - 1);
            }

            // 创建请求
            Request request = new Request.Builder()
                    .url(urlBuilder.toString())
                    .addHeader("X-ZQ-Ignore", "1")
                    .build();

            // 发送请求
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                // 直接返回响应内容
                Object result = com.alibaba.fastjson.JSON.parse(responseBody);
                return HoppinResponse.success(result);
            } else {
                return HoppinResponse.fail("获取星座运势失败: " + response.message());
            }
        } catch (IOException e) {
            log.error("获取星座运势失败", e);
            return HoppinResponse.fail("获取星座运势失败: " + e.getMessage());
        }
    }

    /**
     * 获取大模型排名代理接口
     */
    @GetMapping("/hellogithub/lm-rank")
    public HoppinResponse<Object> getLmRank(@RequestParam(required = false) Map<String, String> params) {
        try {
            // 构建请求URL
            StringBuilder urlBuilder = new StringBuilder("https://hellogithub.com/_next/data/JdGrOpd-mbD4CLgVF9Yfl/zh/report/lm-rank.json");
            if (params != null && !params.isEmpty()) {
                urlBuilder.append("?");
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    urlBuilder.append(entry.getKey()).append("=")
                            .append(java.net.URLEncoder.encode(entry.getValue(), "UTF-8")).append("&");
                }
                urlBuilder.deleteCharAt(urlBuilder.length() - 1);
            }

            // 创建请求
            Request request = new Request.Builder()
                    .url(urlBuilder.toString())
                    .addHeader("X-ZQ-Ignore", "1")
                    .build();

            // 发送请求
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                // 直接返回响应内容
                Object result = com.alibaba.fastjson.JSON.parse(responseBody);
                return HoppinResponse.success(result);
            } else {
                return HoppinResponse.fail("获取大模型排名失败: " + response.message());
            }
        } catch (IOException e) {
            log.error("获取大模型排名失败", e);
            return HoppinResponse.fail("获取大模型排名失败: " + e.getMessage());
        }
    }

    /**
     * 获取编程排名代理接口
     */
    @GetMapping("/hellogithub/tiobe")
    public HoppinResponse<Object> getTiobeRank(@RequestParam(required = false) Map<String, String> params) {
        try {
            // 构建请求URL
            StringBuilder urlBuilder = new StringBuilder("https://hellogithub.com/_next/data/JdGrOpd-mbD4CLgVF9Yfl/zh/report/tiobe.json");
            if (params != null && !params.isEmpty()) {
                urlBuilder.append("?");
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    urlBuilder.append(entry.getKey()).append("=")
                            .append(java.net.URLEncoder.encode(entry.getValue(), "UTF-8")).append("&");
                }
                urlBuilder.deleteCharAt(urlBuilder.length() - 1);
            }

            // 创建请求
            Request request = new Request.Builder()
                    .url(urlBuilder.toString())
                    .addHeader("X-ZQ-Ignore", "1")
                    .build();

            // 发送请求
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                // 直接返回响应内容
                Object result = com.alibaba.fastjson.JSON.parse(responseBody);
                return HoppinResponse.success(result);
            } else {
                return HoppinResponse.fail("获取编程排名失败: " + response.message());
            }
        } catch (IOException e) {
            log.error("获取编程排名失败", e);
            return HoppinResponse.fail("获取编程排名失败: " + e.getMessage());
        }
    }
}
