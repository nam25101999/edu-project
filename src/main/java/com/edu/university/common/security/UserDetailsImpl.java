package com.edu.university.common.security;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

@Getter
@Builder
public class UserDetailsImpl implements UserDetails {

    private UUID id;
    private String username;
    private String password;
    private String email;
    private boolean emailVerified;
    private java.util.Set<String> roles;
    private java.util.Collection<? extends GrantedAuthority> authorities;
    private boolean isActive; // Thêm trường này để quản lý trạng thái tài khoản
    private java.time.LocalDateTime lastLoginAt;

    // ========================
    // OVERRIDE METHODS
    // ========================

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Bạn có thể map với trường lockUntil sau này nếu muốn
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Đã sửa: Trả về trạng thái thực tế của User thay vì luôn luôn là true, xử lý null-safe
        return Boolean.TRUE.equals(isActive);
    }
}