package gld.bookstore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import gld.bookstore.controller.dto.*;
import gld.bookstore.controller.vo.Message;
import gld.bookstore.controller.vo.TokenMessage;
import gld.bookstore.controller.vo.UserMessage;
import gld.bookstore.entity.User;
import gld.bookstore.mapper.UserMapper;
import gld.bookstore.service.UserService;
import gld.bookstore.utils.IHttpStatus;
import gld.bookstore.utils.JWTUtils;
import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.*;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    UserMapper userMapper;
    RedisTemplate redisTemplate;

    @Override
    public ResponseEntity<Message> register(RegisterBody registerBody) {
        User user=new User();
        user.setName(registerBody.getName());
        user.setNickname(registerBody.getName());
        user.setPassword(registerBody.getPassword());
        try{
            userMapper.insert(user);
        }catch (DuplicateKeyException e){
            return new ResponseEntity<>(
                    new Message("注册失败，用户名已存在"),
                    null, IHttpStatus.EXISTED_USER
            );
        }
        redisTemplate.boundSetOps("user").add(user.getName());
        return new ResponseEntity<>(
                new Message("ok"),
                HttpStatus.OK
        );
    }

    @Override
    public ResponseEntity<Message> unregister(RegisterBody registerBody) {
        LambdaQueryWrapper<User> query=new LambdaQueryWrapper<>();
        query.eq(User::getName,registerBody.getName());
        query.eq(User::getPassword,registerBody.getPassword());
        if(userMapper.delete(query)>0){
            redisTemplate.boundSetOps("user").remove(registerBody.getName());
            return new ResponseEntity<>(
                    new Message("ok"),
                    HttpStatus.OK
            );
        }else {
            return new ResponseEntity<>(
                    new Message("注销失败，用户名不存在或密码错误"),
                    HttpStatus.UNAUTHORIZED
            );
        }
    }

    @Override
    public ResponseEntity<TokenMessage> login(LoginBody loginBody) {
        LambdaQueryWrapper<User> query=new LambdaQueryWrapper<>();
        query.eq(User::getName,loginBody.getName());
        query.eq(User::getPassword,loginBody.getPassword());
        User user = userMapper.selectOne(query);
        if(user != null){
            String token=JWTUtils.generateToken(loginBody);
            System.out.println(token);
            String md5Password = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
            redisTemplate.boundHashOps("user_" + loginBody.getName()).put("password", md5Password);
            redisTemplate.boundHashOps("user_" + loginBody.getName()).put("money", user.getMoney());
            return new ResponseEntity<>(
                    new TokenMessage("ok",token),
                    HttpStatus.OK
            );
        }else {
            return new ResponseEntity<>(
                    new TokenMessage("登陆失败，用户名不存在或密码错误",null),
                    HttpStatus.UNAUTHORIZED
            );
        }
    }

    @Override
    public ResponseEntity<Message> password(PasswordBody passwordBody) {
        User user=new User();
        user.setName(passwordBody.getName());
        user.setPassword(passwordBody.getOldPassword());
        LambdaUpdateWrapper<User> update=new LambdaUpdateWrapper<>();
        update.eq(User::getName,passwordBody.getName());
        update.eq(User::getPassword,passwordBody.getOldPassword());
        update.set(User::getPassword,passwordBody.getNewPassword());
        if(userMapper.update(null,update)>0){
            String md5Password = DigestUtils.md5DigestAsHex(passwordBody.getNewPassword().getBytes());
            redisTemplate.boundHashOps("user_" + passwordBody.getName()).put("password", md5Password);
            return new ResponseEntity<>(
                    new Message("ok"),
                    HttpStatus.OK
            );
        }else {
            return new ResponseEntity<>(
                    new Message("更改密码失败，用户名不存在或密码错误"),
                    HttpStatus.UNAUTHORIZED
            );
        }
    }

    @Override
    public ResponseEntity<Message> logout(LogoutBody logoutBody) {
        LambdaQueryWrapper<User> query=new LambdaQueryWrapper<>();
        query.eq(User::getName,logoutBody.getName());
        if(userMapper.selectOne(query)!=null){
            return new ResponseEntity<>(
                    new Message("ok"),
                    HttpStatus.OK
            );
        }else {
            return new ResponseEntity<>(
                    new Message("登出失败，用户名不存在"),
                    HttpStatus.UNAUTHORIZED
            );
        }
    }

    @Override
    public Set<String> listUsername() {
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        query.select(User::getName);
        List<User> userList = userMapper.selectList(query);
        Set<String> nameSet = new HashSet<>();
        for(User user : userList){
            nameSet.add(user.getName());
        }
        return nameSet;
    }

    @Override
    public ResponseEntity<UserMessage> getInfo(String name) {
        User user=userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getName,name)
        );
        if(user==null){
            return new ResponseEntity<>(
                    new UserMessage("获取失败，用户ID不存在",null),
                    null,IHttpStatus.NON_EXIST_USER
            );
        }
        user.setPassword("");
        return new ResponseEntity<>(
                new UserMessage("ok",user),
                HttpStatus.OK
        );
    }

    @Override
    public ResponseEntity<Message> update(UpdateUserBody updateUser) {
        User user=userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getName,updateUser.getName())
        );
        if(user==null){
            return new ResponseEntity<>(
                    new Message("更新失败，用户ID不存在"),
                    null,IHttpStatus.NON_EXIST_USER
            );
        }
        LambdaUpdateWrapper<User>update=new LambdaUpdateWrapper<User>()
                .eq(User::getName,updateUser.getName());
        if(updateUser.getNickname()!=null && !updateUser.getNickname().equals("")){
            update.set(User::getNickname,updateUser.getNickname());
        }
        if(updateUser.getPhone()!=null && !updateUser.getPhone().equals("")){
            update.set(User::getPhone,updateUser.getPhone());
        }
        if(updateUser.getAddress()!=null && !updateUser.getAddress().equals("")){
            update.set(User::getAddress,updateUser.getAddress());
        }
        userMapper.update(null,update);
        return new ResponseEntity<>(
                new Message("ok"),
                HttpStatus.OK
        );
    }
}
