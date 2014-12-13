package com.chicovg.symptommgmt.rest.domain;

import com.chicovg.symptommgmt.rest.annotations.BundleField;
import com.chicovg.symptommgmt.rest.annotations.BundleFieldType;
import com.chicovg.symptommgmt.rest.api.SymptomMgmtApi;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * Created by victorguthrie on 11/25/14.
 */
public class MedicationTaken {

    @BundleField(type = BundleFieldType.LONG)
    private long id;
    @BundleField(type = BundleFieldType.LONG)
    private long clientId;
    @BundleField(type = BundleFieldType.LONG)
    private long patientId;
    @BundleField(type = BundleFieldType.LONG)
    private long checkInResponseId;
    @BundleField(type = BundleFieldType.STRING)
    private String medicationName;
    @BundleField(type = BundleFieldType.DATETIME)
    @JsonFormat(pattern = SymptomMgmtApi.DATETIME_FORMAT)
    private Date timestamp;

    public MedicationTaken() {
    }

    public MedicationTaken(long patientId, String medicationName, Date timestamp) {
        this.patientId = patientId;
        this.medicationName = medicationName;
        this.timestamp = timestamp;
    }

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

    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public long getCheckInResponseId() {
        return checkInResponseId;
    }

    public void setCheckInResponseId(long checkInResponseId) {
        this.checkInResponseId = checkInResponseId;
    }
}
