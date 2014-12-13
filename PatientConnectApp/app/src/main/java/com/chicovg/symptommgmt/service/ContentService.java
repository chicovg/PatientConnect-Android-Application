package com.chicovg.symptommgmt.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import com.chicovg.symptommgmt.provider.SymptomMgmtContract;
import com.chicovg.symptommgmt.rest.api.SymptomMgmtApi;
import com.chicovg.symptommgmt.rest.domain.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import static com.chicovg.symptommgmt.util.ContentUtil.*;

/**
 *
 * Created by victorguthrie on 11/15/14.
 */
public class ContentService {

    private static DateFormat datetimeFormat = new SimpleDateFormat(SymptomMgmtApi.DATETIME_FORMAT);

    /**
     * Insert/Update patient in local database
     * @param patient
     * @return
     */
    public static boolean savePatient(Context context, Patient patient) {
        ContentValues patientContent = patientToContentValues(patient);
        if (saveOrUpdate(context, SymptomMgmtContract.PatientColumns.URI, patientContent, patient.getId(), patient.getClientId())) {
            if(null!=patient.getCheckIns()){
                for (CheckIn checkIn : patient.getCheckIns()) {
                    saveCheckIn(context, checkIn);
                }
            }
            if(null!=patient.getReminders()){
                for (Reminder reminder : patient.getReminders()) {
                    saveReminder(context, reminder);
                }
            }
            if(null!=patient.getMedications()){
                for (Medication medication : patient.getMedications()) {
                    saveMedication(context, medication);
                }
            }
            return true;
        }
        return false;
    }

    public static Patient saveAndReturnPatient(Context context, Patient patient){
        if(savePatient(context, patient)){
            return getPatientById(context, patient.getId());
        }
        return null;
    }

    /**
     * Insert/Update doctor in local database
     * @param doctor
     * @return
     */
    public static boolean saveDoctor(Context context, Doctor doctor) {
        ContentValues doctorContent = doctorToContentValues(doctor);
        if (saveOrUpdate(context, SymptomMgmtContract.DoctorColumns.URI, doctorContent, doctor.getId(), doctor.getClientId())) {
            for(Patient patient : doctor.getPatients()){
                savePatient(context, patient);
            }
            return true;
        }
        return false;
    }

    public static Doctor saveAndReturnDoctor(Context context, Doctor doctor){
        if(saveDoctor(context, doctor)){
            return getDoctorById(context, doctor.getId());
        }
        return null;
    }

    /**
     * Insert/Update check-in record in local database
     * @param checkIn
     */
    public static boolean saveCheckIn(Context context, CheckIn checkIn){
        ContentValues contentValues = checkInToContentValues(checkIn);
        if(saveOrUpdate(context, SymptomMgmtContract.CheckInColumns.URI, contentValues, checkIn.getId(), checkIn.getClientId())){
            for(CheckInResponse response : checkIn.getResponses()){
                saveCheckInResponse(context, response);
            }
        }
        return true;
    }

    /**
     * Insert/Update response record in local database
     * @param checkInResponse
     */
    private static boolean saveCheckInResponse(Context context, CheckInResponse checkInResponse){
        ContentValues contentValues = checkInResponseToContentValues(checkInResponse);
        if(saveOrUpdate(context, SymptomMgmtContract.CheckInResponseColumns.URI, contentValues, checkInResponse.getId(), checkInResponse.getClientId())){
            if(null!=checkInResponse.getMedicationTaken()){
                saveMedicationTaken(context, checkInResponse.getMedicationTaken());
            }
        }
        return true;
    }

    /**
     * Insert/Update check-in record in local database
     * @param reminder
     */
    public static boolean saveReminder(Context context, Reminder reminder){
        ContentValues contentValues = reminderToContentValues(reminder);
        return saveOrUpdate(context, SymptomMgmtContract.ReminderColumns.URI, contentValues, reminder.getId(), reminder.getClientId());
    }

    /**
     * Insert/Update medication record in local database
     * @param medication
     */
    public static boolean saveMedication(Context context, Medication medication){
        ContentValues contentValues = medicationToContentValues(medication);
        return saveOrUpdate(context, SymptomMgmtContract.MedicationColumns.URI, contentValues, medication.getId(), medication.getClientId());
    }

    /**
     * Insert/Update medication taken record in local database
     */
    public static boolean saveMedicationTaken(Context context, MedicationTaken medicationTaken){
        ContentValues contentValues = medicationTakenToContentValues(medicationTaken);
        return saveOrUpdate(context, SymptomMgmtContract.MedicationTakenColumns.URI, contentValues, medicationTaken.getId(), medicationTaken.getClientId());
    }

