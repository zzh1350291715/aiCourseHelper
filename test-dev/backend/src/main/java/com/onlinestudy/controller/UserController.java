package com.onlinestudy.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlinestudy.domain.User;
import com.onlinestudy.dto.UserRegistrationDto;
import com.onlinestudy.service.UserService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
@CrossOrigin
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public UserController(UserService userService , StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.userService = userService;
        this.redisTemplate =  redisTemplate;
        this.objectMapper =  objectMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<?>  registerUser(@RequestBody UserRegistrationDto userRegistrationDto){
        try{
            User newuser = userService.regiserUser(userRegistrationDto);
            return  ResponseEntity.ok("注册成功!");
        }catch(IllegalArgumentException e){
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message",e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserRegistrationDto userRegistrationDto){
        try{
            User user =userService.login(userRegistrationDto);
            String sessionId = UUID.randomUUID().toString();
            String userJson =objectMapper.writeValueAsString(user);

            redisTemplate.opsForValue().set("session"+sessionId, userJson, Duration.ofMinutes(30));

            return ResponseEntity.ok(Map.of(
                    "sessionId",sessionId,
                    "message","登录成功!",
                    "username",user.getUsername(),
                    "role",user.getRole(),
                    "userId",user.getId()
            ));
        }
        catch (IllegalArgumentException e){
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message",e.getMessage()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/getalluser")
    List<User> getAllUser(){
        try {
            return userService.getAllUser();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/deleteuserbyid")
    boolean deleteUserById(@RequestBody Map<String,Object> map){
        Long user_id = Long.valueOf(map.get("user_id").toString());
        return userService.deleteUser(user_id);
    }
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        try {
            return ResponseEntity.ok("User service is running");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Service error: " + e.getMessage());
        }
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        try {
            return ResponseEntity.ok(Map.of(
                    "message", "Backend is working",
                    "timestamp", System.currentTimeMillis(),
                    "status", "OK"
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Test failed: " + e.getMessage());
        }
    }


}
