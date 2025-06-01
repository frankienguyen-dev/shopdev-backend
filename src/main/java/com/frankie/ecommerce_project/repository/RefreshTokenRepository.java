package com.frankie.ecommerce_project.repository;

import com.frankie.ecommerce_project.model.Device;
import com.frankie.ecommerce_project.model.RefreshToken;
import com.frankie.ecommerce_project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    /**
     * Finds a refresh token by its value.
     *
     * @param token Refresh token value
     * @return Optional containing the RefreshToken, if found
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Retrieves all refresh tokens for a user.
     *
     * @param user User associated with the tokens
     * @return List of refresh tokens
     */
    List<RefreshToken> findByUser(User user);

    /**
     * Retrieves the refresh token associated with a device, if it exists.
     *
     * @param device the device associated with the refresh token
     * @return an Optional containing the refresh token, or empty if none exists
     */
    Optional<RefreshToken> findByDevice(Device device);


    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM RefreshToken rt WHERE rt.user = :user AND rt.device = :device")
    void deleteByUserAndDevice(User user, Device device);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM RefreshToken rt WHERE rt.user = :user")
    void deleteByUser(@Param("user") User user);
}
