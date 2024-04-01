package com.example.iothealth.service;


import com.example.iothealth.domain.RolesInforDetails;
import com.example.iothealth.model.Role;
import com.example.iothealth.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RoleService roleService;

    @BeforeEach
    public void setUp() {
        roleService = new RoleService(roleRepository, modelMapper);
    }

    @Test
    public void testGetAll() {
        Role role1 = new Role();
        Role role2 = new Role();
        List<Role> roles = Arrays.asList(role1, role2);

        when(roleRepository.findAll()).thenReturn(roles);

        RolesInforDetails rolesInforDetails1 = new RolesInforDetails();
        RolesInforDetails rolesInforDetails2 = new RolesInforDetails();

        when(modelMapper.map(role1, RolesInforDetails.class)).thenReturn(rolesInforDetails1);
        when(modelMapper.map(role2, RolesInforDetails.class)).thenReturn(rolesInforDetails2);

        List<RolesInforDetails> result = roleService.getAll();

        assertNotNull(result);
        assertEquals(roles.size(), result.size());
    }

    @Test
    public void testReformatUserOrganisationDetails() {
        // Create a Role object
        Role role = new Role();
        role.setId(1);
        role.setName("admin");

        // Create a RolesInforDetails object with the same properties as the Role object
        RolesInforDetails expectedRolesInforDetails = new RolesInforDetails();
        expectedRolesInforDetails.setId(1);
        expectedRolesInforDetails.setRoleName("admin");

        // Mock the ModelMapper response
        when(modelMapper.map(role, RolesInforDetails.class)).thenReturn(expectedRolesInforDetails);

        // Call the method to test
        RolesInforDetails actualRolesInforDetails = roleService.reformatUserOrganisationDetails(role);

        // Assertions
        assertNotNull(actualRolesInforDetails);
        assertEquals(expectedRolesInforDetails.getId(), actualRolesInforDetails.getId());
        assertEquals(expectedRolesInforDetails.getRoleName(), actualRolesInforDetails.getRoleName());
    }
}