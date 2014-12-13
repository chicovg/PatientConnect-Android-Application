package com.chicovg.symptommgmt.core.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by victorguthrie on 10/11/14.
 */
@Entity
public class DoctorPo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdtDatetime;

    private String username;
    private String firstName;
    private String lastName;
    private String title;
    private String phoneNumber;
    private String emailAddress;

    @OneToMany(cascade = CascadeType.ALL)
    private List<PatientPo> patientPoList;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public List<PatientPo> getPatientPoList() {
        return patientPoList;
    }

    public void setPatientPoList(List<PatientPo> patientPoList) {
        this.patientPoList = patientPoList;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getLastUpdtDatetime() {
        return lastUpdtDatetime;
    }

    public void setLastUpdtDatetime(Date lastUpdtDatetime) {
        this.lastUpdtDatetime = lastUpdtDatetime;
    }

    public void addPatient(PatientPo patientPo){
        if(null==this.patientPoList){
            this.patientPoList = new LinkedList<>();
        }
        patientPo.setDoctorPo(this);
        this.patientPoList.add(patientPo);
    }
}
