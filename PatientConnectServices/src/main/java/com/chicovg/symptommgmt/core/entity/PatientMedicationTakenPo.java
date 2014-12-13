package com.chicovg.symptommgmt.core.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by victorguthrie on 11/25/14.
 */
@Entity
public class PatientMedicationTakenPo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;
    private String medicationName;

    @OneToOne(cascade = CascadeType.ALL)
    private PatientCheckinResponsePo patientCheckinResponsePo;

    public PatientMedicationTakenPo() {
    }

    public PatientMedicationTakenPo(Date timestamp, String medicationName) {
        this.timestamp = timestamp;
        this.medicationName = medicationName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public PatientCheckinResponsePo getPatientCheckinResponsePo() {
        return patientCheckinResponsePo;
    }

    public void setPatientCheckinResponsePo(PatientCheckinResponsePo patientCheckinResponsePo) {
        this.patientCheckinResponsePo = patientCheckinResponsePo;
    }
}
