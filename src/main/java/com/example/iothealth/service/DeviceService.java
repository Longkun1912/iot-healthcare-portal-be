package com.example.iothealth.service;

import com.example.iothealth.domain.CustomerId;
import com.example.iothealth.domain.UserInfoDetails;
import com.example.iothealth.model.Device;
import com.example.iothealth.model.Organisation;
import com.example.iothealth.model.Role;
import com.example.iothealth.model.User;
import com.example.iothealth.payload.request.*;
import com.example.iothealth.payload.response.AuthResponse;
import com.example.iothealth.payload.response.UserDeviceResponse;
import com.example.iothealth.repository.DeviceRepository;
import com.example.iothealth.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class DeviceService {
    private final ModelMapper mapper;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final DeviceRepository deviceRepository;
    private final WebClient webClient;

    public List<UserDeviceResponse> viewDevicesByOwner() throws UsernameNotFoundException {
        List<UserDeviceResponse> userDeviceResponses = new ArrayList<>();
        List<Device> devices = deviceRepository.getDevicesByOwner(authService.getCurrentAuthenticatedUser());
        for (Device device : devices) {
            UserDeviceResponse userDeviceResponse = mapper.map(device, UserDeviceResponse.class);
            userDeviceResponse.setOwnerUserName(device.getOwner().getUsername());
            if (device.getImage() == null || device.getImage().isEmpty()){
                userDeviceResponse.setPicture("https://res.cloudinary.com/dokyaftrm/image/upload/v1706801462/iot-web-portal/devices/device_default.png");
            }
            else {
                userDeviceResponse.setPicture(device.getImage());
            }
            userDeviceResponses.add(userDeviceResponse);
        }
        return userDeviceResponses;
    }
    public List<UserDeviceResponse> viewDevicesByManager() throws UsernameNotFoundException {
        List<UserDeviceResponse> userDeviceResponses = new ArrayList<>();
        List<Device> devices = deviceRepository.getDevicesByManager(authService.getCurrentAuthenticatedUser().getOrganisation());

        for (Device device : devices) {
            UserDeviceResponse userDeviceResponse = mapper.map(device, UserDeviceResponse.class);
            if(device.getOwner() != null){
                userDeviceResponse.setOwnerUserName(device.getOwner().getUsername());
            }
            if (device.getImage() == null || device.getImage().isEmpty()){
                userDeviceResponse.setPicture("https://res.cloudinary.com/dokyaftrm/image/upload/v1706801462/iot-web-portal/devices/device_default.png");
            }
            else {
                userDeviceResponse.setPicture(device.getImage());
            }
            userDeviceResponses.add(userDeviceResponse);
        }
        return userDeviceResponses;
    }
    public List<UserDeviceResponse> viewUnassignedDevices() throws UsernameNotFoundException {
        List<UserDeviceResponse> userDeviceResponses = new ArrayList<>();
        List<Device> devices = deviceRepository.getUnassignedDevicesInOrg(authService.getCurrentAuthenticatedUser().getOrganisation());

        for (Device device : devices) {
            UserDeviceResponse userDeviceResponse = mapper.map(device, UserDeviceResponse.class);
            if (device.getImage() == null || device.getImage().isEmpty()){
                userDeviceResponse.setPicture("https://res.cloudinary.com/dokyaftrm/image/upload/v1706801462/iot-web-portal/devices/device_default.png");
            }
            else {
                userDeviceResponse.setPicture(device.getImage());
            }
            userDeviceResponses.add(userDeviceResponse);
        }
        return userDeviceResponses;
    }
    public UserDeviceResponse assignDevice(UUID deviceId, UUID userId){
        AuthResponse authResponse = authService.loginWithThingsBoard("tenant@thingsboard.org", "tenant");

        CountDownLatch latch = new CountDownLatch(1);
        UserDeviceResponse[] deviceResult = new UserDeviceResponse[1];
        webClient.post()
                .uri("/api/customer/{customerId}/device/{deviceId}", userId, deviceId)
                .header("Authorization", "Bearer " + authResponse.getAccessToken())
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(response -> {
                    try {
                        deviceResult[0] = assignedDeviceToUser(response, userId);
                        latch.countDown();
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }

                });
        try {
            latch.await();
            return deviceResult[0];
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
    public UserDeviceResponse assignedDeviceToUser(String response, UUID userId) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
        Map<String, String> idMap = (Map<String, String>) responseMap.get("id");
        String id = idMap.get("id");
        Optional<Device> existingDevice = deviceRepository.findById(UUID.fromString(id));
        Device device = existingDevice.get();
        User user = userRepository.findById(userId).get();
        device.setOwner(user);
        deviceRepository.save(device);
        return reformatDeviceResponse(device);
    }
    public UserDeviceResponse addDevice(UUID deviceId, Organisation organisation){
        Optional<Device> existingDevice = deviceRepository.findById(deviceId);
        Device device = existingDevice.get();
        device.setOrganisation(organisation);
        deviceRepository.save(device);
        return reformatDeviceResponse(device);
    }
    public void unassignDevice(UUID deviceId){
        AuthResponse authResponse = authService.loginWithThingsBoard("tenant@thingsboard.org", "tenant");

        CountDownLatch latch = new CountDownLatch(1);
        UserDeviceResponse[] deviceResult = new UserDeviceResponse[1];
        webClient.delete()
                .uri("/api/customer/device/{deviceId}", deviceId)
                .header("Authorization", "Bearer " + authResponse.getAccessToken())
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(response -> {
                    try {
                        unassignedDeviceToUser(response);
                        latch.countDown();
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }

                });
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
    public void unassignedDeviceToUser(String response) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
        Map<String, String> idMap = (Map<String, String>) responseMap.get("id");

        String id = idMap.get("id");
        Optional<Device> existingDevice = deviceRepository.findById(UUID.fromString(id));
        Device device = existingDevice.get();
        device.setOwner(null);
        deviceRepository.save(device);
    }
    public UserDeviceResponse editDevice(EditDeviceRequest editDeviceRequest) {
        AuthResponse authResponse = authService.loginWithThingsBoard("tenant@thingsboard.org", "tenant");
        CountDownLatch latch = new CountDownLatch(1);
        UserDeviceResponse[] deviceResult = new UserDeviceResponse[1];

        // Fetching device details
        Mono<String> deviceDetailsMono = webClient.get()
                .uri("/api/device/" + editDeviceRequest.getId())
                .header("Authorization", "Bearer " + authResponse.getAccessToken())
                .retrieve()
                .bodyToMono(String.class);

        deviceDetailsMono.flatMap(deviceDetailsResponse -> processDeviceDetails(deviceDetailsResponse, authResponse, editDeviceRequest, deviceResult, latch))
                .subscribe();

        try {
            latch.await();
            return deviceResult[0];
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private Mono<String> processDeviceDetails(String deviceDetailsResponse, AuthResponse authResponse, EditDeviceRequest editDeviceRequest, UserDeviceResponse[] deviceResult, CountDownLatch latch) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> responseMap = objectMapper.readValue(deviceDetailsResponse, Map.class);
            // Map to a new object and set name again
            Map<String, Object> updatedDeviceMap = new HashMap<>(responseMap);
            updatedDeviceMap.put("name", editDeviceRequest.getName());
            // Update the device in the database and return the updated response
            return webClient.post()
                    .uri("/api/device?accessToken=" + authResponse.getAccessToken())
                    .header("Authorization", "Bearer " + authResponse.getAccessToken())
                    .body(BodyInserters.fromValue(updatedDeviceMap))
                    .retrieve()
                    .bodyToMono(String.class)
                    .flatMap(response -> {
                        deviceResult[0] = editDeviceInDb(editDeviceRequest.getId(), editDeviceRequest.getName());
                        latch.countDown();
                        return Mono.just(response);
                    });
        } catch (JsonProcessingException e) {
            System.out.print(e);
            throw new RuntimeException(e);
        }
    }



    public UserDeviceResponse editDeviceInDb(UUID deviceId, String name) {
        Optional<Device> existingDevice = this.deviceRepository.findById(deviceId);
        Device device = existingDevice.get();
        device.setName(name);
        this.deviceRepository.save(device);
        return this.reformatDeviceResponse(device);
    }
    public boolean assigned(UUID deviceId){
        Device checkAssigned = deviceRepository.assigned(deviceId).orElse(null);
        return checkAssigned != null;
    }

    public UserDeviceResponse reformatDeviceResponse(Device device){
        UserDeviceResponse userDeviceResponse = mapper.map(device, UserDeviceResponse.class);
        User owner = device.getOwner();
//        if(owner != null ){
//            System.out.println("lmao");
//            userDeviceResponse.setOwner(device.getOwner().getUsername());
//        }
        LocalDateTime last_updated = device.getLast_updated();
        if (last_updated != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy 'at' hh:mm a");
            userDeviceResponse.setLast_updated(last_updated.format(formatter));
        } else {
            userDeviceResponse.setLast_updated("Not available");
        }

        return userDeviceResponse;
    }



    @Scheduled(fixedRate = 60000)
    @Transactional
    public void fetchAndUpdateDevicesFromThingsBoard() {
        AuthResponse authResponse = authService.loginWithThingsBoard("tenant@thingsboard.org", "tenant");
        fetchAndUpdateDevicesRecursive(0, authResponse);
    }

    // Define a recursive method to fetch and update devices from all pages
    private void fetchAndUpdateDevicesRecursive(int page, AuthResponse authResponse) {
        webClient.get()
                .uri("/api/tenant/deviceInfos?pageSize=10&page=" + page)
                .header("Authorization", "Bearer " + authResponse.getAccessToken())
                .retrieve()
                .bodyToMono(Map.class)
                .subscribe(response -> {
                    Map<String, Object> apiResponse = (Map<String, Object>) response;
                    List<Device> devices = mapApiResponseToDevices(apiResponse);

                    System.out.println("Devices from ThingsBoard: " + devices.size());

                    for (Device device : devices) {
                        Optional<Device> existingDeviceOptional = deviceRepository.findById(device.getId());

                        if (existingDeviceOptional.isEmpty()){
                            // Device doesn't exist yet, save it
                            deviceRepository.save(device);
                        }
                        else {
                            // Device exists, check for changes
                            Device existingDevice = existingDeviceOptional.get();

                            // Compare attributes and update if necessary
                            if (!existingDevice.equals(device)){
                                existingDevice.setName(device.getName());
                                existingDevice.setLabel(device.getLabel());
                                existingDevice.set_active(device.is_active());
                                existingDevice.set_gateway(device.is_gateway());
                                existingDevice.setType(device.getType());
                                existingDevice.setDevice_profile_name(device.getDevice_profile_name());
                                existingDevice.setAdditional_info(device.getAdditional_info());
                                existingDevice.setOwner(device.getOwner());
                                deviceRepository.save(existingDevice);
                            }
                        }
                    }

                    // Check if there are more pages
                    boolean hasNextPage = (boolean) apiResponse.get("hasNext");
                    if (hasNextPage) {
                        try {
                            TimeUnit.SECONDS.sleep(5);
                            fetchAndUpdateDevicesRecursive(page + 1, authResponse);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    public List<Device> mapApiResponseToDevices(Map<String, Object> apiResponse){
        List<Map<String, Object>> data = (List<Map<String, Object>>) apiResponse.get("data");
        List<Device> devices = new ArrayList<>();

        for (Map<String, Object> deviceData : data) {
            Device device = new Device();
            device.setId(UUID.fromString(((Map<String, String>) deviceData.get("id")).get("id")));
            device.setName((String) deviceData.get("name"));
            device.setLabel((String) deviceData.get("label"));
            device.set_active((Boolean) deviceData.get("active"));
            device.setType((String) deviceData.get("type"));
            device.setDevice_profile_name((String) deviceData.get("deviceProfileName"));
            device.setCreated_time(LocalDateTime.ofInstant(Instant.ofEpochMilli((Long) deviceData.get("createdTime")), ZoneId.systemDefault()));

            String userId = extractUserIdFromDeviceData(deviceData);
            if (userId != null) {
                UUID userIdUUID = UUID.fromString(userId);
                User assignedUser = userRepository.findById(userIdUUID).orElse(null);
                device.setOwner(assignedUser);
            }

            devices.add(device);

        }
        return devices;
    }

    public String extractUserIdFromDeviceData(Map<String, Object> deviceData) {
        Object customerIdObject = deviceData.get("customerId");
        if (customerIdObject instanceof Map) {
            Map<String, String> customerIdMap = (Map<String, String>) customerIdObject;
            return customerIdMap.get("id");
        }
        return null;
    }


}
