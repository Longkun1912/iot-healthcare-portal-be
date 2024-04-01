package com.example.iothealth.service;

import com.example.iothealth.domain.HealthRecommendationDetails;
import com.example.iothealth.model.HealthRecommendation;
import com.example.iothealth.payload.request.AddHealthRecommendationRequest;
import com.example.iothealth.payload.request.UpdateHealthRecommendationRequest;
import com.example.iothealth.repository.HealthRecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HealthRecommendationService {
    private final ModelMapper mapper;
    private final HealthRecommendationRepository healthRecommendationRepository;

    public List<HealthRecommendationDetails> getAllHealthRecommendations(){
        return healthRecommendationRepository.findAll()
                .stream()
                .map(healthRecommendation -> mapper.map(healthRecommendation, HealthRecommendationDetails.class))
                .toList();
    }

    public HealthRecommendation addHealthRecommendation(AddHealthRecommendationRequest addHealthRecommendationRequest){
        HealthRecommendation healthRecommendation = mapper.map(addHealthRecommendationRequest, HealthRecommendation.class);
        return healthRecommendationRepository.save(healthRecommendation);
    }

    public HealthRecommendation updateHealthRecommendation(UpdateHealthRecommendationRequest updateHealthRecommendationRequest){
        HealthRecommendation healthRecommendation = healthRecommendationRepository
                .findById(updateHealthRecommendationRequest.getId())
                .orElseThrow(() -> new NullPointerException("Health Recommendation not found with ID: " + updateHealthRecommendationRequest.getId()));
        healthRecommendation.setName(updateHealthRecommendationRequest.getName());
        healthRecommendation.setHeart_rate_impact(updateHealthRecommendationRequest.getHeart_rate_impact());
        healthRecommendation.setBlood_pressure_impact(updateHealthRecommendationRequest.getBlood_pressure_impact());
        healthRecommendation.setTemperature_impact(updateHealthRecommendationRequest.getTemperature_impact());
        healthRecommendation.setDescription(updateHealthRecommendationRequest.getDescription());
        healthRecommendation.setGuide_link(updateHealthRecommendationRequest.getGuide_link());
        return healthRecommendationRepository.save(healthRecommendation);
    }
}
