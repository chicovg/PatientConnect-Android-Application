package com.chicovg.symptommgmt.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.chicovg.symptommgmt.R;
import com.chicovg.symptommgmt.rest.api.SymptomMgmtApi;
import com.chicovg.symptommgmt.rest.domain.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.ApacheClient;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static com.chicovg.symptommgmt.service.ContentService.*;
import static com.chicovg.symptommgmt.service.NotificationService.sendPatientAlerts;
import static com.chicovg.symptommgmt.service.NotificationService.setRemindersForPatient;
import static com.chicovg.symptommgmt.util.BundleUtil.fromBundle;
import static com.chicovg.symptommgmt.util.BundleUtil.toBundle;

/**
 * A service for updating patient data in the background
 */
public class HttpService extends IntentService {

    private static RestAdapter restAdapter;
    private static Long mId;
    private static User mUser;

    public static final String ACTION_LOGIN = "com.chicovg.symptommgmt.service.action.LOGIN";
    public static final String ACTION_LOGOUT = "com.chicovg.symptommgmt.service.action.LOGOUT";
    public static final String ACTION_PULL_PATIENT = "com.chicovg.symptommgmt.service.action.PULL_PATIENT";
    public static final String ACTION_PUSH_PATIENT = "com.chicovg.symptommgmt.service.action.PUSH_PATIENT";
    public static final String ACTION_PUSH_CHECK_IN = "com.chicovg.symptommgmt.service.action.PUSH_CHECK_IN";
    public static final String ACTION_PUSH_REMINDER = "com.chicovg.symptommgmt.service.action.PUSH_REMINDER";
    public static final String ACTION_PULL_DOCTOR = "com.chicovg.symptommgmt.service.action.PULL_DOCTOR";
    public static final String ACTION_PUSH_DOCTOR = "com.chicovg.symptommgmt.service.action.PUSH_DOCTOR";
    public static final String ACTION_CHECK_IN_SEARCH = "com.chicovg.symptommgmt.service.action.CHECK_IN_SEARCH";
    public static final String ACTION_NEW_MEDICATION = "com.chicovg.symptommgmt.service.action.NEW_MEDICATION";
    public static final String ACTION_UPDATE_MEDICATION = "com.chicovg.symptommgmt.service.action.UPDATE_MEDICATION";
    public static final String ACTION_DELETE_MEDICATION = "com.chicovg.symptommgmt.service.action.DELETE_MEDICATION";

    private static final String USERNAME_extra = "com.chicovg.symptommgmt.service.extra.USERNAME";
    private static final String PASSWORD_extra = "com.chicovg.symptommgmt.service.extra.PASSWORD";
    private static final String PATIENT_ID_extra = "com.chicovg.symptommgmt.service.extra.PATIENT_ID";
    private static final String PATIENT_extra = "com.chicovg.symptommgmt.service.extra.PATIENT";
    private static final String CHECK_IN_extra = "com.chicovg.symptommgmt.service.extra.CHECK_IN";
    private static final String REMINDER_extra = "com.chicovg.symptommgmt.service.extra.REMINDER";
    private static final String DOCTOR_ID_extra = "com.chicovg.symptommgmt.service.extra.DOCTOR_ID";
    private static final String DOCTOR_extra = "com.chicovg.symptommgmt.service.extra.DOCTOR";
    private static final String SEARCH_STRING_extra = "com.chicovg.symptommgmt.service.extra.DOCTOR";
    private static final String MEDICATION_ID_extra = "com.chicovg.symptommgmt.service.extra.MEDICATION_ID";
    private static final String MEDICATION_extra = "com.chicovg.symptommgmt.service.extra.MEDICATION";

    public static final String BROADCAST_NO_CONNECTION = "com.chicovg.symptommgmt.service.broadcast.NO_CONNECTION";
    public static final String BROADCAST_LOGIN_FAILED = "com.chicovg.symptommgmt.service.broadcast.LOGIN_FAILED";
    public static final String BROADCAST_LOGOUT_SUCCESS = "com.chicovg.symptommgmt.service.broadcast.LOGOUT_SUCCESS";
    public static final String BROADCAST_HTTP_REQUEST_SUCCESS = "com.chicovg.symptommgmt.service.broadcast.HTTP_REQUEST_SUCCESS";
    public static final String BROADCAST_HTTP_REQUEST_FAILED = "com.chicovg.symptommgmt.service.broadcast.HTTP_REQUEST_FAILED";

