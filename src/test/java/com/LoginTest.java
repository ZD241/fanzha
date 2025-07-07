package com;

import com.controller.UsersController;
import com.entity.UsersEntity;
import com.service.UsersService;
import com.service.TokenService;
import com.utils.R;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(UsersController.class)
public class LoginTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsersService usersService;

    @MockBean
    private TokenService tokenService;

    // 正常流程测试：正确的用户名和密码登录
    @Test
    public void testLoginSuccess() throws Exception {
        String username = "testUser";
        String password = "testPassword";
        String captcha = "1234";

        UsersEntity user = new UsersEntity();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole("user");
        user.setId(1);

        Mockito.when(usersService.selectOne(Mockito.any())).thenReturn(user);
        Mockito.when(tokenService.generateToken(Mockito.anyInt(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn("testToken");

        mockMvc.perform(MockMvcRequestBuilders.post("/users/login")
                        .param("username", username)
                        .param("password", password)
                        .param("captcha", captcha)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value("testToken"));
    }

    // 异常输入处理：错误的用户名或密码登录
    @Test
    public void testLoginFailure() throws Exception {
        String username = "testUser";
        String password = "wrongPassword";
        String captcha = "1234";

        Mockito.when(usersService.selectOne(Mockito.any())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/users/login")
                        .param("username", username)
                        .param("password", password)
                        .param("captcha", captcha)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("账号或密码不正确"));
    }

    // 边界值测试：空用户名登录
    @Test
    public void testLoginWithEmptyUsername() throws Exception {
        String username = "";
        String password = "testPassword";
        String captcha = "1234";

        Mockito.when(usersService.selectOne(Mockito.any())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/users/login")
                        .param("username", username)
                        .param("password", password)
                        .param("captcha", captcha)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("账号或密码不正确"));
    }
}