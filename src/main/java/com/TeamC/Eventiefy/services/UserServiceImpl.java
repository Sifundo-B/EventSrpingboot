package com.TeamC.Eventiefy.services;

import com.TeamC.Eventiefy.entity.PaymentMethod;
import com.TeamC.Eventiefy.enums.Role;
import com.TeamC.Eventiefy.repository.PasswordResetTokenRepository;
import com.TeamC.Eventiefy.repository.PaymentMethodRepository;
import com.TeamC.Eventiefy.repository.UserRepo;
import com.TeamC.Eventiefy.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserDetailsService, UserService {

    private final UserRepo userRepo;
    private final EmailService emailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final PaymentMethodRepository paymentMethodRepository;

    // Method to get the user's preferred payment method
    public Optional<PaymentMethod> getUserPreferredPaymentMethod(Long userId) {
        Optional<User> user = userRepo.findById(userId);
        return user.map(User::getPreferredPaymentMethod);
    }

    // Method to update the user's preferred payment method
    public void updateUserPreferredPaymentMethod(Long userId, Long paymentMethodId) {
        Optional<User> userOptional = userRepo.findById(userId);
        Optional<PaymentMethod> paymentMethodOptional = paymentMethodRepository.findById(paymentMethodId);

        if (userOptional.isPresent() && paymentMethodOptional.isPresent()) {
            User user = userOptional.get();
            PaymentMethod paymentMethod = paymentMethodOptional.get();
            user.setPreferredPaymentMethod(paymentMethod);
            userRepo.save(user);
        } else {
            throw new IllegalArgumentException("User or Payment Method not found");
        }
    }

    // Method to retrieve all premium users
    public List<User> getAllPremiumUsers() {
        return userRepo.findByIsPremium(true);
    }

    // Method to update subscription status of a user
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

    // Method required by UserDetailsService to load user by email
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // Method to find a user by ID
    public Optional<User> findById(Long id) {
        return userRepo.findById(id);
    }

    // Method to find a user by email
    public Optional<User> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    // Method to find a user by email and role
    public Optional<User> findByEmailAndRole(String email, Role role) {
        return userRepo.findByEmailAndRole(email, role);
    }

    // Method to find a user by reset token (for password reset)
    public Optional<User> findByResetToken(String resetToken) {
        return userRepo.findByResetToken(resetToken);
    }

    // Method to get a user by their ID
    public User getUserById(Long id) {
        Optional<User> optional = userRepo.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new EntityNotFoundException("User with id " + id + " is not found");
        }
    }

    // Method to save a user
    public void save(User user) {
        userRepo.save(user);
    }

    // Method to delete a user
    public void delete(User user) {
        userRepo.delete(user);
    }

    // Method to retrieve all users
    public List<User> findAll() {
        return userRepo.findAll();
    }

    // Method to get the current user from the security context
    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return userRepo.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        } else if (principal instanceof String) {
            return userRepo.findByEmail((String) principal).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }
}
