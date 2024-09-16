package com.TeamC.Eventiefy.services;

import com.TeamC.Eventiefy.enums.Role;
import com.TeamC.Eventiefy.user.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<User> findByEmailAndRole(String email, Role role);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    Optional<User> findByResetToken(String resetToken);
    List<User> findAll();
    void save(User user);
    void delete(User user);
    User getCurrentUser();

    // New methods for premium user management

    /**
     * Retrieves all users with a premium subscription.
     * @return List of premium users.
     */
    List<User> getAllPremiumUsers();

    /**
     * Updates the subscription status and package type of a user.
     * @param userId The ID of the user to update.
     * @param isPremium True if the user has a premium subscription, false otherwise.
     * @param packageType The type of premium package the user has.
     */
    void updateUserSubscriptionStatus(Long userId, boolean isPremium, String packageType);

    /**
     * Manages user access by granting or revoking access.
     * @param userId The ID of the user whose access is to be managed.
     * @param grantAccess True to grant access, false to revoke it.
     */
    void manageUserAccess(Long userId, boolean grantAccess);
}
