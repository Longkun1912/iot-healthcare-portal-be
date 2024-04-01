package com.example.iothealth.repository;

import com.example.iothealth.model.Organisation;
import com.example.iothealth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("SELECT u from User u WHERE u.email =:email")
    Optional<User> findUserByEmail(@Param("email") String email);

    @Query("SELECT u from User u WHERE u.username =:username")
    Optional<User> findUserByUsername(@Param("username") String username);

    @Query("SELECT u from User u WHERE u.mobile =:mobile")
    Optional<User> findUserByMobile(@Param("mobile") String mobile);

    @Query("SELECT u from User u WHERE u.organisation =:organisation")
    List<User> findUserByOrganisation(@Param("organisation") Organisation organisation);

    @Query("SELECT u from User u JOIN u.roles r WHERE r.name =:roleName")
    List<User> findUsersByRoleName(@Param("roleName") String roleName);
}
