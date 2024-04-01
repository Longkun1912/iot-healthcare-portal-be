package com.example.iothealth.repository;

import com.example.iothealth.model.Organisation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrganisationRepositoryTest {

    @Mock
    private OrganisationRepository organisationRepository;

    @Before
    public void setUp() {
        Organisation organisation = new Organisation();
        organisation.setName("testOrganisation");

        when(organisationRepository.findOrganisationByName(any(String.class))).thenReturn(Optional.of(organisation));
    }

    @Test
    public void testFindOrganisationByName() {
        Organisation found = organisationRepository.findOrganisationByName("testOrganisation").orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("testOrganisation");
    }
}
