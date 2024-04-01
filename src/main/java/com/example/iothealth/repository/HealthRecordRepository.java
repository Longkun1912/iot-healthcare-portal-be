package com.example.iothealth.repository;

import com.example.iothealth.model.HealthRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HealthRecordRepository extends JpaRepository<HealthRecord, UUID>{
    @Query("SELECT h FROM HealthRecord h WHERE h.user.id = :user_id " +
            "AND h.last_updated = (SELECT MAX(h2.last_updated) FROM HealthRecord h2 WHERE h2.user.id = :user_id)")
    Optional<HealthRecord> findLastUpdatedHealthRecordByUser(@Param("user_id") UUID user_id);

    @Query(value = "SELECT * FROM health_records h WHERE h.user_id = :user_id AND DATE(h.last_updated) = DATE(:dateTime) AND EXTRACT(HOUR FROM h.last_updated) = EXTRACT(HOUR FROM CAST(:dateTime AS TIMESTAMP)) AND h.last_updated = (SELECT MAX(h2.last_updated) FROM health_records h2 WHERE h2.user_id = h.user_id AND DATE(h2.last_updated) = DATE(:dateTime) AND EXTRACT(HOUR FROM h2.last_updated) = EXTRACT(HOUR FROM CAST(:dateTime AS TIMESTAMP)))", nativeQuery = true)
    Optional<HealthRecord> findHistoricalHealthRecordByUser(@Param("user_id") UUID user_id, @Param("dateTime") LocalDateTime dateTime);

    @Query("SELECT h FROM HealthRecord h WHERE h.user.id = :user_id ")
    Optional<List<HealthRecord>> findHealthRecordHistoryByUser(@Param("user_id") UUID user_id);

    @Query("SELECT h FROM HealthRecord h WHERE h.user.id = :user_id AND CAST(h.last_updated AS DATE) = :date")
    Optional<List<HealthRecord>> findHealthRecordForToday(@Param("user_id") UUID user_id, @Param("date") LocalDate date);


    @Query("SELECT h FROM HealthRecord h WHERE h.user.id = :user_id AND EXTRACT(WEEK FROM CAST(h.last_updated AS DATE)) = EXTRACT(WEEK FROM CAST(:date AS DATE)) AND EXTRACT(YEAR FROM CAST(h.last_updated AS DATE)) = EXTRACT(YEAR FROM CAST(:date AS DATE))")
    Optional<List<HealthRecord>> findHealthRecordForThisWeek(@Param("user_id") UUID user_id, @Param("date") LocalDate date);

    @Query("SELECT h FROM HealthRecord h WHERE h.user.id = :user_id AND EXTRACT(MONTH FROM CAST(h.last_updated AS DATE)) = EXTRACT(MONTH FROM CAST(:date AS DATE)) AND EXTRACT(YEAR FROM CAST(h.last_updated AS DATE)) = EXTRACT(YEAR FROM CAST(:date AS DATE))")
    Optional<List<HealthRecord>> findHealthRecordForThisMonth(@Param("user_id") UUID user_id, @Param("date") LocalDate date);



}