    /**
     * Tries to update a record in the local database with the specified uri and id.
     *  if the record does not exist, it will be inserted
     * @param contentUri
     * @param contentValues
     * @param id
     * @return
     */
    private static boolean saveOrUpdate(Context context, Uri contentUri, ContentValues contentValues, long id, long clientId){
        int updated = 0;
        if(id==0 && clientId ==0){
            Uri uri = context.getContentResolver().insert(contentUri, contentValues);
            return uri != null;
        }else if (clientId == 0){
            updated = context.getContentResolver().update(contentUri, contentValues,
                    SymptomMgmtContract.CommonColumns.QUERY_BY_ID, new String[]{String.valueOf(id)});
        }else if (id == 0){
            updated = context.getContentResolver().update(contentUri, contentValues,
                    SymptomMgmtContract.CommonColumns.QUERY_BY_CLIENT_ID, new String[]{String.valueOf(clientId)});
        }else {
            updated = context.getContentResolver().update(contentUri, contentValues,
                    SymptomMgmtContract.CommonColumns.QUERY_BY_CLIENT_ID_AND_ID, new String[]{String.valueOf(clientId), String.valueOf(id)});
        }
        if(updated==0){
            Uri uri = context.getContentResolver().insert(contentUri, contentValues);
            return uri != null;
        }else{
            return true;
        }
    }

    public static Patient getPatientById(Context context, Long patientId){
        Cursor c =  context.getContentResolver().query(SymptomMgmtContract.PatientColumns.URI,
                SymptomMgmtContract.PatientColumns.PROJECTION, SymptomMgmtContract.PatientColumns.QUERY_BY_ID, new String[]{String.valueOf(patientId)}, null, null);
        try{
            if(c != null && c.getCount() > 0){
                while(c.moveToNext()){
                    return extractPatientFromCursor(c);
                }
            }
            return null;
        }finally {
            if(null!=c && !c.isClosed())
                c.close();
        }
    }

    public static Patient extractPatientFromCursor(Cursor c){
        Patient patient = new Patient();
        try {
            patient.setClientId(c.getLong(c.getColumnIndex(SymptomMgmtContract.PatientColumns.CLIENT_ID)));
            patient.setId(c.getLong(c.getColumnIndex(SymptomMgmtContract.PatientColumns._ID)));
            patient.setUsername(c.getString(c.getColumnIndex(SymptomMgmtContract.PatientColumns.USERNAME)));
            patient.setFirstName(c.getString(c.getColumnIndex(SymptomMgmtContract.PatientColumns.FIRST_NAME)));
            patient.setLastName(c.getString(c.getColumnIndex(SymptomMgmtContract.PatientColumns.LAST_NAME)));
            patient.setDateOfBirth(datetimeFormat.parse(c.getString(c.getColumnIndex(SymptomMgmtContract.PatientColumns.DATE_OF_BIRTH))));
            patient.setEmailAddress(c.getString(c.getColumnIndex(SymptomMgmtContract.PatientColumns.EMAIL_ADDRESS)));
            patient.setPhoneNumber(c.getString(c.getColumnIndex(SymptomMgmtContract.PatientColumns.PHONE_NUMBER)));
            patient.setLastUpdtDatetime(datetimeFormat.parse(c.getString(c.getColumnIndex(SymptomMgmtContract.PatientColumns.LAST_UPDT_DATETIME))));
            patient.setDoctorId(c.getLong(c.getColumnIndex(SymptomMgmtContract.PatientColumns.DOCTOR_ID)));
            patient.setLastPainLevel(PainLevel.values()[c.getInt(c.getColumnIndex(SymptomMgmtContract.PatientColumns.LAST_PAIN_LEVEL))]);
            if(null!=c.getString(c.getColumnIndex(SymptomMgmtContract.PatientColumns.LAST_PAIN_LEVEL_REPORTED_DTM))){
                patient.setLastPainLevelReportedDtm(datetimeFormat.parse(c.getString(c.getColumnIndex(SymptomMgmtContract.PatientColumns.LAST_PAIN_LEVEL_REPORTED_DTM))));
            }
            if(null!=c.getString(c.getColumnIndex(SymptomMgmtContract.PatientColumns.LAST_PAIN_LEVEL_CHANGED_DTM))){
                patient.setLastPainLevelChangedDtm(datetimeFormat.parse(c.getString(c.getColumnIndex(SymptomMgmtContract.PatientColumns.LAST_PAIN_LEVEL_CHANGED_DTM))));
            }
            if(null!=c.getString(c.getColumnIndex(SymptomMgmtContract.PatientColumns.LAST_RPTD_UNABLE_TO_EAT))){
                patient.setLastReportedUnableToEat(datetimeFormat.parse(c.getString(c.getColumnIndex(SymptomMgmtContract.PatientColumns.LAST_RPTD_UNABLE_TO_EAT))));
            }
        } catch (ParseException e) {
            Log.e("PatientContentService", e.getMessage());
        }
        return patient;
    }

