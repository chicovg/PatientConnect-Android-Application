package com.chicovg.symptommgmt.rest.domain;

import com.chicovg.symptommgmt.rest.annotations.BundleField;
import com.chicovg.symptommgmt.rest.annotations.BundleFieldType;
import com.chicovg.symptommgmt.rest.api.SymptomMgmtApi;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

/**
 * Created by victorguthrie on 10/29/14.
 */
public class Doctor {

    @BundleField(type= BundleFieldType.LONG)
    private long id;
    @BundleField(type= BundleFieldType.LONG)
    private long clientId;
    @JsonFormat(pattern = SymptomMgmtApi.DATETIME_FORMAT)
    private Date lastUpdtDatetime;
    @BundleField(type= BundleFieldType.STRING)
    private String username;
    @BundleField(type= BundleFieldType.STRING)
    private String firstName;
    @BundleField(type= BundleFieldType.STRING)
    private String lastName;
    @BundleField(type= BundleFieldType.STRING)
    private String title;
    @BundleField(type= BundleFieldType.STRING)
    private String phoneNumber;
    @BundleField(type= BundleFieldType.STRING)
    private String emailAddress;

    private List<Patient> patients;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public void setPatients(List<Patient> patients) {
        this.patients = patients;
    }

    public Date getLastUpdtDatetime() {
        return lastUpdtDatetime;
    }

    public void setLastUpdtDatetime(Date lastUpdtDatetime) {
        this.lastUpdtDatetime = lastUpdtDatetime;
    }
}
