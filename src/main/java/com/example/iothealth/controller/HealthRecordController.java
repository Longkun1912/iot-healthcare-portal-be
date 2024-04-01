package com.example.iothealth.controller;

import com.example.iothealth.domain.HealthRecordDetails;
import com.example.iothealth.model.User;
import com.example.iothealth.payload.request.HealthRecordRequest;
import com.example.iothealth.repository.UserRepository;
import com.example.iothealth.security.services.UserDetailsImpl;
import com.example.iothealth.service.HealthRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/health_record-management")
public class HealthRecordController {
    private final UserRepository userRepository;
    private final HealthRecordService healthRecordService;

    @GetMapping("/health_record")
    public ResponseEntity<?> getCurrentHealthRecordForUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        try {
            Optional<User> currentUser = userRepository.findById(userDetails.getId());
            if (currentUser.isEmpty()){
                return ResponseEntity.internalServerError().body("Oops! The server encountered an error. Please try again later");
            }
            else {
                HealthRecordDetails healthRecordDetails = healthRecordService.viewCurrentHealth(currentUser.get().getId());
                return ResponseEntity.ok(healthRecordDetails);
            }
        } catch (NullPointerException e){
            return ResponseEntity.badRequest().body("No health record was found for this user. ");
        }
    }
    @GetMapping("/health_history")
    public ResponseEntity<?> getHealthRecordHistoryForUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        try {
            Optional<User> currentUser = userRepository.findById(userDetails.getId());
            if (currentUser.isEmpty()){
                return ResponseEntity.internalServerError().body("Oops! The server encountered an error. Please try again later");
            }
            else {
                List<HealthRecordDetails> healthRecordDetails = healthRecordService.viewHealthHistory(currentUser.get().getId());
                return ResponseEntity.ok(healthRecordDetails);
            }
        } catch (NullPointerException e){
            return ResponseEntity.badRequest().body("No health record was found for this user. ");
        }
    }

    @GetMapping("/health_history/day")
    public ResponseEntity<?> getHealthRecordHistoryTodayForUser(
            @RequestParam("id") String id,
            @RequestParam("date") String date) {
        UUID userId = UUID.fromString(id);
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = dateFormat.parse(date);
            LocalDate localDate = parsedDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

            Optional<User> currentUser = userRepository.findById(userId);
            if (currentUser.isEmpty()) {
                return ResponseEntity.internalServerError().body("Oops! The server encountered an error. Please try again later");
            } else {
                List<HealthRecordDetails> healthRecordDetails = healthRecordService.viewHealthHistoryForToday(currentUser.get().getId(), localDate);
                return ResponseEntity.ok(healthRecordDetails);
            }
        } catch (ParseException dateTimeParseException) {
            return ResponseEntity.badRequest().body(dateTimeParseException.getMessage());
        } catch (NullPointerException e) {
            return ResponseEntity.badRequest().body("No health record was found for this user.");
        }
    }



    @GetMapping("/health_history/week")
    public ResponseEntity<?> getHealthRecordHistoryThisWeekForUser(
            @RequestParam("id") String idRequest,
            @RequestParam("date") String date){
        System.out.print(date);
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//        UUID userId = userDetails.getId();

        UUID userId = UUID.fromString(idRequest);

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = dateFormat.parse(date);
            LocalDate localDate = parsedDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

            Optional<User> currentUser = userRepository.findById(userId);
            if (currentUser.isEmpty()){
                return ResponseEntity.internalServerError().body("Oops! The server encountered an error. Please try again later");
            }
            else {
                List<HealthRecordDetails> healthRecordDetails = healthRecordService.viewHealthHistoryForThisWeek(currentUser.get().getId(), localDate);
                return ResponseEntity.ok(healthRecordDetails);
            }
        }catch (ParseException dateTimeParseException) {
            return ResponseEntity.badRequest().body(dateTimeParseException.getMessage());
        } catch (NullPointerException e){
            return ResponseEntity.badRequest().body("No health record was found for this user. ");
        }
    }

    @GetMapping("/health_history/month")
    public ResponseEntity<?> getHealthRecordHistoryThisMonthForUser(
            @RequestParam("id") String idRequest,
            @RequestParam("date") String date){
        //        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//        UUID userId = userDetails.getId();

        UUID userId = UUID.fromString(idRequest);
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = dateFormat.parse(date);
            LocalDate localDate = parsedDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            Optional<User> currentUser = userRepository.findById(userId);
            if (currentUser.isEmpty()){
                return ResponseEntity.internalServerError().body("Oops! The server encountered an error. Please try again later");
            }
            else {
                List<HealthRecordDetails> healthRecordDetails = healthRecordService.viewHealthHistoryForThisMonth(currentUser.get().getId(), localDate);
                return ResponseEntity.ok(healthRecordDetails);
            }
        }catch (ParseException dateTimeParseException) {
            return ResponseEntity.badRequest().body(dateTimeParseException.getMessage());
        } catch (NullPointerException e){
            return ResponseEntity.badRequest().body("No health record was found for this user. ");
        }
    }

}
