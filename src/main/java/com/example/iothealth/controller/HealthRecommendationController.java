package com.example.iothealth.controller;

import com.example.iothealth.domain.HealthRecommendationDetails;
import com.example.iothealth.model.HealthRecommendation;
import com.example.iothealth.payload.request.AddHealthRecommendationRequest;
import com.example.iothealth.payload.request.UpdateHealthRecommendationRequest;
import com.example.iothealth.repository.HealthRecommendationRepository;
import com.example.iothealth.service.HealthRecommendationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/health_recommendation-management")
public class HealthRecommendationController {
    private final HealthRecommendationService healthRecommendationService;
    private final HealthRecommendationRepository healthRecommendationRepository;

    @GetMapping("/health_recommendations")
    public ResponseEntity<?> getAllHealthRecommendations(){
        List<HealthRecommendationDetails> healthRecommendationsList = healthRecommendationService.getAllHealthRecommendations();
        if(healthRecommendationsList.isEmpty()){
            return ResponseEntity.badRequest().body("No health recommendation was found currently.");
        }
        return ResponseEntity.ok().body(healthRecommendationsList);
    }
    @PostMapping("/health_recommendation")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> addHealthRecommendation(@Valid @RequestBody AddHealthRecommendationRequest addHealthRecommendationRequest){
        System.out.println("Data updated: " + addHealthRecommendationRequest);
        HealthRecommendation healthRecommendation = healthRecommendationService.addHealthRecommendation(addHealthRecommendationRequest);
        return ResponseEntity.ok().body(healthRecommendation);
    }

    @PutMapping("/health_recommendation")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> updateHealthRecommendation(@Valid @RequestBody UpdateHealthRecommendationRequest updateHealthRecommendationRequest){
        HealthRecommendation healthRecommendation = healthRecommendationService.updateHealthRecommendation(updateHealthRecommendationRequest);
        return ResponseEntity.ok().body(healthRecommendation);
    }

    @PreAuthorize("hasAuthority('admin')")
    @DeleteMapping("/health_recommendation")
    @Transactional
    public ResponseEntity<?> deleteHealthRecommendation(@RequestParam("health_recommendation_id") String idRequest){
        Integer healthRecommendationID = Integer.parseInt(idRequest);
        healthRecommendationRepository.deleteById(healthRecommendationID);
        return ResponseEntity.ok().body("Health Recommendation with ID: " + healthRecommendationID + " was deleted successfully.");
    }
}
