package com.skaz.security.core;

import lombok.Data;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * @author jungle
 */
@Data
public abstract class AbstractAuthentication implements Authentication, CredentialsContainer {

    protected Collection<? extends GrantedAuthority> authorities = AuthorityUtils.NO_AUTHORITIES;


    protected Object details;
    protected boolean authenticated = false;


    public AbstractAuthentication(Collection<? extends GrantedAuthority> authorities) {
        if (authorities == null) {
            this.authorities = AuthorityUtils.NO_AUTHORITIES;
        } else {
            Iterator var2 = authorities.iterator();
            GrantedAuthority a;
            do {
                if (!var2.hasNext()) {
                    ArrayList<GrantedAuthority> temp = new ArrayList(authorities.size());
                    temp.addAll(authorities);
                    this.authorities = Collections.unmodifiableList(temp);
                    return;
                }
                a = (GrantedAuthority) var2.next();
            } while (a != null);

            throw new IllegalArgumentException("Authorities collection cannot contain any null elements");
        }
    }

    @Override
    public String getName() {
        if (this.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) this.getPrincipal()).getUsername();
        } else if (this.getPrincipal() instanceof Principal) {
            return ((Principal) this.getPrincipal()).getName();
        } else {
            return this.getPrincipal() == null ? "" : this.getPrincipal().toString();
        }
    }

}
