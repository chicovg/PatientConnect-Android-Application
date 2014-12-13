package com.chicovg.symptommgmt.rest.domain;

import com.chicovg.symptommgmt.rest.annotations.BundleField;
import com.chicovg.symptommgmt.rest.annotations.BundleFieldType;

/**
 * Created by victorguthrie on 11/11/14.
 */
public class Reminder {

    public static final String ACTIVE_YES = "Y";
    public static final String ACTIVE_NO = "N";

    @BundleField(type = BundleFieldType.LONG)
    private long id;
    @BundleField(type = BundleFieldType.LONG)
    private long clientId;
    @BundleField(type = BundleFieldType.LONG)
    private long patientId;
    @BundleField(type = BundleFieldType.INTEGER)
    private int hour; // (0-23)
    @BundleField(type = BundleFieldType.INTEGER)
    private int minute; // (0-59)
    @BundleField(type = BundleFieldType.STRING)
    private String active;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public boolean isActive(){
        return ACTIVE_YES.equals(this.active);
    }
}
