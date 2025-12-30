package com.gms.security;

import com.gms.entity.User;
import com.gms.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
@AllArgsConstructor
public class UserPrincipal implements UserDetails {
    
    private Long id;
    private String username;
    private String password;
    private Role role;
    private boolean active;
    
    public static UserPrincipal create(User user) {
        return new UserPrincipal(
            user.getId(),
            user.getUsername(),
            user.getPassword(),
            user.getRole(),
            user.getActive()
        );
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }
    
    @Override
    public boolean isAccountNonExpired() { return true; }
    
    @Override
    public boolean isAccountNonLocked() { return true; }
    
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    
    @Override
    public boolean isEnabled() { return active; }
}
