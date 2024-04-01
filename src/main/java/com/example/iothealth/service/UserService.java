package com.example.iothealth.service;

import com.example.iothealth.domain.*;
import com.example.iothealth.exception.AccountNotPresentInThingsBoardException;
import com.example.iothealth.exception.ExistingManagerInOrganisationException;
import com.example.iothealth.model.Chat;
import com.example.iothealth.model.Organisation;
import com.example.iothealth.model.Role;
import com.example.iothealth.model.User;
import com.example.iothealth.payload.request.*;
import com.example.iothealth.payload.response.AuthResponse;
import com.example.iothealth.repository.ChatRepository;
import com.example.iothealth.repository.OrganisationRepository;
import com.example.iothealth.repository.RoleRepository;
import com.example.iothealth.repository.UserRepository;
import com.example.iothealth.security.services.UserDetailsImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final ModelMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ChatRepository chatRepository;
    private final OrganisationRepository organisationRepository;
    private final AuthService authService;
    private final WebClient webClient;

    // Admin role
    public List<UserInfoDetails> getAllUsers(){
        return userRepository.findAll()
                .stream()
                .map(this::reformatUserDetail)
                .collect(Collectors.toList());
    }

    public UserInfoDetails viewPersonalProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Optional<User> currentUser = userRepository.findById(userDetails.getId());
        if (currentUser.isEmpty()){
            throw new UsernameNotFoundException("User not found.");
        }
        else {
            return reformatUserDetail(currentUser.get());
        }
    }

    // Admin role
    public UserInfoDetails addUser(
            String username, String avatar, String email,
            String password, String mobile, String gender,
            List<String> roles, String organisation) throws RuntimeException {
        AuthResponse authResponse = authService.loginWithThingsBoard("tenant@thingsboard.org", "tenant");
        CustomerCreatingRequest customerCreatingRequest = new CustomerCreatingRequest(username, mobile, email);


        Set<Role> userRoles = new HashSet<>();
        for (String role : roles) {
            Optional<Role> userRole = roleRepository.findRoleByName(role);
            userRole.ifPresent(userRoles::add);
        }

        Role adminRole = roleRepository.findRoleByName("admin").orElseThrow();
        Role managerRole = roleRepository.findRoleByName("manager").orElseThrow();
        Role userRole = roleRepository.findRoleByName("user").orElseThrow();

        if (roles.contains(managerRole.getName())) {
            if (checkExistingManagerInOrganisation(organisation)){
                throw new ExistingManagerInOrganisationException();
            }
        }

        CountDownLatch latch = new CountDownLatch(1);
        UserInfoDetails[] userResult = new UserInfoDetails[1]; // Use an array to store the result

        // Save user to ThingsBoard if user is manager or user
        if (userRoles.contains(userRole) && !userRoles.contains(adminRole)) {
            webClient.post()
                    .uri("/api/customer")
                    .header("Authorization", "Bearer " + authResponse.getAccessToken())
                    .body(BodyInserters.fromValue(customerCreatingRequest))
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(response -> {
                        try {
                            userResult[0] = addUserFromThingsBoardResponse(avatar, password, organisation, gender, userRoles, response);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                        latch.countDown(); // Signal that the operation is complete
                    });
        }

        try {
            latch.await(); // Wait for the latch to be counted down
            return userResult[0]; // Return the created user
        } catch (InterruptedException e) {
            System.out.println("Lock 4");
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }


    // From ThingsBoard response to save user to database
    public UserInfoDetails addUserFromThingsBoardResponse(String avatar, String password, String organisation, String gender, Set<Role> roles, String response) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
        Map<String, String> idMap = (Map<String, String>) responseMap.get("id");
        String id = idMap.get("id");
        String phone = (String) responseMap.get("phone");
        String emailResponse = (String) responseMap.get("email");
        String title = (String) responseMap.get("title");

        User user = new User();
        user.setId(UUID.fromString(id));
        user.setUsername(title);
        System.out.println(avatar);
        if (avatar.isEmpty()){
            user.setAvatar("https://res.cloudinary.com/dokyaftrm/image/upload/v1706801569/iot-web-portal/users/anonymous_user.png");
        } else {
            user.setAvatar(avatar);
        }

        user.setEmail(emailResponse);
        user.setPassword(passwordEncoder.encode(password));
        user.setMobile(phone);

        if (gender.isEmpty()){
            user.setGender("Other");
        } else {
            user.setGender(gender);
        }

        user.setOrganisation(organisationRepository.findOrganisationByName(organisation).orElseThrow(()->new UsernameNotFoundException("Organisation not found: "+organisation)));
        user.setRoles(roles);
        user.setLast_updated(LocalDateTime.now());
        userRepository.save(user);
        return reformatUserDetail(user);
    }


    // Admin role
    public UserInfoDetails editUser(AddEditUserRequest addEditUserRequest, String avatarName){
        Optional<User> existingUser = userRepository.findUserByEmail(addEditUserRequest.getEmail());
        if (existingUser.isEmpty()){
            throw new UsernameNotFoundException("User not found: "+ addEditUserRequest.getEmail());
        }
        else {
            AuthResponse authResponse = authService.loginWithThingsBoard("tenant@thingsboard.org", "tenant");
            String userId = existingUser.get().getId().toString();
            CustomerId customerId = new CustomerId(userId, "CUSTOMER");

            Set<Role> userRoles = new HashSet<>();
            for(String role: addEditUserRequest.getRoles()){
                Role userRole = roleRepository.findRoleByName(role).orElseThrow(() -> new UsernameNotFoundException("Role not found: "+role));;
                userRoles.add(userRole);
            }

            UserInfoDetails updatedUser = updateUserFromThingsBoardResponse(addEditUserRequest, avatarName, userRoles);

            // Update user to ThingsBoard server
            Role adminRole = roleRepository.findRoleByName("admin").orElseThrow();
            Role userRole = roleRepository.findRoleByName("user").orElseThrow();

            if (userRoles.contains(userRole)){
                if (!userRoles.contains(adminRole)){
                    Customer customer = new Customer();

                    webClient.get()
                            .uri("/api/customer/" + userId)
                            .header("Authorization", "Bearer " + authResponse.getAccessToken())
                            .retrieve()
                            .bodyToMono(String.class)
                            .subscribe(response -> {
                                try {
                                    ObjectMapper objectMapper = new ObjectMapper();
                                    Map<String, Object> customerDetails = objectMapper.readValue(response, Map.class);

                                    customer.setId(customerId);
                                    customer.setTitle(addEditUserRequest.getUsername());
                                    customer.setPhone(addEditUserRequest.getMobile());
                                    customer.setEmail(addEditUserRequest.getEmail());
                                    customer.setName(addEditUserRequest.getUsername());

                                    Long createdTime = (Long) customerDetails.get("createdTime");
                                    customer.setCreatedTime(createdTime);

                                    Map<String, Object> additionalInfo = (Map<String, Object>) customerDetails.get("additionalInfo");
                                    if (additionalInfo != null) {
                                        List<String> emergencyEmails = (List<String>) additionalInfo.get("emergencyEmail");
                                        customer.setAdditionalInfo(new AdditionalInfo(emergencyEmails));
                                    }

                                    String country = (String) customerDetails.get("country");
                                    customer.setCountry(country);
                                    String state = (String) customerDetails.get("state");
                                    customer.setState(state);

                                    Map<String, Object> tenantId = (Map<String, Object>) customerDetails.get("tenantId");
                                    String tenantEntityType = (String) tenantId.get("entityType");
                                    String tenantIdValue = (String) tenantId.get("id");
                                    customer.setTenantId(new TenantId(tenantEntityType, tenantIdValue));

                                    String externalId = (String) customerDetails.get("externalId");
                                    customer.setExternalId(externalId);

                                    System.out.println("Inner Customer: " + customer);

                                } catch (JsonProcessingException e) {
                                    throw new RuntimeException(e);
                                }
                            });

                    System.out.println("Outer Customer: " + customer);

                    webClient.post()
                            .uri("/api/customer")
                            .header("Authorization", "Bearer " + authResponse.getAccessToken())
                            .body(BodyInserters.fromValue(customer))
                            .retrieve()
                            .bodyToMono(String.class)
                            .doOnError(WebClientResponseException.NotFound.class, e -> {
                                throw new AccountNotPresentInThingsBoardException("This account is updated despite not present in ThingsBoard.");
                            })
                            .subscribe(System.out::println);
                }
            }
            return updatedUser;
        }
    }

    public UserInfoDetails updateUserFromThingsBoardResponse(AddEditUserRequest addEditUserRequest, String avatar, Set<Role> roles){
        Optional<User> existingUser = userRepository.findUserByEmail(addEditUserRequest.getEmail());
        if (existingUser.isEmpty()){
            throw new UsernameNotFoundException("User not found: "+ addEditUserRequest.getEmail());
        } else {
            User user = existingUser.get();
            user.setUsername(addEditUserRequest.getUsername());

            if (avatar.isEmpty()){
                user.setAvatar("https://res.cloudinary.com/dokyaftrm/image/upload/v1706801569/iot-web-portal/users/anonymous_user.png");
            } else {
                user.setAvatar(avatar);
            }

            if (addEditUserRequest.getGender().isEmpty()){
                user.setGender("Other");
            } else {
                user.setGender(addEditUserRequest.getGender());
            }

            System.out.println("Password: " + addEditUserRequest.getPassword());

            user.setMobile(addEditUserRequest.getMobile());
            if (!addEditUserRequest.getPassword().equals("undefined")){
                System.out.println("Password changed.");
                user.setPassword(passwordEncoder.encode(addEditUserRequest.getPassword()));
            }

            if (roles.contains(roleRepository.findRoleByName("manager").orElseThrow())) {
                if (checkExistingManagerInOrganisation(addEditUserRequest.getOrganisation())){
                    System.out.println("This organisation has a manager already.");

                    // Check if user old role is not manager
                    if (user.getRoles().stream().noneMatch(role -> role.getName().equals("manager"))){
                        throw new ExistingManagerInOrganisationException();
                    }
                }
            }

            user.setOrganisation(organisationRepository.findOrganisationByName(addEditUserRequest.getOrganisation()).orElseThrow(()->new RuntimeException("Organisation not found.")));
            user.setRoles(roles);


            user.setLast_updated(LocalDateTime.now());
            userRepository.save(user);
            return reformatUserDetail(user);
        }
    }

@Scheduled(fixedRate = 300000)
public void synchronizeUsersFromThingsBoardCustomer(){
    System.out.println("Fetching users from ThingsBoard server...");
    AuthResponse authResponse = authService.loginWithThingsBoard("tenant@thingsboard.org", "tenant");

    AtomicInteger page = new AtomicInteger(0);
    boolean hasNext = true;
    while (hasNext) {
        Mono<Map> result = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/customers")
                .queryParam("pageSize", 5)
                .queryParam("page", page.get())
                .queryParam("sortProperty", "createdTime")
                .queryParam("sortOrder", "DESC")
                .build())
            .header("Authorization", "Bearer " + authResponse.getAccessToken())
            .retrieve()
            .bodyToMono(Map.class)
            .doOnError(e -> System.out.println("Error occurred: " + e.getMessage()));

        Map<String, Object> response = result.block();

        if (response == null || response.isEmpty()) {
            break;
        }

        List<User> users = userRepository.findAll();

        // Process the response data...
        List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");
        for (Map<String, Object> item : data) {
            String email = (String) item.get("email");

            // Check if this user in ThingsBoard is not in database then add it to database
            if (users.stream().noneMatch(user -> user.getEmail().equals(email))) {
                User user = new User();
                user.setUsername((String) item.get("title"));
                user.setEmail(email);
                user.setMobile((String) item.get("phone"));
                user.setAvatar("https://res.cloudinary.com/dokyaftrm/image/upload/v1706801569/iot-web-portal/users/anonymous_user.png");
                user.setGender("Other");
                user.setOrganisation(organisationRepository.findOrganisationByName("Toshiba").orElseThrow());
                user.setRoles(Set.of(roleRepository.findRoleByName("user").orElseThrow()));
                user.setPassword(passwordEncoder.encode("password"));
                user.setLast_updated(LocalDateTime.now());
                userRepository.save(user);
            }

            // Check if this user in database is not in ThingsBoard then delete it from database
            if (users.stream().noneMatch(user -> user.getEmail().equals(email))) {
                // Exception for users in migration
                if (!email.equals("longphgbh200168@fpt.edu.vn")
                        && !email.equals("khanhntngch210731@fpt.edu.vn")
                        && !email.equals("hungcuong28597@gmail.com")
                        && !email.equals("andtrinh3189@gmail.com")){
                    userRepository.delete(userRepository.findUserByEmail(email).orElseThrow());
                }
            }

            // Check if this user in both ThingsBoard and database but updated in ThingsBoard then update it in database
            if (users.stream().anyMatch(user -> user.getEmail().equals(email))) {
                User user = userRepository.findUserByEmail(email).orElseThrow();
                user.setUsername((String) item.get("title"));
                user.setMobile((String) item.get("phone"));
                user.setLast_updated(LocalDateTime.now());
                userRepository.save(user);
            }
        }

        hasNext = (boolean)response.get("hasNext");
        page.getAndIncrement();
    }
    System.out.println("Users synchronized successfully.");
}


    public boolean checkExistingManagerInOrganisation(String organisationName){
        Organisation organisation = organisationRepository.findOrganisationByName(organisationName).orElseThrow();
        return userRepository.findUserByOrganisation(organisation).stream().anyMatch(user -> user.getRoles().stream().anyMatch(role -> role.getName().equals("manager")));
    }

    // Admin and Manager role
    public void deleteUser(UUID id){
        AuthResponse authResponse = authService.loginWithThingsBoard("tenant@thingsboard.org", "tenant");
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            String customerId = existingUser.get().getId().toString();
            userRepository.delete(existingUser.get());
            webClient.delete().uri(String.format("/api/customer/%s", customerId))
                    .header("Authorization", "Bearer " + authResponse.getAccessToken())
                    .retrieve()
                    .bodyToMono(Void.class)
                    .doOnError(WebClientResponseException.NotFound.class, e -> {
                        throw new AccountNotPresentInThingsBoardException("This account is removed despite not present in ThingsBoard.");
                    })
                    .block();
        }
    }

    // Manager role
    public List<UserInfoDetails> getUserInOrganisation(User manager, Organisation organisation){
        List<User> users = new ArrayList<>(userRepository.findUserByOrganisation(organisation));
        users.remove(manager);
        users.removeIf(user -> user.getRoles().stream().anyMatch(role -> role.getName().equals("admin")));
        return users.stream().map(this::reformatUserDetail).toList();
    }

    // User role
    public void updateUser(UserProfileRequest userProfileRequest, String avatarName){
        Optional<User> existingUser = userRepository.findUserByEmail(userProfileRequest.getEmail());
        if (existingUser.isEmpty()){
            throw new UsernameNotFoundException("User not found: "+ userProfileRequest.getEmail());
        }
        else {
            editUserProfileFromPublicServer(userProfileRequest, avatarName);

            // Update user to ThingsBoard server
            AuthResponse authResponse = authService.loginWithThingsBoard("tenant@thingsboard.org", "tenant");
            String userId = existingUser.get().getId().toString();
            CustomerId customerId = new CustomerId(userId, "CUSTOMER");
            CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(customerId,userProfileRequest.getUsername(), userProfileRequest.getMobile(), userProfileRequest.getEmail());

            webClient.post()
                    .uri("/api/customer")
                    .header("Authorization", "Bearer " + authResponse.getAccessToken())
                    .body(BodyInserters.fromValue(customerUpdateRequest))
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnError(WebClientResponseException.NotFound.class, e -> {
                        throw new AccountNotPresentInThingsBoardException("This account is updated despite not present in ThingsBoard.");
                    })
                    .subscribe(System.out::println);
        }
    }

    public void editUserProfileFromPublicServer(UserProfileRequest userProfileRequest, String avatar) {
        User user = userRepository.findUserByEmail(userProfileRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
        user.setUsername(userProfileRequest.getUsername());
        user.setMobile(userProfileRequest.getMobile());

        if (!avatar.isEmpty()){
            user.setAvatar(avatar);
        }

        user.setGender(userProfileRequest.getGender());
        user.setLast_updated(LocalDateTime.now());
        userRepository.save(user);
    }

    // Users can register themselves
    public String saveNewRegisteredUser(SignupRequest signupRequest, String avatarName){
        AuthResponse authResponse = authService.loginWithThingsBoard("tenant@thingsboard.org", "tenant");
        CustomerCreatingRequest customerCreatingRequest = new CustomerCreatingRequest(signupRequest.getUsername(), signupRequest.getMobile(), signupRequest.getEmail());

        webClient.post()
                .uri("/api/customer")
                .header("Authorization", "Bearer " + authResponse.getAccessToken())
                .body(BodyInserters.fromValue(customerCreatingRequest))
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(response -> {
                    try {
                        registerUserWithThingsBoardResponse(signupRequest, avatarName, response);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        return "Signup successfully.";
    }

    public void registerUserWithThingsBoardResponse(SignupRequest signupRequest, String avatarName, String response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
        Map<String, String> idMap = (Map<String, String>) responseMap.get("id");
        String id = idMap.get("id");

        User user = mapper.map(signupRequest, User.class);
        if (user != null) {
            user.setId(UUID.fromString(id));

            System.out.println("Avatar name: " + avatarName);
            user.setAvatar(avatarName);
            user.setGender(signupRequest.getGender());

            Organisation default_org = organisationRepository.findOrganisationByName("Toshiba").orElseThrow();
            user.setOrganisation(default_org);

            Set<Role> roles = new HashSet<>();
            Role role_user = roleRepository.findRoleByName("user").orElseThrow();
            roles.add(role_user);
            user.setRoles(roles);

            user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
            user.setLast_updated(LocalDateTime.now());
            userRepository.save(user);
        } else {
            throw new UsernameNotFoundException("User not found.");
        }
    }

    public boolean existsByEmail(String email) {
        User checkUser = userRepository.findUserByEmail(email).orElse(null);
        return checkUser != null;
    }

    public List<UserInfoDetails> findMembersInYourOrganisationForInbox(){
    User currentUser = authService.getCurrentAuthenticatedUser();
    Set<User> usersInChats = getUsersInChats(currentUser);

    return userRepository.findUserByOrganisation(currentUser.getOrganisation())
            .stream()
            .filter(user -> !user.equals(currentUser) && !usersInChats.contains(user))
            .filter(user -> user.getRoles().stream().noneMatch(role -> role.getName().equals("admin")))
            .map(this::reformatUserDetail)
            .toList();
}

    public List<UserInfoDetails> findAllUserForInboxAsAdmin(){
        User currentUser = authService.getCurrentAuthenticatedUser();
        Set<User> usersInChats = getUsersInChats(currentUser);

        return userRepository.findAll()
                .stream()
                .filter(user -> !user.equals(currentUser) && !usersInChats.contains(user))
                .filter(user -> user.getRoles().stream().noneMatch(role -> role.getName().equals("admin")))
                .map(this::reformatUserDetail)
                .toList();
    }

    private Set<User> getUsersInChats(User currentUser) {
        List<Chat> currentUserChats = chatRepository.findAllChatsByUserId(currentUser.getId());

        Set<User> usersInChats = new HashSet<>();
        for (Chat chat : currentUserChats) {
            if (chat.getMember1().equals(currentUser)) {
                usersInChats.add(chat.getMember2());
            } else if (chat.getMember2().equals(currentUser)) {
                usersInChats.add(chat.getMember1());
            }
        }
        return usersInChats;
    }

    public String fetchAdminEmail() {
        User admin = userRepository.findUsersByRoleName("admin").get(0);
        return admin.getEmail();
    }

    public UserInfoDetails reformatUserDetail(User user){
        UserInfoDetails userInfoDetails = mapper.map(user, UserInfoDetails.class);

        LocalDateTime last_updated = user.getLast_updated();
        if (last_updated != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy 'at' hh:mm a");
            userInfoDetails.setLast_updated(last_updated.format(formatter));
        } else {
            userInfoDetails.setLast_updated("Not available");
        }

        Set<String> roles = new HashSet<>();
        for(Role role: user.getRoles()){
            roles.add(role.getName());
        }

        if(user.getOrganisation() == null){
            userInfoDetails.setOrganisation("");
        }else{
            userInfoDetails.setOrganisation(user.getOrganisation().getName());
        }

        userInfoDetails.setRoles(roles);
        return userInfoDetails;
    }
}
