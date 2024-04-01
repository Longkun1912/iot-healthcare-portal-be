package com.example.iothealth.controller;

import com.example.iothealth.domain.UserInfoDetails;
import com.example.iothealth.model.Organisation;
import com.example.iothealth.model.User;
import com.example.iothealth.payload.request.AddEditUserRequest;
import com.example.iothealth.repository.UserRepository;
import com.example.iothealth.security.services.CloudinaryService;
import com.example.iothealth.security.services.UserDetailsImpl;
import com.example.iothealth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/manager")
public class ManagerController {
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final UserService userService;
    @GetMapping("/users")
    @PreAuthorize("hasAuthority('manager')")
    public ResponseEntity<?> getUsersFromOrganisation(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Optional<User> currentManager = userRepository.findById(userDetails.getId());
        if (currentManager.isEmpty()){
            return ResponseEntity.internalServerError().body("Current manager is not found");
        }
        else {
            List<UserInfoDetails> usersList = userService.getUserInOrganisation(currentManager.get(), currentManager.get().getOrganisation());
            return ResponseEntity.ok().body(usersList);
        }
    }
    @PostMapping("/user")
    @PreAuthorize("hasAuthority('manager')")
    public ResponseEntity<?> addUser(AddEditUserRequest addEditUserRequest){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String currentOrganisation = userDetails.getOrganisation().getName();
        String editedUserOrg = addEditUserRequest.getOrganisation();
        if(!currentOrganisation.equals(editedUserOrg)){
            return ResponseEntity.badRequest().body("Can not add an account with different organisation");
        }else{
            try{
                if (userService.existsByEmail(addEditUserRequest.getEmail())) {
                    return ResponseEntity.badRequest().body("The email is already in use.");
                }
                if (userRepository.findUserByMobile(addEditUserRequest.getMobile()).isPresent()){
                    return ResponseEntity.badRequest().body("The mobile is already in use.");
                }
                else {
                    String avatarName = cloudinaryService.uploadUserAvatar(addEditUserRequest.getEmail(), addEditUserRequest.getAvatar());
                    userService.addUser(addEditUserRequest.getUsername(), avatarName, addEditUserRequest.getEmail(),
                            addEditUserRequest.getPassword(), addEditUserRequest.getMobile(), addEditUserRequest.getGender(),
                            addEditUserRequest.getRoles(), addEditUserRequest.getOrganisation());
                    return ResponseEntity.ok().body("User added successfully.");
                }
            } catch (RuntimeException | IOException e){
                return ResponseEntity.internalServerError().body("Oop! Something went wrong. Please try again later.");
            }
            catch(Exception e){
                return ResponseEntity.internalServerError().body(e);
            }
        }

    }

    @PutMapping("/user")
    @PreAuthorize("hasAuthority('manager')")
    public ResponseEntity<?> editUser(AddEditUserRequest addEditUserRequest){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String currentOrganisation = userDetails.getOrganisation().getName();
        String editedUserOrg = addEditUserRequest.getOrganisation();
        if (!currentOrganisation.equals(editedUserOrg)) {
            return ResponseEntity.badRequest().body("Can not edit to account with different organisation");
        }else {
            try {
                if (addEditUserRequest.getRoles().contains("admin") || addEditUserRequest.getRoles().contains("manager")) {
                    System.out.println("Blocked.");
                    return ResponseEntity.badRequest().body("Cannot edit other admin or manager.");
                } else {
                    Optional<User> existingUser = userRepository.findUserByMobile(addEditUserRequest.getMobile());
                    if (existingUser.isPresent() && !existingUser.get().getEmail().equals(addEditUserRequest.getEmail())) {
                        return ResponseEntity.badRequest().body("This mobile is already in use.");
                    } else {
                        String avatarName = "";
                        if (addEditUserRequest.getAvatar() == null || addEditUserRequest.getAvatar().isEmpty()) {
                            System.out.println("Avatar unchanged.");
                            avatarName = cloudinaryService.getUserAvatar(addEditUserRequest.getEmail());
                        } else {
                            System.out.println("Avatar changed.");
                            cloudinaryService.deleteUserAvatar(addEditUserRequest.getEmail());
                            avatarName = cloudinaryService.uploadUserAvatar(addEditUserRequest.getEmail(), addEditUserRequest.getAvatar());
                        }
                        userService.editUser(addEditUserRequest, avatarName);
                        return ResponseEntity.ok().body("User updated successfully.");
                    }
                }
            } catch (RuntimeException e) {
                return ResponseEntity.internalServerError().body("Oop! Something went wrong. Please try again later.");
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Bad request. Please check your input.");
            }
        }
    }

    @PreAuthorize("hasAuthority('manager')")
    @DeleteMapping("/user")
    @Transactional
    public ResponseEntity<?> deleteUser(@RequestParam("user_id") String idRequest){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        System.out.println("Auth: " + authentication);
        UUID userId = UUID.fromString(idRequest);

        try {
            if (userRepository.existsById(userId)) {
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
            throw new RuntimeException(e);
        }
    }


}
