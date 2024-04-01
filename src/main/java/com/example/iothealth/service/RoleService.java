package com.example.iothealth.service;

import com.example.iothealth.domain.RolesInforDetails;
import com.example.iothealth.domain.UserOrganisationDetails;
import com.example.iothealth.model.Organisation;
import com.example.iothealth.model.Role;
import com.example.iothealth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final ModelMapper mapper;

    public List<RolesInforDetails> getAll(){
        List<RolesInforDetails> rolesInforDetailsArrayList = new ArrayList<>();
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        SecurityContextHolder.getContext().setAuthentication(auth);
        List<Role> roles = roleRepository.findAll();
        for (Role role : roles){
            RolesInforDetails rolesInforDetails = reformatUserOrganisationDetails(role);
            rolesInforDetailsArrayList.add(rolesInforDetails);
        }
        return rolesInforDetailsArrayList;
    }

    public RolesInforDetails reformatUserOrganisationDetails(Role role){
        RolesInforDetails rolesInforDetails = mapper.map(role, RolesInforDetails.class);
        return rolesInforDetails;
    }
}
