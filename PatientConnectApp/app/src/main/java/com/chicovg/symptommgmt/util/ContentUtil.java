package com.chicovg.symptommgmt.util;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import com.chicovg.symptommgmt.R;
import com.chicovg.symptommgmt.rest.api.SymptomMgmtApi;
import com.chicovg.symptommgmt.rest.domain.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.chicovg.symptommgmt.provider.SymptomMgmtContract.CheckInColumns.EMAIL_ADDRESS;
import static com.chicovg.symptommgmt.provider.SymptomMgmtContract.CheckInColumns.FIRST_NAME;
import static com.chicovg.symptommgmt.provider.SymptomMgmtContract.CheckInColumns.LAST_NAME;
import static com.chicovg.symptommgmt.provider.SymptomMgmtContract.CheckInColumns.LAST_UPDT_DATETIME;
import static com.chicovg.symptommgmt.provider.SymptomMgmtContract.CheckInColumns.MEDICATION_NAME;
import static com.chicovg.symptommgmt.provider.SymptomMgmtContract.CheckInColumns.*;
import static com.chicovg.symptommgmt.provider.SymptomMgmtContract.CheckInColumns.PATIENT_ID;
import static com.chicovg.symptommgmt.provider.SymptomMgmtContract.CheckInColumns.PHONE_NUMBER;
import static com.chicovg.symptommgmt.provider.SymptomMgmtContract.CheckInColumns.TIMESTAMP;
import static com.chicovg.symptommgmt.provider.SymptomMgmtContract.CheckInColumns.USERNAME;
import static com.chicovg.symptommgmt.provider.SymptomMgmtContract.CheckInColumns._ID;
import static com.chicovg.symptommgmt.provider.SymptomMgmtContract.CheckInResponseColumns.*;
import static com.chicovg.symptommgmt.provider.SymptomMgmtContract.DoctorColumns.TITLE;
import static com.chicovg.symptommgmt.provider.SymptomMgmtContract.MedicationColumns.*;
import static com.chicovg.symptommgmt.provider.SymptomMgmtContract.MedicationTakenColumns.CHECK_IN_RESPONSE_ID;
import static com.chicovg.symptommgmt.provider.SymptomMgmtContract.PatientColumns.*;
import static com.chicovg.symptommgmt.provider.SymptomMgmtContract.ReminderColumns.HOUR;
import static com.chicovg.symptommgmt.provider.SymptomMgmtContract.ReminderColumns.MINUTE;

/**
 * Created by victorguthrie on 11/12/14.
 */
public class ContentUtil {

    private static DateFormat datetimeFormat = new SimpleDateFormat(SymptomMgmtApi.DATETIME_FORMAT);

    public static ContentValues patientToContentValues(Patient patient){
        ContentValues contentValues = new ContentValues();
        contentValues.put(_ID, patient.getId());
        contentValues.put(USERNAME, patient.getUsername());
        contentValues.put(FIRST_NAME, patient.getFirstName());
        contentValues.put(LAST_NAME, patient.getLastName());
        contentValues.put(DATE_OF_BIRTH, datetimeFormat.format(patient.getDateOfBirth()));
        contentValues.put(EMAIL_ADDRESS, patient.getEmailAddress());
        contentValues.put(PHONE_NUMBER, patient.getPhoneNumber());
        contentValues.put(LAST_UPDT_DATETIME, datetimeFormat.format(patient.getLastUpdtDatetime()));
        contentValues.put(POINTS, patient.getPoints());
        contentValues.put(CHECK_IN_STREAK, patient.getCheckInStreak());
        contentValues.put(TOTAL_CHECK_INS, (null!= patient.getCheckIns() ? patient.getCheckIns().size() : 0));
        contentValues.put(DOCTOR_ID, patient.getDoctorId());
        contentValues.put(LAST_PAIN_LEVEL, patient.getLastPainLevel().ordinal());
        if(null!=patient.getLastPainLevelReportedDtm()){
            contentValues.put(LAST_PAIN_LEVEL_REPORTED_DTM, datetimeFormat.format(patient.getLastPainLevelReportedDtm()));
        }
        if(null!=patient.getLastPainLevelChangedDtm()){
            contentValues.put(LAST_PAIN_LEVEL_CHANGED_DTM, datetimeFormat.format(patient.getLastPainLevelChangedDtm()));
        }
        if(null!=patient.getLastReportedUnableToEat()){
            contentValues.put(LAST_RPTD_UNABLE_TO_EAT, datetimeFormat.format(patient.getLastPainLevelChangedDtm()));
        }
        return contentValues;
    }

