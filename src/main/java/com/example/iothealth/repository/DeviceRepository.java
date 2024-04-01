package com.example.iothealth.repository;

import com.example.iothealth.model.Device;
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
public interface DeviceRepository extends JpaRepository<Device, UUID> {
    @Query("SELECT d from Device d WHERE d.name =:name")
    Optional<Device> findDeviceByName(@Param("name") String name);

    @Query("SELECT d from Device d WHERE d.owner =:owner")
    List<Device> getDevicesByOwner(@Param("owner") User owner);
    @Query("SELECT d FROM Device d WHERE d.organisation =:organisation")
    List<Device> getDevicesByManager(@Param("organisation") Organisation organisation);

    @Query("SELECT d FROM Device d WHERE d.organisation =:organisation AND d.owner IS NULL")
    List<Device> getUnassignedDevicesInOrg(@Param("organisation") Organisation organisation);


    @Query("SELECT d FROM Device d WHERE d.id = :id AND d.owner IS NOT NULL")
    Optional<Device> assigned(@Param("id") UUID id);

    @Query("SELECT d FROM Device d WHERE d.is_active = true AND d.owner IS NOT NULL")
    List<Device> getActiveDevicesWithOwner();

}
