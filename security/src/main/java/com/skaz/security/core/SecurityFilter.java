package com.skaz.security.core;

import com.skaz.Securitys;
import com.skaz.utils.Servlets;
import lombok.AllArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author jungle
 */
@AllArgsConstructor
public class SecurityFilter extends GenericFilterBean {

    private SecurityManager securityManager;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String accessToken = request.getHeader(Securitys.TOKEN);
        try {
            if (StringUtils.hasText(accessToken)) {
                securityManager.filter(accessToken, request);
            }
            filterChain.doFilter(servletRequest, servletResponse);

        } catch (Exception e) {
            Servlets.sendFailure(request, response, e);
        }
    }
}
