package com.TeamC.Eventiefy.services;

import com.TeamC.Eventiefy.user.User;
import com.TeamC.Eventiefy.repository.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PremiumUserServiceImpl implements PremiumUserServiceInit {

    private final UserRepo userRepo;

    // Method to retrieve all premium users
    @Override
    public List<User> getAllPremiumUsers() {
        return userRepo.findByIsPremium(true);
    }

    // Method to update subscription status of a user
    @Override
    public void updateUserSubscriptionStatus(Long userId, boolean isPremium, String packageType) {
        Optional<User> userOptional = userRepo.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPremium(isPremium);
            user.setPremiumPackageType(packageType);
            userRepo.save(user);
        } else {
            throw new EntityNotFoundException("User with id " + userId + " is not found");
        }
    }

    // Method to manage user access
    @Override
    public void manageUserAccess(Long userId, boolean grantAccess) {
        Optional<User> userOptional = userRepo.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setAccessGranted(grantAccess); // Set access permissions based on the boolean flag
            userRepo.save(user);
        } else {
            throw new EntityNotFoundException("User with id " + userId + " is not found");
        }
    }

    //Fetches all users with a specific premium package type.
    @Override
    public List<User> getPremiumUsersByPackageType(String packageType) {
        return userRepo.findByIsPremiumAndPremiumPackageType(true, packageType);
    }

    //Upgrades a user to a premium subscription and sets their package type.
    @Override
    public void upgradeUserToPremium(Long userId, String packageType) {
        Optional<User> userOptional = userRepo.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPremium(true);
            user.setPremiumPackageType(packageType);
            userRepo.save(user);
        } else {
            throw new EntityNotFoundException("User with id " + userId + " is not found");
        }
    }

    //Downgrades a user from a premium subscription.
    @Override
    public void downgradeUserFromPremium(Long userId) {
        Optional<User> userOptional = userRepo.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPremium(false);
            user.setPremiumPackageType(null);
            userRepo.save(user);
        } else {
            throw new EntityNotFoundException("User with id " + userId + " is not found");
        }
    }

    @Override
    public User getPremiumUserById(Long userId) {
        Optional<User> userOptional = userRepo.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.isPremium()) {
                return user;
            } else {
                throw new IllegalArgumentException("User with id " + userId + " is not a premium user");
            }
        } else {
            throw new EntityNotFoundException("User with id " + userId + " is not found");
        }
    }
}
