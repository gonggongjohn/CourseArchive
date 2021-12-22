package gld.bookstore.controller;

import gld.bookstore.controller.dto.*;
import gld.bookstore.controller.vo.Message;
import gld.bookstore.controller.vo.TokenMessage;
import gld.bookstore.controller.vo.UserMessage;
import gld.bookstore.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class UserController {
    UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Message> register(@RequestBody RegisterBody registerBody){
        return userService.register(registerBody);
    }

    @PostMapping("/unregister")
    public ResponseEntity<Message> unregister(@RequestBody RegisterBody registerBody){
        return userService.unregister(registerBody);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenMessage> login(@RequestBody LoginBody loginBody){
        return userService.login(loginBody);
    }

    @PostMapping("/password")
    public ResponseEntity<Message> password(@RequestBody PasswordBody passwordBody){
        return userService.password(passwordBody);
    }

    @PostMapping("/logout")
    public ResponseEntity<Message> logout(@RequestBody LogoutBody logoutBody){
        return userService.logout(logoutBody);
    }

    @GetMapping("/{name}")
    public ResponseEntity<UserMessage>getInfo(@PathVariable String name){
        return userService.getInfo(name);
    }

    @PatchMapping
    public ResponseEntity<Message>update(@RequestBody UpdateUserBody updateUser){
        return userService.update(updateUser);
    }
}
