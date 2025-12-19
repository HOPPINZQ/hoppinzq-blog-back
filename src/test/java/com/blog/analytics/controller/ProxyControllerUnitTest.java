package com.blog.analytics.controller;

import com.blog.analytics.dto.HoppinResponse;
import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * ProxyController单元测试类
 * 直接测试方法逻辑，不通过HTTP接口
 *
 * @author hoppinzq
 * @since 2025-12-18
 */
class ProxyControllerUnitTest {

    @Mock
    private OkHttpClient okHttpClient;

    @Mock
    private Call mockCall;

    @InjectMocks
    private ProxyController proxyController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * 测试getNewsHotList方法 - 无参数
     * 预期结果：返回成功的HoppinResponse，包含新闻数据
     * @throws Exception
     */
    @Test
    void testGetNewsHotListMethodWithoutParams() throws Exception {
        // 模拟响应数据
        String mockNewsData = "{\"code\":200,\"message\":\"success\",\"data\":[{\"title\":\"测试新闻1\",\"url\":\"http://test.com/1\"}]}";

        // 构建真实的Response对象
        Response mockResponse = new Response.Builder()
                .request(new Request.Builder().url("https://soso-b-api.cqttech.com/api/v1/hot_list").build())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create(mockNewsData.getBytes(), okhttp3.MediaType.parse("application/json")))
                .build();

        // 模拟OkHttpClient行为
        when(okHttpClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);

        // 执行方法调用
        HoppinResponse<Object> response = proxyController.getNewsHotList(null);

        // 验证结果
        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertEquals(200, response.getCode());
        assertNotNull(response.getData());
    }

    /**
     * 测试getNewsHotList方法 - 带参数
     * 预期结果：返回成功的HoppinResponse，包含新闻数据
     * @throws Exception
     */
    @Test
    void testGetNewsHotListMethodWithParams() throws Exception {
        // 模拟响应数据
        String mockNewsData = "{\"code\":200,\"message\":\"success\",\"data\":[{\"title\":\"测试新闻2\",\"url\":\"http://test.com/2\"}]}";

        // 准备请求参数
        Map<String, String> params = new HashMap<>();
        params.put("date", "2025-12-18");
        params.put("type", "hot");

        // 构建真实的Response对象
        Response mockResponse = new Response.Builder()
                .request(new Request.Builder().url("https://soso-b-api.cqttech.com/api/v1/hot_list?date=2025-12-18&type=hot").build())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create(mockNewsData.getBytes(), okhttp3.MediaType.parse("application/json")))
                .build();

        // 模拟OkHttpClient行为
        when(okHttpClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);

        // 执行方法调用
        HoppinResponse<Object> response = proxyController.getNewsHotList(params);

        // 验证结果
        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertEquals(200, response.getCode());
        assertNotNull(response.getData());
    }

    /**
     * 测试getNewsHotList方法 - 外部API失败
     * 预期结果：返回失败的HoppinResponse
     * @throws Exception
     */
    @Test
    void testGetNewsHotListMethodExternalApiFailure() throws Exception {
        // 构建失败的Response对象
        Response mockResponse = new Response.Builder()
                .request(new Request.Builder().url("https://soso-b-api.cqttech.com/api/v1/hot_list").build())
                .protocol(Protocol.HTTP_1_1)
                .code(500)
                .message("API Error")
                .body(ResponseBody.create("{}".getBytes(), okhttp3.MediaType.parse("application/json")))
                .build();

        // 模拟OkHttpClient行为
        when(okHttpClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);

        // 执行方法调用
        HoppinResponse<Object> response = proxyController.getNewsHotList(null);

        // 验证结果
        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals(500, response.getCode());
        assertTrue(response.getMessage().contains("获取新闻热搜失败"));
    }

    /**
     * 测试getNewsHotList方法 - IOException异常
     * 预期结果：返回失败的HoppinResponse，包含异常信息
     * @throws Exception
     */
    @Test
    void testGetNewsHotListMethodIOException() throws Exception {
        // 模拟OkHttpClient行为 - 抛出IOException
        when(okHttpClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenThrow(new IOException("Connection timeout"));

        // 执行方法调用
        HoppinResponse<Object> response = proxyController.getNewsHotList(null);

        // 验证结果
        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals(500, response.getCode());
        assertTrue(response.getMessage().contains("获取新闻热搜失败"));
        assertTrue(response.getMessage().contains("Connection timeout"));
    }

    /**
     * 测试getNewsHotList方法 - 空响应体
     * 预期结果：返回失败的HoppinResponse
     * @throws Exception
     */
    @Test
    void testGetNewsHotListMethodEmptyResponseBody() throws Exception {
        // 构建成功但响应体为空的Response对象
        Response mockResponse = new Response.Builder()
                .request(new Request.Builder().url("https://soso-b-api.cqttech.com/api/v1/hot_list").build())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(null)
                .build();

        // 模拟OkHttpClient行为
        when(okHttpClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);

        // 执行方法调用
        HoppinResponse<Object> response = proxyController.getNewsHotList(null);

        // 验证结果
        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals(500, response.getCode());
        assertTrue(response.getMessage().contains("获取新闻热搜失败"));
    }
}
