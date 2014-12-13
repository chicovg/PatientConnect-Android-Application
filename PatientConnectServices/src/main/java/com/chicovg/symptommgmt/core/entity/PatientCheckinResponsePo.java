package com.chicovg.symptommgmt.core.entity;

import javax.persistence.*;

/**
 * Created by victorguthrie on 10/11/14.
 */
@Entity
public class PatientCheckinResponsePo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private int questionId;
    private String medication;
    private String response;

    @OneToOne(cascade = CascadeType.ALL)
    private PatientMedicationTakenPo medicationTakenPo;

    public PatientCheckinResponsePo() {
    }

    public PatientCheckinResponsePo(int questionId, String response) {
        this.questionId = questionId;
        this.response = response;
    }

    public PatientCheckinResponsePo(int questionId, String response, String medication) {
        this.questionId = questionId;
        this.medication = medication;
        this.response = response;
    }

    public PatientCheckinResponsePo(int questionId, String response, PatientMedicationTakenPo medicationTakenPo) {
        this.questionId = questionId;
        this.setMedicationTakenPo(medicationTakenPo);
        this.medication = medicationTakenPo.getMedicationName();
        this.response = response;
    }

    @ManyToOne
    private PatientCheckinPo patientCheckinPo;

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

    public PatientCheckinPo getPatientCheckinPo() {
        return patientCheckinPo;
    }

    public void setPatientCheckinPo(PatientCheckinPo patientCheckinPo) {
        this.patientCheckinPo = patientCheckinPo;
    }

    public PatientMedicationTakenPo getMedicationTakenPo() {
        return medicationTakenPo;
    }

    public void setMedicationTakenPo(PatientMedicationTakenPo medicationTakenPo) {
        if(null!=medicationTakenPo){
            medicationTakenPo.setPatientCheckinResponsePo(this);
        }
        this.medicationTakenPo = medicationTakenPo;
    }
}
