package com.TeamC.Eventiefy.services;

import com.TeamC.Eventiefy.user.User;

import java.util.List;

public interface PremiumUserServiceInit {

    /**
     * Retrieves all users with a premium subscription.
     * @return List of premium users.
     */
    List<User> getAllPremiumUsers();

    /**
     * Updates the subscription status and package type of user.
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

    List<User> getPremiumUsersByPackageType(String packageType);
    void upgradeUserToPremium(Long userId, String packageType);
    void downgradeUserFromPremium(Long userId);
    User getPremiumUserById(Long userId);
}
