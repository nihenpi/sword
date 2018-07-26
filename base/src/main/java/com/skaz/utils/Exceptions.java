package com.skaz.utils;


import com.skaz.Constants;

/**
 * @author jungle
 */
public class Exceptions {

    public static final String EXCEPTION_CLASS_SECURITY_SPRING_AccessDeniedException = "org.springframework.security.access.AccessDeniedException";
    public static final String EXCEPTION_CLASS_SECURITY_SPRING_AuthenticationException = "org.springframework.security.core.AuthenticationException";
    public static final String EXCEPTION_CLASS_SECURITY_SPRING_AccountExpiredException = "org.springframework.security.authentication.AccountExpiredException";
    public static final String EXCEPTION_CLASS_SECURITY_SHIRO_AuthorizationException = "org.apache.shiro.authz.AuthorizationException";
    public static final String EXCEPTION_CLASS_SECURITY_SHIRO_UnauthorizedException = "org.apache.shiro.authz.UnauthorizedException";
    public static final String EXCEPTION_CLASS_SECURITY_SHIRO_UnauthenticatedException = "org.apache.shiro.authz.UnauthenticatedException";


    private static final String[] SECURITY_SPRING_EXCEPTIONS = {
            EXCEPTION_CLASS_SECURITY_SPRING_AccessDeniedException,
            EXCEPTION_CLASS_SECURITY_SPRING_AuthenticationException,
            EXCEPTION_CLASS_SECURITY_SPRING_AccountExpiredException
    };
    private static final String[] SECUIRTY_SHIRO_EXCEPTIONS = {
            EXCEPTION_CLASS_SECURITY_SHIRO_AuthorizationException,
            EXCEPTION_CLASS_SECURITY_SHIRO_UnauthorizedException,
            EXCEPTION_CLASS_SECURITY_SHIRO_UnauthenticatedException
    };

    public static Integer determinResponseStatus(Throwable e) {
        if (isException(e, EXCEPTION_CLASS_SECURITY_SPRING_AuthenticationException)) {
            return Constants.ERROR_STATUS_401;
        } else if (isException(e, EXCEPTION_CLASS_SECURITY_SPRING_AccountExpiredException)) {
            return Constants.ERROR_STATUS_401;
        } else if (isException(e, SECURITY_SPRING_EXCEPTIONS, SECUIRTY_SHIRO_EXCEPTIONS)) {
            return Constants.ERROR_STATUS_403;
        }
        return Constants.ERROR_STATUS_500;
    }

    public static boolean isException(Throwable throwable, String[]... exceptions) {
        for (String[] exception : exceptions) {
            if (isException(throwable, exception)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isException(Throwable throwable, String... exceptions) {
        for (String exception : exceptions) {
            if (getExceptionClassName(throwable).equals(exception)) {
                return true;
            }
        }
        return false;
    }

    private static String getExceptionClassName(Throwable throwable) {
        return getRootCause(throwable).getClass().getName();
    }

    public static Throwable getRootCause(Throwable e) {
        Throwable cause;
        while ((cause = e.getCause()) != null) {
            e = cause;
        }
        return e;
    }

    public static RuntimeException unchecked(Throwable ex) {
        if (ex instanceof RuntimeException) {
            return (RuntimeException) ex;
        } else {
            return new RuntimeException(ex);
        }
    }

    public static RuntimeException newRuntimeException(String msg) {
        return new RuntimeException(msg);
    }

    public static RuntimeException newRuntimeException(Throwable ex) {
        return new RuntimeException(ex.getMessage(), ex);
    }

    public static RuntimeException newRuntimeException(Throwable ex, String msg) {
        return new RuntimeException(msg, ex);
    }

    public static RuntimeException newRuntimeException(String msg, Throwable ex) {
        return new RuntimeException(msg, ex);
    }

    public static String determinResponseMessage(Throwable e) {
        if (isException(e, EXCEPTION_CLASS_SECURITY_SPRING_AuthenticationException)) {
            return Constants.ERROR_STATUS_401_MSG;
        } else if (isException(e, EXCEPTION_CLASS_SECURITY_SPRING_AccountExpiredException)) {
            return Constants.ERROR_STATUS_401_EXPIRED_MSG;
        } else if (isException(e, SECURITY_SPRING_EXCEPTIONS, SECUIRTY_SHIRO_EXCEPTIONS)) {
            return Constants.ERROR_STATUS_403_MSG;
        }
        return Constants.ERROR_STATUS_500_MSG;    }

    public static boolean isSecurityException(Throwable ex) {
        return isSecuritySpringException(ex) || isSecurityShiroException(ex);
    }

    private static boolean isSecurityShiroException(Throwable ex) {
        return isException(ex, SECUIRTY_SHIRO_EXCEPTIONS);
    }

    private static boolean isSecuritySpringException(Throwable ex) {
        return isException(ex, SECURITY_SPRING_EXCEPTIONS);

    }
}
