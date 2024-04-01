package com.example.iothealth.repository;

import com.example.iothealth.model.Organisation;
import com.example.iothealth.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface OrganisationRepository extends JpaRepository<Organisation, UUID> {
    @Query("SELECT r from Organisation r WHERE r.name =:name")
    Optional<Organisation> findOrganisationByName(@Param("name") String name);
}
