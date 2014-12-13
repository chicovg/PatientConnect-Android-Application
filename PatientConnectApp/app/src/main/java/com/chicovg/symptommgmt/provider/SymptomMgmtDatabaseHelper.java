package com.chicovg.symptommgmt.provider;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by victorguthrie on 10/30/14.
 */
public class SymptomMgmtDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_PATH = "/data/data/com.chicovg.symptommgmt/databases/";
    private static final String DB_NAME = "symptommgmt.db";

    public SymptomMgmtDatabaseHelper(Context context){
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(logTag(), "Creating database: "+ DB_NAME);
        db.execSQL(SymptomMgmtContract.PatientColumns.CREATE_STMT);
        db.execSQL(SymptomMgmtContract.CheckInColumns.CREATE_STMT);
        db.execSQL(SymptomMgmtContract.CheckInResponseColumns.CREATE_STMT);
        db.execSQL(SymptomMgmtContract.ReminderColumns.CREATE_STMT);
        db.execSQL(SymptomMgmtContract.MedicationColumns.CREATE_STMT);
        db.execSQL(SymptomMgmtContract.MedicationTakenColumns.CREATE_STMT);
        db.execSQL(SymptomMgmtContract.DoctorColumns.CREATE_STMT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void createDataBase(Context context){
        boolean dbExist = checkDatabase();
        SQLiteDatabase db = this.getWritableDatabase();

        /*if(!dbExist){
            this.getReadableDatabase();
            try {
                copyDatabase(context, DB_NAME);
            } catch (IOException e) {
                Log.e(logTag(), "Error copying database");
                throw new Error("Error copying database");
            }
        }*/

    }

    private boolean checkDatabase(){
        SQLiteDatabase checkDB = null;
        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }catch(SQLiteException e){
            Log.w(logTag(), "Database does not exist, it will be copied from assets");
        }

        if(checkDB != null){
            checkDB.close();
        }

        return checkDB != null;
    }

    public void copyDatabase(Context context, String databaseName) throws IOException{
        final File dbPath = context.getDatabasePath(databaseName);

        // Make sure we have a path to the file
        dbPath.getParentFile().mkdirs();

        // Try to copy database file
        AssetManager am = context.getResources().getAssets();
        final InputStream inputStream = context.getAssets().open(databaseName);
        final OutputStream output = new FileOutputStream(dbPath);

        byte[] buffer = new byte[8192];
        int length;

        while ((length = inputStream.read(buffer, 0, 8192)) > 0) {
            output.write(buffer, 0, length);
        }

        output.flush();
        output.close();
        inputStream.close();
    }

    private String logTag(){
        return this.getClass().getName();
    }
}
