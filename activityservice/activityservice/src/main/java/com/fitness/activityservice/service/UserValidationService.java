package com.fitness.activityservice.service;


import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

@Service
public class UserValidationService {
    private final WebClient userServiceWebClient;

    public UserValidationService(WebClient userServiceWebClient){
        this.userServiceWebClient = userServiceWebClient;
    }

    public boolean validateUser (String userId){
        try{
            return Boolean.TRUE.equals(userServiceWebClient.get()
                    .uri("/api/users/{userId}/validate", userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block());
        }
        catch (WebClientException e){
//            e.printStackTrace();
            System.out.println(e.getMessage());

        }

        return false;

    }
}
