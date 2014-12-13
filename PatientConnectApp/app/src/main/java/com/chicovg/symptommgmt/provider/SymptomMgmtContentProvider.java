package com.chicovg.symptommgmt.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by victorguthrie on 10/30/14.
 */
public class SymptomMgmtContentProvider extends ContentProvider {

    private SymptomMgmtDatabaseHelper databaseHelper;
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int PATIENT_KEY = 0;
    private static final int PATIENT_ITEM_KEY = 1;
    private static final int CHECK_IN_KEY = 2;
    private static final int CHECK_IN_ITEM_KEY = 3;
    private static final int REMINDER_KEY = 4;
    private static final int REMINDER_ITEM_KEY = 5;
    private static final int MEDICATION_KEY = 6;
    private static final int MEDICATION_ITEM_KEY = 7;
    private static final int DOCTOR_KEY = 8;
    private static final int DOCTOR_ITEM_KEY = 9;
    private static final int CHECK_IN_RESPONSE_KEY = 10;
    private static final int CHECK_IN_RESPONSE_ITEM_KEY = 11;
    private static final int MEDICATION_TAKEN_KEY = 12;
    private static final int MEDICATION_TAKEN_ITEM_KEY = 13;

    private Set<SymptomMgmtContract.CommonColumns> tableSet = new HashSet<SymptomMgmtContract.CommonColumns>();

    @Override
    public boolean onCreate() {
        databaseHelper = new SymptomMgmtDatabaseHelper(getContext());
        databaseHelper.createDataBase(getContext());

        URI_MATCHER.addURI(SymptomMgmtContract.AUTHORITY, SymptomMgmtContract.PatientColumns.PATH, PATIENT_KEY);
        URI_MATCHER.addURI(SymptomMgmtContract.AUTHORITY, SymptomMgmtContract.PatientColumns.ITEM_PATH, PATIENT_ITEM_KEY);
        URI_MATCHER.addURI(SymptomMgmtContract.AUTHORITY, SymptomMgmtContract.CheckInColumns.PATH, CHECK_IN_KEY);
        URI_MATCHER.addURI(SymptomMgmtContract.AUTHORITY, SymptomMgmtContract.CheckInColumns.ITEM_PATH, CHECK_IN_ITEM_KEY);
        URI_MATCHER.addURI(SymptomMgmtContract.AUTHORITY, SymptomMgmtContract.ReminderColumns.PATH, REMINDER_KEY);
        URI_MATCHER.addURI(SymptomMgmtContract.AUTHORITY, SymptomMgmtContract.ReminderColumns.ITEM_PATH, REMINDER_ITEM_KEY);
        URI_MATCHER.addURI(SymptomMgmtContract.AUTHORITY, SymptomMgmtContract.MedicationColumns.PATH, MEDICATION_KEY);
        URI_MATCHER.addURI(SymptomMgmtContract.AUTHORITY, SymptomMgmtContract.MedicationColumns.ITEM_PATH, MEDICATION_ITEM_KEY);
        URI_MATCHER.addURI(SymptomMgmtContract.AUTHORITY, SymptomMgmtContract.DoctorColumns.PATH, DOCTOR_KEY);
        URI_MATCHER.addURI(SymptomMgmtContract.AUTHORITY, SymptomMgmtContract.DoctorColumns.ITEM_PATH, DOCTOR_ITEM_KEY);
        URI_MATCHER.addURI(SymptomMgmtContract.AUTHORITY, SymptomMgmtContract.CheckInResponseColumns.PATH, CHECK_IN_RESPONSE_KEY);
        URI_MATCHER.addURI(SymptomMgmtContract.AUTHORITY, SymptomMgmtContract.CheckInResponseColumns.ITEM_PATH, CHECK_IN_RESPONSE_ITEM_KEY);
        URI_MATCHER.addURI(SymptomMgmtContract.AUTHORITY, SymptomMgmtContract.MedicationTakenColumns.PATH, MEDICATION_TAKEN_KEY);
        URI_MATCHER.addURI(SymptomMgmtContract.AUTHORITY, SymptomMgmtContract.MedicationTakenColumns.ITEM_PATH, MEDICATION_TAKEN_ITEM_KEY);
        return true;
    }

