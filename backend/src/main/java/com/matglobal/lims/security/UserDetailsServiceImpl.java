package com.matglobal.lims.security;

import com.matglobal.lims.entity.User;
import com.matglobal.lims.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        var authorities = user.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority(r.getName().name()))
                .collect(Collectors.toList());
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername()).password(user.getPassword())
                .authorities(authorities).accountLocked(!user.getIsActive()).build();
    }
}
