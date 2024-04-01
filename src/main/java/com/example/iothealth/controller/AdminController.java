package com.example.iothealth.controller;

import com.example.iothealth.domain.RolesInforDetails;
import com.example.iothealth.domain.UserInfoDetails;
import com.example.iothealth.exception.ExistingManagerInOrganisationException;
import com.example.iothealth.model.User;
import com.example.iothealth.payload.request.AddEditUserRequest;
import com.example.iothealth.repository.UserRepository;
import com.example.iothealth.security.services.CloudinaryService;
import com.example.iothealth.service.RoleService;
import com.example.iothealth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
public class AdminController {
    private final CloudinaryService cloudinaryService;
    private final AuthenticationManager authenticationManager;
    private final RoleService roleService;
    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> getAllUsers( ){
        List<UserInfoDetails> usersList = userService.getAllUsers();
        if(usersList.isEmpty()){
            return ResponseEntity.badRequest().body("No user was found");
        }
        return ResponseEntity.ok().body(usersList);
    }

    @PostMapping("/user")
    @PreAuthorize("hasAuthority('manager')")
    public ResponseEntity<?> addUser(AddEditUserRequest addEditUserRequest){
        try{
            if (userService.existsByEmail(addEditUserRequest.getEmail())) {
                return ResponseEntity.badRequest().body("The email is already in use.");
            }
            if (userRepository.findUserByMobile(addEditUserRequest.getMobile()).isPresent()){
                return ResponseEntity.badRequest().body("The mobile is already in use.");
            }
            else {
                String avatarName = cloudinaryService.uploadUserAvatar(addEditUserRequest.getEmail(), addEditUserRequest.getAvatar());

                UserInfoDetails user =  userService.addUser(addEditUserRequest.getUsername(), avatarName, addEditUserRequest.getEmail(),
                        addEditUserRequest.getPassword(), addEditUserRequest.getMobile(), addEditUserRequest.getGender(),
                        addEditUserRequest.getRoles(), addEditUserRequest.getOrganisation());
                return ResponseEntity.ok().body(user);
            }
        } catch (ExistingManagerInOrganisationException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().body("Oop! Something went wrong. Please try again later.");
        }
    }

    @PutMapping("/user")
    @PreAuthorize("hasAuthority('manager')")
    public ResponseEntity<?> editUser(AddEditUserRequest addEditUserRequest){
        try {
            if(userRepository.findById(UUID.fromString(addEditUserRequest.getId())).get().getRoles().contains("admin")){
                System.out.println("Admin blocked.");
                return ResponseEntity.badRequest().body("Cannot edit other admin.");
            }
            else {
                Optional<User> existingUser = userRepository.findUserByMobile(addEditUserRequest.getMobile());
                if (existingUser.isPresent() && !existingUser.get().getEmail().equals(addEditUserRequest.getEmail())){
                    return ResponseEntity.badRequest().body("This mobile is already in use.");
                }
                else {
                    if (addEditUserRequest.getAvatar() == null || addEditUserRequest.getAvatar().isEmpty()){
                        Optional<User> updatingUser = userRepository.findUserByEmail(addEditUserRequest.getEmail());
                        if (updatingUser.isEmpty()){
                            return ResponseEntity.badRequest().body("User not found.");
                        }
                        else {
                            String avatarName = cloudinaryService.getUserAvatar(updatingUser.get().getEmail());
                            // Keep old user avatar
                            return ResponseEntity.ok().body(userService.editUser(addEditUserRequest, avatarName));
                        }
                    } else {
                        System.out.println("Avatar changed.");
                        // Remove old user avatar and upload new one (avatar name must remain)
                        cloudinaryService.deleteUserAvatar(addEditUserRequest.getEmail());
                        String avatarName = cloudinaryService.uploadUserAvatar(addEditUserRequest.getEmail(), addEditUserRequest.getAvatar());
                        return ResponseEntity.ok().body(userService.editUser(addEditUserRequest, avatarName));
                    }
                }
            }
        } catch (ExistingManagerInOrganisationException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (RuntimeException e){
            return ResponseEntity.internalServerError().body("Oop! Something went wrong. Please try again later.");
        } catch (Exception e){
            return ResponseEntity.badRequest().body("Bad request. Please check your input.");
        }
    }

    @PreAuthorize("hasAuthority('admin')")
    @DeleteMapping("/user")
    @Transactional
    public ResponseEntity<?> deleteUser(@RequestParam("user_id") String idRequest){
        UUID userId = UUID.fromString(idRequest);
        try {
            //convert id from string to uuid
            // Check if the user exists
            if (userRepository.existsById(userId)) {
                // Delete the user by ID
                cloudinaryService.deleteUserAvatar(userRepository.findById(userId).get().getEmail());
                userService.deleteUser(userId);
                return ResponseEntity.ok("User deleted successfully");
            } else {
                return ResponseEntity.badRequest().body("User not found");
            }
        } catch (IllegalArgumentException e) {
            // Handle the case where the provided ID is not a valid UUID
            return ResponseEntity.badRequest().body("Invalid user ID");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/roles")
    @Transactional
    public ResponseEntity<?> getAllRoles(){
        List<RolesInforDetails> adminRolesDetails = roleService.getAll().stream()
                .filter(role -> !role.getRoleName().equals("admin"))
                .toList();
        if(adminRolesDetails.isEmpty()){
            return ResponseEntity.badRequest().body("No roles was found");
        }
        return ResponseEntity.ok().body(adminRolesDetails);
    }
}
