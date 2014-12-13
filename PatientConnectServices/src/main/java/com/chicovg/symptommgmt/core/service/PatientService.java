package com.chicovg.symptommgmt.core.service;

import com.chicovg.symptommgmt.core.entity.*;
import com.chicovg.symptommgmt.core.repository.*;
import com.chicovg.symptommgmt.rest.domain.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static com.chicovg.symptommgmt.core.service.PatientTranslator.*;

/**
 * Created by victorguthrie on 10/29/14.
 */
@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientCheckinRepository checkinRepository;

    @Autowired
    private MedicationTakenRepository medicationTakenRepository;

    @Autowired
    private MedicationRepository medicationRepository;

    @Autowired
    private ReminderRepository reminderRepository;

    public Patient savePatient(Patient patient){
        DoctorPo doctorPo = doctorRepository.findOne(patient.getDoctorId());
        PatientPo po = toPatientPo(patient);
        po.setDoctorPo(doctorPo);
        po.setLastUpdtDatetime(new Date());
        po = patientRepository.save(po);
        return toPatient(po);
    }

    public Patient updatePatient(Patient existing, Patient update){
        PatientPo existingPo = toPatientPo(existing);
        if(update.getLastUpdtDatetime().after(existing.getLastUpdtDatetime())){
            BeanUtils.copyProperties(update, existingPo);
            existingPo.setLastUpdtDatetime(new Date());
            existingPo = patientRepository.save(existingPo);
            BeanUtils.copyProperties(existingPo, update);
        }
        return update;
    }

    public Patient findPatientById(Long id){
        PatientPo po = patientRepository.findOne(id);
        return toPatient(po);
    }

    public CheckIn saveCheckIn(Patient patient, CheckIn checkIn) {
        PatientPo patientPo = toPatientPo(patient);
        if (null != patientPo) {
            PatientCheckinPo po = toPatientCheckinPo(checkIn);
            po.setPatientPo(patientPo);

            if(!patientPo.getLastPainLevel().equals(po.getOverallPainLevel())){
                patientPo.setLastPainLevelChangedDtm(po.getTimestamp());
            }
            patientPo.setLastPainLevel(po.getOverallPainLevel());
            patientPo.setLastPainLevelReportedDtm(po.getTimestamp());

            if(po.isUnableToEat()){
                patientPo.setLastReportedUnableToEat(po.getTimestamp());
            } else {
                patientPo.setLastReportedUnableToEat(null);
            }

            patientPo.addCheckIn(po);
            patientRepository.save(patientPo);
            return toCheckIn(po);
        }
        return null;
    }

    public MedicationTaken saveMedicationTaken(Patient patient, MedicationTaken medicationTaken) {
        PatientPo patientPo = toPatientPo(patient);
        if(null!=patientPo){
            PatientMedicationTakenPo po = toPatientMedicationTakenPo(medicationTaken);

            po = medicationTakenRepository.save(po);
            return toMedicationTaken(po);
        }
        return null;
    }

    public Medication findMedicationById(Long id) {
        PatientMedicationPo po = medicationRepository.findOne(id);
        return toMedication(po);
    }

    public Medication saveMedication(Patient patient, Medication medication) {
        PatientPo patientPo = toPatientPo(patient);
        if(null!=patientPo){
            PatientMedicationPo po = toPatientMedicationPo(medication);
            po.setPatientPo(patientPo);

            po = medicationRepository.save(po);
            return toMedication(po);
        }
        return null;
    }

    public void deleteMedication(Long id){
        medicationRepository.delete(id);
    }

    public Reminder findReminderById(Long id){
        PatientReminderPo po = reminderRepository.findOne(id);
        return toReminder(po);
    }

    public Reminder saveReminder(Patient patient, Reminder reminder){
        PatientPo patientPo = toPatientPo(patient);
        if(null!=patientPo){
            PatientReminderPo po = toPatientReminderPo(reminder);
            po.setPatientPo(patientPo);

            po = reminderRepository.save(po);
            return toReminder(po);
        }
        return null;
    }


    public List<CheckIn> findCheckInByPatientNameLike(String searchString){
        List<CheckIn> checkIns = new LinkedList<>();
        Collection<PatientPo> patientPos = patientRepository.findByFirstNameContainingOrLastNameContaining(searchString, searchString);
        for(PatientPo po : patientPos){
            for(PatientCheckinPo checkinPo: po.getPatientCheckinPoList()){
                CheckIn checkIn = toCheckIn(checkinPo);
                checkIn.setPatientId(po.getId());
                checkIn.setPatientFirstName(po.getFirstName());
                checkIn.setPatientLastName(po.getLastName());
                checkIns.add(checkIn);
            }
        }
        return checkIns;
    }
}
