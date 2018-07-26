package com.skaz.security.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.skaz.Securitys;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * @author jungle
 */
@Data
@NoArgsConstructor
public class SecurityDetails implements UserDetails {

    private Long id;

    private String userName;

    private String password;

    protected Collection<SimpleGrantedAuthority> authorities;

    public SecurityDetails(Long id, String username, List<String> authorities) {
        this(id, username, Securitys.mapToListGrantedAuthorities(authorities));
    }

    public SecurityDetails(Long id, String userName, Collection<SimpleGrantedAuthority> authorities) {
        this.id = id;
        this.userName = userName;
        this.authorities = authorities;
    }

    @JsonIgnore
    @Override
    public Collection<SimpleGrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return this.password;
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return this.userName;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }
}
