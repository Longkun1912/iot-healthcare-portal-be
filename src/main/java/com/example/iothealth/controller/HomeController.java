package com.example.iothealth.controller;

import com.example.iothealth.model.User;
import com.example.iothealth.payload.request.SignupRequest;
import com.example.iothealth.repository.UserRepository;
import com.example.iothealth.security.services.CloudinaryService;
import com.example.iothealth.security.services.UserDetailsImpl;
import com.example.iothealth.payload.response.UserInfoResponse;
import com.example.iothealth.payload.request.LoginRequest;
import com.example.iothealth.security.jwt.JwtUtils;
import com.example.iothealth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class HomeController {
    private final CloudinaryService cloudinaryService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        System.out.println("Current cookie login: " + jwtCookie);

        System.out.println("Account signed in: " + SecurityContextHolder.getContext().getAuthentication());

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        User currentUser = userRepository.findById(userDetails.getId()).get();

        Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Current authentication: " + currentAuthentication.getName());

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(new UserInfoResponse(userDetails.getId(),
                        userDetails.getUsername(),
                        currentUser.getAvatar(),
                        currentUser.getMobile(),
                        currentUser.getPassword(),
                        currentUser.getGender(),
                        userDetails.getEmail(),roles,
                        userDetails.getOrganisation().getName(), jwtCookie.getValue()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {

        System.out.println("Account signed out: " + SecurityContextHolder.getContext().getAuthentication());

        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        System.out.println("Current cookie logout: " + cookie);

        SecurityContextHolder.getContext().setAuthentication(null);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("You've been signed out");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(SignupRequest signupRequest){
        if (userRepository.findUserByEmail(signupRequest.getEmail()).isPresent()){
            return ResponseEntity.badRequest().body("Email already exists.");
        }
        else if (userRepository.findUserByUsername(signupRequest.getUsername()).isPresent()){
            return ResponseEntity.badRequest().body("Username already exists.");
        }
        else if (userRepository.findUserByMobile(signupRequest.getMobile()).isPresent()){
            return ResponseEntity.badRequest().body("Mobile already exists.");
        }
        else {
            try {
                String avatarName = cloudinaryService.uploadUserAvatar(signupRequest.getEmail(), signupRequest.getAvatar());
                return ResponseEntity.ok(userService.saveNewRegisteredUser(signupRequest, avatarName));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Error: " + e.getMessage());
            }
        }
    }
}
