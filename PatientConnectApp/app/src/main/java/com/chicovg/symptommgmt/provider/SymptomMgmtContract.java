package com.chicovg.symptommgmt.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by victorguthrie on 10/30/14.
 */
public class SymptomMgmtContract {

    public static final String AUTHORITY = "com.chicovg.symptommgmt";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static interface CommonColumns extends BaseColumns {
        public static final Uri BASE_URI = SymptomMgmtContract.CONTENT_URI;
        public static final String BASE_TYPE = SymptomMgmtContract.AUTHORITY;

        public static final String CLIENT_ID = "client_id";
        public static final String LAST_UPDT_DATETIME = "last_updt_datetime";
        public static final String USERNAME = "username";
        public static final String FIRST_NAME = "first_name";
        public static final String LAST_NAME = "last_name";
        public static final String PHONE_NUMBER = "phone_number";
        public static final String EMAIL_ADDRESS = "email_address";
        public static final String PATIENT_ID = "patient_id";
        public static final String MEDICATION_NAME = "medication_name";
        public static final String TIMESTAMP = "timestamp";

        public static final String QUERY_BY_CLIENT_ID = CLIENT_ID + " = ? ";
        public static final String QUERY_BY_ID = _ID + " = ? ";
        public static final String QUERY_BY_CLIENT_ID_AND_ID = CLIENT_ID + " = ? AND " + _ID + " = ? ";

        public static final String QUERY_BY_PATIENT = PATIENT_ID+" = ? ";
        public static final String QUERY_BY_NOT_PATIENT = PATIENT_ID+" != ? ";
    }

    public static final class AppConfig implements CommonColumns {
        public static final String TABLE_NAME = "app_config";
        public static final String PATH = "app_config";
        public static final Uri URI = Uri.withAppendedPath(BASE_URI, PATH);
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + BASE_TYPE + "_" + TABLE_NAME;
        public static final String ITEM_CONTENT_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + BASE_TYPE + "_" + TABLE_NAME;
    }

    public static final class PatientColumns implements CommonColumns {

        public static final String TABLE_NAME = "patient";
        public static final String PATH = "patient";
        public static final String ITEM_PATH = "patient/#";
        public static final Uri URI = Uri.withAppendedPath(BASE_URI, PATH);
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + BASE_TYPE + "_" + TABLE_NAME;
        public static final String ITEM_CONTENT_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + BASE_TYPE + "_" + TABLE_NAME;

        public static final String DATE_OF_BIRTH = "date_of_birth";
        public static final String DOCTOR_ID = "doctor_id";
        public static final String POINTS = "points";
        public static final String TOTAL_CHECK_INS = "total_check_ins";
        public static final String CHECK_IN_STREAK = "check_in_streak";
        public static final String LAST_PAIN_LEVEL = "last_pain_level";
        public static final String LAST_PAIN_LEVEL_REPORTED_DTM = "last_pain_level_reported_dtm";
        public static final String LAST_PAIN_LEVEL_CHANGED_DTM = "last_pain_level_changed_dtm";
        public static final String LAST_RPTD_UNABLE_TO_EAT = "last_rptd_unable_to_eat";

        public static final String CREATE_STMT = " CREATE TABLE " + TABLE_NAME + " ("
                        + CLIENT_ID + " INTEGER PRIMARY KEY, "
                        + _ID + " INTEGER, "
                        + DOCTOR_ID + " INTEGER NOT NULL, "
                        + USERNAME + " TEXT NOT NULL, "
                        + FIRST_NAME + " TEXT NOT NULL, "
                        + LAST_NAME + " TEXT NOT NULL, "
                        + DATE_OF_BIRTH + " TEXT NOT NULL, "
                        + EMAIL_ADDRESS + " TEXT, "
                        + PHONE_NUMBER + " TEXT, "
                        + LAST_UPDT_DATETIME + " TEXT NOT NULL, "
                        + POINTS + " INTEGER NOT NULL, "
                        + TOTAL_CHECK_INS + " INTEGER NOT NULL, "
                        + CHECK_IN_STREAK + " INTEGER NOT NULL, "
                        + LAST_PAIN_LEVEL + " INTEGER, "
                        + LAST_PAIN_LEVEL_REPORTED_DTM + " TEXT, "
                        + LAST_PAIN_LEVEL_CHANGED_DTM + " TEXT, "
                        + LAST_RPTD_UNABLE_TO_EAT + " TEXT "
                        + ");";
        public static final String[] PROJECTION = {CLIENT_ID, _ID, USERNAME, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH,
                PHONE_NUMBER, EMAIL_ADDRESS, DOCTOR_ID, LAST_UPDT_DATETIME, POINTS, TOTAL_CHECK_INS, CHECK_IN_STREAK,
                LAST_PAIN_LEVEL, LAST_PAIN_LEVEL_REPORTED_DTM, LAST_PAIN_LEVEL_CHANGED_DTM, LAST_RPTD_UNABLE_TO_EAT};

