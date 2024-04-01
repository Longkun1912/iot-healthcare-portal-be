package com.example.iothealth.controller;

import com.example.iothealth.domain.UserOrganisationDetails;
import com.example.iothealth.payload.request.AddEditOrganisationRequest;
import com.example.iothealth.repository.OrganisationRepository;
import com.example.iothealth.service.OrganisationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/organisation")
public class OrganisationController {
    public final OrganisationRepository organisationRepository;
    public final OrganisationService organisationService;

    @GetMapping("/organisations")
//    @Transactional
    public ResponseEntity<?> getAllOrganisation(){
        List<UserOrganisationDetails> organisationsList = organisationService.getAll();
        if(organisationsList.isEmpty()){
            return ResponseEntity.badRequest().body("No organisation was found");
        }
        return ResponseEntity.ok().body(organisationsList);
    }
    @PostMapping("/organisation")
//    @Transactional
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> addOrganisation(@Valid @RequestBody AddEditOrganisationRequest addEditOrganisationRequest){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Auth: " + authentication);
        if (organisationService.existsByName(addEditOrganisationRequest.getOrganisationName())) {
            return ResponseEntity.badRequest().body("The organisation is already in use.");
        }
        UserOrganisationDetails organisation = organisationService.addOrganisation(
                addEditOrganisationRequest.getOrganisationName(), addEditOrganisationRequest.getOrgnisationDescription(),
                addEditOrganisationRequest.getOrganisationAdress(), addEditOrganisationRequest.getOrganisationContactNumber());
        return ResponseEntity.ok().body(organisation);
    }

    @PutMapping("/organisation")
//    @Transactional
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> editOrganisation(@Valid @RequestBody AddEditOrganisationRequest addEditOrganisationRequest){
        if (!organisationService.existsByName(addEditOrganisationRequest.getOrganisationName())) {
            return ResponseEntity.badRequest().body("The organisation is not exist.");
        }
        String organisationNameCheck = organisationRepository.findOrganisationByName(addEditOrganisationRequest.getOrganisationName())
                .orElseThrow(() -> new UsernameNotFoundException("Organisation Name not found: " + addEditOrganisationRequest.getOrganisationName())).getName();
        if (!addEditOrganisationRequest.getOrganisationName().equals(organisationNameCheck)) {
            if (organisationService.existsByName(addEditOrganisationRequest.getOrganisationName())) {
                return ResponseEntity.badRequest().body("The organisation is already in use.");
            }
        }
        UserOrganisationDetails organisationDetails = organisationService.editOrganisation(
                addEditOrganisationRequest.getOrganisationName(), addEditOrganisationRequest.getOrgnisationDescription(),
                addEditOrganisationRequest.getOrganisationAdress(), addEditOrganisationRequest.getOrganisationContactNumber());
        return ResponseEntity.ok().body(organisationDetails);
    }

    @PreAuthorize("hasAuthority('admin')")
    @DeleteMapping("/organisation")
    @Transactional
    public ResponseEntity<?> deleteOrganisation(@RequestParam("org_id") String idRequest){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Auth: " + authentication);
        UUID organisationId = UUID.fromString(idRequest);
        try {
            //convert id from string to uuid
            // Check if the user exists
            if (organisationRepository.existsById(organisationId)) {
                // Delete the user by ID
                organisationService.deleteOrganisation(organisationId);
                return ResponseEntity.ok("Organisation deleted successfully");
            } else {
                return ResponseEntity.badRequest().body("Organisation not found");
            }
        } catch (IllegalArgumentException e) {
            // Handle the case where the provided ID is not a valid UUID
            return ResponseEntity.badRequest().body("Invalid organisation ID");
        }
    }
}
