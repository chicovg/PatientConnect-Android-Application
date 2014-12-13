package com.chicovg.symptommgmt.core.exceptions;

/**
 * Created by victorguthrie on 10/16/14.
 */
public class UserNameExistsException extends Exception {

    public UserNameExistsException(String username) {
        super(String.format("The requested username(%s) already exists", username));
    }
}
