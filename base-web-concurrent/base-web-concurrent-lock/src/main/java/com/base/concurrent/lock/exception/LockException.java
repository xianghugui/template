package com.base.concurrent.lock.exception;

/**
 * Created by   on 16-5-14.
 */
public class LockException extends RuntimeException {
    public LockException(String message) {
        super(message);
    }

    public LockException(String message, Throwable cause) {
        super(message, cause);
    }
}
