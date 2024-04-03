package com.example.iothealth.service;

import com.example.iothealth.domain.HealthRecordDetails;
import com.example.iothealth.model.Device;
import com.example.iothealth.model.HealthRecord;
import com.example.iothealth.model.User;
import com.example.iothealth.payload.response.AuthResponse;
import com.example.iothealth.repository.DeviceRepository;
import com.example.iothealth.repository.HealthRecordRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HealthRecordService {
    private final ModelMapper mapper;
    private final HealthRecordRepository healthRecordRepository;
    private final AuthService authService;
    private final WebClient webClient;

    private final DeviceRepository deviceRepository;

    public HealthRecordDetails viewCurrentHealth(UUID userID) throws NullPointerException{
        HealthRecordDetails healthRecordDetails;
        Optional<HealthRecord> latestHealthRecord = healthRecordRepository.findLastUpdatedHealthRecordByUser(userID);
        if (latestHealthRecord.isPresent()){
            healthRecordDetails = mapper.map(latestHealthRecord.get(), HealthRecordDetails.class);
            return healthRecordDetails;
        } else {
            throw new NullPointerException("No health record was found for this user.");
        }
    }

    public HealthRecordDetails viewHistoricalHealth(UUID userID, LocalDateTime dateTime) throws NullPointerException{
        System.out.println("Date time: " + dateTime.toString());
        Optional<HealthRecord> healthRecord = healthRecordRepository.findHistoricalHealthRecordByUser(userID, dateTime);
        if (healthRecord.isPresent()){
            System.out.println("There are available health records for this user at this time.");
            return mapper.map(healthRecord.get(), HealthRecordDetails.class);
        } else {
            System.out.println("No health record was found for this user at this time.");
            throw new NullPointerException("No health record was found for this user at this time.");
        }
    }

    public List<HealthRecordDetails> viewHealthHistory(UUID userID) throws NullPointerException{
        Optional<List<HealthRecord>> healthRecordHistory = healthRecordRepository.findHealthRecordHistoryByUser(userID);
        if (healthRecordHistory.isPresent()){
            List<HealthRecordDetails> healthRecordDetailsList = new ArrayList<>();
            for (HealthRecord healthRecord : healthRecordHistory.get()) {
                HealthRecordDetails healthRecordDetails = mapper.map(healthRecord, HealthRecordDetails.class);
                healthRecordDetailsList.add(healthRecordDetails);
            }
            return healthRecordDetailsList;
        } else {
            throw new NullPointerException("No health record was found for this user.");
        }
    }

    private LocalDateTime truncateToHour(LocalDateTime dateTime) {
        return LocalDateTime.of(dateTime.toLocalDate(), LocalTime.of(dateTime.getHour(), 0));
    }

    private LocalDateTime truncateToDay(LocalDateTime dateTime) {
        return dateTime.toLocalDate().atStartOfDay();
    }

    private LocalDate truncateToWeek(LocalDate date) {
        return date.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
    }

    public List<HealthRecordDetails> viewHealthHistoryForToday(UUID userID, LocalDate date) throws NullPointerException {
        Optional<List<HealthRecord>> healthRecordForToday = healthRecordRepository.findHealthRecordForToday(userID, date);

        if (healthRecordForToday.isPresent()) {
            List<HealthRecordDetails> healthRecordDetailsList = new ArrayList<>();

            // Group health records by hour
            Map<LocalDateTime, List<HealthRecord>> recordsByHour = healthRecordForToday.get().stream()
                    .collect(Collectors.groupingBy(record -> truncateToHour(record.getLast_updated())));

            // Calculate average for each hour
            for (Map.Entry<LocalDateTime, List<HealthRecord>> entry : recordsByHour.entrySet()) {
                LocalDateTime hourStart = entry.getKey();
                List<HealthRecord> recordsForHour = entry.getValue();

                // Calculate average heart rate for the hour
                double averageHeartRate = recordsForHour.stream()
                        .mapToInt(HealthRecord::getHeart_rate)
                        .average()
                        .orElse(0.0);
                double averageTemperature = recordsForHour.stream()
                        .mapToDouble(HealthRecord::getTemperature)
                        .average()
                        .orElse(0.0);
                double averageBloodPressure = recordsForHour.stream()
                        .mapToDouble(HealthRecord::getBlood_pressure)
                        .average()
                        .orElse(0.0);

                // Create HealthRecordDetails for the hour
                HealthRecordDetails healthRecordDetails = new HealthRecordDetails();
                healthRecordDetails.setLast_updated(hourStart);
                healthRecordDetails.setTemperature((int) averageTemperature);
                healthRecordDetails.setHeart_rate((int) averageHeartRate);
                healthRecordDetails.setBlood_pressure((int) averageBloodPressure);

                healthRecordDetailsList.add(healthRecordDetails);
                healthRecordDetailsList.sort(Comparator.comparing(HealthRecordDetails::getLast_updated));
            }

            return healthRecordDetailsList;
        } else {
            throw new NullPointerException("No health records found for today.");
        }
    }

    public List<HealthRecordDetails> viewHealthHistoryForThisWeek(UUID userID, LocalDate date) throws NullPointerException {
        Optional<List<HealthRecord>> healthRecordThisWeek = healthRecordRepository.findHealthRecordForThisWeek(userID, date);

        if (healthRecordThisWeek.isPresent()) {
            List<HealthRecordDetails> healthRecordDetailsList = new ArrayList<>();

            // Group health records by day
            Map<LocalDateTime, List<HealthRecord>> recordsByDay = healthRecordThisWeek.get().stream()
                    .collect(Collectors.groupingBy(record -> truncateToDay(record.getLast_updated())));
            // Calculate average for each day
            for (Map.Entry<LocalDateTime, List<HealthRecord>> entry : recordsByDay.entrySet()) {
                LocalDateTime day = entry.getKey();
                List<HealthRecord> recordsForDay = entry.getValue();

                // Calculate average heart rate for the day
                double averageHeartRate = recordsForDay.stream()
                        .mapToInt(HealthRecord::getHeart_rate)
                        .average()
                        .orElse(0.0);
                double averageTemperature = recordsForDay.stream()
                        .mapToDouble(HealthRecord::getTemperature)
                        .average()
                        .orElse(0.0);
                double averageBloodPressure = recordsForDay.stream()
                        .mapToDouble(HealthRecord::getBlood_pressure)
                        .average()
                        .orElse(0.0);

                // Create HealthRecordDetails for the day
                HealthRecordDetails healthRecordDetails = new HealthRecordDetails();
                healthRecordDetails.setLast_updated(day);
                healthRecordDetails.setTemperature((int) averageTemperature);
                healthRecordDetails.setHeart_rate((int) averageHeartRate);
                healthRecordDetails.setBlood_pressure((int) averageBloodPressure);
                healthRecordDetailsList.add(healthRecordDetails);
                healthRecordDetailsList.sort(Comparator.comparing(HealthRecordDetails::getLast_updated));
            }
            return healthRecordDetailsList;
        } else {
            throw new NullPointerException("No health records found for this week.");
        }
    }

    public List<HealthRecordDetails> viewHealthHistoryForThisMonth(UUID userID, LocalDate date) throws NullPointerException {
        Optional<List<HealthRecord>> healthRecordThisMonth = healthRecordRepository.findHealthRecordForThisMonth(userID, date);

        if (healthRecordThisMonth.isPresent()) {
            List<HealthRecordDetails> healthRecordDetailsList = new ArrayList<>();

            // Group health records by week
            Map<LocalDate, List<HealthRecord>> recordsByWeek = healthRecordThisMonth.get().stream()
                    .collect(Collectors.groupingBy(record -> truncateToWeek(record.getLast_updated().toLocalDate())));

            // Calculate average for each week
            for (Map.Entry<LocalDate, List<HealthRecord>> entry : recordsByWeek.entrySet()) {
                LocalDate weekStart = entry.getKey();
                List<HealthRecord> recordsForWeek = entry.getValue();

                // Calculate average heart rate for the week
                double averageHeartRate = recordsForWeek.stream()
                        .mapToInt(HealthRecord::getHeart_rate)
                        .average()
                        .orElse(0.0);
                double averageTemperature = recordsForWeek.stream()
                        .mapToDouble(HealthRecord::getTemperature)
                        .average()
                        .orElse(0.0);
                double averageBloodPressure = recordsForWeek.stream()
                        .mapToDouble(HealthRecord::getBlood_pressure)
                        .average()
                        .orElse(0.0);
                // Create HealthRecordDetails for the week
                HealthRecordDetails healthRecordDetails = new HealthRecordDetails();
                healthRecordDetails.setHeart_rate((int) averageHeartRate);
                healthRecordDetails.setTemperature((int) averageTemperature);
                healthRecordDetails.setBlood_pressure((int) averageBloodPressure);
                healthRecordDetails.setLast_updated(LocalDateTime.of(weekStart, LocalTime.MIDNIGHT));


                healthRecordDetailsList.add(healthRecordDetails);
                healthRecordDetailsList.sort(Comparator.comparing(HealthRecordDetails::getLast_updated));
            }

            return healthRecordDetailsList;
        } else {
            throw new NullPointerException("No health records found for this month.");
        }
    }


    public HealthRecord mapApiResponseToHealthRecord(Map<String, Object> apiResponse) {
        HealthRecord healthRecord = new HealthRecord();
        healthRecord.setId(UUID.randomUUID());
        healthRecord.setLast_updated(LocalDateTime.now());

        // Extract and calculate average temperature
        List<Map<String, Object>> temperatureData = (List<Map<String, Object>>) apiResponse.get("temperature");
        double sumTemperature = 0.0;
        int countTemperature = 0;
        for (Map<String, Object> entry : temperatureData) {
            Object temperatureValueObj = entry.get("value");
            if (temperatureValueObj instanceof Number) {
                double temperatureValue = ((Number) temperatureValueObj).doubleValue();
                sumTemperature += temperatureValue;
                countTemperature++;
            }
        }
        double averageTemperature = countTemperature > 0 ? sumTemperature / countTemperature : 0.0;
        healthRecord.setTemperature((int) Math.floor(averageTemperature));
        System.out.println("Average Temperature: " + averageTemperature);

        // Extract and calculate average blood pressure
        List<Map<String, Object>> bloodPressureData = (List<Map<String, Object>>) apiResponse.get("BloodPressure");
        double sumBloodPressure = 0.0;
        int countBloodPressure = 0;
        for (Map<String, Object> entry : bloodPressureData) {
            Object bloodPressureValueObj = entry.get("value");
            if (bloodPressureValueObj instanceof Number) {
                double bloodPressureValue = ((Number) bloodPressureValueObj).doubleValue();
                sumBloodPressure += bloodPressureValue;
                countBloodPressure++;
            }
        }
        double averageBloodPressure = countBloodPressure > 0 ? sumBloodPressure / countBloodPressure : 0.0;
        healthRecord.setBlood_pressure((int) Math.floor(averageBloodPressure));
        System.out.println("Average Blood Pressure: " + averageBloodPressure);

        // Extract and calculate average heart rate
        List<Map<String, Object>> heartRateData = (List<Map<String, Object>>) apiResponse.get("BPM");
        double sumHeartRate = 0.0;
        int countHeartRate = 0;
        for (Map<String, Object> entry : heartRateData) {
            Object heartRateValueObj = entry.get("value");
            if (heartRateValueObj instanceof Number) {
                double heartRateValue = ((Number) heartRateValueObj).doubleValue();
                sumHeartRate += heartRateValue;
                countHeartRate++;
            }
        }
        double averageHeartRate = countHeartRate > 0 ? sumHeartRate / countHeartRate : 0.0;
        healthRecord.setHeart_rate((int) Math.floor(averageHeartRate));
        System.out.println("Average Heart Rate: " + averageHeartRate);

        return healthRecord;
    }

    // Fetch and save data from ThingsBoard every 1 minutes
    @Scheduled(fixedRate = 3600000) // Fetch data every one hour
    public void fetchHealthRecord() {
        AuthResponse authResponse = authService.loginWithThingsBoard("tenant@thingsboard.org", "tenant");
        List<Device> devices = deviceRepository.getActiveDevicesWithOwner();
        System.out.println("Devices: " + devices.size());

        // Calculate start and end timestamps for the last hour
        long endTs = Instant.now().toEpochMilli();
        long startTs = Instant.now().minus(1, ChronoUnit.HOURS).toEpochMilli();

        for (Device device : devices) {
            System.out.println("device: " + device.getId());
            Mono<Map> result = webClient.get()
                    .uri(uriBuilder ->
                            uriBuilder.path("/api/plugins/telemetry/DEVICE/{deviceId}/values/timeseries")
                                    .queryParam("startTs", startTs)
                                    .queryParam("endTs", endTs)
                                    .queryParam("entityId", device.getId())
                                    // Add other parameters as needed
                                    .build(device.getId()))
                    .header("Authorization", "Bearer " + authResponse.getAccessToken())
                    .retrieve()
                    .bodyToMono(Map.class);

            result.subscribe(response -> {
                Map<String, Object> apiResponse = (Map<String, Object>) response;
                HealthRecord healthRecord = mapApiResponseToHealthRecord(apiResponse);
                System.out.println("Health: " + healthRecord.getTemperature());
                healthRecord.setUser(device.getOwner());
                healthRecordRepository.save(healthRecord);
            });
        }
    }

}
