package com.chicovg.symptommgmt.rest.domain;

import com.chicovg.symptommgmt.rest.annotations.BundleField;
import com.chicovg.symptommgmt.rest.annotations.BundleFieldType;

/**
 * Created by victorguthrie on 10/15/14.
 *
 * A domain object representing a user of the system
 */
public class User {

    @BundleField(type = BundleFieldType.STRING)
    private String username;
    private String password; //only used for registration
    @BundleField(type = BundleFieldType.ENUM)
    private UserType type;
    @BundleField(type = BundleFieldType.LONG)
    private long id;

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
