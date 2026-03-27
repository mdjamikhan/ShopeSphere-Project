package com.lpu.shopsphere.admin.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
      if (a == null) continue;
      if (a.contains("ADMIN")) return UserRole.ROLE_ADMIN;
    }
    return UserRole.ROLE_CUSTOMER;
  }
}

