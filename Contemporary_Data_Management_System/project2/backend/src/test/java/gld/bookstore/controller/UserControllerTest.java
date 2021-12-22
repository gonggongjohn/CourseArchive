package gld.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gld.bookstore.controller.dto.*;
import gld.bookstore.controller.vo.Message;
import gld.bookstore.controller.vo.TokenMessage;
import gld.bookstore.controller.vo.UserMessage;
import gld.bookstore.service.UserService;
import gld.bookstore.utils.IHttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    String baseUrl="/auth";
    int ok=200;
    int unauthorized=401;

    UserService userService;
    IMockMvc iMockMvc;

    public @Autowired UserControllerTest(
            MockMvc mockMvc,
            UserService userService,
            ObjectMapper objectMapper
    ) {
        this.userService = userService;
        this.iMockMvc = new IMockMvc(mockMvc,objectMapper);
    }

    String getStringWithUUID(String s){
        return s+"_"+ UUID.randomUUID();
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestRegister{
        String name="test_register_user";
        String password="test_register_password";

        @Test
        void test_register_ok() throws Exception {
            // given
            RegisterBody registerBody=new RegisterBody(
                    getStringWithUUID(name),
                    getStringWithUUID(password)
            );
            // when/then
            iMockMvc.testPost(baseUrl+"/register",registerBody,ok, Message.class);
        }

        @Test
        void test_unregister_ok() throws Exception {
            // given
            RegisterBody registerBody=new RegisterBody(
                    getStringWithUUID(name),
                    getStringWithUUID(password)
            );
            // when/then
            // 注册
            iMockMvc.testPost(baseUrl+"/register",registerBody,ok,Message.class);
            // 注销
            iMockMvc.testPost(baseUrl+"/unregister",registerBody,ok,Message.class);
        }

        @Test
        void test_unregister_error_authorization() throws Exception {
            // given
            RegisterBody registerBody=new RegisterBody(
                    getStringWithUUID(name),
                    getStringWithUUID(password)
            );
            // when/then
            // 注册
            iMockMvc.testPost(baseUrl+"/register",registerBody,ok,Message.class);
            // 注销_用户名不存在
            RegisterBody registerBody1=new RegisterBody(
                    registerBody.getName()+"_x",
                    registerBody.getPassword()
            );
            iMockMvc.testPost(baseUrl+"/unregister",registerBody1,unauthorized,Message.class);
            // 注销_密码错误
            RegisterBody registerBody2=new RegisterBody(
                    registerBody.getName(),
                    registerBody.getPassword()+"_x"
            );
            iMockMvc.testPost(baseUrl+"/unregister",registerBody2,unauthorized,Message.class);
        }

        @Test
        void test_register_error_exist_user_id() throws Exception {
            // given
            RegisterBody registerBody=new RegisterBody(
                    getStringWithUUID(name),
                    getStringWithUUID(password)
            );
            // when/then
            // 注册
            iMockMvc.testPost(baseUrl+"/register",registerBody,ok,Message.class);
            // 再次注册
            iMockMvc.testPost(baseUrl+"/register",registerBody,IHttpStatus.EXISTED_USER,Message.class);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestLogin{
        String name;
        String password;
        String terminal;

        // 注册
        @BeforeEach
        void pre_run_initialization() throws Exception {
            // given
            name=getStringWithUUID("test_login");
            password="password_"+name;
            terminal="terminal_"+name;
            RegisterBody registerBody=new RegisterBody(name, password);
            // when/then
            iMockMvc.testPost(baseUrl+"/register",registerBody,ok,Message.class);
        }

        @Test
        void test_ok() throws Exception {
            // given
            LoginBody loginBody=new LoginBody(name,password,terminal);
            // when/then
            // 登录
            TokenMessage tokenMessage=iMockMvc.testPost(baseUrl+"/login",loginBody,ok,TokenMessage.class);
            String token=tokenMessage.getToken();
            // 登出_用户名不存在
            LogoutBody logoutBody=new LogoutBody(name+"_x");
            iMockMvc.testPost(baseUrl+"/logout",logoutBody,unauthorized,Message.class,token);
            // 登出_token错误
            logoutBody.setName(name);
            iMockMvc.testPost(baseUrl+"/logout",logoutBody,unauthorized,Message.class,token+"_x");
            // 登出
            iMockMvc.testPost(baseUrl+"/logout",logoutBody,ok,Message.class,token);
        }

        @Test
        void test_error_user_id() throws Exception {
            // given
            LoginBody loginBody=new LoginBody(name+"_x",password,terminal);
            // when/then
            iMockMvc.testPost(baseUrl+"/login",loginBody,unauthorized,TokenMessage.class);
        }

        @Test
        void test_error_password() throws Exception {
            // given
            LoginBody loginBody=new LoginBody(name, password+"_x", terminal);
            // when/then
            iMockMvc.testPost(baseUrl+"/login",loginBody,unauthorized,TokenMessage.class);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestPassword{
        String name;
        String oldPassword;
        String newPassword;
        String terminal;

        @BeforeEach
        void pre_run_initialization() throws Exception {
            // given
            name=getStringWithUUID("test_password");
            oldPassword="old_password_"+name;
            newPassword="new_password_"+name;
            terminal="terminal_"+name;
            RegisterBody registerBody=new RegisterBody(name, oldPassword);
            // when/then
            iMockMvc.testPost(baseUrl+"/register",registerBody,ok,Message.class);
        }

        @Test
        void test_ok() throws Exception {
            // given
            PasswordBody passwordBody=new PasswordBody(name,oldPassword,newPassword);
            // when/then
            iMockMvc.testPost(baseUrl+"/password",passwordBody,ok,Message.class);
            // 旧密码登录
            LoginBody loginBody=new LoginBody(name,oldPassword,terminal);
            iMockMvc.testPost(baseUrl+"/login",loginBody,unauthorized,TokenMessage.class);
            // 新密码登录
            loginBody.setPassword(newPassword);
            TokenMessage tokenMessage=iMockMvc.testPost(baseUrl+"/login",loginBody,ok,TokenMessage.class);
            String token=tokenMessage.getToken();
            // 登出
            LogoutBody logoutBody=new LogoutBody(name);
            iMockMvc.testPost(baseUrl+"/logout",logoutBody,ok,Message.class,token);
        }

        @Test
        void test_error_password() throws Exception {
            // given
            PasswordBody passwordBody=new PasswordBody(name,oldPassword+"_x",newPassword);
            // when/then
            iMockMvc.testPost(baseUrl+"/password",passwordBody,unauthorized,Message.class);
            // 新密码登录
            LoginBody loginBody=new LoginBody(name,newPassword,terminal);
            iMockMvc.testPost(baseUrl+"/login",loginBody,unauthorized,TokenMessage.class);
        }

        @Test
        void test_error_user_id() throws Exception {
            // given
            PasswordBody passwordBody=new PasswordBody(name+"_x",oldPassword,newPassword);
            // when/then
            iMockMvc.testPost(baseUrl+"/password",passwordBody,unauthorized,Message.class);
            // 新密码登录
            LoginBody loginBody=new LoginBody(name,newPassword,terminal);
            iMockMvc.testPost(baseUrl+"/login",loginBody,unauthorized,TokenMessage.class);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestGetInfo{
        String name;
        String password;
        String token;

        @BeforeEach
        void pre_run_initialization() throws Exception {
            name=getStringWithUUID("test_get_info");
            password=name;
            // 注册
            RegisterBody registerBody=new RegisterBody(name, password);
            iMockMvc.testPost(baseUrl+"/register",registerBody,ok,Message.class);
            // 获取token
            LoginBody loginBody=new LoginBody(name,password,"");
            TokenMessage response=iMockMvc.testPost(baseUrl+"/login",loginBody,ok,TokenMessage.class);
            token=response.getToken();
        }

        @Test
        void test_ok() throws Exception {
            iMockMvc.testGet(baseUrl+"/"+name,ok, UserMessage.class,token);
        }

        @Test
        void test_non_exit_user() throws Exception {
            iMockMvc.testGet(baseUrl+"/"+name+"_x",IHttpStatus.NON_EXIST_USER, UserMessage.class,token);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestUpdate{
        String name;
        String password;
        String token;

        @BeforeEach
        void pre_run_initialization() throws Exception {
            name=getStringWithUUID("test_update");
            password=name;
            // 注册
            RegisterBody registerBody=new RegisterBody(name, password);
            iMockMvc.testPost(baseUrl+"/register",registerBody,ok,Message.class);
            // 获取token
            LoginBody loginBody=new LoginBody(name,password,"");
            TokenMessage response=iMockMvc.testPost(baseUrl+"/login",loginBody,ok,TokenMessage.class);
            token=response.getToken();
        }

        @Test
        void test_ok() throws Exception {
            UpdateUserBody updateUserBody=new UpdateUserBody(name,"nickname","111","address");
            iMockMvc.testPatch(baseUrl,updateUserBody,ok, UserMessage.class,token);
        }

        @Test
        void test_non_exit_user() throws Exception {
            UpdateUserBody updateUserBody=new UpdateUserBody(name+"_x","nickname","111","address");
            iMockMvc.testPatch(baseUrl,updateUserBody,IHttpStatus.NON_EXIST_USER, UserMessage.class,token);
        }
    }
}