package com.chicovg.symptommgmt.core.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by victorguthrie on 10/11/14.
 */
@Entity
public class PatientCheckinPo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;
    private String status;
    private String overallPainLevel;
    private boolean unableToEat;

    @OneToMany(cascade = CascadeType.ALL)
    private List<PatientCheckinResponsePo> patientCheckinResponsePoList;

    @ManyToOne
    private PatientPo patientPo;

    public PatientCheckinPo() {
    }

    public PatientCheckinPo(Date timestamp, String status, String overallPainLevel) {
        this.timestamp = timestamp;
        this.status = status;
        this.overallPainLevel = overallPainLevel;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<PatientCheckinResponsePo> getPatientCheckinResponsePoList() {
        return patientCheckinResponsePoList;
    }

    public void setPatientCheckinResponsePoList(List<PatientCheckinResponsePo> patientCheckinResponsePoList) {
        this.patientCheckinResponsePoList = patientCheckinResponsePoList;
    }

    public PatientPo getPatientPo() {
        return patientPo;
    }

    public void setPatientPo(PatientPo patientPo) {
        this.patientPo = patientPo;
    }

    public String getOverallPainLevel() {
        return overallPainLevel;
    }

    public void setOverallPainLevel(String overallPainLevel) {
        this.overallPainLevel = overallPainLevel;
    }

    public boolean isUnableToEat() {
        return unableToEat;
    }

    public void setUnableToEat(boolean unableToEat) {
        this.unableToEat = unableToEat;
    }

    public void addResponse(PatientCheckinResponsePo responsePo){
        if(null==this.patientCheckinResponsePoList){
            this.patientCheckinResponsePoList = new LinkedList<>();
        }
        responsePo.setPatientCheckinPo(this);
        this.patientCheckinResponsePoList.add(responsePo);
    }

}
