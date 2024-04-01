package com.example.iothealth.service;

import com.example.iothealth.domain.UserInfoDetails;
import com.example.iothealth.domain.UserOrganisationDetails;
import com.example.iothealth.model.Organisation;
import com.example.iothealth.model.Role;
import com.example.iothealth.model.User;
import com.example.iothealth.repository.OrganisationRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrganisationService {
    private final OrganisationRepository organisationRepository;
    private final ModelMapper mapper;

    public List<UserOrganisationDetails> getAll(){
        List<UserOrganisationDetails> userOrganisationDetailsList = new ArrayList<>();
        List<Organisation> organisations = organisationRepository.findAll();
        for (Organisation organisation : organisations){
            UserOrganisationDetails userOrganisationDetails = reformatUserOrganisationDetails(organisation);
            userOrganisationDetailsList.add(userOrganisationDetails);
        }
        return userOrganisationDetailsList;
    }
    private UserOrganisationDetails reformatUserOrganisationDetails(Organisation organisation){
        return mapper.map(organisation, UserOrganisationDetails.class);
    }
    public UserOrganisationDetails addOrganisation(
            String organisationName,String description,
            String address, String contactNumber){
        Organisation organisation = new Organisation();
        organisation.setId(UUID.randomUUID());
        organisation.setName(organisationName);
        organisation.setDescription(description);
        organisation.setAddress(address);
        organisation.setContact_number(contactNumber);
        organisation.setLast_updated(LocalDateTime.now());
        organisationRepository.save(organisation);
        return reformatUserOrganisationDetails(organisation);
    }
    public UserOrganisationDetails editOrganisation(
            String organisationName,String description,
            String address, String contactNumber){

        Organisation organisation = organisationRepository.findOrganisationByName(organisationName)
                .orElseThrow(() -> new UsernameNotFoundException("Organisation not found: " + organisationName));

        organisation.setName(organisationName);
        organisation.setDescription(description);
        organisation.setAddress(address);
        organisation.setContact_number(contactNumber);
        organisation.setLast_updated(LocalDateTime.now());
        // Save the updated user
        organisationRepository.save(organisation);
        return reformatUserOrganisationDetails(organisation);

    }
    public void deleteOrganisation(UUID id){
        Optional<Organisation> existingUser = organisationRepository.findById(id);
        if (existingUser.isPresent()) {
            Organisation organisation = existingUser.get();
            organisationRepository.delete(organisation);
        }
    }
    public boolean existsByName(String name) {
        Organisation checkOrganisation = organisationRepository.findOrganisationByName(name).orElse(null);
        return checkOrganisation != null;
    }
}
