package com.fdmgroup.pilotbank2.authentication.security;

import com.fdmgroup.pilotbank2.models.User;
import com.fdmgroup.pilotbank2.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    public UserDetailsServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public MyUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException(String.format("User with username: %s not found!", username)));

        return new UserPrincipal(user);
    }

    public List<String> convertGrantedAuthoritiesToStrings(List<GrantedAuthority> authoritiesToConvert){
        List<String> convertedAuthorities = new ArrayList<>();
        for (GrantedAuthority authority : authoritiesToConvert) {
            convertedAuthorities.add(authority.toString());
        }
        return convertedAuthorities;
    }

}
