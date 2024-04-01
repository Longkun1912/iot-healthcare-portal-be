package com.example.iothealth.service;

import com.example.iothealth.domain.UserOrganisationDetails;
import com.example.iothealth.model.Organisation;
import com.example.iothealth.repository.OrganisationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrganisationServiceTest {

    @Mock
    private OrganisationRepository organisationRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private OrganisationService organisationService;

    private Organisation organisation;
    private UserOrganisationDetails userOrganisationDetails;

    @BeforeEach
    public void setup() {
        organisation = new Organisation();
        organisation.setId(UUID.randomUUID());
        organisation.setName("Test Organisation");

        userOrganisationDetails = new UserOrganisationDetails();
        userOrganisationDetails.setName("Test Organisation");

        lenient().when(modelMapper.map(organisation, UserOrganisationDetails.class)).thenReturn(userOrganisationDetails);
    }


    @Test
    public void testGetAll() {
        when(organisationRepository.findAll()).thenReturn(Collections.singletonList(organisation));

        assertEquals(1, organisationService.getAll().size());
        verify(organisationRepository, times(1)).findAll();
    }

    @Test
    public void testAddOrganisation() {
        Organisation organisation = new Organisation();
        organisation.setId(UUID.randomUUID());
        organisation.setName("Test Organisation");

        UserOrganisationDetails userOrganisationDetails = new UserOrganisationDetails();
        userOrganisationDetails.setName("Test Organisation");

        when(modelMapper.map(any(Organisation.class), eq(UserOrganisationDetails.class))).thenReturn(userOrganisationDetails);
        when(organisationRepository.save(any(Organisation.class))).thenReturn(organisation);

        UserOrganisationDetails result = organisationService.addOrganisation("Test Organisation", "Description", "Address", "ContactNumber");

        assertEquals("Test Organisation", result.getName());
        verify(organisationRepository, times(1)).save(any(Organisation.class));
    }

    @Test
    public void testEditOrganisation() {
        when(organisationRepository.findOrganisationByName("Test Organisation")).thenReturn(Optional.of(organisation));
        when(organisationRepository.save(any(Organisation.class))).thenReturn(organisation);

        UserOrganisationDetails result = organisationService.editOrganisation("Test Organisation", "Description", "Address", "ContactNumber");

        assertEquals("Test Organisation", result.getName());
        verify(organisationRepository, times(1)).save(any(Organisation.class));
    }

    @Test
    public void testDeleteOrganisation() {
        when(organisationRepository.findById(any(UUID.class))).thenReturn(Optional.of(organisation));

        organisationService.deleteOrganisation(organisation.getId());

        verify(organisationRepository, times(1)).delete(organisation);
    }

    @Test
    public void testExistsByName() {
        when(organisationRepository.findOrganisationByName("Test Organisation")).thenReturn(Optional.of(organisation));

        assertTrue(organisationService.existsByName("Test Organisation"));
        verify(organisationRepository, times(1)).findOrganisationByName("Test Organisation");
    }
}
