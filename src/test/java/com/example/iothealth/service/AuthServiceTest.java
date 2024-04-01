package com.example.iothealth.service;

import com.example.iothealth.model.Organisation;
import com.example.iothealth.model.User;
import com.example.iothealth.payload.response.AuthResponse;
import com.example.iothealth.repository.UserRepository;
import com.example.iothealth.security.services.UserDetailsImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private WebClient webClient;

    @InjectMocks
    private AuthService authService;

    @Before
    public void setUp() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");
    }


    @Test
    public void testGetCurrentAuthenticatedUser() {
        // Create a mock UserDetailsImpl
        Organisation organisation = new Organisation();
        UserDetailsImpl userDetails = new UserDetailsImpl(UUID.randomUUID(), "username", "test@example.com", "password", organisation, new ArrayList<>());

        // Create a mock Authentication
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);

        // Set the Authentication in the SecurityContext
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);

        // Mock the UserRepository response
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));

        // Perform the test
        User found = authService.getCurrentAuthenticatedUser();

        // Assertions
        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    public void testLoginWithThingsBoard() {
        // Mock WebClient response
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/api/auth/login")).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.contentType(any())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.header(any(), any())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Map.class)).thenReturn(Mono.just(Map.of("token", "token", "refreshToken", "refreshToken")));

        // Perform the test
        AuthResponse response = authService.loginWithThingsBoard("username", "password");

        // Assertions
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("token");
        assertThat(response.getRefreshToken()).isEqualTo("refreshToken");
    }
}