    public static CheckIn extractCheckInFromCursor(Context context, Cursor c){
        CheckIn checkIn = new CheckIn();
        try{
            checkIn.setId(c.getLong(c.getColumnIndex(SymptomMgmtContract.CheckInColumns._ID)));
            checkIn.setClientId(c.getLong(c.getColumnIndex(SymptomMgmtContract.CheckInColumns.CLIENT_ID)));
            checkIn.setPatientId(c.getLong(c.getColumnIndex(SymptomMgmtContract.CheckInColumns.PATIENT_ID)));
            checkIn.setOverallPainLevel(PainLevel.valueOf(c.getString(c.getColumnIndex(SymptomMgmtContract.CheckInColumns.OVERALL_PAIN_LEVEL))));
            checkIn.setTimestamp(datetimeFormat.parse(c.getString(c.getColumnIndex(SymptomMgmtContract.CheckInColumns.TIMESTAMP))));
            checkIn.setStatus(CheckInStatus.valueOf(c.getString(c.getColumnIndex(SymptomMgmtContract.CheckInColumns.STATUS))));
            checkIn.setUnableToEat(c.getInt(c.getColumnIndex(SymptomMgmtContract.CheckInColumns.UNABLE_TO_EAT)) == 1);
            List<CheckInResponse> responses = getResponsesByCheckInId(context, checkIn.getId());
            checkIn.setResponses(responses);
        } catch (ParseException e){
            Log.e("PatientContentService", e.getMessage());
        }
        return checkIn;
    }

    public static List<Medication> getMedicationsByPatientId(Context context, long id){
        List<Medication> medications = new LinkedList<>();

        Cursor c =  context.getContentResolver().query(SymptomMgmtContract.MedicationColumns.URI,
                SymptomMgmtContract.MedicationColumns.PROJECTION, SymptomMgmtContract.MedicationColumns.QUERY_BY_PATIENT,
                new String[]{String.valueOf(id)}, null, null);
        if(c != null && c.getCount() > 0){
            while(c.moveToNext()){
                medications.add(extractMedicationFromCursor(context, c));
            }
        }
        return medications;
    }

    public static Medication extractMedicationFromCursor(Context context, Cursor c){
        Medication medication = new Medication();
        medication.setId(c.getLong(c.getColumnIndex(SymptomMgmtContract.MedicationColumns._ID)));
        medication.setPatientId(c.getLong(c.getColumnIndex(SymptomMgmtContract.MedicationColumns.PATIENT_ID)));
        medication.setName(c.getString(c.getColumnIndex(SymptomMgmtContract.MedicationColumns.MEDICATION_NAME)));
        medication.setDosage(c.getInt(c.getColumnIndex(SymptomMgmtContract.MedicationColumns.DOSAGE)));
        medication.setDosageUnit(c.getString(c.getColumnIndex(SymptomMgmtContract.MedicationColumns.DOSAGE_UNIT)));
        medication.setInstructions(c.getString(c.getColumnIndex(SymptomMgmtContract.MedicationColumns.INSTRUCTIONS)));
        return medication;
    }

