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
            User existingUser = repository.findByEmail(request.getEmail());

            UserResponse response = new UserResponse();
            response.setId(existingUser.getId());
            response.setEmail(existingUser.getEmail());
            response.setPassword(existingUser.getPassword());
            response.setFirstName(existingUser.getFirstName());
            response.setLastName(existingUser.getLastName());
            response.setKeycloakId(existingUser.getKeycloakId());
            response.setCreatedAt(existingUser.getCreatedAt());
            response.setUpdatedAt(existingUser.getUpdatedAt());

            return response;


        }
        User user = User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .keycloakId(request.getKeycloakId())
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
        response.setKeycloakId(saveUser.getKeycloakId());
        response.setCreatedAt(saveUser.getCreatedAt());
        response.setUpdatedAt(saveUser.getUpdatedAt());

        return response;
    }

    public UserResponse getUserProfile(String userId) {
        User user = repository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User not found "));

        return mapToResponse(user);
    }

    public Boolean existByUserId(String userId) {
        return repository.existsByKeycloakId(userId);

    }
}
