package com.chicovg.symptommgmt.core.entity;

import javax.persistence.*;

/**
 * Created by victorguthrie on 11/11/14.
 */
@Entity
public class PatientReminderPo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private int hour; // (0-23)
    private int minute; // (0-59)

    @ManyToOne
    private PatientPo patientPo;

    public PatientReminderPo() {
    }

    public PatientReminderPo(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public PatientPo getPatientPo() {
        return patientPo;
    }

    public void setPatientPo(PatientPo patientPo) {
        this.patientPo = patientPo;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}
