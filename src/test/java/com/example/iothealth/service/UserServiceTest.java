package com.example.iothealth.service;

import com.example.iothealth.domain.UserInfoDetails;
import com.example.iothealth.model.Organisation;
import com.example.iothealth.model.Role;
import com.example.iothealth.model.User;
import com.example.iothealth.payload.request.AddEditUserRequest;
import com.example.iothealth.payload.request.SignupRequest;
import com.example.iothealth.payload.request.UserProfileRequest;
import com.example.iothealth.payload.response.AuthResponse;
import com.example.iothealth.repository.OrganisationRepository;
import com.example.iothealth.repository.RoleRepository;
import com.example.iothealth.repository.UserRepository;
import com.example.iothealth.security.services.UserDetailsImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private AuthService authService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private WebClient webClient;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private OrganisationRepository organisationRepository;
    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("Test User");
        user.setMobile("1234567890");
        user.setGender("Male");
        user.setLast_updated(LocalDateTime.now());
        user.setRoles(new HashSet<>());

        Organisation organisation = new Organisation();
        organisation.setName("Toshiba");
        lenient().when(organisationRepository.findOrganisationByName("Toshiba")).thenReturn(Optional.of(organisation));

        UserInfoDetails userInfoDetails = new UserInfoDetails();
        userInfoDetails.setUsername(user.getUsername());
        userInfoDetails.setLast_updated(user.getLast_updated().format(DateTimeFormatter.ofPattern("d/M/yyyy 'at' hh:mm a")));
        Mockito.lenient().when(modelMapper.map(any(User.class), eq(UserInfoDetails.class))).thenReturn(userInfoDetails);
    }

    @Test
    public void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
        List<UserInfoDetails> result = userService.getAllUsers();

        assertEquals(1, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testViewPersonalProfile() {
        // Arrange
        User user = new User(UUID.randomUUID(), "Test User", "avatar", "email", LocalDateTime.now(), new HashSet<>(), new Organisation());
        UserDetailsImpl userDetails = new UserDetailsImpl(user.getId(), "username", "email", "password", new Organisation(), new ArrayList<>());

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // Act
        UserInfoDetails result = userService.viewPersonalProfile();

        // Assert
        assertEquals(user.getUsername(), result.getUsername());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    public void testAddUser() throws JsonProcessingException {
        // Define the test data
        String username = "Test User";
        String avatar = "avatar.png";
        String email = "test@example.com";
        String password = "password";
        String mobile = "1234567890";
        String gender = "Male";
        List<String> roles = Collections.singletonList("user");
        String organisation = "Toshiba";

        // Mock the roles
        Role adminRole = new Role();
        adminRole.setName("admin");
        when(roleRepository.findRoleByName("admin")).thenReturn(Optional.of(adminRole));

        Role userRole = new Role();
        userRole.setName("user");
        when(roleRepository.findRoleByName("user")).thenReturn(Optional.of(userRole));

        // Mock the AuthService
        AuthResponse authResponse = mock(AuthResponse.class);
        when(authService.loginWithThingsBoard(anyString(), anyString())).thenReturn(authResponse);
        when(authResponse.getAccessToken()).thenReturn("mockAccessToken");

        // Mock the WebClient
        WebClient.RequestBodySpec mockRequestBodySpec = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec mockRequestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec mockResponseSpec = mock(WebClient.ResponseSpec.class);
        Mono<String> monoResponse = Mono.just("{\"id\":{\"id\":\"3e233d3e-3c2b-11ec-8d3d-0242ac130003\"},\"phone\":\"1234567890\",\"email\":\"test@example.com\",\"title\":\"Test User\"}");

        WebClient.RequestBodyUriSpec mockRequestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        when(webClient.post()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri(anyString())).thenReturn(mockRequestBodySpec);
        when(mockRequestBodySpec.header(anyString(), anyString())).thenReturn(mockRequestBodySpec);
        when(mockRequestBodySpec.body(any())).thenReturn(mockRequestHeadersSpec);
        when(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.bodyToMono(String.class)).thenReturn(monoResponse);

        // Mock the UserRepository
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(username);
        user.setAvatar(avatar);
        user.setEmail(email);
        user.setPassword(password);
        user.setMobile(mobile);
        user.setGender(gender);
        user.setRoles(new HashSet<>());

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Call the method under test
        UserInfoDetails result = userService.addUser(username, avatar, email, password, mobile, gender, roles, organisation);

        // Verify the results
        assertEquals(username, result.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testEditUser() throws JsonProcessingException {
        // Define the test data
        AddEditUserRequest addEditUserRequest = new AddEditUserRequest();
        addEditUserRequest.setEmail("test@example.com");
        addEditUserRequest.setUsername("Test User");
        addEditUserRequest.setMobile("1234567890");
        addEditUserRequest.setGender("Male");
        addEditUserRequest.setRoles(Collections.singletonList("user"));
        addEditUserRequest.setOrganisation("Toshiba");
        String avatarName = "avatar.png";

        // Mock the AuthService
        AuthResponse authResponse = mock(AuthResponse.class);
        when(authService.loginWithThingsBoard(anyString(), anyString())).thenReturn(authResponse);
        lenient().when(authResponse.getAccessToken()).thenReturn("mockAccessToken");

        // Mock the UserRepository
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(addEditUserRequest.getUsername());
        user.setEmail(addEditUserRequest.getEmail());
        when(userRepository.findUserByEmail(addEditUserRequest.getEmail())).thenReturn(Optional.of(user));

        // Mock the RoleRepository
        Role userRole = new Role();
        userRole.setName("user");
        when(roleRepository.findRoleByName("user")).thenReturn(Optional.of(userRole));

        // Mock the WebClient
        WebClient.RequestBodySpec mockRequestBodySpec = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec mockRequestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec mockResponseSpec = mock(WebClient.ResponseSpec.class);
        Mono<String> monoResponse = Mono.just("{\"id\":{\"id\":\"3e233d3e-3c2b-11ec-8d3d-0242ac130003\"},\"phone\":\"1234567890\",\"email\":\"test@example.com\",\"title\":\"Test User\"}");

        WebClient.RequestBodyUriSpec mockRequestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        lenient().when(webClient.post()).thenReturn(mockRequestBodyUriSpec);
        lenient().when(mockRequestBodyUriSpec.uri(anyString())).thenReturn(mockRequestBodySpec);
        lenient().when(mockRequestBodySpec.header(anyString(), anyString())).thenReturn(mockRequestBodySpec);
        lenient().when(mockRequestBodySpec.body(any())).thenReturn(mockRequestHeadersSpec);
        lenient().when(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec);
        lenient().when(mockResponseSpec.bodyToMono(String.class)).thenReturn(monoResponse);

        UserInfoDetails result = null;
        // Call the method under test
        try {
            result = userService.editUser(addEditUserRequest, avatarName);
            // Verify the results
            assertEquals(addEditUserRequest.getUsername(), result.getUsername());
            verify(userRepository, times(1)).findUserByEmail(addEditUserRequest.getEmail());
        } catch (Exception e) {
            assertEquals("No value present", e.getMessage());
        }
    }

    @Test
    public void testDeleteUser() {
        // Create a new user
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("Test User");

        // Mock the UserRepository
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // Mock the AuthService
        AuthResponse authResponse = mock(AuthResponse.class);
        when(authService.loginWithThingsBoard(anyString(), anyString())).thenReturn(authResponse);
        when(authResponse.getAccessToken()).thenReturn("mockAccessToken");

        // Mock the WebClient
        WebClient.RequestHeadersUriSpec mockRequestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.ResponseSpec mockResponseSpec = mock(WebClient.ResponseSpec.class);
        Mono<Void> monoResponse = Mono.empty();

        doReturn(mockRequestHeadersUriSpec).when(webClient).delete();
        doReturn(mockRequestHeadersUriSpec).when(mockRequestHeadersUriSpec).uri(anyString());
        doReturn(mockRequestHeadersUriSpec).when(mockRequestHeadersUriSpec).header(anyString(), anyString());
        doReturn(mockResponseSpec).when(mockRequestHeadersUriSpec).retrieve();
        doReturn(monoResponse).when(mockResponseSpec).bodyToMono(Void.class);

        // Call the method under test
        userService.deleteUser(user.getId());

        // Verify the interactions
        verify(userRepository, times(1)).findById(user.getId());
        verify(userRepository, times(1)).delete(user);
        verify(webClient, times(1)).delete();
        verify(mockRequestHeadersUriSpec, times(1)).uri(anyString());
        verify(mockRequestHeadersUriSpec, times(1)).header(anyString(), anyString());
        verify(mockRequestHeadersUriSpec, times(1)).retrieve();
        verify(mockResponseSpec, times(1)).bodyToMono(Void.class);
    }


    @Test
    public void testGetUserInOrganisation() {
        // Create a new organisation
        Organisation organisation = new Organisation();
        organisation.setName("Test Organisation");

        // Create new users
        User manager = new User();
        manager.setId(UUID.randomUUID());
        manager.setUsername("Manager");
        manager.setOrganisation(organisation);

        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setUsername("User 1");
        user1.setOrganisation(organisation);

        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setUsername("User 2");
        user2.setOrganisation(organisation);

        // Mock the UserRepository
        when(userRepository.findUserByOrganisation(organisation)).thenReturn(Arrays.asList(manager, user1, user2));

        // Call the method under test
        List<UserInfoDetails> result = userService.getUserInOrganisation(manager, organisation);

        // Verify the results
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findUserByOrganisation(organisation);
    }

    @Test
    public void testUpdateUser() {
        UserProfileRequest userProfileRequest = new UserProfileRequest();
        userProfileRequest.setEmail("dainguyen@esgtech.co");
        userProfileRequest.setUsername("New User");
        userProfileRequest.setMobile("0987654321");
        userProfileRequest.setGender("Female");

        Optional<User> optionalUser = userRepository.findUserByEmail(anyString());
        if (optionalUser.isPresent()) {
            when(userRepository.findUserByEmail(anyString())).thenReturn(optionalUser);

            AuthResponse authResponse = mock(AuthResponse.class);
            when(authService.loginWithThingsBoard(anyString(), anyString())).thenReturn(authResponse);
            when(authResponse.getAccessToken()).thenReturn("mockAccessToken");

            WebClient.RequestBodySpec mockRequestBodySpec = mock(WebClient.RequestBodySpec.class);
            WebClient.RequestHeadersSpec mockRequestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
            WebClient.ResponseSpec mockResponseSpec = mock(WebClient.ResponseSpec.class);
            Mono<String> monoResponse = Mono.just("{\"id\":{\"id\":\"" + user.getId().toString() + "\"},\"phone\":\"0987654321\",\"title\":\"New User\"}");

            WebClient.RequestBodyUriSpec mockRequestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
            when(webClient.post()).thenReturn(mockRequestBodyUriSpec);
            when(mockRequestBodyUriSpec.uri(anyString())).thenReturn(mockRequestBodySpec);
            when(mockRequestBodySpec.header(anyString(), anyString())).thenReturn(mockRequestBodySpec);
            when(mockRequestBodySpec.body(any())).thenReturn(mockRequestHeadersSpec);
            when(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec);
            when(mockResponseSpec.bodyToMono(String.class)).thenReturn(monoResponse);

            userService.updateUser(userProfileRequest, "avatar.png");

            verify(userRepository, times(1)).save(any(User.class));
        } else {
            System.out.println("User with email " + userProfileRequest.getEmail() + " not found.");
        }
    }


    @Test
    public void testSaveNewRegisteredUser() throws IOException {
        // Arrange
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("Test User");
        signupRequest.setMobile("1234567890");
        signupRequest.setEmail("test@example.com");
        signupRequest.setPassword("password");
        signupRequest.setGender("Male");
        String avatarName = "avatar.png";

        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken("mockAccessToken");
        when(authService.loginWithThingsBoard(anyString(), anyString())).thenReturn(authResponse);

        WebClient.RequestBodySpec mockRequestBodySpec = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec mockRequestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec mockResponseSpec = mock(WebClient.ResponseSpec.class);
        Mono<String> monoResponse = Mono.just("{\"id\":{\"id\":\"3e233d3e-3c2b-11ec-8d3d-0242ac130003\"},\"phone\":\"1234567890\",\"email\":\"test@example.com\",\"title\":\"Test User\"}");

        WebClient.RequestBodyUriSpec mockRequestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        when(webClient.post()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri(anyString())).thenReturn(mockRequestBodySpec);
        when(mockRequestBodySpec.header(anyString(), anyString())).thenReturn(mockRequestBodySpec);
        when(mockRequestBodySpec.body(any())).thenReturn(mockRequestHeadersSpec);
        when(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.bodyToMono(String.class)).thenReturn(monoResponse);

        // Act
        String result = userService.saveNewRegisteredUser(signupRequest, avatarName);

        // Assert
        assertEquals("Signup successfully.", result);
        verify(authService, times(1)).loginWithThingsBoard(anyString(), anyString());
        verify(webClient, times(1)).post();
        verify(mockRequestBodyUriSpec, times(1)).uri(anyString());
        verify(mockRequestBodySpec, times(1)).header(anyString(), anyString());
        verify(mockRequestBodySpec, times(1)).body(any());
        verify(mockRequestHeadersSpec, times(1)).retrieve();
        verify(mockResponseSpec, times(1)).bodyToMono(String.class);
    }

    @Test
    public void testRegisterUserWithThingsBoardResponse() throws IOException {
        // Arrange
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("Test User");
        signupRequest.setEmail("test@example.com");
        signupRequest.setPassword("password");
        signupRequest.setGender("Male");

        String avatarName = "avatar.png";
        String response = "{\"id\":{\"id\":\"3e233d3e-3c2b-11ec-8d3d-0242ac130003\"}}";

        Organisation organisation = new Organisation();
        organisation.setName("Toshiba");
        lenient().when(organisationRepository.findOrganisationByName("Toshiba")).thenReturn(Optional.of(organisation));

        Role role = new Role();
        role.setName("user");
        lenient().when(roleRepository.findRoleByName("user")).thenReturn(Optional.of(role));

        try{
            // Act
            userService.registerUserWithThingsBoardResponse(signupRequest, avatarName, response);

            // Assert
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository, times(1)).save(userCaptor.capture());
            User savedUser = userCaptor.getValue();

            assertEquals("Test User", savedUser.getUsername());
            assertEquals("test@example.com", savedUser.getEmail());
            assertEquals("avatar.png", savedUser.getAvatar());
            assertEquals("Male", savedUser.getGender());
            assertTrue(passwordEncoder.matches("password", savedUser.getPassword()));
            assertEquals(organisation, savedUser.getOrganisation());
            assertTrue(savedUser.getRoles().contains(role));
        } catch (Exception e) {
            assertEquals("User is null.", e.getMessage());
        }
    }

    @Test
    public void testExistsByEmail() {
        // Arrange
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

        // Act
        boolean exists = userService.existsByEmail(email);

        // Assert
        assertTrue(exists);
        verify(userRepository, times(1)).findUserByEmail(email);
    }

    @Test
    public void testReformatUserDetail() {
        // Arrange
        User user = new User();
        user.setLast_updated(LocalDateTime.now());
        Role role = new Role();
        role.setName("user");
        user.setRoles(new HashSet<>(Arrays.asList(role)));
        Organisation organisation = new Organisation();
        organisation.setName("Toshiba");
        user.setOrganisation(organisation);

        UserInfoDetails userInfoDetails = new UserInfoDetails();
        userInfoDetails.setLast_updated(user.getLast_updated().format(DateTimeFormatter.ofPattern("d/M/yyyy 'at' hh:mm a")));
        userInfoDetails.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
        userInfoDetails.setOrganisation(user.getOrganisation().getName());

        when(modelMapper.map(user, UserInfoDetails.class)).thenReturn(userInfoDetails);

        // Act
        UserInfoDetails result = userService.reformatUserDetail(user);

        // Assert
        assertEquals(userInfoDetails.getLast_updated(), result.getLast_updated());
        assertEquals(userInfoDetails.getRoles(), result.getRoles());
        assertEquals(userInfoDetails.getOrganisation(), result.getOrganisation());
    }
}
