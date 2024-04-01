package com.example.iothealth.controller;

import com.example.iothealth.exception.HealthRecordNotFoundException;
import com.example.iothealth.model.HealthObjective;
import com.example.iothealth.model.User;
import com.example.iothealth.payload.request.AddHealthObjectiveRequest;
import com.example.iothealth.repository.HealthObjectiveRepository;
import com.example.iothealth.security.services.CloudinaryService;
import com.example.iothealth.service.AuthService;
import com.example.iothealth.service.HealthObjectiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/health_objective-management")
public class HealthObjectiveController {
    private final AuthService authService;
    private final CloudinaryService cloudinaryService;
    private final HealthObjectiveRepository healthObjectiveRepository;
    private final HealthObjectiveService healthObjectiveService;

    @GetMapping("/health_objectives")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> getAllHealthObjectives() {
        return ResponseEntity.ok(healthObjectiveService.getAllHealthObjectives());
    }

    @GetMapping("/user_health_objectives")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<?> getUserHealthObjectives() {
        return ResponseEntity.ok(healthObjectiveService.getAllHealthObjectives());
    }

    @GetMapping("/recommended_health_objectives")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<?> getRecommendedHealthObjectives() {
        try {
            User currentAuthenticatedUser = authService.getCurrentAuthenticatedUser();
            return ResponseEntity.ok(healthObjectiveService.recommendHealthObjectiveForUser(currentAuthenticatedUser));
        } catch (HealthRecordNotFoundException e) {
            return ResponseEntity.badRequest().body("Please provide any health record for recommendation.");
        }
    }

    @PutMapping("/health_objective")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<?> setUserHealthObjective(@RequestParam("health_objective_id") Integer idRequest) {
        try {
            User currentAuthenticatedUser = authService.getCurrentAuthenticatedUser();
            healthObjectiveService.setUserHealthObjective(idRequest, currentAuthenticatedUser);
            return ResponseEntity.ok("Health Objective was set successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Oop! Something went wrong, please try again later.");
        }
    }

    @PostMapping("/health_objective")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> createHealthObjective(AddHealthObjectiveRequest addHealthObjectiveRequest) {
        if (healthObjectiveRepository.findHealthObjectiveByTitle(addHealthObjectiveRequest.getTitle()).isPresent()) {
            return ResponseEntity.badRequest().body("Health Objective with this title is already taken!");
        }
        else {
            try {
                String pictureName = cloudinaryService.uploadHealthObjectivePicture(addHealthObjectiveRequest.getTitle(), addHealthObjectiveRequest.getPicture());
                HealthObjective healthObjective = healthObjectiveService.createHealthObjective(addHealthObjectiveRequest, pictureName);
                return ResponseEntity.ok(healthObjective);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Failed to create this Health Objective! Please try again.");
            }
        }
    }

    @DeleteMapping("/health_objective")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> deleteHealthObjective(@RequestParam("health_objective_id") String idRequest) {
        Integer healthObjectiveId = Integer.parseInt(idRequest);
        try {
            String pictureName = healthObjectiveRepository.findById(healthObjectiveId).get().getTitle();
            cloudinaryService.deleteHealthObjectivePicture(pictureName);
            healthObjectiveRepository.deleteById(healthObjectiveId);
            return ResponseEntity.ok().body("Health Objective was deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete this Health Objective! Please try again.");
        }
    }
}
