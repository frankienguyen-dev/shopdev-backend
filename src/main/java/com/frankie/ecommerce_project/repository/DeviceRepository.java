package com.frankie.ecommerce_project.repository;

import com.frankie.ecommerce_project.model.Device;
import com.frankie.ecommerce_project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, String> {

    /**
     * Finds a device by user, user agent, and IP address.
     *
     * @param user      User associated with the device
     * @param userAgent User agent string
     * @return Optional containing the Device, if found
     */
    Optional<Device> findByUserAndUserAgent(User user, String userAgent);

    /**
     * Retrieves all active devices for a user.
     *
     * @param user User whose devices are retrieved
     * @return List of active devices
     */
    List<Device> findByUserAndIsActiveTrue(User user);
}
