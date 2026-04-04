package com.fitness.userservice.service;

import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.entity.User;
import com.fitness.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository repository;
    public UserService(UserRepository userRepository){
        this.repository = userRepository;
    }
    public UserResponse register(RegisterRequest request) {
        if(repository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email already exist");

        }
        User user = User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();

        User saveUser = repository.save(user);
        return mapToResponse(saveUser);


    }

    private UserResponse mapToResponse(User saveUser) {
        UserResponse response = new UserResponse();
        response.setId(saveUser.getId());
        response.setEmail(saveUser.getEmail());
        response.setPassword(saveUser.getPassword());
        response.setFirstName(saveUser.getFirstName());
        response.setLastName(saveUser.getLastName());
        response.setCreatedAt(saveUser.getCreatedAt());
        response.setUpdatedAt(saveUser.getUpdatedAt());

        return response;
    }

    public UserResponse getUserProfile(String userId) {
        User user = repository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User not found "));

        return mapToResponse(user);
    }
}
