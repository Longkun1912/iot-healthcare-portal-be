package com.example.iothealth.service;

import com.example.iothealth.model.User;
import com.example.iothealth.payload.response.AuthResponse;
import com.example.iothealth.repository.UserRepository;
import com.example.iothealth.security.system.KeyGenerator;
import com.example.iothealth.security.services.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final WebClient webClient;
    private final UserRepository userRepository;

    public User getCurrentAuthenticatedUser() {
        Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) currentAuthentication.getPrincipal();
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new UsernameNotFoundException("This user is not authenticated."));
    }

    public AuthResponse loginWithThingsBoard(String username, String password) {
        // Retrieve the stored key instead of generating a new one each time
        SecretKey key = KeyGenerator.generateKey();
        String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());

        Mono<Map> result = webClient.post()
                .uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + encodedKey)
                .bodyValue(Map.of("username", username, "password", password))
                .retrieve()
                .bodyToMono(Map.class);

        // Extract the tokens from the response
        Map<String, Object> response = result.blockOptional()
                .orElseThrow(() -> new RuntimeException("Failed to retrieve login response"));

        String accessToken = (String) response.get("token");
        String refreshToken = (String) response.get("refreshToken");

        return new AuthResponse(accessToken, refreshToken);
    }
}
