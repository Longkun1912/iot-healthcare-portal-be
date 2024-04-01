package com.example.iothealth.repository;

import com.example.iothealth.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    @Query("SELECT r from Role r WHERE r.name =:name")
    Optional<Role> findRoleByName(@Param("name") String name);
}
