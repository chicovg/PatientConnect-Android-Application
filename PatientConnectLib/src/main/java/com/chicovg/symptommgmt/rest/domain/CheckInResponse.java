package com.chicovg.symptommgmt.rest.domain;

import com.chicovg.symptommgmt.rest.annotations.BundleField;
import com.chicovg.symptommgmt.rest.annotations.BundleFieldType;

/**
 * Created by victorguthrie on 11/16/14.
 */
public class CheckInResponse {

    @BundleField(type = BundleFieldType.LONG)
    private long id;
    @BundleField(type = BundleFieldType.LONG)
    private long clientId;
    @BundleField(type = BundleFieldType.LONG)
    private long checkInId;
    @BundleField(type = BundleFieldType.INTEGER)
    private int questionId;
    @BundleField(type = BundleFieldType.STRING)
    private String medication;
    @BundleField(type = BundleFieldType.OBJECT)
    private MedicationTaken medicationTaken;
    @BundleField(type = BundleFieldType.STRING)
    private String response;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getMedication() {
        return medication;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public long getCheckInId() {
        return checkInId;
    }

    public void setCheckInId(long checkInId) {
        this.checkInId = checkInId;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public MedicationTaken getMedicationTaken() {
        return medicationTaken;
    }

    public void setMedicationTaken(MedicationTaken medicationTaken) {
        this.medicationTaken = medicationTaken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CheckInResponse that = (CheckInResponse) o;

        if (questionId != that.questionId) return false;
        if (medication != null ? !medication.equals(that.medication) : that.medication != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = questionId;
        result = 31 * result + (medication != null ? medication.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CheckInResponse{" +
                "id=" + id +
                ", checkInId=" + checkInId +
                ", questionId=" + questionId +
                ", medication='" + medication + '\'' +
                ", response='" + response + '\'' +
                '}';
    }
}
