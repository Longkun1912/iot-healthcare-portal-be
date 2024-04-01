package com.example.iothealth.controller;

import com.example.iothealth.model.User;
import com.example.iothealth.payload.request.UserProfileRequest;
import com.example.iothealth.payload.request.UserUpdatePasswordRequest;
import com.example.iothealth.repository.UserRepository;
import com.example.iothealth.security.services.CloudinaryService;
import com.example.iothealth.service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final CloudinaryService cloudinaryService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<?> viewPersonalProfile(){
        return ResponseEntity.ok().body(userService.viewPersonalProfile());
    }

    @PutMapping("/profile")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<?> editUser(UserProfileRequest userProfileRequest){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Current authentication: " + authentication);
        if (!userService.existsByEmail(userProfileRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This email is not exist.");
        }
        else {
            Optional<User> existingUser = userRepository.findUserByMobile(userProfileRequest.getMobile());
            if (existingUser.isPresent() && !existingUser.get().getEmail().equals(userProfileRequest.getEmail()))
                return ResponseEntity.badRequest().body("This mobile is already in use.");
            else {
                String avatarName;
                try{
                    Optional<User> updatingUser = userRepository.findUserByEmail(userProfileRequest.getEmail());
                    if (updatingUser.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This user account does not exist.");
                    }
                    else {
                        if (userProfileRequest.getAvatar() == null || userProfileRequest.getAvatar().isEmpty()) {
                            avatarName = "";
                            System.out.println("Avatar unchanged.");
                        }
                        else{
                            System.out.println("Avatar changed.");
                            cloudinaryService.deleteUserAvatar(userProfileRequest.getEmail());
                            avatarName = cloudinaryService.uploadUserAvatar(userProfileRequest.getEmail(), userProfileRequest.getAvatar());
                        }
                        userService.updateUser(userProfileRequest, avatarName);
                        return ResponseEntity.ok().body("User profile updated successfully.");
                    }
                } catch (Exception e) {
                    return ResponseEntity.badRequest().body("Error: " + e.getMessage());
                }
            }
        }
    }

    @GetMapping("/adminEmail")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<?> getAdminEmail() {
        return ResponseEntity.ok().body(userService.fetchAdminEmail());
    }

    @GetMapping("/organisation/members")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<?> getMembersInYourOrganisation() {
        return ResponseEntity.ok().body(userService.findMembersInYourOrganisationForInbox());
    }

    @GetMapping("/all/members")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> getAllUsers(){
        return ResponseEntity.ok().body(userService.findAllUserForInboxAsAdmin());
    }


    @PutMapping("/password")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<?> updateUserPassword(@RequestBody UserUpdatePasswordRequest userUpdatePasswordRequest) {
        Optional<User> existingUser = userRepository.findUserByEmail(userUpdatePasswordRequest.getEmail());
        if (existingUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This user account does not exist.");
        } else {
            User user = existingUser.get();
            if (!passwordEncoder.matches(userUpdatePasswordRequest.getOldPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Old password is incorrect.");
            } else {
                user.setPassword(passwordEncoder.encode(userUpdatePasswordRequest.getNewPassword()));
                userRepository.save(user);
                return ResponseEntity.ok().body("Password updated successfully.");
            }
        }
    }
}
