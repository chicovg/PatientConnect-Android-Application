package com.chicovg.symptommgmt.core.entity;

import javax.persistence.*;

/**
 * Created by victorguthrie on 10/11/14.
 */
@Entity
public class PatientMedicationPo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;
    private int dosage;
    private String dosageUnit;
    private String instructions;

    @ManyToOne
    private PatientPo patientPo;

    public PatientMedicationPo() {
    }

    public PatientMedicationPo(String name, int dosage, String dosageUnit, String instructions) {
        this.name = name;
        this.dosage = dosage;
        this.dosageUnit = dosageUnit;
        this.instructions = instructions;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDosage() {
        return dosage;
    }

    public void setDosage(int dosage) {
        this.dosage = dosage;
    }

    public String getDosageUnit() {
        return dosageUnit;
    }

    public void setDosageUnit(String dosageUnit) {
        this.dosageUnit = dosageUnit;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public PatientPo getPatientPo() {
        return patientPo;
    }

    public void setPatientPo(PatientPo patientPo) {
        this.patientPo = patientPo;
    }

}
