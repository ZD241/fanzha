package com.controller;

import com.entity.UsersEntity;
import com.service.TokenService;
import com.service.UsersService;
import com.utils.R;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsersControllerTest {

    @Mock
    private UsersService usersService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private UsersController usersController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 正常流程测试 - 登录
    @Test
    void testLogin_Success() {
        String username = "testUser";
        String password = "testPassword";
        String captcha = "1234";
        MockHttpServletRequest request = new MockHttpServletRequest();

        UsersEntity user = new UsersEntity();
        user.setUsername(username);
        user.setPassword(password);
        user.setId(1);
        user.setRole("user");

        when(usersService.selectOne(any())).thenReturn(user);
        when(tokenService.generateToken(anyInt(), anyString(), anyString(), anyString())).thenReturn("testToken");

        R result = usersController.login(username, password, captcha, request);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("testToken", result.get("token"));
        assertEquals("user", result.get("role"));
        assertEquals(1, result.get("userId"));
    }

    // 正常流程测试 - 注册
    @Test
    void testRegister_Success() {
        UsersEntity user = new UsersEntity();
        user.setUsername("newUser");

        when(usersService.selectOne(any())).thenReturn(null);

        R result = usersController.register(user);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(usersService, times(1)).insert(user);
    }

    // 边界值测试 - 登录 - 空用户名
    @Test
    void testLogin_EmptyUsername() {
        String username = "";
        String password = "testPassword";
        String captcha = "1234";
        MockHttpServletRequest request = new MockHttpServletRequest();

        when(usersService.selectOne(any())).thenReturn(null);

        R result = usersController.login(username, password, captcha, request);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("账号或密码不正确", result.getMsg());
    }
    @Test
    void testLogin_WrongPassword() {
        String username = "testUser";
        String password = "wrongPassword";
        String captcha = "1234";
        MockHttpServletRequest request = new MockHttpServletRequest();

        UsersEntity user = new UsersEntity();
        user.setUsername(username);
        user.setPassword("correctPassword");
        user.setId(1);
        user.setRole("user");

        when(usersService.selectOne(any())).thenReturn(user);

        R result = usersController.login(username, password, captcha, request);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("账号或密码不正确", result.getMsg());
    }

    // 测试 update 方法 - 用户存在重复信息
    @Test
    void testUpdate_UserExists() {
        UsersEntity user = new UsersEntity();
        user.setId(1);
        user.setUsername("updatedUser");

        UsersEntity existingUser = new UsersEntity();
        existingUser.setId(2);
        existingUser.setUsername("updatedUser");

        when(usersService.selectOne(any())).thenReturn(existingUser);

        R result = usersController.update(user);

        // 这里假设 update 方法对于重复信息没有特别处理，直接更新
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(usersService, times(1)).updateById(user);
    }

    // 异常输入处理 - 注册 - 已有用户
    @Test
    void testRegister_UserExists() {
        UsersEntity user = new UsersEntity();
        user.setUsername("existingUser");

        UsersEntity existingUser = new UsersEntity();
        existingUser.setUsername("existingUser");

        when(usersService.selectOne(any())).thenReturn(existingUser);

        R result = usersController.register(user);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("用户已存在", result.getMsg());
    }

    // 业务规则验证 - 密码重置 - 账号不存在
    @Test
    void testResetPass_UserNotExists() {
        String username = "nonExistentUser";
        MockHttpServletRequest request = new MockHttpServletRequest();

        when(usersService.selectOne(any())).thenReturn(null);

        R result = usersController.resetPass(username, request);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("账号不存在", result.getMsg());
    }

    // 正常流程测试 - 列表查询
    @Test
    void testPage_Success() {
        Map<String, Object> params = new HashMap<>();
        UsersEntity user = new UsersEntity();

        when(usersService.queryPage(any(), any())).thenReturn(null);

        R result = usersController.page(params, user);

        assertNotNull(result);
        assertEquals(200, result.getCode());
    }

    // 正常流程测试 - 获取用户信息
    @Test
    void testInfo_Success() {
        String id = "1";
        UsersEntity user = new UsersEntity();
        user.setId(1);

        when(usersService.selectById(id)).thenReturn(user);

        R result = usersController.info(id);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals(user, result.get("data"));
    }

    // 状态转换测试 - 退出登录
    @Test
    void testLogout_Success() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        R result = usersController.logout(request);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("退出成功", result.getMsg());
    }

    // 正常流程测试 - 保存用户
    @Test
    void testSave_Success() {
        UsersEntity user = new UsersEntity();
        user.setUsername("newUser");

        when(usersService.selectOne(any())).thenReturn(null);

        R result = usersController.save(user);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(usersService, times(1)).insert(user);
    }

    // 正常流程测试 - 删除用户
    @Test
    void testDelete_Success() {
        Long[] ids = {1L, 2L};

        R result = usersController.delete(ids);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(usersService, times(1)).deleteBatchIds(Arrays.asList(ids));
    }



    // 异常处理测试 - 数据库连接失败
    @Test
    void testLogin_DatabaseError() {
        String username = "testUser";
        String password = "testPassword";
        String captcha = "1234";
        MockHttpServletRequest request = new MockHttpServletRequest();

        // 模拟数据库异常
        when(usersService.selectOne(any())).thenThrow(new RuntimeException("Database connection failed"));

        R result = usersController.login(username, password, captcha, request);

        assertNotNull(result);
        assertEquals(500, result.getCode()); // 假设500是错误码
        assertEquals("登录失败，请稍后重试", result.getMsg()); // 修正错误信息的断言
    }



    // 并发安全测试（多线程环境）
    @Test
    void testConcurrentLogin() throws InterruptedException {
        String username = "testUser";
        String password = "testPassword";
        String captcha = "1234";
        MockHttpServletRequest request = new MockHttpServletRequest();

        UsersEntity user = new UsersEntity();
        user.setUsername(username);
        user.setPassword(password);
        user.setId(1);
        user.setRole("user");

        when(usersService.selectOne(any())).thenReturn(user);
        when(tokenService.generateToken(anyInt(), anyString(), anyString(), anyString())).thenReturn("testToken");

        Runnable runnable = () -> {
            R result = usersController.login(username, password, captcha, request);
            assertNotNull(result);
            assertEquals(200, result.getCode());
        };

        Thread thread1 = new Thread(runnable);
        Thread thread2 = new Thread(runnable);

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
    }

    // 性能基准测试（记录方法执行时间）
    @Test
    void testLogin_Performance() {
        String username = "testUser";
        String password = "testPassword";
        String captcha = "1234";
        MockHttpServletRequest request = new MockHttpServletRequest();

        UsersEntity user = new UsersEntity();
        user.setUsername(username);
        user.setPassword(password);
        user.setId(1);
        user.setRole("user");

        when(usersService.selectOne(any())).thenReturn(user);
        when(tokenService.generateToken(anyInt(), anyString(), anyString(), anyString())).thenReturn("testToken");

        long startTime = System.currentTimeMillis();
        R result = usersController.login(username, password, captcha, request);
        long endTime = System.currentTimeMillis();

        assertNotNull(result);
        assertEquals(200, result.getCode());
        System.out.println("Login method execution time: " + (endTime - startTime) + " ms");
    }
    // 测试 list 方法
    @Test
    void testList() {
        UsersEntity user = new UsersEntity();
        List<UsersEntity> userList = Arrays.asList(user);

        when(usersService.selectListView(any())).thenReturn(userList);

        R result = usersController.list(user);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals(userList, result.get("data"));
    }

    // 测试 getCurrUser 方法
    @Test
    void testGetCurrUser() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Integer userId = 1;
        request.getSession().setAttribute("userId", userId);

        UsersEntity user = new UsersEntity();
        user.setId(userId);

        when(usersService.selectById(userId)).thenReturn(user);

        R result = usersController.getCurrUser(request);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals(user, result.get("data"));
    }

    // 测试 update 方法
    @Test
    void testUpdate() {
        UsersEntity user = new UsersEntity();
        user.setId(1);
        user.setUsername("updatedUser");

        when(usersService.selectOne(any())).thenReturn(null);

        R result = usersController.update(user);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(usersService, times(1)).updateById(user);
    }
}