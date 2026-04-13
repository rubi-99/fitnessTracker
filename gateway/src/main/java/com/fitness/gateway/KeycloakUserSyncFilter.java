package com.fitness.gateway;

import com.fitness.gateway.user.RegisterRequest;
import com.fitness.gateway.user.UserService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.text.ParseException;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeycloakUserSyncFilter implements WebFilter {

    private final UserService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        String userId = exchange.getRequest().getHeaders().getFirst("X-User-ID");
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");

        if(token ==  null){
            log.error("Token is not present");
            return Mono.empty();
        }

        RegisterRequest registerRequest = getUserDetails(token);

        if(userId == null && registerRequest != null){
            userId =registerRequest.getKeycloakId();
        }

        if(userId != null) {
            String finalUserId = userId;
            return userService.validateUser(userId)
                    .flatMap(exist -> {
                        if (!exist) {
                            if (registerRequest != null) {
                                return userService.registerUser(registerRequest).then();
                            }
                            return Mono.empty();
                        } log.info("user already exist skipping sync");
                        return Mono.empty();
                    })
                    .then(Mono.defer(() -> {
                        ServerHttpRequest mutateRequest = exchange.getRequest().mutate()
                                .header("X-User-ID", finalUserId)
                                .build();

                        return chain.filter(exchange.mutate().request(mutateRequest).build());
                    }));
        }
        return chain.filter(exchange);
    }

    private RegisterRequest getUserDetails(String token) {
        try{
            String tokenWithoutBearer = token.replace("Bearer ","").trim();
            SignedJWT signedJWT = SignedJWT.parse(tokenWithoutBearer);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            RegisterRequest request = new RegisterRequest();
            request.setEmail(claims.getClaimAsString("email"));
            request.setKeycloakId(claims.getClaimAsString("sub"));
            request.setPassword("dummy@123");
            request.setFirstName(claims.getClaimAsString("given_name"));
            request.setLastName(claims.getClaimAsString("family_name"));

            return request;


        } catch (ParseException e) {
            log.error("Unable to register user :: {}", e.getMessage());
            return null;
        }
    }
}
