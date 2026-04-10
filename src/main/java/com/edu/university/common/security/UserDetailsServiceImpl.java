package com.edu.university.common.security;

import com.edu.university.modules.auth.entity.Users;
import com.edu.university.modules.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        // Đã sửa: Dùng findByIdentifier thay vì findByUsername để hỗ trợ đăng nhập linh hoạt
        Users user = userRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with identifier: " + identifier));

        // ✅ convert Set<Role> -> List<GrantedAuthority>
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .toList();

        // ✅ convert Set<Role> -> Set<String> (For MeResponse)
        java.util.Set<String> roles = user.getRoles().stream()
                .map(com.edu.university.modules.auth.entity.Role::getName)
                .collect(java.util.stream.Collectors.toSet());

        return UserDetailsImpl.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .email(user.getEmail())
                .emailVerified(user.isEmailVerified())
                .authorities(authorities)
                .roles(roles)
                .isActive(user.isActive())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }
}