        public static final String QUERY_BY_DOCTOR = DOCTOR_ID+" = ? ";
        public static final String SORT_FOR_DOCTOR_DASHBOARD = LAST_PAIN_LEVEL+" DESC, "+LAST_PAIN_LEVEL_REPORTED_DTM+" DESC";
    }

    public static final class CheckInColumns implements CommonColumns {
        public static final String TABLE_NAME = "check_in";
        public static final String PATH = "check_in";
        public static final String ITEM_PATH = "check_in/#";
        public static final Uri URI = Uri.withAppendedPath(BASE_URI, PATH);
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + BASE_TYPE + "_" + TABLE_NAME;
        public static final String ITEM_CONTENT_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + BASE_TYPE + "_" + TABLE_NAME;

        public static final String OVERALL_PAIN_LEVEL = "overall_pain_level";
        public static final String STATUS = "status";
        public static final String UNABLE_TO_EAT = "unable_to_eat";

        public static final String CREATE_STMT = " CREATE TABLE " +TABLE_NAME + " ("
                + CLIENT_ID + " INTEGER PRIMARY KEY, "
                + _ID + " INTEGER, "
                + PATIENT_ID + " INTEGER NOT NULL, "
                + TIMESTAMP + " TEXT, "
                + STATUS + " TEXT NOT NULL, "
                + OVERALL_PAIN_LEVEL + " TEXT NOT NULL, "
                + UNABLE_TO_EAT + " BOOL "
                + ");";
        public static final String[] PROJECTION = {CLIENT_ID, _ID, PATIENT_ID, TIMESTAMP, STATUS, OVERALL_PAIN_LEVEL, UNABLE_TO_EAT};
        public static final String DEFAULT_SORT = TIMESTAMP+" DESC";
    }

    public static final class CheckInResponseColumns implements CommonColumns {
        public static final String TABLE_NAME = "check_in_response";
        public static final String PATH = "check_in_response";
        public static final String ITEM_PATH = "check_in_response/#";
        public static final Uri URI = Uri.withAppendedPath(BASE_URI, PATH);
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + BASE_TYPE + "_" + TABLE_NAME;
        public static final String ITEM_CONTENT_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + BASE_TYPE + "_" + TABLE_NAME;

        public static final String CHECK_IN_ID = "check_in_id";
        public static final String QUESTION_ID = "question_id";
        public static final String MEDICATION = "medication";
        public static final String RESPONSE = "response";

        public static final String QUERY_BY_CHECK_IN = CHECK_IN_ID+" = ? ";

        public static final String CREATE_STMT = " CREATE TABLE " +TABLE_NAME + " ("
                + CLIENT_ID + " INTEGER PRIMARY KEY, "
                + _ID + " INTEGER, "
                + CHECK_IN_ID + " INTEGER NOT NULL, "
                + QUESTION_ID + " INTEGER NOT NULL, "
                + MEDICATION + " TEXT, "
                + RESPONSE + " TEXT "
                + ");";
        public static final String[] PROJECTION = {CLIENT_ID, _ID, CHECK_IN_ID, QUESTION_ID, MEDICATION, RESPONSE};
        public static final String DEFAULT_SORT = QUESTION_ID+" ASC";
    }

    public static final class ReminderColumns implements CommonColumns {
        public static final String TABLE_NAME = "reminder";
        public static final String PATH = "reminder";
        public static final String ITEM_PATH = "reminder/#";
        public static final Uri URI = Uri.withAppendedPath(BASE_URI, PATH);
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + BASE_TYPE + "_" + TABLE_NAME;
        public static final String ITEM_CONTENT_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + BASE_TYPE + "_" + TABLE_NAME;

        public static final String HOUR = "hour";
        public static final String MINUTE = "minute";

        public static final String CREATE_STMT = " CREATE TABLE " +TABLE_NAME + " ("
                + CLIENT_ID + " INTEGER PRIMARY KEY, "
                + _ID + " INTEGER, "
                + PATIENT_ID + " INTEGER NOT NULL, "
                + HOUR + " INTEGER NOT NULL, "
                + MINUTE + " INTEGER NOT NULL "
                + ");";

