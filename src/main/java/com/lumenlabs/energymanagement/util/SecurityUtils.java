package com.lumenlabs.energymanagement.util;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.lumenlabs.energymanagement.model.Company;
import com.lumenlabs.energymanagement.model.User;
import com.lumenlabs.energymanagement.repository.UserRepository;

@Component
public class SecurityUtils {
	
	@Autowired
    private final UserRepository userRepository;

    public SecurityUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getLoggedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new RuntimeException("User not authenticated");
        }

        String email = auth.getName();
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public UUID getLoggedUserId() {
        return getLoggedUser().getId();
    }

    public Company getLoggedUserCompany() {
        return getLoggedUser().getCompany();
    }
}
