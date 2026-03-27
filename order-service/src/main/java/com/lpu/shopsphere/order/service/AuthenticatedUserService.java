package com.lpu.shopsphere.order.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.lpu.shopsphere.order.security.UserRole;

@Service
public class AuthenticatedUserService {
  public String getCurrentEmail() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
      throw new IllegalStateException("Unauthenticated");
    }
    return auth.getName();
  }

  public UserRole getCurrentRole() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || auth.getAuthorities() == null) {
      return UserRole.ROLE_CUSTOMER;
    }
    for (var ga : auth.getAuthorities()) {
      String a = ga.getAuthority();
      try {
        return UserRole.valueOf(a);
      } catch (Exception ignored) {
        // continue
      }
    }
    return UserRole.ROLE_CUSTOMER;
  }
}

