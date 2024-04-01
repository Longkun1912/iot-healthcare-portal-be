package com.example.iothealth.controller;

import com.example.iothealth.model.User;
import com.example.iothealth.service.AuthService;
import com.example.iothealth.service.HealthProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/health_progress")
public class HealthProgressController {
    private final AuthService authService;
    private final HealthProgressService healthProgressService;

    @GetMapping("/")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<?> viewScheduledHealthProgressByUser(@RequestParam(value = "dateTime", required = false) String dateTime){
        User currentUser = authService.getCurrentAuthenticatedUser();
        try{
            return ResponseEntity.ok(healthProgressService.estimateUserHealthProgress(currentUser, dateTime));
        } catch (Exception e){
            return ResponseEntity.badRequest().body("Oops! The server encountered an error. Please try again later");
        }
    }
}
