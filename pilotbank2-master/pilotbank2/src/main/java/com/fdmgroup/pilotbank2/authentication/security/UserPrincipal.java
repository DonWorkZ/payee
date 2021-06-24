package com.fdmgroup.pilotbank2.authentication.security;

import com.fdmgroup.pilotbank2.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserPrincipal implements MyUserDetails {

    private static final long serialVersionUID = 1L;
    private User user;
    private Long userId;
    private String username;
    private String firstName;
    private String lastName;
    private String password;
    private LocalDateTime passwordExpires;
    private String email;
    private Boolean isActive;
    private Boolean accountLockedFlag;
    private LocalDateTime accountExpires;
    private int failedLoginCount;
    private LocalDateTime lastFailedLoginDate;
    private List<GrantedAuthority> authorities;

    public UserPrincipal(){}

    public UserPrincipal(User user) {
        this.user = user;
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.password = user.getPassword();
        this.passwordExpires = user.getPasswordExpires();
        this.email = user.getEmail();
        this.isActive = user.getIsActive();
        this.accountLockedFlag = user.getAccountLockedFlag();
        this.accountExpires = user.getAccountExpires();
        this.failedLoginCount = user.getFailedLoginCount();
        this.lastFailedLoginDate = user.getLastFailedLoginDate();
        this.authorities = Arrays.stream(user.getRole().split(",")).map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() { return username; }

    @Override
    public String getPassword() { return password; }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public String getEmail(){return email;}

    @Override
    //Most accounts will not have accountExpires set. (The idea of Employees whose accounts COULD expire haven't
    //been implemented yet). This logic handles both cases.
    public boolean isAccountNonExpired() {
        if (this.accountExpires != null ) {
            return this.accountExpires.isBefore(LocalDateTime.now()) ? false : true;
        }
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return  failedLoginCount < 3 ||
                lastFailedLoginDate == null ? true : lastFailedLoginDate.isBefore(LocalDateTime.now().minus(24L, ChronoUnit.HOURS));
    }

    @Override
    //isCredentialsNonExpired - Credentials have not expired
    //Credential Expiration is calculated dynamically, no flag needed
    //False in this case means the credential is expired.
    public boolean isCredentialsNonExpired() {
        return this.passwordExpires.isBefore(LocalDateTime.now()) ? false : true;
    }

    @Override
    public boolean isEnabled() { return this.isActive; }

    @Override
    public Long getId() {
        return this.userId;
    }

    public void setAuthorities(){
        Arrays.stream(user.getRole().split(",")).map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

}