    private DateFormat dateFormat = new SimpleDateFormat(SymptomMgmtApi.DATETIME_FORMAT);

    /**
     * Starts the service to log into the application
     * @param context
     * @param username
     * @param password
     */
    public static void login(Context context, String username, String password) {
        Intent intent = new Intent(context, HttpService.class);
        intent.setAction(ACTION_LOGIN);
        intent.putExtra(USERNAME_extra, username);
        intent.putExtra(PASSWORD_extra, password);
        context.startService(intent);
    }

    /**
     * Starts the service to log out of the application
     */
    public static void logout(Context context) {
        Intent intent = new Intent(context, HttpService.class);
        intent.setAction(ACTION_LOGOUT);
        context.startService(intent);
    }

    /**
     * Starts the service to pull the latest patient info from the server
     * @param context
     * @param patientId
     */
    public static void pullPatient(Context context, Long patientId) {
        Intent intent = new Intent(context, HttpService.class);
        intent.setAction(ACTION_PULL_PATIENT);
        intent.putExtra(PATIENT_ID_extra, patientId);
        context.startService(intent);
    }

    /**
     * Starts the service to push patient updates to the server
     * @param context
     * @param patient
     */
    public static void pushPatient(Context context, Patient patient){
        Intent intent = new Intent(context, HttpService.class);
        intent.setAction(ACTION_PUSH_PATIENT);
        intent.putExtra(PATIENT_extra, toBundle(patient));
        context.startService(intent);
    }

    /**
     * Starts the service to push a check in to the server
     * @param context
     * @param checkIn
     */
    public static void pushCheckIn(Context context, CheckIn checkIn){
        Intent intent = new Intent(context, HttpService.class);
        intent.setAction(ACTION_PUSH_CHECK_IN);
        intent.putExtra(CHECK_IN_extra, toBundle(checkIn));
        context.startService(intent);
    }

    /**
     * Starts the service to push a reminder to the server
     * @param context
     * @param reminder
     */
    public static void pushReminder(Context context, Reminder reminder){
        Intent intent = new Intent(context, HttpService.class);
        intent.setAction(ACTION_PUSH_REMINDER);
        intent.putExtra(REMINDER_extra, toBundle(reminder));
        context.startService(intent);
    }

    /**
     * Starts the service to pull the latest doctor info from the server
     * @param context
     * @param doctorId
     */
    public static void pullDoctor(Context context, Long doctorId) {
        Intent intent = new Intent(context, HttpService.class);
        intent.setAction(ACTION_PULL_DOCTOR);
        intent.putExtra(DOCTOR_ID_extra, doctorId);
        context.startService(intent);
    }

    /**
     * Starts the service to push doctor updates to the server
     * @param context
     * @param doctor
     */
    public static void pushDoctor(Context context, Doctor doctor){
        Intent intent = new Intent(context, HttpService.class);
        intent.setAction(ACTION_PULL_DOCTOR);
        intent.putExtra(DOCTOR_extra, toBundle(doctor));
        context.startService(intent);
    }

    public static void searchCheckInData(Context context, String searchString){
        Intent intent = new Intent(context, HttpService.class);
        intent.setAction(ACTION_CHECK_IN_SEARCH);
        intent.putExtra(SEARCH_STRING_extra, searchString);
        context.startService(intent);
    }

    public static void updateMedication(Context context, long patientId, long medicationId, Medication medication){
        Intent intent = new Intent(context, HttpService.class);
        intent.setAction(ACTION_UPDATE_MEDICATION);
        intent.putExtra(PATIENT_ID_extra, patientId);
        intent.putExtra(MEDICATION_ID_extra, medicationId);
        intent.putExtra(MEDICATION_extra, toBundle(medication));
        context.startService(intent);
    }

    public static void deleteMedication(Context context, long patientId, long medicationId){
        Intent intent = new Intent(context, HttpService.class);
        intent.setAction(ACTION_DELETE_MEDICATION);
        intent.putExtra(PATIENT_ID_extra, patientId);
        intent.putExtra(MEDICATION_ID_extra, medicationId);
        context.startService(intent);
    }

