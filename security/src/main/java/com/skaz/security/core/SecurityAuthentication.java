package com.skaz.security.core;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.util.Collection;

/**
 * @author jungle
 */
@Data
public class SecurityAuthentication extends AbstractAuthentication {

    private String accessToken;

    private UserDetails principal;

    public SecurityAuthentication(String accessToken, UserDetails principal) {
        super(principal.getAuthorities());
        this.principal = principal;
        this.accessToken = accessToken;
        super.setAuthenticated(true);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public Object getCredentials() {
        return accessToken;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean b) throws IllegalArgumentException {

    }

    @Override
    public void eraseCredentials() {

    }
}
