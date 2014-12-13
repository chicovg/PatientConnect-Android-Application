package com.chicovg.symptommgmt.core.service;

import com.chicovg.symptommgmt.core.entity.*;
import com.chicovg.symptommgmt.rest.domain.*;
import org.springframework.beans.BeanUtils;

import java.util.LinkedList;

import static com.chicovg.symptommgmt.core.service.DoctorTranslator.toDoctorNoPatients;
import static com.chicovg.symptommgmt.core.service.DoctorTranslator.toDoctorPo;

/**
 * Created by victorguthrie on 11/15/14.
 */
public class PatientTranslator {

    /* to Entity */
    public static PatientPo toPatientPo(Patient patient) {
        if (null != patient) {
            PatientPo po = new PatientPo();

            BeanUtils.copyProperties(patient, po);
            po.setLastPainLevel(patient.getLastPainLevel().toString());
            po.setDoctorPo(toDoctorPo(patient.getDoctor()));
            if(null!=patient.getCheckIns() && patient.getCheckIns().size()>0){
                for(CheckIn checkIn : patient.getCheckIns()){
                    po.addCheckIn(toPatientCheckinPo(checkIn));
                }
            }
            if(null!=patient.getReminders() && patient.getReminders().size()>0){
                for(Reminder reminder: patient.getReminders()){
                    po.addReminder(toPatientReminderPo(reminder));
                }
            }
            if(null!=patient.getMedications() && patient.getMedications().size()>0){
                for(Medication medication : patient.getMedications()){
                    po.addMedication(toPatientMedicationPo(medication));
                }
            }
            return po;
        }
        return null;
    }

    public static PatientCheckinPo toPatientCheckinPo(CheckIn checkIn) {
        if (null != checkIn) {
            PatientCheckinPo po = new PatientCheckinPo();

            BeanUtils.copyProperties(checkIn, po);
            po.setStatus(checkIn.getStatus().toString());
            po.setOverallPainLevel(checkIn.getOverallPainLevel().name());
            if(null!=checkIn.getResponses() && checkIn.getResponses().size()>0){
                for(CheckInResponse response : checkIn.getResponses()){
                    po.addResponse(toPatientCheckinResponsePo(response));
                }
            }
            return po;
        }
        return null;
    }

    public static PatientCheckinResponsePo toPatientCheckinResponsePo(CheckInResponse checkInResponse) {
        if (null != checkInResponse) {
            PatientCheckinResponsePo po = new PatientCheckinResponsePo();

            BeanUtils.copyProperties(checkInResponse, po);
            if(null!=checkInResponse.getMedicationTaken())
                po.setMedicationTakenPo(toPatientMedicationTakenPo(checkInResponse.getMedicationTaken()));
            return po;
        }
        return null;
    }

    public static PatientReminderPo toPatientReminderPo(Reminder reminder) {
        if (null != reminder) {
            PatientReminderPo po = new PatientReminderPo();

            BeanUtils.copyProperties(reminder, po);
            return po;
        }
        return null;
    }

    public static PatientMedicationPo toPatientMedicationPo(Medication medication){
        if(null!=medication){
            PatientMedicationPo po = new PatientMedicationPo();

            BeanUtils.copyProperties(medication, po);
            return po;
        }
        return null;
    }

    public static PatientMedicationTakenPo toPatientMedicationTakenPo(MedicationTaken medicationTaken){
        if(null!=medicationTaken){
            PatientMedicationTakenPo po = new PatientMedicationTakenPo();

            BeanUtils.copyProperties(medicationTaken, po);
            return po;
        }
        return null;
    }


    /* to Domain */
    public static Patient toPatient(PatientPo po){
        if(null!=po){
            Patient patient = new Patient();

            BeanUtils.copyProperties(po, patient);
            patient.setLastPainLevel(PainLevel.valueOf(po.getLastPainLevel()));
            patient.setDoctor(toDoctorNoPatients(po.getDoctorPo()));
            patient.setCheckIns(new LinkedList<CheckIn>());
            if(null!=po.getPatientCheckinPoList() && po.getPatientCheckinPoList().size()>0){
                for(PatientCheckinPo checkinPo : po.getPatientCheckinPoList()) {
                    CheckIn checkIn = toCheckIn(checkinPo);
                    checkIn.setPatientId(po.getId());
                    patient.getCheckIns().add(checkIn);
                }
            }
            patient.setReminders(new LinkedList<Reminder>());
            if (null!=po.getPatientReminderPoList() && po.getPatientReminderPoList().size()>0){
                for(PatientReminderPo reminderPo : po.getPatientReminderPoList()) {
                    Reminder reminder = toReminder(reminderPo);
                    reminder.setPatientId(po.getId());
                    patient.getReminders().add(reminder);
                }
            }
            patient.setMedications(new LinkedList<Medication>());
            if(null!=po.getPatientMedicationPoList() && po.getPatientMedicationPoList().size()>0){
                for(PatientMedicationPo medicationPo : po.getPatientMedicationPoList()){
                    Medication medication = toMedication(medicationPo);
                    medication.setPatientId(patient.getId());
                    patient.getMedications().add(medication);
                }
            }

            return patient;
        }
        return null;
    }

    public static CheckIn toCheckIn(PatientCheckinPo po){
        if(null!= po){
            CheckIn checkIn = new CheckIn();

            BeanUtils.copyProperties(po, checkIn);
            checkIn.setPatientId(po.getPatientPo().getId());
            checkIn.setStatus(CheckInStatus.valueOf(po.getStatus()));
            checkIn.setOverallPainLevel(PainLevel.valueOf(po.getOverallPainLevel()));
            checkIn.setResponses(new LinkedList<CheckInResponse>());
            if(null!=po.getPatientCheckinResponsePoList() && po.getPatientCheckinResponsePoList().size()>0){
                for(PatientCheckinResponsePo responsePo: po.getPatientCheckinResponsePoList()){
                    CheckInResponse response = toCheckInResponse(responsePo);
                    response.setCheckInId(po.getId());
                    checkIn.getResponses().add(response);
                }
            }
            return checkIn;
        }
        return null;
    }

    public static CheckInResponse toCheckInResponse(PatientCheckinResponsePo po){
        if(null!=po){
            CheckInResponse response = new CheckInResponse();

            BeanUtils.copyProperties(po, response);
            if(null!=po.getMedicationTakenPo()){
                MedicationTaken medicationTaken = toMedicationTaken(po.getMedicationTakenPo());
                medicationTaken.setPatientId(po.getPatientCheckinPo().getPatientPo().getId());
                medicationTaken.setCheckInResponseId(po.getId());
                response.setMedicationTaken(medicationTaken);
            }

            return response;
        }
        return null;
    }

    public static Reminder toReminder(PatientReminderPo po){
        if(null!= po){
            Reminder reminder = new Reminder();

            BeanUtils.copyProperties(po, reminder);
            reminder.setPatientId(po.getPatientPo().getId());
            return reminder;
        }
        return null;
    }

    public static Medication toMedication(PatientMedicationPo po){
        if(null!=po){
            Medication medication = new Medication();

            BeanUtils.copyProperties(po, medication);
            return medication;
        }
        return null;
    }

    public static MedicationTaken toMedicationTaken(PatientMedicationTakenPo po){
        if(null!=po){
            MedicationTaken medicationTaken = new MedicationTaken();

            BeanUtils.copyProperties(po, medicationTaken);
            return medicationTaken;
        }
        return null;
    }

}
