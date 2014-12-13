package com.chicovg.symptommgmt.rest.domain;

import com.chicovg.symptommgmt.rest.annotations.BundleField;
import com.chicovg.symptommgmt.rest.annotations.BundleFieldType;
import com.chicovg.symptommgmt.rest.api.SymptomMgmtApi;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

/**
 * Created by victorguthrie on 10/29/14.
 *
 * This object represents a patient user of the application
 *
 */
public class Patient {

    @BundleField(type= BundleFieldType.LONG)
    private long id;
    @BundleField(type= BundleFieldType.LONG)
    private long clientId;
    @BundleField(type= BundleFieldType.LONG)
    private long doctorId;
    @BundleField(type = BundleFieldType.DATE)
    @JsonFormat(pattern = SymptomMgmtApi.DATETIME_FORMAT)
    private Date lastUpdtDatetime;
    @BundleField(type= BundleFieldType.STRING)
    private String username;
    @BundleField(type= BundleFieldType.STRING)
    private String firstName;
    @BundleField(type= BundleFieldType.STRING)
    private String lastName;
    @BundleField(type= BundleFieldType.STRING)
    private String phoneNumber;
    @BundleField(type= BundleFieldType.STRING)
    private String emailAddress;
    @BundleField(type = BundleFieldType.DATE)
    @JsonFormat(pattern = SymptomMgmtApi.DATETIME_FORMAT)
    private Date dateOfBirth;
    @BundleField(type= BundleFieldType.LONG)
    private long points;
    @BundleField(type= BundleFieldType.INTEGER)
    private int checkInStreak;
    @BundleField(type= BundleFieldType.LONG)
    private long pointsThisWeek;
    @BundleField(type= BundleFieldType.LONG)
    private long pointsThisMonth;
    @BundleField(type = BundleFieldType.ENUM)
    private PainLevel lastPainLevel;
    @BundleField(type = BundleFieldType.DATE)
    @JsonFormat(pattern = SymptomMgmtApi.DATETIME_FORMAT)
    private Date lastPainLevelReportedDtm;
    @BundleField(type = BundleFieldType.DATE)
    @JsonFormat(pattern = SymptomMgmtApi.DATETIME_FORMAT)
    private Date lastPainLevelChangedDtm;
    @BundleField(type = BundleFieldType.DATE)
    @JsonFormat(pattern = SymptomMgmtApi.DATETIME_FORMAT)
    private Date lastReportedUnableToEat;

    private Doctor doctor;
    private List<CheckIn> checkIns;
    private List<Reminder> reminders;
    private List<Medication> medications;

    public Patient() {
        this.lastPainLevel = PainLevel.NONE;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    public int getCheckInStreak() {
        return checkInStreak;
    }

    public void setCheckInStreak(int checkInStreak) {
        this.checkInStreak = checkInStreak;
    }

    public long getPointsThisWeek() {
        return pointsThisWeek;
    }

    public void setPointsThisWeek(long pointsThisWeek) {
        this.pointsThisWeek = pointsThisWeek;
    }

    public long getPointsThisMonth() {
        return pointsThisMonth;
    }

    public Date getLastUpdtDatetime() {
        return lastUpdtDatetime;
    }

    public void setLastUpdtDatetime(Date lastUpdtDatetime) {
        this.lastUpdtDatetime = lastUpdtDatetime;
    }

    public void setPointsThisMonth(long pointsThisMonth) {
        this.pointsThisMonth = pointsThisMonth;
    }

    public List<CheckIn> getCheckIns() {
        return checkIns;
    }

    public void setCheckIns(List<CheckIn> checkIns) {
        this.checkIns = checkIns;
    }

    public List<Reminder> getReminders() {
        return reminders;
    }

    public void setReminders(List<Reminder> reminders) {
        this.reminders = reminders;
    }

    public List<Medication> getMedications() {
        return medications;
    }

    public void setMedications(List<Medication> medications) {
        this.medications = medications;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public PainLevel getLastPainLevel() {
        return lastPainLevel;
    }

    public void setLastPainLevel(PainLevel lastPainLevel) {
        this.lastPainLevel = lastPainLevel;
    }

    public Date getLastPainLevelReportedDtm() {
        return lastPainLevelReportedDtm;
    }

    public void setLastPainLevelReportedDtm(Date lastPainLevelReportedDtm) {
        this.lastPainLevelReportedDtm = lastPainLevelReportedDtm;
    }

    public long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(long doctorId) {
        this.doctorId = doctorId;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
        this.doctorId = doctor.getId();
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Date getLastPainLevelChangedDtm() {
        return lastPainLevelChangedDtm;
    }

    public void setLastPainLevelChangedDtm(Date lastPainLevelChangedDtm) {
        this.lastPainLevelChangedDtm = lastPainLevelChangedDtm;
    }

    public Date getLastReportedUnableToEat() {
        return lastReportedUnableToEat;
    }

    public void setLastReportedUnableToEat(Date lastReportedUnableToEat) {
        this.lastReportedUnableToEat = lastReportedUnableToEat;
    }
}
