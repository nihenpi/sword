package com.skaz.exception;

import lombok.NoArgsConstructor;

/**
 * @author jungle
 */
@NoArgsConstructor
public class SystemException extends RuntimeException {
    private static final long serialVersionUID = 2868413578191501029L;

    public SystemException(String message) {
        super(message);
    }

    public SystemException(Throwable cause) {
        super(cause);
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }
}
