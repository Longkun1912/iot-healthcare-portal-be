package com.example.iothealth.controller;

import com.example.iothealth.domain.UserInfoDetails;
import com.example.iothealth.model.User;
import com.example.iothealth.payload.request.AssignDeviceRequest;
import com.example.iothealth.payload.request.EditDeviceRequest;
import com.example.iothealth.payload.response.UserDeviceResponse;
import com.example.iothealth.repository.UserRepository;
import com.example.iothealth.security.services.UserDetailsImpl;
import com.example.iothealth.service.DeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.sound.midi.SysexMessage;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/device-management")
public class DeviceController {
    private final DeviceService deviceService;
    private final UserRepository userRepository;
    @GetMapping("/devices")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<?> viewDevicesByOwner(){
        try {
            return ResponseEntity.ok(deviceService.viewDevicesByOwner());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to retrieve devices.");
        }
    }
    @PostMapping("/device/assign")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<?> assignDevice(@Valid @RequestBody AssignDeviceRequest assignDeviceRequest){
        Optional<User> userOptional = userRepository.findUserByUsername(assignDeviceRequest.getOwner());
        UUID ownerId = userOptional.get().getId();
        try{
            if (deviceService.assigned(assignDeviceRequest.getDevice_id())) {
                return ResponseEntity.badRequest().body("This device is assigned already");
            }
            else if (!userRepository.existsById(ownerId)){
                return ResponseEntity.badRequest().body("User not found");
            }
            else {
                UserDeviceResponse assignDeviceResult =  deviceService.assignDevice(assignDeviceRequest.getDevice_id(), ownerId);
                return ResponseEntity.ok().body(assignDeviceResult);
            }
        } catch (Exception e){
            return ResponseEntity.internalServerError().body("Oop! Something went wrong. Please try again later.");
        }
    }
    @GetMapping("/manager/devices")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<?> getManagerDevices(){
        try {
            return ResponseEntity.ok(deviceService.viewDevicesByManager());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to retrieve devices.");
        }
    }
    @GetMapping("/devices/unassigned")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<?> getUnassignedDevices(){
        try {
            return ResponseEntity.ok(deviceService.viewUnassignedDevices());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to retrieve devices.");
        }
    }
    @PostMapping("/device/add")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<?> addDeviceToOrganisation(@RequestParam("device_id") String idRequest){
        UUID deviceId = UUID.fromString(idRequest);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        try{
            if (deviceService.assigned(deviceId)){
                return ResponseEntity.badRequest().body("This device is assigned already");
            }
            else {
                UserDeviceResponse assignDeviceResult =  deviceService.addDevice(deviceId, userDetails.getOrganisation());
                return ResponseEntity.ok().body(assignDeviceResult);
            }
        } catch (Exception e){
            return ResponseEntity.internalServerError().body("Oop! Something went wrong. Please try again later.");
        }
    }
    @PreAuthorize("hasAuthority('user')")
    @PostMapping("/device/unassign")
    @Transactional
    public ResponseEntity<?> unassignDevice(@Valid @RequestBody AssignDeviceRequest assignDeviceRequest){
//        UUID deviceId = UUID.fromString(assignDeviceRequest.getDevice_id());
//        UUID ownerId = UUID.fromString(assignDeviceRequest.getOwner_id());
//        System.out.println(deviceId);
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//        return null;
        Optional<User> userOptional = userRepository.findUserByUsername(assignDeviceRequest.getOwner());
        UUID ownerId = userOptional.get().getId();
        try{
            if (!deviceService.assigned(assignDeviceRequest.getDevice_id())) {
                return ResponseEntity.badRequest().body("This device is assigned already");
            }
            else if (!userRepository.existsById(ownerId)){
                return ResponseEntity.badRequest().body("User not found");
            }
            else {
                deviceService.unassignDevice(assignDeviceRequest.getDevice_id());
                return ResponseEntity.ok().body("Device has been unassigned");
            }
        } catch (Exception e){
            return ResponseEntity.internalServerError().body("Oop! Something went wrong. Please try again later.");
        }
    }
    @PostMapping({"/device/edit"})
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<?> editDevice(@RequestBody @Valid EditDeviceRequest editDeviceRequest) {
        try {
            UserDeviceResponse editDeviceResult = this.deviceService.editDevice(editDeviceRequest);
            return ResponseEntity.ok().body(editDeviceResult);
        } catch (Exception var3) {
            return ResponseEntity.internalServerError().body("Oop! Something went wrong. Please try again later.");
        }
    }
}
