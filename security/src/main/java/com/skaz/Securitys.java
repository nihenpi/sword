package com.skaz;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jungle
 */
public abstract class Securitys {
    public static final String TOKEN = "master";
    public static final String SECRET_KEY = Constants.SECURITY_SECRET;
    public static final String SESSION_NAME = Constants.SESSION_NAME;
    public static final int SESSION_EXPIRE_BROWSER = Constants.SECURITY_SESSION_EXPIRE_BROWSER;

    public static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public static boolean isPasswordValidate(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }


    public static List<SimpleGrantedAuthority> mapToListGrantedAuthorities(List<String> authorities) {
        return authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }
}
