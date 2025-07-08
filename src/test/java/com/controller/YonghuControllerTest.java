package com.controller;

import com.entity.YonghuEntity;
import com.entity.view.YonghuView;
import com.service.DictionaryService;
import com.service.TokenService;
import com.service.YonghuService;
import com.utils.R;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.BeanUtils;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class YonghuControllerTest {

    @Mock
    private YonghuService yonghuService;

    @Mock
    private TokenService tokenService;

    @Mock
    private DictionaryService dictionaryService;

    @InjectMocks
    private YonghuController yonghuController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 正常流程测试 - 后端列表
    @Test
    void testPage_Success() {
        Map<String, Object> params = new HashMap<>();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute("role", "管理员");

        when(yonghuService.queryPage(any())).thenReturn(null);

        R result = yonghuController.page(params, request);

        assertNotNull(result);
        assertEquals(200, result.getCode());
    }

    // 异常处理 - 后端列表 - 权限为空
    @Test
    void testPage_PermissionEmpty() {
        Map<String, Object> params = new HashMap<>();
        MockHttpServletRequest request = new MockHttpServletRequest();

        R result = yonghuController.page(params, request);

        assertNotNull(result);
        assertEquals(511, result.getCode());
        assertEquals("权限为空", result.getMsg());
    }

    // 正常流程测试 - 后端列表 - 用户角色
    @Test
    void testPage_UserRole() {
        Map<String, Object> params = new HashMap<>();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute("role", "用户");
        request.getSession().setAttribute("userId", 1);

        when(yonghuService.queryPage(any())).thenReturn(null);

        R result = yonghuController.page(params, request);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals(1, params.get("yonghuId"));
    }

    // 正常流程测试 - 后端详情
    @Test
    void testInfo_Success() {
        Long id = 1L;
        YonghuEntity yonghu = new YonghuEntity();
        yonghu.setId(Math.toIntExact(id));

        when(yonghuService.selectById(id)).thenReturn(yonghu);

        MockHttpServletRequest request = new MockHttpServletRequest();
        R result = yonghuController.info(id, request);

        assertNotNull(result);
        assertEquals(200, result.getCode());

        // 将 YonghuEntity 转换为 YonghuView
        YonghuView expectedView = new YonghuView();
        expectedView.setId(Math.toIntExact(id));

        // 验证 dictionaryService 的调用
        verify(dictionaryService, times(1)).dictionaryConvert(any(YonghuView.class), any(MockHttpServletRequest.class));

        assertEquals(expectedView.getId(), ((YonghuView) result.get("data")).getId());
    }

    // 异常处理 - 后端详情 - 查不到数据
    @Test
    void testInfo_DataNotFound() {
        Long id = 1L;
        MockHttpServletRequest request = new MockHttpServletRequest();

        when(yonghuService.selectById(id)).thenReturn(null);

        R result = yonghuController.info(id, request);

        assertNotNull(result);
        assertEquals(511, result.getCode());
        assertEquals("查不到数据", result.getMsg());
    }

    // 正常流程测试 - 后端保存
    @Test
    void testSave_Success() {
        YonghuEntity yonghu = new YonghuEntity();
        yonghu.setUsername("newUser");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute("role", "管理员");

        when(yonghuService.selectOne(any())).thenReturn(null);

        R result = yonghuController.save(yonghu, request);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(yonghuService, times(1)).insert(yonghu);
    }

    // 异常处理 - 后端保存 - 账户或手机号或身份证号已被使用
    @Test
    void testSave_UserExists() {
        YonghuEntity yonghu = new YonghuEntity();
        yonghu.setUsername("existingUser");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute("role", "管理员");

        YonghuEntity existingUser = new YonghuEntity();
        existingUser.setUsername("existingUser");

        when(yonghuService.selectOne(any())).thenReturn(existingUser);

        R result = yonghuController.save(yonghu, request);

        assertNotNull(result);
        assertEquals(511, result.getCode());
        assertEquals("账户或者手机号或者身份证号已经被使用", result.getMsg());
    }

    // 异常处理 - 后端保存 - 权限为空
    @Test
    void testSave_PermissionEmpty() {
        YonghuEntity yonghu = new YonghuEntity();
        yonghu.setUsername("newUser");
        MockHttpServletRequest request = new MockHttpServletRequest();

        R result = yonghuController.save(yonghu, request);

        assertNotNull(result);
        assertEquals(511, result.getCode());
        assertEquals("权限为空", result.getMsg());
    }

    // 正常流程测试 - 后端修改
    @Test
    void testUpdate_Success() {
        YonghuEntity yonghu = new YonghuEntity();
        yonghu.setId(1);
        yonghu.setUsername("updatedUser");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute("role", "管理员");

        when(yonghuService.selectOne(any())).thenReturn(null);

        R result = yonghuController.update(yonghu, request);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(yonghuService, times(1)).updateById(yonghu);
    }

    // 异常处理 - 后端修改 - 账户或手机号或身份证号已被使用
    @Test
    void testUpdate_UserExists() {
        YonghuEntity yonghu = new YonghuEntity();
        yonghu.setId(1);
        yonghu.setUsername("existingUser");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute("role", "管理员");

        YonghuEntity existingUser = new YonghuEntity();
        existingUser.setUsername("existingUser");

        when(yonghuService.selectOne(any())).thenReturn(existingUser);

        R result = yonghuController.update(yonghu, request);

        assertNotNull(result);
        assertEquals(511, result.getCode());
        assertEquals("账户或者手机号或者身份证号已经被使用", result.getMsg());
    }

    // 正常流程测试 - 删除
    @Test
    void testDelete_Success() {
        Integer[] ids = {1, 2};

        R result = yonghuController.delete(ids);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(yonghuService, times(1)).updateBatchById(anyList());
    }

    // 异常处理测试 - 数据库连接失败
    @Test
    void testSave_DatabaseError() {
        YonghuEntity yonghu = new YonghuEntity();
        yonghu.setUsername("newUser");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute("role", "管理员");

        // 模拟数据库异常
        when(yonghuService.selectOne(any())).thenThrow(new RuntimeException("Database connection failed"));

        R result = yonghuController.save(yonghu, request);

        assertNotNull(result);
        assertEquals(500, result.getCode()); // 假设500是错误码
        // 可以根据实际情况修改错误信息验证
    }

    // 正常流程测试 - 批量上传 - 成功


    // 异常处理 - 批量上传 - 文件无后缀
    @Test
    void testBatchInsert_NoSuffix() {
        String fileName = "test";
        MockHttpServletRequest request = new MockHttpServletRequest();

        R result = yonghuController.save(fileName);

        assertNotNull(result);
        assertEquals(511, result.getCode());
        assertEquals("该文件没有后缀", result.getMsg());
    }

    // 异常处理 - 批量上传 - 非xls文件
    @Test
    void testBatchInsert_NotXls() {
        String fileName = "test.txt";
        MockHttpServletRequest request = new MockHttpServletRequest();

        R result = yonghuController.save(fileName);

        assertNotNull(result);
        assertEquals(511, result.getCode());
        assertEquals("只支持后缀为xls的excel文件", result.getMsg());
    }

    // 异常处理 - 批量上传 - 文件不存在
    @Test
    void testBatchInsert_FileNotFound() {
        String fileName = "nonexistent.xls";
        MockHttpServletRequest request = new MockHttpServletRequest();

        R result = yonghuController.save(fileName);

        assertNotNull(result);
        assertEquals(511, result.getCode());
        assertEquals("找不到上传文件，请联系管理员", result.getMsg());
    }


    // 正常流程测试 - 后端修改 - 照片为空处理
    @Test
    void testUpdate_PhotoEmpty() {
        YonghuEntity yonghu = new YonghuEntity();
        yonghu.setId(1);
        yonghu.setUsername("updatedUser");
        yonghu.setYonghuPhoto("");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute("role", "管理员");

        when(yonghuService.selectOne(any())).thenReturn(null);

        R result = yonghuController.update(yonghu, request);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(yonghuService, times(1)).updateById(yonghu);
        assertNull(yonghu.getYonghuPhoto());
    }
}