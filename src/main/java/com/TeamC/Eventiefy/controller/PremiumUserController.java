package com.TeamC.Eventiefy.controller;

import com.TeamC.Eventiefy.services.PremiumUserServiceImpl;
import com.TeamC.Eventiefy.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/premiumUsers")
@RequiredArgsConstructor
public class PremiumUserController {
    private final PremiumUserServiceImpl premiumUserService;

    // Endpoint to retrieve all premium users
    @GetMapping
    public ResponseEntity<List<User>> getAllPremiumUsers() {
        List<User> users = premiumUserService.getAllPremiumUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // Endpoint to retrieve premium users by package type
    @GetMapping("/package-type/{packageType}")
    public ResponseEntity<List<User>> getPremiumUsersByPackageType(@PathVariable String packageType) {
        List<User> users = premiumUserService.getPremiumUsersByPackageType(packageType);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }


    // Endpoint to get a premium user by ID
    @GetMapping("/{userId}")
    public ResponseEntity<User> getPremiumUserById(@PathVariable Long userId) {
        try {
            User user = premiumUserService.getPremiumUserById(userId);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint to upgrade a user to premium
    @PostMapping("/upgrade/{userId}")
    public ResponseEntity<String> upgradeUserToPremium(@PathVariable Long userId, @RequestParam String packageType) {
        try {
            premiumUserService.upgradeUserToPremium(userId, packageType);
            return new ResponseEntity<>("User upgraded to premium successfully", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint to downgrade a user from premium
    @PostMapping("/downgrade/{userId}")
    public ResponseEntity<String> downgradeUserFromPremium(@PathVariable Long userId) {
        try {
            premiumUserService.downgradeUserFromPremium(userId);
            return new ResponseEntity<>("User downgraded from premium successfully", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint to manage user access (grant/revoke)
    @PostMapping("/access/{userId}")
    public ResponseEntity<String> manageUserAccess(@PathVariable Long userId, @RequestParam boolean grantAccess) {
        try {
            premiumUserService.manageUserAccess(userId, grantAccess);
            return new ResponseEntity<>(grantAccess ? "Access granted" : "Access revoked", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }
}
