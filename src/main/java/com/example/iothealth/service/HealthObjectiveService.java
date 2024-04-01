package com.example.iothealth.service;

import com.example.iothealth.domain.HealthObjectiveDetails;
import com.example.iothealth.exception.HealthRecordNotFoundException;
import com.example.iothealth.model.HealthObjective;
import com.example.iothealth.model.HealthRecord;
import com.example.iothealth.model.User;
import com.example.iothealth.payload.request.AddHealthObjectiveRequest;
import com.example.iothealth.repository.HealthObjectiveRepository;
import com.example.iothealth.repository.HealthRecordRepository;
import com.example.iothealth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class HealthObjectiveService {
    private final ModelMapper mapper;
    private final HealthRecordRepository healthRecordRepository;
    private final HealthObjectiveRepository healthObjectiveRepository;
    private final UserRepository userRepository;

    public List<HealthObjectiveDetails> getAllHealthObjectives() {
        return healthObjectiveRepository.findAll().stream().map(healthObjective -> mapper.map(healthObjective, HealthObjectiveDetails.class)).toList();
    }

    public HealthObjective createHealthObjective(AddHealthObjectiveRequest addHealthObjectiveRequest, String pictureName) {
        HealthObjective healthObjective = mapper.map(addHealthObjectiveRequest, HealthObjective.class);
        healthObjective.setImage(pictureName);
        return healthObjectiveRepository.save(healthObjective);
    }

    public HealthObjectiveDetails viewCurrentHealthObjectiveByUser(User user) {
        Optional<HealthObjective> healthObjective = healthObjectiveRepository.findHealthObjectiveByUser(user.getId());
        return healthObjective.map(objective -> mapper.map(objective, HealthObjectiveDetails.class)).orElse(null);
    }

    public void setUserHealthObjective(Integer idRequest, User user) {
        Optional<HealthObjective> healthObjective = healthObjectiveRepository.findById(idRequest);
        healthObjective.ifPresent(user::setHealth_objective);
        userRepository.save(user);
    }

    public List<HealthObjectiveDetails> recommendHealthObjectiveForUser(User user){
        List<HealthObjectiveDetails> recommendedHealthObjectives = new ArrayList<>();
        Optional<HealthRecord> latestHealthRecord = healthRecordRepository.findLastUpdatedHealthRecordByUser(user.getId());
        if (latestHealthRecord.isPresent()){
            HealthRecord healthRecord = latestHealthRecord.get();
            boolean isBloodPressureNormal = isWithinRange(healthRecord.getBlood_pressure(), 70, 140);
            boolean isTemperatureNormal = isWithinRange(healthRecord.getTemperature(), 36.5, 37.5);
            boolean isHeartRateNormal = isWithinRange(healthRecord.getHeart_rate(), 60, 100);

            if (!isBloodPressureNormal && !isTemperatureNormal && !isHeartRateNormal) {
                recommendedHealthObjectives = mapToDetails(healthObjectiveRepository.findHealthObjectivesWithNormalVitals());
            } else if (!isBloodPressureNormal && !isTemperatureNormal) {
                recommendedHealthObjectives = mapToDetails(healthObjectiveRepository.findHealthObjectivesWithNormalBloodPressureAndTemperature());
            } else if (!isBloodPressureNormal && !isHeartRateNormal) {
                recommendedHealthObjectives = mapToDetails(healthObjectiveRepository.findHealthObjectivesWithNormalBloodPressureAndHeartRate());
            } else if (!isTemperatureNormal && !isHeartRateNormal) {
                recommendedHealthObjectives = mapToDetails(healthObjectiveRepository.findHealthObjectivesWithNormalTemperatureAndHeartRate());
            } else if (!isBloodPressureNormal) {
                recommendedHealthObjectives = mapToDetails(healthObjectiveRepository.findHealthObjectivesWithNormalBloodPressure());
            } else if (!isTemperatureNormal) {
                recommendedHealthObjectives = mapToDetails(healthObjectiveRepository.findHealthObjectivesWithNormalTemperature());
            } else if (!isHeartRateNormal) {
                recommendedHealthObjectives = mapToDetails(healthObjectiveRepository.findHealthObjectivesWithNormalHeartRate());
            } else {
                recommendedHealthObjectives = mapToDetails(healthObjectiveRepository.findAll());
            }
        }
        else {
            throw new HealthRecordNotFoundException("No health record found for this user.");
        }
        System.out.println("Recommended Health Objectives: " + recommendedHealthObjectives.size());
        return recommendedHealthObjectives;
    }

    private boolean isWithinRange(double value, double min, double max) {
        return value >= min && value <= max;
    }

    private List<HealthObjectiveDetails> mapToDetails(List<HealthObjective> healthObjectives) {
        return healthObjectives.stream()
                .map(healthObjective -> mapper.map(healthObjective, HealthObjectiveDetails.class))
                .toList();
    }
}