    public static void createMedication(Context context, long patientId, Medication medication){
        Intent intent = new Intent(context, HttpService.class);
        intent.setAction(ACTION_NEW_MEDICATION);
        intent.putExtra(PATIENT_ID_extra, patientId);
        intent.putExtra(MEDICATION_extra, toBundle(medication));
        context.startService(intent);
    }

    public HttpService() {
        super("SymptomMgmt-HttpService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        long patientId = intent.getLongExtra(PATIENT_ID_extra, -1L);
        long doctorId = intent.getLongExtra(DOCTOR_ID_extra, -1L);
        if (intent != null) {
            final String action = intent.getAction();
            if(!connectionExists()){
                sendBroadcastNoConnection(action);
            } else if (ACTION_LOGIN.equals(action)) {
                handleLogin(intent);
            } else if (ACTION_LOGOUT.equals(action)) {
                handleLogout(intent);
            } else if (ACTION_PULL_PATIENT.equals(action)) {
                handlePullPatient(patientId);
            } else if (ACTION_PUSH_PATIENT.equals(action)) {
                Patient patient = (Patient)fromBundle(intent.getBundleExtra(PATIENT_extra));
                handlePushPatient(patient);
            } else if (ACTION_PUSH_CHECK_IN.equals(action)) {
                CheckIn checkIn = (CheckIn)fromBundle(intent.getBundleExtra(CHECK_IN_extra));
                handlePushCheckIn(checkIn);
            } else if (ACTION_PUSH_REMINDER.equals(action)) {
                Reminder reminder = (Reminder)fromBundle(intent.getBundleExtra(REMINDER_extra));
                handlePushReminder(reminder);
            } else if (ACTION_PULL_DOCTOR.equals(action)){
                handlePullDoctor(doctorId);
            } else if (ACTION_PUSH_DOCTOR.equals(action)){

            } else if (ACTION_CHECK_IN_SEARCH.equals(action)){
                handleCheckInSearch(intent.getStringExtra(SEARCH_STRING_extra));
            } else if(ACTION_NEW_MEDICATION.equals(action)){
                Medication medication = (Medication)fromBundle(intent.getBundleExtra(MEDICATION_extra));
                handleCreateMedication(patientId, medication);
            } else if(ACTION_UPDATE_MEDICATION.equals(action)){
                long medicationId = intent.getLongExtra(MEDICATION_ID_extra, 0);
                Medication medication = (Medication)fromBundle(intent.getBundleExtra(MEDICATION_extra));
                handleUpdateMedication(patientId, medicationId, medication);
            } else if(ACTION_DELETE_MEDICATION.equals(action)){
                long medicationId = intent.getLongExtra(MEDICATION_ID_extra, 0);
                handleDeleteMedication(patientId, medicationId);
            }
        }
    }

    /**
     *
     * @param intent
     */
    private void handleLogin(Intent intent) {
        final String username = intent.getStringExtra(USERNAME_extra);
        final String password = intent.getStringExtra(PASSWORD_extra);
        if(null==username || null==password){
            Log.e(logTag(), "Username or password was null, cannot login");
            HttpService.this.sendBroadcastMessage(BROADCAST_LOGIN_FAILED);
            return;
        }
        getService().login(username, password, new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                mId = user.getId();
                mUser = user;
                switch (user.getType()) {
                    case PATIENT:
                        handlePullPatient(mId);
                        break;
                    case DOCTOR:
                        handlePullDoctor(mId);
                        break;
                }
            }

            @Override
            public void failure(RetrofitError error) {
                sendBroadcastMessage(BROADCAST_LOGIN_FAILED);
            }
        });
    }

    /**
     *
     * @param intent
     */
    private void handleLogout(Intent intent) {
        getService().logout(new Callback<Void>() {
            @Override
            public void success(Void aVoid, Response response) {
                sendBroadcastMessage(BROADCAST_LOGOUT_SUCCESS);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(logTag(), "Logout failed!");
            }
        });
    }

    /**
     * Pull patient data from the server and update SQLite
     */
    private void handlePullPatient(long patientId) {
        Log.d(logTag(), "Pulling Patient");
        getService().getPatient(patientId, new Callback<Patient>() {
            @Override
            public void success(Patient patient, Response response) {
                patient = saveAndReturnPatient(getApplicationContext(), patient);
                sendBroadcastHttpRequestSuccess(ACTION_PULL_PATIENT, toBundle(patient));
                setRemindersForPatient(getApplicationContext(), patient.getId());
            }

            @Override
            public void failure(RetrofitError error) {
                int status = error.getResponse().getStatus();
                String reason = error.getResponse().getReason();
                sendBroadcastHttpRequestFailed(ACTION_PULL_PATIENT, status, reason);
            }
        });
    }

    /**
     * Push the specified patient to the server
     * @param patient
     */
    private void handlePushPatient(Patient patient){
        Log.d(logTag(), "Updating Patient");
        getService().updatePatient(patient.getId(), patient, new Callback<Patient>() {
            @Override
            public void success(Patient patient, Response response) {
                patient = saveAndReturnPatient(getApplicationContext(), patient);
                sendBroadcastHttpRequestSuccess(ACTION_PUSH_PATIENT, toBundle(patient));
            }

            @Override
            public void failure(RetrofitError error) {
                int status = error.getResponse().getStatus();
                String reason = error.getResponse().getReason();
                sendBroadcastHttpRequestFailed(ACTION_PUSH_PATIENT, status, reason);
            }
        });
    }

    /**
     * Push the specified check in record to the server
     * @param checkIn
     */
    private void handlePushCheckIn(CheckIn checkIn) {
        Log.d(logTag(), "Creating new Check In record");
        getService().createCheckIn(checkIn.getPatientId(), checkIn, new Callback<CheckIn>() {
            @Override
            public void success(CheckIn checkIn, Response response) {
                saveCheckIn(getApplicationContext(), checkIn);
                sendBroadcastHttpRequestSuccess(ACTION_PUSH_CHECK_IN, new Bundle());
            }

            @Override
            public void failure(RetrofitError error) {
                int status = error.getResponse().getStatus();
                String reason = error.getResponse().getReason();
                sendBroadcastHttpRequestFailed(ACTION_PUSH_CHECK_IN, status, reason);
            }
        });
    }

    /**
     * Push the specified reminder record to the server
     * @param reminder
     */
    private void handlePushReminder(Reminder reminder) {
        Log.d(logTag(), "Updating Reminder");
        getService().updateReminderForPatient(reminder.getPatientId(), reminder.getId(), reminder, new Callback<Reminder>() {
            @Override
            public void success(Reminder reminder, Response response) {
                saveReminder(getApplicationContext(), reminder);
                setRemindersForPatient(getApplicationContext(), reminder.getPatientId());
                sendBroadcastHttpRequestSuccess(ACTION_PUSH_REMINDER, new Bundle());
            }

            @Override
            public void failure(RetrofitError error) {
                int status = error.getResponse().getStatus();
                String reason = error.getResponse().getReason();
                sendBroadcastHttpRequestFailed(ACTION_PUSH_CHECK_IN, status, reason);
            }
        });
    }

    /**
     * Pull patient data from the server and update SQLite
     */
    private void handlePullDoctor(long doctorId) {
        Log.d(logTag(), "Pulling Doctor");
        getService().getDoctor(String.valueOf(doctorId), new Callback<Doctor>() {
            @Override
            public void success(Doctor doctor, Response response) {
                sendPatientAlerts(getApplicationContext(), doctor.getPatients());
                doctor = saveAndReturnDoctor(getApplicationContext(), doctor);
                sendBroadcastHttpRequestSuccess(ACTION_PULL_DOCTOR, toBundle(doctor));
            }

            @Override
            public void failure(RetrofitError error) {
                int status = error.getResponse().getStatus();
                String reason = error.getResponse().getReason();
                sendBroadcastHttpRequestFailed(ACTION_PULL_DOCTOR, status, reason);
            }
        });
    }

    private void handleCheckInSearch(String searchString){
        getService().searchCheckInData(searchString, new Callback<CheckInSearchResults>() {
            @Override
            public void success(CheckInSearchResults checkInSearchResults, Response response) {
                sendBroadcastHttpRequestSuccess(ACTION_CHECK_IN_SEARCH, toBundle(checkInSearchResults));
            }

            @Override
            public void failure(RetrofitError error) {
                int status = error.getResponse().getStatus();
                String reason = error.getResponse().getReason();
                sendBroadcastHttpRequestFailed(ACTION_CHECK_IN_SEARCH, status, reason);
            }
        });
    }

    private void handleCreateMedication(long patientId, Medication medication){
        getService().createMedicationForPatient(patientId, medication, new Callback<Medication>() {
            @Override
            public void success(Medication medication, Response response) {
                saveMedication(getApplicationContext(), medication);
                sendBroadcastHttpRequestSuccess(ACTION_NEW_MEDICATION, new Bundle());
            }

            @Override
            public void failure(RetrofitError error) {
                int status = error.getResponse().getStatus();
                String reason = error.getResponse().getReason();
                sendBroadcastHttpRequestFailed(ACTION_NEW_MEDICATION, status, reason);
            }
        });
    }

    private void handleUpdateMedication(long patientId, long medicationId, Medication medication){
        getService().updateMedicationForPatient(patientId, medicationId, medication, new Callback<Medication>() {
            @Override
            public void success(Medication medication, Response response) {
                saveMedication(getApplicationContext(), medication);
                sendBroadcastHttpRequestSuccess(ACTION_UPDATE_MEDICATION, new Bundle());
            }

            @Override
            public void failure(RetrofitError error) {
                int status = error.getResponse().getStatus();
                String reason = error.getResponse().getReason();
                sendBroadcastHttpRequestFailed(ACTION_UPDATE_MEDICATION, status, reason);
            }
        });
    }

    private void handleDeleteMedication(long patientId, long medicationId){
       getService().deleteMedicationForPatient(patientId, medicationId, new Callback<Void>() {
           @Override
           public void success(Void aVoid, Response response) {
               sendBroadcastHttpRequestSuccess(ACTION_DELETE_MEDICATION, new Bundle());
           }

           @Override
           public void failure(RetrofitError error) {
               int status = error.getResponse().getStatus();
               String reason = error.getResponse().getReason();
               sendBroadcastHttpRequestFailed(ACTION_DELETE_MEDICATION, status, reason);
           }
       });
    }

    /**
     * Checks to see if the device has an internet connection
     * @return
     */
    private boolean connectionExists(){
        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private SymptomMgmtApi getService(){
        if(null==restAdapter){
            Gson gson = new GsonBuilder()
                    .setDateFormat(SymptomMgmtApi.DATETIME_FORMAT)
                    .create();

            restAdapter = new RestAdapter.Builder()
                    .setClient(new ApacheClient())
                    .setEndpoint("http://10.0.2.2:8080")
                    //.setEndpoint("https://localhost:8443")
                    .setConverter(new GsonConverter(gson))
                    .setLogLevel(RestAdapter.LogLevel.FULL).build();
        }
        return restAdapter.create(SymptomMgmtApi.class);
    }

    private void sendBroadcastHttpRequestFailed(String action, int status, String message){
        Intent intent = new Intent(BROADCAST_HTTP_REQUEST_FAILED);
        intent.putExtra(getString(R.string.ACTION_EXTRA), action);
        intent.putExtra(getString(R.string.HTTP_STATUS_EXTRA), status);
        intent.putExtra(getString(R.string.HTTP_FAILURE_REASON_EXTRA), message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendBroadcastHttpRequestSuccess(String action, Bundle bundle){
        Intent intent = new Intent(BROADCAST_HTTP_REQUEST_SUCCESS);
        intent.putExtra(getString(R.string.ACTION_EXTRA), action);
        intent.putExtra(getString(R.string.USER_EXTRA), toBundle(mUser));
        intent.putExtra(getString(R.string.HTTP_RESPONSE_ENTITY_EXTRA), bundle);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendBroadcastNoConnection(String action){
        Intent intent = new Intent(BROADCAST_NO_CONNECTION);
        intent.putExtra(getString(R.string.ACTION_EXTRA), action);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendBroadcastMessage(String action){
        Intent intent = new Intent(action);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private String logTag(){
        return this.getClass().getName();
    }
}
