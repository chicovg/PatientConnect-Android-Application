package com.chicovg.symptommgmt.rest.controller;

import com.chicovg.symptommgmt.core.service.PatientService;
import com.chicovg.symptommgmt.rest.api.SymptomMgmtApi;
import com.chicovg.symptommgmt.rest.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.annotation.security.PermitAll;
import java.util.Collection;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Created by victorguthrie on 10/16/14.
 */
@Controller
@RequestMapping(SymptomMgmtApi.PATIENT_PATH)
public class PatientController {

    @Autowired
    private PatientService patientService;

    @RequestMapping(method = POST)
    @PermitAll
    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {
        Patient newPatient = patientService.savePatient(patient);
        return new ResponseEntity<>(newPatient, HttpStatus.OK);
    }

    @RequestMapping(method = GET)
    public @ResponseBody Collection<Patient> listPatients() {
        throw new NotImplementedException();
    }


    @RequestMapping(value = "/{id}", method = GET)
    public ResponseEntity<Patient> getPatient(@PathVariable Long id) {
        Patient patient = patientService.findPatientById(id);
        if(null==patient){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(patient, HttpStatus.OK);
    }

    /*@RequestMapping(value = "/{id}"+SymptomMgmtApi.UPDATES_PATH, method = GET)
    public ResponseEntity<Patient> getPatientUpdates(@PathVariable Long id,
            @RequestParam(SymptomMgmtApi.LAST_UPDT_DTM_PARAM) @DateTimeFormat(pattern = SymptomMgmtApi.DATETIME_FORMAT) Date lastUpdtDatetime) {
        Patient patient = patientService.findPatientById(id);
        if(null==patient){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }else if(!patient.getLastUpdtDatetime().after(lastUpdtDatetime)){
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }
        return new ResponseEntity<>(patient, HttpStatus.OK);
    }*/

    @RequestMapping(value = "/{id}", method = PUT)
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id, @RequestBody Patient patient) {
        Patient existing = patientService.findPatientById(id);
        if(null==existing){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }else{
            Patient updatedPatient = patientService.updatePatient(existing, patient);
            return new ResponseEntity<>(updatedPatient, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/{id}"+SymptomMgmtApi.CHECK_IN_PATH, method = POST)
    public ResponseEntity<CheckIn> createCheckIn(@PathVariable Long id, @RequestBody CheckIn checkIn) {
        Patient existing = patientService.findPatientById(id);
        if(null==existing){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }else{
            CheckIn savedCheckin = patientService.saveCheckIn(existing, checkIn);
            return new ResponseEntity<>(savedCheckin, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/{id}"+SymptomMgmtApi.MEDICATION_TAKEN_PATH, method = POST)
    public ResponseEntity<MedicationTaken> createMedicationTaken(@PathVariable Long id, @RequestBody MedicationTaken medicationTaken) {
        Patient existing = patientService.findPatientById(id);
        if(null==existing){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }else{
            medicationTaken = patientService.saveMedicationTaken(existing, medicationTaken);
            return new ResponseEntity<>(medicationTaken, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/{id}"+SymptomMgmtApi.MEDICATION_PATH, method = POST)
    public ResponseEntity<Medication> createMedication(@PathVariable Long id, @RequestBody Medication medication) {
        Patient existing = patientService.findPatientById(id);
        if(null==existing){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }else{
            medication = patientService.saveMedication(existing, medication);
            return new ResponseEntity<>(medication, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/{id}"+SymptomMgmtApi.MEDICATION_PATH+"/{medId}", method = PUT)
    public ResponseEntity<Medication> updateMedication(@PathVariable Long id, @PathVariable Long medId, @RequestBody Medication medication) {
        Patient existingPatient = patientService.findPatientById(id);
        Medication existing = patientService.findMedicationById(medId);
        if(null==existingPatient || null==existing){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }else{
            medication = patientService.saveMedication(existingPatient, medication);
            return new ResponseEntity<>(medication, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/{id}"+SymptomMgmtApi.MEDICATION_PATH+"/{medId}", method = DELETE)
    public ResponseEntity<?> deleteMedication(@PathVariable Long id, @PathVariable Long medId) {
        Patient existingPatient = patientService.findPatientById(id);
        Medication existing = patientService.findMedicationById(medId);
        if(null==existingPatient || null==existing){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }else{
            patientService.deleteMedication(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/{id}"+SymptomMgmtApi.REMINDER_PATH+"/{reminderId}", method = PUT)
    public ResponseEntity<Reminder> updateReminder(@PathVariable Long id, @PathVariable Long reminderId, @RequestBody Reminder reminder) {
        Patient existingPatient = patientService.findPatientById(id);
        Reminder existing = patientService.findReminderById(reminderId);
        if(null==existingPatient || null==existing){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }else{
            reminder = patientService.saveReminder(existingPatient, reminder);
            return new ResponseEntity<>(reminder, HttpStatus.OK);
        }
    }

    @RequestMapping(value = SymptomMgmtApi.CHECK_IN_PATH+SymptomMgmtApi.SEARCH_PATH, method = POST)
    public ResponseEntity<CheckInSearchResults> searchCheckInData(@Param(SymptomMgmtApi.SEARCH_STRING_PARAM) String searchString){
        CheckInSearchResults checkInSearchResults = new CheckInSearchResults();
        checkInSearchResults.setCheckIns(patientService.findCheckInByPatientNameLike(searchString));
        checkInSearchResults.setTotalRecords(checkInSearchResults.getCheckIns().size());
        return new ResponseEntity<>(checkInSearchResults, HttpStatus.OK);
    }

}
