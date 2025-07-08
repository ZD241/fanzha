package com.controller;

import com.entity.NewsEntity;
import com.entity.view.NewsView;
import com.utils.PoiUtil;
import com.service.DictionaryService;
import com.service.NewsService;
import com.service.TokenService;
import com.service.YonghuService;
import com.utils.PageUtils;
import com.utils.R;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.mock.web.MockHttpServletRequest;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.sql.DataSource;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NewsControllerTest {

    @Mock
    private DataSource dataSource;



    @Mock
    private TokenService tokenService;

    @Mock
    private DictionaryService dictionaryService;

    @Mock
    private YonghuService yonghuService;
    @Mock
    private NewsService newsService;

    @InjectMocks
    private NewsController newsController;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 测试数据库连接成功的情况
    @Test
    void testDbConnection_Success() throws SQLException {
        Connection connection = mock(Connection.class);
        when(dataSource.getConnection()).thenReturn(connection);

        R result = newsController.testDbConnection();

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("数据库连接成功", result.getMsg());
    }

    // 测试数据库连接失败的情况
    @Test
    void testDbConnection_Failure() throws SQLException {
        when(dataSource.getConnection()).thenThrow(new SQLException("Database connection failed"));

        R result = newsController.testDbConnection();

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertTrue(result.getMsg().contains("数据库连接失败"));
    }

    // 测试后端列表查询成功的情况
    // 测试后端列表查询成功的情况
    @Test
    void testPage_Success() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute("role", "用户");
        request.getSession().setAttribute("userId", 1);

        Map<String, Object> params = new HashMap<>();

        // 创建 PageUtils 模拟对象
        PageUtils page = mock(PageUtils.class);

        // 创建 NewsView 列表
        List<NewsView> newsViewList = new ArrayList<>();
        NewsView newsView = new NewsView();
        newsViewList.add(newsView);

        // 使用 Mockito 的泛型类型推断来避免类型不匹配问题
        @SuppressWarnings("unchecked")
        OngoingStubbing<List<NewsView>> stubbing = when((List<NewsView>) page.getList());
        stubbing.thenReturn(newsViewList);

        // 设置 newsService.queryPage 方法返回值
        when(newsService.queryPage(params)).thenReturn(page);

        R result = newsController.page(params, request);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(newsService, times(1)).queryPage(params);
        verify(dictionaryService, times(newsViewList.size())).dictionaryConvert(any(), eq(request));
    }


    // 测试后端详情查询成功的情况
    @Test
    void testInfo_Success() {
        Long id = 1L;
        NewsEntity news = new NewsEntity();
        when(newsService.selectById(id)).thenReturn(news);

        MockHttpServletRequest request = new MockHttpServletRequest();
        R result = newsController.info(id, request);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(newsService, times(1)).selectById(id);
        verify(dictionaryService, times(1)).dictionaryConvert(any(), eq(request));
    }

    // 测试后端详情查询失败的情况
    @Test
    void testInfo_Failure() {
        Long id = 1L;
        when(newsService.selectById(id)).thenReturn(null);

        MockHttpServletRequest request = new MockHttpServletRequest();
        R result = newsController.info(id, request);

        assertNotNull(result);
        assertEquals(511, result.getCode());
        verify(newsService, times(1)).selectById(id);
    }

    // 测试后端保存成功的情况
    @Test
    void testSave_Success() {
        NewsEntity news = new NewsEntity();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute("role", "用户");
        when(newsService.selectOne(any())).thenReturn(null);

        R result = newsController.save(news, request);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(newsService, times(1)).insert(news);
    }

    // 测试后端保存失败（表中有相同数据）的情况
    @Test
    void testSave_Failure() {
        NewsEntity news = new NewsEntity();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute("role", "用户");
        when(newsService.selectOne(any())).thenReturn(new NewsEntity());

        R result = newsController.save(news, request);

        assertNotNull(result);
        assertEquals(511, result.getCode());
        verify(newsService, never()).insert(news);
    }

    // 测试后端修改成功的情况
    @Test
    void testUpdate_Success() {
        NewsEntity news = new NewsEntity();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute("role", "用户");
        when(newsService.selectOne(any())).thenReturn(null);

        R result = newsController.update(news, request);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(newsService, times(1)).updateById(news);
    }

    // 测试后端修改失败（表中有相同数据）的情况
    @Test
    void testUpdate_Failure() {
        NewsEntity news = new NewsEntity();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute("role", "用户");
        when(newsService.selectOne(any())).thenReturn(new NewsEntity());

        R result = newsController.update(news, request);

        assertNotNull(result);
        assertEquals(511, result.getCode());
        verify(newsService, never()).updateById(news);
    }

    // 测试删除成功的情况
    @Test
    void testDelete_Success() {
        Integer[] ids = {1, 2};
        R result = newsController.delete(ids);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(newsService, times(1)).updateBatchById(anyList());
    }

    // 测试批量上传成功的情况
    @Test
    void testBatchInsert_Success() {
        String fileName = "test.xls";
        try {
            // 模拟文件存在
            URL mockUrl = new URL("file:///mock/path/" + fileName);
            File mockFile = mock(File.class);
            when(mockFile.exists()).thenReturn(true);

            // 模拟类加载器获取资源
            ClassLoader mockClassLoader = mock(ClassLoader.class);
            when(newsController.getClass().getClassLoader()).thenReturn(mockClassLoader);
            when(mockClassLoader.getResource("static/upload/" + fileName)).thenReturn(mockUrl);

            // 模拟读取 Excel 文件
            List<List<String>> mockDataList = new ArrayList<>();
            List<String> mockData = new ArrayList<>();
            mockData.add("test");
            mockDataList.add(mockData);

            // 使用 Mockito.mockStatic 模拟静态方法
            try (MockedStatic<PoiUtil> mockedPoiUtil = Mockito.mockStatic(PoiUtil.class)) {
                mockedPoiUtil.when(() -> PoiUtil.poiImport(mockFile.getPath())).thenReturn(mockDataList);

                // 模拟插入操作成功
                List<NewsEntity> newsList = new ArrayList<>();
                NewsEntity newsEntity = new NewsEntity();
                newsList.add(newsEntity);
                doNothing().when(newsService).insertBatch(newsList);

                // 调用批量插入方法
                R result = newsController.save(fileName);

                // 验证结果
                assertEquals(200, result.getCode());
                verify(newsService, times(1)).insertBatch(newsList);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }



    // 测试批量上传失败（文件后缀不正确）的情况
    @Test
    void testBatchInsert_Failure_InvalidSuffix() {
        String fileName = "test.txt";
        R result = newsController.save(fileName);

        assertNotNull(result);
        assertEquals(511, result.getCode());
        verify(newsService, never()).insertBatch(anyList());
    }
}