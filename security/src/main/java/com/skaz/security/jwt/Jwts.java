package com.skaz.security.jwt;

import com.skaz.Securitys;
import com.skaz.utils.Dates;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * @author jungle
 */
public class Jwts {
    public static final String secretKey = Securitys.SECRET_KEY;


    public static boolean validateToken(String accessToken) {
        try {
            io.jsonwebtoken.Jwts.parser().setSigningKey(secretKey).parse(accessToken);
            return true;
        } catch (Exception e) {

        }
        return false;
    }

    public static String getSubject(String accessToken) {
        return getClaims(accessToken).getSubject();
    }

    private static Claims getClaims(String accessToken) {
        return io.jsonwebtoken.Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken).getBody();
    }

    public static String createToken(String subject) {
        JwtBuilder jwt = io.jsonwebtoken.Jwts.builder();
        jwt.setSubject(subject);
        jwt.setIssuedAt(Dates.newDate());
        jwt.signWith(SignatureAlgorithm.HS512, secretKey);
        return jwt.compact();
    }
}
