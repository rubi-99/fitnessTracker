package com.fitness.userservice.controller;


import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")

public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register( @Valid @RequestBody RegisterRequest request){
        UserResponse response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable String userId){
        UserResponse user = userService.getUserProfile(userId);
        return  ResponseEntity.ok(user);

    }

    @GetMapping("/{userId}/validate")
    public ResponseEntity<Boolean> ValidateUser(@PathVariable String userId){
        return ResponseEntity.ok(userService.existByUserId(userId));

    }
}
