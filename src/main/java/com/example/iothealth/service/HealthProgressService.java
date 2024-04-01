package com.example.iothealth.service;

import com.example.iothealth.domain.*;
import com.example.iothealth.model.User;
import com.example.iothealth.repository.HealthRecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HealthProgressService {
    private final ModelMapper mapper;
    private final HealthRecommendationRepository healthRecommendationRepository;
    private final HealthRecordService healthRecordService;
    private final HealthObjectiveService healthObjectiveService;

    public HealthProgress estimateUserHealthProgress(User user, String selectedDateTime) throws NullPointerException {
        HealthProgress healthProgress = new HealthProgress(user.getId());
        HealthAnalysis healthAnalysis = new HealthAnalysis();
        HealthRecordDetails currentHealth = new HealthRecordDetails();
        Set<HealthRecommendationDetails> recommendations = new HashSet<>();
        HealthObjectiveDetails targetedHealth = healthObjectiveService.viewCurrentHealthObjectiveByUser(user);
        try {
            if (selectedDateTime != null){
                if (!selectedDateTime.isEmpty()){
                    selectedDateTime = selectedDateTime.replace(" ", "+");
                    System.out.println("Selected date time: " + selectedDateTime);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
                    LocalDateTime dateTime = null;
                    dateTime = LocalDateTime.parse(selectedDateTime, formatter);
                    System.out.println("Parsed date time: " + dateTime);
                    currentHealth = healthRecordService.viewHistoricalHealth(user.getId(), dateTime);
                }
            }
            else {
                currentHealth = healthRecordService.viewCurrentHealth(user.getId());
            }
            healthProgress.setCurrentHealth(currentHealth);
            if (targetedHealth != null){
                healthProgress.setTargetedHealth(targetedHealth);

                // Analyse the health progress based on the current health and targeted health
                Set<HealthRecommendationDetails> recommendationBasedOnBloodPressureComparison = analyseBasedOnBloodPressureComparison(currentHealth.getBlood_pressure(), targetedHealth.getBlood_pressure(), healthAnalysis);
                Set<HealthRecommendationDetails> recommendationBasedOnHeartRateComparison = analyseBasedOnHeartRateComparison(currentHealth.getHeart_rate(), targetedHealth.getHeart_rate(), healthAnalysis);
                Set<HealthRecommendationDetails> recommendationBasedOnTemperatureComparison = analyseBasedOnTemperatureComparison(currentHealth.getTemperature(), targetedHealth.getTemperature(), healthAnalysis);

                // Blood pressure is the most essential factor
                recommendations.addAll(recommendationBasedOnBloodPressureComparison);

                // Blood pressure > Temperature > Heart rate
                for (HealthRecommendationDetails recommendation : recommendationBasedOnBloodPressureComparison){
                    if (recommendationBasedOnTemperatureComparison.contains(recommendation)){
                        recommendations.add(recommendation);
                    }
                }

                // Heart rate is the least important factor
                for (HealthRecommendationDetails recommendation : recommendationBasedOnTemperatureComparison){
                    if (recommendationBasedOnHeartRateComparison.contains(recommendation)){
                        recommendations.add(recommendation);
                    }
                }

                // Complete the health analysis
                healthAnalysis.setRecommendations(recommendations);
                analyseOverallHealthStatus(healthAnalysis);
                healthProgress.setHealthAnalysis(healthAnalysis);
            }
        } catch (NullPointerException e) {
            throw new NullPointerException("Current health record is not available. Please check your health record.");
        }
        return healthProgress;
    }

    private Set<HealthRecommendationDetails> analyseBasedOnBloodPressureComparison(int currentBloodPressure, int targetedBloodPressure, HealthAnalysis healthAnalysis){
        int bloodPressureImpact = calculateBloodPressureImpact(currentBloodPressure, targetedBloodPressure);
        if (bloodPressureImpact > 0){
            healthAnalysis.setBloodPressureStatus("High / Need to decrease");
            healthAnalysis.setRecommendations(healthRecommendationRepository.findHealthRecommendationByBloodPressureImpact("Decrease").stream().map(healthRecommendation -> mapper.map(healthRecommendation, HealthRecommendationDetails.class)).collect(Collectors.toSet()));
        } else if (bloodPressureImpact < 0) {
            healthAnalysis.setBloodPressureStatus("Low / Need to increase");
            healthAnalysis.setRecommendations(healthRecommendationRepository.findHealthRecommendationByBloodPressureImpact("Increase").stream().map(healthRecommendation -> mapper.map(healthRecommendation, HealthRecommendationDetails.class)).collect(Collectors.toSet()));
        } else {
            healthAnalysis.setBloodPressureStatus("Normal / Status Achieved");
            healthAnalysis.setRecommendations(healthRecommendationRepository.findHealthRecommendationByBloodPressureImpact("Stable").stream().map(healthRecommendation -> mapper.map(healthRecommendation, HealthRecommendationDetails.class)).collect(Collectors.toSet()));
        }
        return healthAnalysis.getRecommendations();
    }

    private Set<HealthRecommendationDetails> analyseBasedOnHeartRateComparison(int currentHeartRate, int targetedHeartRate, HealthAnalysis healthAnalysis){
        int heartRateImpact = calculateHeartRateImpact(currentHeartRate, targetedHeartRate);
        if (heartRateImpact > 0){
            healthAnalysis.setHeartRateStatus("High / Need to decrease");
            healthAnalysis.setRecommendations(healthRecommendationRepository.findHealthRecommendationByHeartRateImpact("Decrease").stream().map(healthRecommendation -> mapper.map(healthRecommendation, HealthRecommendationDetails.class)).collect(Collectors.toSet()));
        } else if (heartRateImpact < 0){
            healthAnalysis.setHeartRateStatus("Low / Need to increase");
            healthAnalysis.setRecommendations(healthRecommendationRepository.findHealthRecommendationByHeartRateImpact("Increase").stream().map(healthRecommendation -> mapper.map(healthRecommendation, HealthRecommendationDetails.class)).collect(Collectors.toSet()));
        }
        else {
            healthAnalysis.setHeartRateStatus("Normal / Status Achieved");
            healthAnalysis.setRecommendations(healthRecommendationRepository.findHealthRecommendationByHeartRateImpact("Stable").stream().map(healthRecommendation -> mapper.map(healthRecommendation, HealthRecommendationDetails.class)).collect(Collectors.toSet()));
        }
        return healthAnalysis.getRecommendations();
    }

    private Set<HealthRecommendationDetails> analyseBasedOnTemperatureComparison(float currentTemperature, float targetedTemperature, HealthAnalysis healthAnalysis){
        float temperatureImpact = calculateTemperatureImpact(currentTemperature, targetedTemperature);
        if (temperatureImpact > 0){
            healthAnalysis.setTemperatureStatus("High / Need to decrease");
            healthAnalysis.setRecommendations(healthRecommendationRepository.findHealthRecommendationByTemperatureImpact("Decrease").stream().map(healthRecommendation -> mapper.map(healthRecommendation, HealthRecommendationDetails.class)).collect(Collectors.toSet()));
        } else if (temperatureImpact < 0){
            healthAnalysis.setTemperatureStatus("Low / Need to increase");
            healthAnalysis.setRecommendations(healthRecommendationRepository.findHealthRecommendationByTemperatureImpact("Increase").stream().map(healthRecommendation -> mapper.map(healthRecommendation, HealthRecommendationDetails.class)).collect(Collectors.toSet()));
        } else {
            healthAnalysis.setTemperatureStatus("Normal / Status Achieved");
            healthAnalysis.setRecommendations(healthRecommendationRepository.findHealthRecommendationByTemperatureImpact("Stable").stream().map(healthRecommendation -> mapper.map(healthRecommendation, HealthRecommendationDetails.class)).collect(Collectors.toSet()));
        }
        return healthAnalysis.getRecommendations();
    }

    private void analyseOverallHealthStatus(HealthAnalysis healthAnalysis){
        if (healthAnalysis.getBloodPressureStatus().equals("Normal / Status Achieved") && healthAnalysis.getHeartRateStatus().equals("Normal / Status Achieved") && healthAnalysis.getTemperatureStatus().equals("Normal / Status Achieved")){
            healthAnalysis.setOverallStatus("Congratulations! Health objective achieved.");
        } else {
            healthAnalysis.setOverallStatus("Health objective not achieved. Please follow the recommendations.");
        }
    }

    private int calculateHeartRateImpact(int currentHeartRate, int targetedHeartRate){
        return currentHeartRate - targetedHeartRate;
    }

    private int calculateBloodPressureImpact(int currentBloodPressure, int targetedBloodPressure){
        return currentBloodPressure - targetedBloodPressure;
    }

    private float calculateTemperatureImpact(float currentTemperature, float targetedTemperature){
        return currentTemperature - targetedTemperature;
    }
}
