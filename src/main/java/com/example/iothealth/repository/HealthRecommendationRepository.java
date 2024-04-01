package com.example.iothealth.repository;

import com.example.iothealth.model.HealthRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface HealthRecommendationRepository extends JpaRepository<HealthRecommendation, Integer>{
    @Query("SELECT h FROM HealthRecommendation h WHERE h.heart_rate_impact =:heart_rate_impact")
    Set<HealthRecommendation> findHealthRecommendationByHeartRateImpact(String heart_rate_impact);

    @Query("SELECT h FROM HealthRecommendation h WHERE h.blood_pressure_impact =:blood_pressure_impact")
    Set<HealthRecommendation> findHealthRecommendationByBloodPressureImpact(String blood_pressure_impact);

    @Query("SELECT h FROM HealthRecommendation h WHERE h.temperature_impact =:temperature_impact")
    Set<HealthRecommendation> findHealthRecommendationByTemperatureImpact(String temperature_impact);
}
