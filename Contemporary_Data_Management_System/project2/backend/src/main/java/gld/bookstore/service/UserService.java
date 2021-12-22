package gld.bookstore.service;

import gld.bookstore.controller.dto.*;
import gld.bookstore.controller.vo.Message;
import gld.bookstore.controller.vo.TokenMessage;
import gld.bookstore.controller.vo.UserMessage;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

public interface UserService{
    ResponseEntity<Message> register(RegisterBody registerBody);

    ResponseEntity<Message> unregister(RegisterBody registerBody);

    ResponseEntity<TokenMessage> login(LoginBody loginBody);

    ResponseEntity<Message> password(PasswordBody passwordBody);

    ResponseEntity<Message> logout(LogoutBody logoutBody);

    Set<String> listUsername();

    ResponseEntity<UserMessage> getInfo(String name);

    ResponseEntity<Message> update(UpdateUserBody updateUser);
}
