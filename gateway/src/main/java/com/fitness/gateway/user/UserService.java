package com.fitness.gateway.user;



import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class UserService {
    private final WebClient userServiceWebClient;

    public UserService(WebClient userServiceWebClient){
        this.userServiceWebClient = userServiceWebClient;
    }

    public Mono<Boolean> validateUser(String userId) {

        log.info("Calling user service to validate {}", userId);

        return userServiceWebClient.get()
                .uri("/api/users/{userId}/validate", userId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume(e -> {
                    log.error("User validation failed {}", e.getMessage());
                    return Mono.just(false);
                });
    }


    public Mono<UserResponse> registerUser(RegisterRequest registerRequest){
       log.info("Calling user Registration for {}", registerRequest.getEmail());
        return userServiceWebClient.post()
                .uri("/api/users/register")
                .bodyValue(registerRequest)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .onErrorResume(WebClientException.class, e -> {
                    System.out.println("Error validating user: " + e.getMessage());
                    return Mono.error(new RuntimeException("Unexpected error:" + e.getMessage()));
                });

    }
}