    public static List<CheckInResponse> getResponsesByCheckInId(Context context, long id){
        List<CheckInResponse> checkInResponses = new LinkedList<>();
        Cursor c = context.getContentResolver().query(SymptomMgmtContract.CheckInResponseColumns.URI,
                SymptomMgmtContract.CheckInResponseColumns.PROJECTION, SymptomMgmtContract.CheckInResponseColumns.QUERY_BY_CHECK_IN,
                new String[]{String.valueOf(id)}, null, null);
        if(c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                CheckInResponse checkInResponse = new CheckInResponse();
                checkInResponse.setId(c.getLong(c.getColumnIndex(SymptomMgmtContract.CheckInResponseColumns._ID)));
                checkInResponse.setClientId(c.getLong(c.getColumnIndex(SymptomMgmtContract.CheckInResponseColumns.CLIENT_ID)));
                checkInResponse.setMedication(c.getString(c.getColumnIndex(SymptomMgmtContract.CheckInResponseColumns.MEDICATION)));
                checkInResponse.setQuestionId(c.getInt(c.getColumnIndex(SymptomMgmtContract.CheckInResponseColumns.QUESTION_ID)));
                checkInResponse.setResponse(c.getString(c.getColumnIndex(SymptomMgmtContract.CheckInResponseColumns.RESPONSE)));
                checkInResponse.setMedicationTaken(getMedicationTakenByResponseId(context, checkInResponse.getId()));
                checkInResponses.add(checkInResponse);
            }
        }
        return checkInResponses;
    }

    public static MedicationTaken getMedicationTakenByResponseId(Context context, long id){
        Cursor c = context.getContentResolver().query(SymptomMgmtContract.MedicationTakenColumns.URI,
                SymptomMgmtContract.MedicationTakenColumns.PROJECTION, SymptomMgmtContract.MedicationTakenColumns.QUERY_BY_CHECK_IN_RESPONSE,
                new String[]{String.valueOf(id)}, null, null);
        if(c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                MedicationTaken medicationTaken = new MedicationTaken();
                medicationTaken.setId(c.getLong(c.getColumnIndex(SymptomMgmtContract.MedicationTakenColumns._ID)));
                medicationTaken.setClientId(c.getLong(c.getColumnIndex(SymptomMgmtContract.MedicationTakenColumns.CLIENT_ID)));
                medicationTaken.setPatientId(c.getLong(c.getColumnIndex(SymptomMgmtContract.MedicationTakenColumns.PATIENT_ID)));
                medicationTaken.setCheckInResponseId(c.getLong(c.getColumnIndex(SymptomMgmtContract.MedicationTakenColumns.CHECK_IN_RESPONSE_ID)));
                medicationTaken.setMedicationName(c.getString(c.getColumnIndex(SymptomMgmtContract.MedicationTakenColumns.MEDICATION_NAME)));
                try {
                    medicationTaken.setTimestamp(datetimeFormat.parse(c.getString(c.getColumnIndex(SymptomMgmtContract.MedicationTakenColumns.TIMESTAMP))));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return medicationTaken;
            }
        }
        return null;
    }

    public static Doctor getDoctorById(Context context, long id){
        Cursor c = context.getContentResolver().query(SymptomMgmtContract.DoctorColumns.URI,
                SymptomMgmtContract.DoctorColumns.PROJECTION, SymptomMgmtContract.DoctorColumns.QUERY_BY_ID,
                new String[]{String.valueOf(id)}, null, null);
        if(c != null && c.getCount() > 0){
            while (c.moveToNext()) {
                Doctor doctor = new Doctor();
                doctor.setId(c.getLong(c.getColumnIndex(SymptomMgmtContract.DoctorColumns._ID)));
                doctor.setClientId(c.getLong(c.getColumnIndex(SymptomMgmtContract.DoctorColumns.CLIENT_ID)));
                doctor.setUsername(c.getString(c.getColumnIndex(SymptomMgmtContract.DoctorColumns.USERNAME)));
                doctor.setFirstName(c.getString(c.getColumnIndex(SymptomMgmtContract.DoctorColumns.FIRST_NAME)));
                doctor.setLastName(c.getString(c.getColumnIndex(SymptomMgmtContract.DoctorColumns.LAST_NAME)));
                doctor.setEmailAddress(c.getString(c.getColumnIndex(SymptomMgmtContract.DoctorColumns.EMAIL_ADDRESS)));
                doctor.setPhoneNumber(c.getString(c.getColumnIndex(SymptomMgmtContract.DoctorColumns.PHONE_NUMBER)));
                try {
                    doctor.setLastUpdtDatetime(datetimeFormat.parse(c.getString(c.getColumnIndex(SymptomMgmtContract.DoctorColumns.LAST_UPDT_DATETIME))));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return doctor;
            }
        }
        return null;
    }

    public static Reminder extractReminderFromCursor(Cursor c){
        Reminder reminder = new Reminder();
        reminder.setId(c.getLong(c.getColumnIndex(SymptomMgmtContract.ReminderColumns._ID)));
        reminder.setClientId(c.getLong(c.getColumnIndex(SymptomMgmtContract.ReminderColumns.CLIENT_ID)));
        reminder.setPatientId(c.getLong(c.getColumnIndex(SymptomMgmtContract.ReminderColumns.PATIENT_ID)));
        reminder.setHour(c.getInt(c.getColumnIndex(SymptomMgmtContract.ReminderColumns.HOUR)));
        reminder.setMinute(c.getInt(c.getColumnIndex(SymptomMgmtContract.ReminderColumns.MINUTE)));
        return reminder;
    }

    /**
     *
     * @param context
     * @return
     */
    public static List<Reminder> getReminders(Context context){
        Cursor c = context.getContentResolver().query(SymptomMgmtContract.ReminderColumns.URI,
                SymptomMgmtContract.ReminderColumns.PROJECTION, null, null, null);
        List<Reminder> reminders = new LinkedList<>();
        if(c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                reminders.add(extractReminderFromCursor(c));
            }
        }
        return reminders;
    }

}