    @Override
    public String getType(Uri uri) {
        int typeId = URI_MATCHER.match(uri);
        switch (typeId){
            case PATIENT_KEY: return SymptomMgmtContract.PatientColumns.CONTENT_TYPE;
            case PATIENT_ITEM_KEY: return SymptomMgmtContract.PatientColumns.ITEM_CONTENT_TYPE;
            case CHECK_IN_KEY: return SymptomMgmtContract.CheckInColumns.CONTENT_TYPE;
            case CHECK_IN_ITEM_KEY: return SymptomMgmtContract.CheckInColumns.ITEM_CONTENT_TYPE;
            case REMINDER_KEY: return SymptomMgmtContract.ReminderColumns.CONTENT_TYPE;
            case REMINDER_ITEM_KEY: return SymptomMgmtContract.ReminderColumns.ITEM_CONTENT_TYPE;
            case MEDICATION_KEY: return SymptomMgmtContract.MedicationColumns.CONTENT_TYPE;
            case MEDICATION_ITEM_KEY: return SymptomMgmtContract.MedicationColumns.ITEM_CONTENT_TYPE;
            case DOCTOR_KEY: return SymptomMgmtContract.DoctorColumns.CONTENT_TYPE;
            case DOCTOR_ITEM_KEY: return SymptomMgmtContract.DoctorColumns.ITEM_CONTENT_TYPE;
            case CHECK_IN_RESPONSE_KEY: return SymptomMgmtContract.CheckInResponseColumns.CONTENT_TYPE;
            case CHECK_IN_RESPONSE_ITEM_KEY: return SymptomMgmtContract.CheckInResponseColumns.ITEM_CONTENT_TYPE;
            case MEDICATION_TAKEN_KEY: return SymptomMgmtContract.MedicationTakenColumns.CONTENT_TYPE;
            case MEDICATION_TAKEN_ITEM_KEY: return SymptomMgmtContract.MedicationTakenColumns.ITEM_CONTENT_TYPE;
            default: return null;
        }
    }

    private String getTableFromType(Uri uri){
        int typeId = URI_MATCHER.match(uri);
        switch (typeId){
            case PATIENT_KEY: return SymptomMgmtContract.PatientColumns.TABLE_NAME;
            case PATIENT_ITEM_KEY: return SymptomMgmtContract.PatientColumns.TABLE_NAME;
            case CHECK_IN_KEY: return SymptomMgmtContract.CheckInColumns.TABLE_NAME;
            case CHECK_IN_ITEM_KEY: return SymptomMgmtContract.CheckInColumns.TABLE_NAME;
            case REMINDER_KEY: return SymptomMgmtContract.ReminderColumns.TABLE_NAME;
            case REMINDER_ITEM_KEY: return SymptomMgmtContract.ReminderColumns.TABLE_NAME;
            case MEDICATION_KEY: return SymptomMgmtContract.MedicationColumns.TABLE_NAME;
            case MEDICATION_ITEM_KEY: return SymptomMgmtContract.MedicationColumns.TABLE_NAME;
            case DOCTOR_KEY: return SymptomMgmtContract.DoctorColumns.TABLE_NAME;
            case DOCTOR_ITEM_KEY: return SymptomMgmtContract.DoctorColumns.TABLE_NAME;
            case CHECK_IN_RESPONSE_KEY: return SymptomMgmtContract.CheckInResponseColumns.TABLE_NAME;
            case CHECK_IN_RESPONSE_ITEM_KEY: return SymptomMgmtContract.CheckInResponseColumns.TABLE_NAME;
            case MEDICATION_TAKEN_KEY: return SymptomMgmtContract.MedicationTakenColumns.TABLE_NAME;
            case MEDICATION_TAKEN_ITEM_KEY: return SymptomMgmtContract.MedicationTakenColumns.TABLE_NAME;
            default: return null;
        }
    }

    private boolean isItemType(Uri uri){
        int typeId = URI_MATCHER.match(uri);
        return !(typeId%2==0);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(logTag(), "Querying URI: "+uri.toString());
        String type = getType(uri);
        if(null!=type) {
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            if(isItemType(uri)){
                String id = uri.getLastPathSegment();
                selectionArgs = new String[]{id};
                selection = SymptomMgmtContract.CommonColumns.CLIENT_ID + " = ? ";
                return db.query(true, getTableFromType(uri), projection, selection,
                        selectionArgs, null, null, sortOrder, null);
            }else{
                Cursor c = db.query(true, getTableFromType(uri), projection, selection,
                        selectionArgs, null, null, sortOrder, null);
                Log.d(logTag(), "returning records: "+c.getCount());
                return c;
            }
        }else{
            Log.d(logTag(), "returning null");
            return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(logTag(), "Inserting URI: "+uri.toString() +" values: "+values);
        String type = getType(uri);
        if(null!=type) {
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            Long id = db.insert(getTableFromType(uri), null, values);
            return Uri.withAppendedPath(uri, id.toString());
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(logTag(), "Updating URI: "+uri.toString()+" values: "+values);
        String type = getType(uri);
        if(null!=type) {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            return db.update(getTableFromType(uri), values, selection, selectionArgs);
        }
        return 0;
    }

    private String logTag(){
        return this.getClass().getName();
    }
}
