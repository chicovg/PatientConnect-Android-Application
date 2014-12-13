package com.chicovg.symptommgmt.rest.domain;

import com.chicovg.symptommgmt.rest.annotations.BundleField;
import com.chicovg.symptommgmt.rest.annotations.BundleFieldType;
import com.chicovg.symptommgmt.rest.api.SymptomMgmtApi;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

/**
 * Created by victorguthrie on 11/11/14.
 */
public class CheckIn {

    public static final int OVERALL_PAIN_QUESTION_ID = 1;
    public static final int GENERAL_MEDICATION_QUESTION_ID = 2;
    public static final int SPECIFIC_MEDICATION_QUESTION_ID = 3;
    public static final int EATING_DRINKING_QUESTION_ID = 4;

    @BundleField(type = BundleFieldType.LONG)
    private long id;
    @BundleField(type = BundleFieldType.LONG)
    private long clientId;
    @BundleField(type = BundleFieldType.LONG)
    private long patientId;
    @BundleField(type = BundleFieldType.STRING)
    private String patientFirstName;
    @BundleField(type = BundleFieldType.STRING)
    private String  patientLastName;
    @JsonFormat(pattern = SymptomMgmtApi.DATETIME_FORMAT)
    @BundleField(type = BundleFieldType.DATETIME)
    private Date timestamp;
    @BundleField(type = BundleFieldType.ENUM)
    private CheckInStatus status;
    @BundleField(type = BundleFieldType.ENUM)
    private PainLevel overallPainLevel;
    @BundleField(type = BundleFieldType.BOOLEAN)
    private boolean unableToEat;
    @BundleField(type = BundleFieldType.COLLECTION)
    private List<CheckInResponse> responses;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public CheckInStatus getStatus() {
        return status;
    }

    public void setStatus(CheckInStatus status) {
        this.status = status;
    }

    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public PainLevel getOverallPainLevel() {
        return overallPainLevel;
    }

    public void setOverallPainLevel(PainLevel overallPainLevel) {
        this.overallPainLevel = overallPainLevel;
    }

    public List<CheckInResponse> getResponses() {
        return responses;
    }

    public void setResponses(List<CheckInResponse> responses) {
        this.responses = responses;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public String getPatientFirstName() {
        return patientFirstName;
    }

    public void setPatientFirstName(String patientFirstName) {
        this.patientFirstName = patientFirstName;
    }

    public String getPatientLastName() {
        return patientLastName;
    }

    public void setPatientLastName(String patientLastName) {
        this.patientLastName = patientLastName;
    }

    public boolean isUnableToEat() {
        return unableToEat;
    }

    public void setUnableToEat(boolean unableToEat) {
        this.unableToEat = unableToEat;
    }
}
