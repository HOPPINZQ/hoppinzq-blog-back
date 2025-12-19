package com.blog.analytics.controller;

import com.blog.analytics.dto.HoppinResponse;
import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ProxyController接口测试类
 * 测试/api/proxy/news/hot-list接口
 *
 * @author hoppinzq
 * @since 2025-12-18
 */
class ProxyControllerTest {

    @Mock
    private OkHttpClient okHttpClient;

    @Mock
    private Call mockCall;

    @InjectMocks
    private ProxyController proxyController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(proxyController).build();
    }

    /**
     * 测试无参数请求/api/proxy/news/hot-list接口
     * 预期结果：返回成功响应，包含新闻热搜数据
     * @throws Exception
     */
    @Test
    void testGetNewsHotListWithoutParams() throws Exception {
        // 模拟响应数据
        String mockNewsData = "{\"code\":200,\"message\":\"success\",\"data\":[{\"title\":\"测试新闻1\",\"url\":\"http://test.com/1\"}]}";

        // 构建真实的Response对象
        Response response = new Response.Builder()
                .request(new Request.Builder().url("https://soso-b-api.cqttech.com/api/v1/hot_list").build())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create(mockNewsData.getBytes(), okhttp3.MediaType.parse("application/json")))
                .build();

        // 模拟OkHttpClient行为
        when(okHttpClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(response);

        // 执行测试
        mockMvc.perform(get("/api/proxy/news/hot-list")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    /**
     * 测试带参数请求/api/proxy/news/hot-list接口
     * 预期结果：返回成功响应，包含新闻热搜数据
     * @throws Exception
     */
    @Test
    void testGetNewsHotListWithParams() throws Exception {
        // 模拟响应数据
        String mockNewsData = "{\"code\":200,\"message\":\"success\",\"data\":[{\"title\":\"测试新闻2\",\"url\":\"http://test.com/2\"}]}";

        // 构建真实的Response对象
        Response response = new Response.Builder()
                .request(new Request.Builder().url("https://soso-b-api.cqttech.com/api/v1/hot_list?date=2025-12-18&limit=10").build())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create(mockNewsData.getBytes(), okhttp3.MediaType.parse("application/json")))
                .build();

        // 模拟OkHttpClient行为
        when(okHttpClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(response);

        // 执行测试（带参数）
        mockMvc.perform(get("/api/proxy/news/hot-list")
                .param("date", "2025-12-18")
                .param("limit", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    /**
     * 测试外部API调用失败的情况
     * 预期结果：返回失败响应，包含错误信息
     * @throws Exception
     */
    @Test
    void testGetNewsHotListExternalApiFailure() throws Exception {
        // 构建失败的Response对象
        Response response = new Response.Builder()
                .request(new Request.Builder().url("https://soso-b-api.cqttech.com/api/v1/hot_list").build())
                .protocol(Protocol.HTTP_1_1)
                .code(500)
                .message("External API Error")
                .body(ResponseBody.create("{}".getBytes(), okhttp3.MediaType.parse("application/json")))
                .build();

        // 模拟OkHttpClient行为
        when(okHttpClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(response);

        // 执行测试
        mockMvc.perform(get("/api/proxy/news/hot-list")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").isString());
    }

    /**
     * 测试网络异常情况
     * 预期结果：返回失败响应，包含网络异常信息
     * @throws Exception
     */
    @Test
    void testGetNewsHotListNetworkException() throws Exception {
        // 模拟OkHttpClient行为 - 网络异常
        when(okHttpClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenThrow(new IOException("Network Error"));

        // 执行测试
        mockMvc.perform(get("/api/proxy/news/hot-list")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").isString());
    }
}
