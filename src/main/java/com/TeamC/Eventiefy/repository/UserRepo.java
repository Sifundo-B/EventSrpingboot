package com.TeamC.Eventiefy.repository;

import com.TeamC.Eventiefy.enums.Role;
import com.TeamC.Eventiefy.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndRole(String email, Role role);
    Optional<User> findByEmail(String email);
    Optional<User> findByResetToken(String resetToken);

    List<User> findByRole(Role role);
    //Optional<User> findByUsername(String username);
    //User save(User user);
    @Modifying
       @Query("UPDATE User u SET u.subscriptionPlan = NULL WHERE u.id = :userId")
        void updateUserSubscriptionToNull(@Param("userId") Long userId);


    // New methods for premium package management
    List<User> findByIsPremium(boolean isPremium);
    List<User> findByIsPremiumAndPremiumPackageType(boolean isPremium, String premiumPackageType);

}
