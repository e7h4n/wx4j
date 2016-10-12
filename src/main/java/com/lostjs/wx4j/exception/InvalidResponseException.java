/**
 * @(#)${FILE_NAME}.java, 12/10/2016.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.lostjs.wx4j.exception;

/**
 * @author pw
 */
public final class InvalidResponseException extends RuntimeException {

    private int ret;

    private String message;

    public InvalidResponseException(int ret, String message) {
        this.ret = ret;
        this.message = message;
    }

    private InvalidResponseException() {
        super();
    }

    private InvalidResponseException(String message) {
        super(message);
    }

    private InvalidResponseException(String message, Throwable cause) {
        super(message, cause);
    }

    private InvalidResponseException(Throwable cause) {
        super(cause);
    }

    private InvalidResponseException(String message, Throwable cause, boolean enableSuppression,
                                     boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public int getRet() {
        return ret;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "InvalidResponseException{" + "ret=" + ret + ", message='" + message + '\'' + '}';
    }
}
