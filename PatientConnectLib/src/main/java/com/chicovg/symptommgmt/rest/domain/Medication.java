package com.chicovg.symptommgmt.rest.domain;

import com.chicovg.symptommgmt.rest.annotations.BundleField;
import com.chicovg.symptommgmt.rest.annotations.BundleFieldType;

/**
 * Created by victorguthrie on 11/15/14.
 */
public class Medication {

    @BundleField(type = BundleFieldType.LONG)
    private long id;
    @BundleField(type = BundleFieldType.LONG)
    private long clientId;
    @BundleField(type = BundleFieldType.LONG)
    private long patientId;
    @BundleField(type = BundleFieldType.STRING)
    private String name;
    @BundleField(type = BundleFieldType.INTEGER)
    private int dosage;
    @BundleField(type = BundleFieldType.STRING)
    private String dosageUnit;
    @BundleField(type = BundleFieldType.STRING)
    private String instructions;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public void setDosage(int dosage) {
        this.dosage = dosage;
    }

    public int getDosage() {
        return dosage;
    }

    public String getDosageUnit() {
        return dosageUnit;
    }

    public void setDosageUnit(String dosageUnit) {
        this.dosageUnit = dosageUnit;
    }
}
