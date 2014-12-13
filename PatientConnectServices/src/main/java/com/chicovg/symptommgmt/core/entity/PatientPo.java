package com.chicovg.symptommgmt.core.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by victorguthrie on 10/11/14.
 */
@Entity
public class PatientPo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdtDatetime;

    private String username;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String emailAddress;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfBirth;
    private long points;
    private long pointsThisMonth;
    private long pointsThisWeek;
    private int checkInStreak;
    private String lastPainLevel = "NONE";
    private Date lastPainLevelReportedDtm;
    private Date lastPainLevelChangedDtm;
    private Date lastReportedUnableToEat;

    @OneToMany(cascade = CascadeType.ALL)
    private List<PatientCheckinPo> patientCheckinPoList;

    @OneToMany(cascade = CascadeType.ALL)
    private List<PatientReminderPo> patientReminderPoList;

    @ManyToOne
    private DoctorPo doctorPo;

    @OneToMany(cascade = CascadeType.ALL)
    private List<PatientMedicationPo> patientMedicationPoList;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getLastUpdtDatetime() {
        return lastUpdtDatetime;
    }

    public void setLastUpdtDatetime(Date lastUpdtDatetime) {
        this.lastUpdtDatetime = lastUpdtDatetime;
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

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    public long getPointsThisMonth() {
        return pointsThisMonth;
    }

    public void setPointsThisMonth(long pointsThisMonth) {
        this.pointsThisMonth = pointsThisMonth;
    }

    public long getPointsThisWeek() {
        return pointsThisWeek;
    }

    public void setPointsThisWeek(long pointsThisWeek) {
        this.pointsThisWeek = pointsThisWeek;
    }

    public int getCheckInStreak() {
        return checkInStreak;
    }

    public void setCheckInStreak(int checkInStreak) {
        this.checkInStreak = checkInStreak;
    }

    public String getLastPainLevel() {
        return lastPainLevel;
    }

    public void setLastPainLevel(String lastPainLevel) {
        this.lastPainLevel = lastPainLevel;
    }

    public Date getLastPainLevelReportedDtm() {
        return lastPainLevelReportedDtm;
    }

    public void setLastPainLevelReportedDtm(Date lastPainLevelReportedDtm) {
        this.lastPainLevelReportedDtm = lastPainLevelReportedDtm;
    }

    public List<PatientCheckinPo> getPatientCheckinPoList() {
        return patientCheckinPoList;
    }

    public void setPatientCheckinPoList(List<PatientCheckinPo> patientCheckinPoList) {
        this.patientCheckinPoList = patientCheckinPoList;
    }

    public List<PatientReminderPo> getPatientReminderPoList() {
        return patientReminderPoList;
    }

    public void setPatientReminderPoList(List<PatientReminderPo> patientReminderPoList) {
        this.patientReminderPoList = patientReminderPoList;
    }

    public DoctorPo getDoctorPo() {
        return doctorPo;
    }

    public void setDoctorPo(DoctorPo doctorPo) {
        this.doctorPo = doctorPo;
    }

    public List<PatientMedicationPo> getPatientMedicationPoList() {
        return patientMedicationPoList;
    }

    public void setPatientMedicationPoList(List<PatientMedicationPo> patientMedicationPoList) {
        this.patientMedicationPoList = patientMedicationPoList;
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

    public void addCheckIn(PatientCheckinPo checkinPo){
        if(null==this.patientCheckinPoList){
            this.patientCheckinPoList = new LinkedList<>();
        }
        checkinPo.setPatientPo(this);
        this.patientCheckinPoList.add(checkinPo);
    }

    public void addReminder(PatientReminderPo reminderPo){
        if(null==this.patientReminderPoList){
            this.patientReminderPoList = new LinkedList<>();
        }
        reminderPo.setPatientPo(this);
        this.patientReminderPoList.add(reminderPo);
    }

    public void addMedication(PatientMedicationPo medicationPo){
        if(null==this.patientMedicationPoList){
            this.patientMedicationPoList = new LinkedList<>();
        }
        medicationPo.setPatientPo(this);
        this.patientMedicationPoList.add(medicationPo);
    }
}
