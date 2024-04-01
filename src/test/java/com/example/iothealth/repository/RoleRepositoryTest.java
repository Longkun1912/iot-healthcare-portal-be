package com.example.iothealth.repository;

import com.example.iothealth.model.Role;
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
public class RoleRepositoryTest {

    @Mock
    private RoleRepository roleRepository;

    @Before
    public void setUp() {
        Role role = new Role();
        role.setName("testRole");

        when(roleRepository.findRoleByName(any(String.class))).thenReturn(Optional.of(role));
    }

    @Test
    public void testFindRoleByName() {
        Role found = roleRepository.findRoleByName("testRole").orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("testRole");
    }
}