        public static final String[] PROJECTION = {CLIENT_ID, PATIENT_ID, _ID, HOUR, MINUTE};
        public static final String SORT_HOUR_MIN_ASC = HOUR+" ASC, "+MINUTE+" ASC";

    }

    public static final class MedicationColumns implements CommonColumns {
        public static final String TABLE_NAME = "medication";
        public static final String PATH = "medication";
        public static final String ITEM_PATH = "medication/#";
        public static final Uri URI = Uri.withAppendedPath(BASE_URI, PATH);
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + BASE_TYPE + "_" + TABLE_NAME;
        public static final String ITEM_CONTENT_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + BASE_TYPE + "_" + TABLE_NAME;

        public static final String DOSAGE = "dosage";
        public static final String DOSAGE_UNIT = "dosage_unit";
        public static final String INSTRUCTIONS = "instructions";

        public static final String CREATE_STMT = " CREATE TABLE " +TABLE_NAME + " ("
                + CLIENT_ID + " INTEGER PRIMARY KEY, "
                + _ID + " INTEGER, "
                + PATIENT_ID + " INTEGER NOT NULL, "
                + MEDICATION_NAME + " TEXT NOT NULL, "
                + DOSAGE + " INTEGER NOT NULL, "
                + DOSAGE_UNIT + " TEXT NOT NULL, "
                + INSTRUCTIONS + " TEXT NOT NULL "
                + ");";

        public static final String[] PROJECTION = {CLIENT_ID, _ID, PATIENT_ID, MEDICATION_NAME, DOSAGE, DOSAGE_UNIT, INSTRUCTIONS};
        public static final String DEFAULT_SORT = MEDICATION_NAME+" ASC";

    }

    public static final class MedicationTakenColumns implements CommonColumns {
        public static final String TABLE_NAME = "medication_taken";
        public static final String PATH = "medication_taken";
        public static final String ITEM_PATH = "medication_taken/#";
        public static final Uri URI = Uri.withAppendedPath(BASE_URI, PATH);
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + BASE_TYPE + "_" + TABLE_NAME;
        public static final String ITEM_CONTENT_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + BASE_TYPE + "_" + TABLE_NAME;

        public static final String CHECK_IN_RESPONSE_ID = "check_in_response_id";

        public static final String CREATE_STMT = " CREATE TABLE " +TABLE_NAME + " ("
                + CLIENT_ID + " INTEGER PRIMARY KEY, "
                + _ID + " INTEGER, "
                + PATIENT_ID + " INTEGER NOT NULL, "
                + CHECK_IN_RESPONSE_ID + " INTEGER NOT NULL, "
                + MEDICATION_NAME + " TEXT NOT NULL, "
                + TIMESTAMP + " TEXT NOT NULL "
                + ");";

        public static final String QUERY_BY_CHECK_IN_RESPONSE = CHECK_IN_RESPONSE_ID+ " = ? ";
        public static final String[] PROJECTION = {CLIENT_ID, _ID, PATIENT_ID, CHECK_IN_RESPONSE_ID, MEDICATION_NAME, TIMESTAMP};
        public static final String DEFAULT_SORT = TIMESTAMP+" DESC";
    }

    public static final class DoctorColumns implements CommonColumns {

        public static final String TABLE_NAME = "doctor";
        public static final String PATH = "doctor";
        public static final String ITEM_PATH = "doctor/#";
        public static final Uri URI = Uri.withAppendedPath(BASE_URI, PATH);
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + BASE_TYPE + "_" + TABLE_NAME;
        public static final String ITEM_CONTENT_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + BASE_TYPE + "_" + TABLE_NAME;

        public static final String TITLE = "title";

        public static final String CREATE_STMT = " CREATE TABLE " + TABLE_NAME + " ("
                + CLIENT_ID + " INTEGER PRIMARY KEY, "
                + _ID + " INTEGER, "
                + USERNAME + " TEXT NOT NULL, "
                + FIRST_NAME + " TEXT NOT NULL, "
                + LAST_NAME + " TEXT NOT NULL, "
                + TITLE + " TEXT, "
                + EMAIL_ADDRESS + " TEXT, "
                + PHONE_NUMBER + " TEXT, "
                + LAST_UPDT_DATETIME + " TEXT NOT NULL "
                + ");";
        public static final String[] PROJECTION = {CLIENT_ID, _ID, USERNAME, FIRST_NAME, LAST_NAME, TITLE,
                PHONE_NUMBER, EMAIL_ADDRESS, LAST_UPDT_DATETIME};
    }
}
