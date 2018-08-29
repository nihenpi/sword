package com.skaz.security.core;

import com.skaz.Constants;
import com.skaz.Securitys;
import com.skaz.cache.Cache;
import com.skaz.cache.CacheTemplate;
import com.skaz.security.jwt.Jwts;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jungle
 */
public class SecurityManager {

    private CacheTemplate cacheTemplate;

    public SecurityManager(CacheTemplate cacheTemplate){
        this.cacheTemplate = cacheTemplate;
    }

    public void filter(String accessToken, HttpServletRequest request) {
        if (!Jwts.validateToken(accessToken)) {
            throw new AccountExpiredException(Constants.ERROR_STATUS_401_MSG);
        }
        String subject = Jwts.getSubject(accessToken);
        if (subject == null || subject.length() == 0) {
            throw new AccountExpiredException(Constants.ERROR_STATUS_401_EXPIRED_MSG);
        }
        SecurityAuthentication authentication = this.getSession(subject);
        if (authentication == null || !accessToken.equalsIgnoreCase(authentication.getAccessToken())) {
            throw new AccountExpiredException(Constants.ERROR_STATUS_401_EXPIRED_MSG);
        }

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        this.refreshSession(authentication);

    }

    private void refreshSession(SecurityAuthentication authentication) {
        if (isSessionAlmostExpire(authentication.getName())) {
            this.setSession(authentication);
        }
    }

    private void setSession(SecurityAuthentication authentication) {
        this.cacheTemplate.set(Securitys.SESSION_NAME, authentication.getName(), authentication, Securitys.SESSION_EXPIRE_BROWSER);
    }

    private boolean isSessionAlmostExpire(String subject) {
        int ttl = this.cacheTemplate.ttl(Securitys.SESSION_NAME, subject, Cache.Level.Local);
        if (ttl == -1) {
            return true;
        }
        return false;
    }

    private SecurityAuthentication getSession(String subject) {
        return this.cacheTemplate.get(Securitys.SESSION_NAME, subject);
    }

    public String login(UserDetails principal, HttpServletRequest request) {
        String accessToken = Jwts.createToken(principal.getUsername());
        SecurityAuthentication authentication = new SecurityAuthentication(accessToken, principal);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        this.setSession(authentication);
        return accessToken;
    }
}
