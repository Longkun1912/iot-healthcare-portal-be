package com.example.iothealth.repository;

import com.example.iothealth.model.HealthObjective;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HealthObjectiveRepository extends JpaRepository<HealthObjective, Integer> {
    @Query("SELECT h FROM HealthObjective h WHERE h.title =:title")
    Optional<HealthObjective> findHealthObjectiveByTitle(@Param("title") String title);

    @Query("SELECT u.health_objective FROM User u WHERE u.id = :userId")
    Optional<HealthObjective> findHealthObjectiveByUser(@Param("userId") UUID userId);

    @Query("SELECT h FROM HealthObjective h WHERE h.blood_pressure >= 70 AND h.blood_pressure <= 140")
    List<HealthObjective> findHealthObjectivesWithNormalBloodPressure();

    @Query("SELECT h FROM HealthObjective h WHERE h.temperature >= 36.5 AND h.temperature <= 37.5")
    List<HealthObjective> findHealthObjectivesWithNormalTemperature();

    @Query("SELECT h FROM HealthObjective h WHERE h.heart_rate >= 60 AND h.heart_rate <= 100")
    List<HealthObjective> findHealthObjectivesWithNormalHeartRate();

    @Query("SELECT h FROM HealthObjective h WHERE h.blood_pressure >= 70 AND h.blood_pressure <= 140" +
            " AND h.temperature >= 36.5 AND h.temperature <= 37.5")
    List<HealthObjective> findHealthObjectivesWithNormalBloodPressureAndTemperature();

    @Query("SELECT h FROM HealthObjective h WHERE h.blood_pressure >= 70 AND h.blood_pressure <= 140" +
            " AND h.heart_rate >= 60 AND h.heart_rate <= 100")
    List<HealthObjective> findHealthObjectivesWithNormalBloodPressureAndHeartRate();

    @Query("SELECT h FROM HealthObjective h WHERE h.temperature >= 36.5 AND h.temperature <= 37.5" +
            " AND h.heart_rate >= 60 AND h.heart_rate <= 100")
    List<HealthObjective> findHealthObjectivesWithNormalTemperatureAndHeartRate();

    @Query("SELECT h FROM HealthObjective h WHERE (h.heart_rate >= 60 AND h.heart_rate <= 100)" +
            " AND (h.temperature >= 36.5 AND h.temperature <= 37.5)" +
            " AND (h.blood_pressure >= 70 AND h.blood_pressure <= 140)")
    List<HealthObjective> findHealthObjectivesWithNormalVitals();
}
