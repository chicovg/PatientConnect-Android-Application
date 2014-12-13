package com.chicovg.symptommgmt.rest.api;

import com.chicovg.symptommgmt.rest.domain.*;
import retrofit.Callback;
import retrofit.http.*;

import java.util.Collection;

/**
 * Created by victorguthrie on 10/15/14.
 */


public interface SymptomMgmtApi {

    public static final String USER_PATH = "/user";
    public static final String LOGIN_PATH = "/login";
    public static final String DOCTOR_PATH = "/doctor";
    public static final String PATIENT_PATH = "/patient";
    public static final String CHECK_IN_PATH = "/check_in";
    public static final String MEDICATION_PATH = "/medication";
    public static final String MEDICATION_TAKEN_PATH = "/medication_taken";
    public static final String REMINDER_PATH = "/reminder";
    public static final String SEARCH_PATH = "/search";

    public static final String USERNAME_PARAM = "username";
    public static final String PASSWORD_PARAM = "password";
    public static final String ID_PARAM = "id";
    public static final String LAST_UPDT_DTM_PARAM = "lastUpdtDatetime";
    public static final String MED_ID_PARAM = "medId";
    public static final String REMINDER_ID_PARAM = "reminderId";
    public static final String SEARCH_STRING_PARAM = "searchString";

    public static final String DATETIME_FORMAT = "yyyyMMddHHmmssZZ";
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * This service takes a username a password to log the user into the app
     * @param username
     * @param pass
     * @return
     */
    @FormUrlEncoded
    @POST(LOGIN_PATH)
    public void login(@Field(USERNAME_PARAM) String username, @Field(PASSWORD_PARAM) String pass, Callback<User> cb);

    /**
     * A service to determine what user is currently logged for the current session
     *  returns an empty user object if no user is logged in
     * @return
     */
    @GET(USER_PATH+LOGIN_PATH)
    public void getCurrentUser(Callback<User> callback);

    /**
     * This service logs the user out of the app
     * @return
     */
    @DELETE(LOGIN_PATH)
    public void logout(Callback<Void>callback);

    /**
     * Create a new patient
     * @param patient
     * @return
     */
    @POST(PATIENT_PATH)
    public void createPatient(@Body Patient patient, Callback<Patient> callback);

    /**
     * List all patients
     * @return
     */
    @GET(PATIENT_PATH)
    public void getPatients(Callback<Collection<Patient>> callback);

    /**
     * Get the specified patient
     * @param id
     * @return
     */
    @GET(PATIENT_PATH+"/{id}")
    public void getPatient(@Path(ID_PARAM) Long id, Callback<Patient> callback);

    /**
     * Update the specified patient
     * @param id
     * @param patient
     * @return
     */
    @PUT(PATIENT_PATH+"/{id}")
    public void updatePatient(@Path(ID_PARAM) Long id, @Body Patient patient, Callback<Patient> callback);

    /**
     *
     * @param id
     * @return
     */
    @POST(PATIENT_PATH+"/{id}"+CHECK_IN_PATH)
    public void createCheckIn(@Path(ID_PARAM) Long id, @Body CheckIn checkIn, Callback<CheckIn> callback);

    /**
     * Create a new medication taken record
     * @param id
     * @param medicationTaken
     * @return
     */
    @POST(PATIENT_PATH+"/{id}"+MEDICATION_TAKEN_PATH)
    public void createMedicationTaken(@Path(ID_PARAM) Long id, @Body MedicationTaken medicationTaken, Callback<MedicationTaken> callback);

    /**
     * Create a new medication record
     * @param id
     * @param medication
     * @return
     */
    @POST(PATIENT_PATH+"/{id}"+MEDICATION_PATH)
    public void createMedicationForPatient(@Path(ID_PARAM) Long id, @Body Medication medication, Callback<Medication> callback);

    /**
     * Update a medication record for a patient
     * @param id
     * @param medication
     * @return
     */
    @PUT(PATIENT_PATH+"/{id}"+MEDICATION_PATH+"/{medId}")
    public void updateMedicationForPatient(@Path(ID_PARAM) Long id, @Path(MED_ID_PARAM) Long medId, @Body Medication medication, Callback<Medication> callback);

    /**
     * Delete a medication record for a patient
     * @param id
     * @param medId
     * @return
     */
    @DELETE(PATIENT_PATH+"/{id}"+MEDICATION_PATH+"/{medId}")
    public void deleteMedicationForPatient(@Path(ID_PARAM) Long id, @Path(MED_ID_PARAM) Long medId, Callback<Void> callback);

    /**
     * Update a reminder record for a patient
     * @param id
     *
     * @return
     */
    @PUT(PATIENT_PATH+"/{id}"+REMINDER_PATH+"/{reminderId}")
    public void updateReminderForPatient(@Path(ID_PARAM) Long patientId, @Path(REMINDER_ID_PARAM) Long id, @Body Reminder reminder, Callback<Reminder> callback);

    /**
     * Get a doctor by id
     * @param id
     * @return
     */
    @GET(DOCTOR_PATH+"/{id}")
    public void getDoctor(@Path(ID_PARAM) String id, Callback<Doctor> callback);

    /**
     * Update the specified doctor
     * @param id
     * @param doctor
     * @return
     */
    @PUT(DOCTOR_PATH+"/{id}")
    public void updateDoctor(@Path(ID_PARAM) String id, @Body Doctor doctor, Callback<Doctor> callback);

    /**
     * Search for check in data
     * @param searchString
     * @return
     */
    @POST(PATIENT_PATH+CHECK_IN_PATH+SEARCH_PATH)
    public void searchCheckInData(@Query(SEARCH_STRING_PARAM) String searchString, Callback<CheckInSearchResults> callback);


}
