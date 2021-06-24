package com.fdmgroup.pilotbank2.authentication.security;

import org.springframework.security.core.userdetails.UserDetails;

public interface MyUserDetails extends UserDetails {
    Long getId();

    String getFirstName();

    String getLastName();

    String getEmail();

}