    public static ContentValues checkInToContentValues(CheckIn checkIn){
        ContentValues contentValues = new ContentValues();
        contentValues.put(_ID, checkIn.getId());
        contentValues.put(PATIENT_ID, checkIn.getPatientId());
        if(null!=checkIn.getOverallPainLevel())
            contentValues.put(OVERALL_PAIN_LEVEL, checkIn.getOverallPainLevel().toString());
        if(null!=checkIn.getStatus())
            contentValues.put(STATUS, checkIn.getStatus().toString());
        if(null!=checkIn.getTimestamp())
            contentValues.put(TIMESTAMP, datetimeFormat.format(checkIn.getTimestamp()));
        contentValues.put(UNABLE_TO_EAT, checkIn.isUnableToEat());
        return contentValues;
    }

    public static ContentValues checkInResponseToContentValues(CheckInResponse checkInResponse){
        ContentValues contentValues = new ContentValues();
        contentValues.put(_ID, checkInResponse.getId());
        contentValues.put(CHECK_IN_ID, checkInResponse.getCheckInId());
        contentValues.put(QUESTION_ID, checkInResponse.getQuestionId());
        contentValues.put(MEDICATION, checkInResponse.getMedication());
        contentValues.put(RESPONSE, checkInResponse.getResponse());
        return contentValues;
    }

    public static ContentValues reminderToContentValues(Reminder reminder){
        ContentValues contentValues = new ContentValues();
        contentValues.put(_ID, reminder.getId());
        contentValues.put(PATIENT_ID, reminder.getPatientId());
        contentValues.put(HOUR, reminder.getHour());
        contentValues.put(MINUTE, reminder.getMinute());
        return contentValues;
    }

    public static ContentValues medicationToContentValues(Medication medication){
        ContentValues contentValues = new ContentValues();
        contentValues.put(_ID, medication.getId());
        contentValues.put(PATIENT_ID, medication.getPatientId());
        contentValues.put(MEDICATION_NAME, medication.getName());
        contentValues.put(DOSAGE, medication.getDosage());
        contentValues.put(DOSAGE_UNIT, medication.getDosageUnit());
        contentValues.put(INSTRUCTIONS, medication.getInstructions());
        return contentValues;
    }

    public static ContentValues medicationTakenToContentValues(MedicationTaken medicationTaken){
        ContentValues contentValues = new ContentValues();
        contentValues.put(_ID, medicationTaken.getId());
        contentValues.put(PATIENT_ID, medicationTaken.getPatientId());
        contentValues.put(CHECK_IN_RESPONSE_ID, medicationTaken.getCheckInResponseId());
        contentValues.put(MEDICATION_NAME, medicationTaken.getMedicationName());
        contentValues.put(TIMESTAMP, datetimeFormat.format(medicationTaken.getTimestamp()));
        return contentValues;
    }

    public static ContentValues doctorToContentValues(Doctor doctor){
        ContentValues contentValues = new ContentValues();
        contentValues.put(_ID, doctor.getId());
        contentValues.put(USERNAME, doctor.getUsername());
        contentValues.put(FIRST_NAME, doctor.getFirstName());
        contentValues.put(LAST_NAME, doctor.getLastName());
        contentValues.put(TITLE, doctor.getTitle());
        contentValues.put(EMAIL_ADDRESS, doctor.getEmailAddress());
        contentValues.put(PHONE_NUMBER, doctor.getPhoneNumber());
        contentValues.put(LAST_UPDT_DATETIME, datetimeFormat.format(doctor.getLastUpdtDatetime()));
        return contentValues;
    }

    public static String timestampFromContentValues(Context context, String timestamp){
        DateFormat androidDateFormat = android.text.format.DateFormat.getLongDateFormat(context);
        DateFormat androidTimeFormat = android.text.format.DateFormat.getTimeFormat(context);

        try {
            Date dt = null != timestamp ? datetimeFormat.parse(timestamp) : null;
            if(null!=dt)
                return androidDateFormat.format(dt) + " " + androidTimeFormat.format(dt);
        } catch (ParseException e) {
            Log.e("ContentUtil", "Error parsing timestamp " + e);
        }
        return null;
    }

    public static int getColorForPainLevel(Context context, PainLevel painLevel){
        switch (painLevel){
            case WELL_CONTROLLED: return context.getResources().getColor(R.color.well_controlled);
            case MODERATE: return context.getResources().getColor(R.color.moderate);
            case SEVERE: return context.getResources().getColor(R.color.severe);
        }
        return context.getResources().getColor(android.R.color.primary_text_light);
    }

}
