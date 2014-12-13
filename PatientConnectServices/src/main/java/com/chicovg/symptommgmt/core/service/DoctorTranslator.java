package com.chicovg.symptommgmt.core.service;

import com.chicovg.symptommgmt.core.entity.DoctorPo;
import com.chicovg.symptommgmt.core.entity.PatientPo;
import com.chicovg.symptommgmt.rest.domain.Doctor;
import com.chicovg.symptommgmt.rest.domain.Patient;
import org.springframework.beans.BeanUtils;

import java.util.LinkedList;

import static com.chicovg.symptommgmt.core.service.PatientTranslator.toPatient;
import static com.chicovg.symptommgmt.core.service.PatientTranslator.toPatientPo;

/**
 * Created by victorguthrie on 11/22/14.
 */
public class DoctorTranslator {

    /* To Entity */
    public static DoctorPo toDoctorPo(Doctor doctor) {
        if (null != doctor) {
            DoctorPo po = new DoctorPo();

            BeanUtils.copyProperties(doctor, po);
            if(null!=doctor.getPatients() && doctor.getPatients().size()>0){
                for(Patient patient : doctor.getPatients()){
                    po.addPatient(toPatientPo(patient));
                }
            }
            return po;
        }
        return null;
    }

    public static Doctor toDoctor(DoctorPo po){
        if(null!=po){
            Doctor doctor = new Doctor();

            BeanUtils.copyProperties(po, doctor);
            doctor.setPatients(new LinkedList<Patient>());
            if(null!=po.getPatientPoList() == po.getPatientPoList().size()>0){
                for(PatientPo patientPo : po.getPatientPoList()){
                    doctor.getPatients().add(toPatient(patientPo));
                }
            }
            return doctor;
        }
        return null;
    }

    public static Doctor toDoctorNoPatients(DoctorPo po){
        if(null!=po){
            Doctor doctor = new Doctor();

            BeanUtils.copyProperties(po, doctor);
            return doctor;
        }
        return null;
    }

}
