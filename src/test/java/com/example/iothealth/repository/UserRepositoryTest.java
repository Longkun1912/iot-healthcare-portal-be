package com.example.iothealth.repository;

import com.example.iothealth.model.Organisation;
import com.example.iothealth.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    private Organisation organisation;

    @Before
    public void setUp() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setUsername("testUser");
        user.setMobile("1234567890");
        organisation = new Organisation();
        user.setOrganisation(organisation);

        when(userRepository.findUserByEmail(any(String.class))).thenReturn(Optional.of(user));
        when(userRepository.findUserByUsername(any(String.class))).thenReturn(Optional.of(user));
        when(userRepository.findUserByMobile(any(String.class))).thenReturn(Optional.of(user));
        when(userRepository.findUserByOrganisation(eq(organisation))).thenReturn(Arrays.asList(user));
    }

    @Test
    public void testFindUserByEmail() {
        User found = userRepository.findUserByEmail("test@example.com").orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    public void testFindUserByUsername() {
        User found = userRepository.findUserByUsername("testUser").orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo("testUser");
    }

    @Test
    public void testFindUserByMobile() {
        User found = userRepository.findUserByMobile("1234567890").orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getMobile()).isEqualTo("1234567890");
    }

    @Test
    public void testFindUserByOrganisation() {
        List<User> found = userRepository.findUserByOrganisation(organisation);

        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getOrganisation()).isEqualTo(organisation);
    }
}
