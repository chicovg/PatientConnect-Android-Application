package com.chicovg.symptommgmt.rest.controller;

import com.chicovg.symptommgmt.core.service.PatientService;
import com.chicovg.symptommgmt.rest.api.SymptomMgmtApi;
import com.chicovg.symptommgmt.rest.domain.Patient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * Created by victorguthrie on 10/16/14.
 */
public class PatientControllerIntegrationTest {

    MockMvc mockMvc;

    @InjectMocks
    private PatientController patientController;

    @Mock
    private PatientService patientService;


    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);

        this.mockMvc = standaloneSetup(patientController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter()).build();
    }

    @Test
    public void testCreatePatient(){
        Patient patient = patient();
        Patient patient1 = patient();
        patient1.setId(1L);

        when(patientService.savePatient(any(Patient.class))).thenReturn(patient1);

        try {
            mockMvc.perform(post(SymptomMgmtApi.PATIENT_PATH)
                    .content(patientToJson(patient))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.username").value(patient.getUsername()));
        } catch (Exception e) {
            fail();
        }

        verify(patientService).savePatient(any(Patient.class));
    }

    @Test
    public void testFindPatientById(){
        Patient patient = patient(1L);

        when(patientService.findPatientById(1L)).thenReturn(patient);

        try {
            mockMvc.perform(get(SymptomMgmtApi.PATIENT_PATH+"/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1));
        } catch (Exception e) {
            fail();
        }

        verify(patientService).findPatientById(1L);
    }

    @Test
    public void testFindPatientByIdNotFound(){
        when(patientService.findPatientById(1L)).thenReturn(null);
        try {
            mockMvc.perform(get(SymptomMgmtApi.PATIENT_PATH+"/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            fail();
        }
        verify(patientService).findPatientById(1L);
    }

    @Test
    public void testUpdatePatient(){
        Patient patient = patient(1L);

        when(patientService.findPatientById(1L)).thenReturn(patient);
        when(patientService.savePatient(any(Patient.class))).thenReturn(patient);

        try {
            mockMvc.perform(put(SymptomMgmtApi.PATIENT_PATH+"/1")
                    .content(patientToJson(patient))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1));
        } catch (Exception e) {
            fail();
        }
        verify(patientService).findPatientById(1L);
        verify(patientService).savePatient(any(Patient.class));
    }

    @Test
    public void testUpdatePatientNotFound(){
        when(patientService.findPatientById(1L)).thenReturn(null);
        try {
            mockMvc.perform(put(SymptomMgmtApi.PATIENT_PATH + "/1")
                    .content(patientToJson(patient()))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            fail();
        }
        verify(patientService).findPatientById(1L);
    }

    /*@Test
    public void testFindPatientUpdate(){
        Date now = new Date();
        Patient patient = patient(1L);
        patient.setLastUpdtDatetime(now);
        Date halfHourAgo = DateUtils.addMinutes(now, -30);
        DateFormat format = new SimpleDateFormat(SymptomMgmtApi.DATETIME_FORMAT);
        when(patientService.findPatientById(1L)).thenReturn(patient);
        try {
            mockMvc.perform(get(SymptomMgmtApi.PATIENT_PATH + "/1" + SymptomMgmtApi.UPDATES_PATH)
                    .param(SymptomMgmtApi.LAST_UPDT_DTM_PARAM, format.format(halfHourAgo))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1));
        } catch (Exception e) {
            fail();
        }

    }*/

    private Patient patient(){
        Patient patient = new Patient();
        patient.setUsername("patient1");
        patient.setEmailAddress("patient1@gmail.com");
        patient.setFirstName("Bob");
        patient.setLastName("Smith");
        patient.setPhoneNumber("703-324-5678");
        patient.setCheckInStreak(1);
        patient.setPoints(5000);
        patient.setPointsThisWeek(100);
        patient.setPointsThisMonth(1000);
        return patient;
    }

    private Patient patient(long id){
        Patient patient = patient();
        patient.setId(id);
        return patient;
    }

    private String patientToJson(Patient patient){
        StringBuffer buf = new StringBuffer();
        buf.append("{").append("\"username\":\"").append(patient.getUsername()).append("\",")
                .append("\"firstName\":\"").append(patient.getFirstName()).append("\",")
                .append("\"lastName\":\"").append(patient.getLastName()).append("\",")
                .append("\"emailAddress\":\"").append(patient.getEmailAddress()).append("\",")
                .append("\"phoneNumber\":\"").append(patient.getPhoneNumber()).append("\",")
                .append("\"points\":").append(patient.getPoints()).append(",")
                .append("\"pointsThisWeek\":").append(patient.getPointsThisWeek()).append(",")
                .append("\"pointsThisMonth\":").append(patient.getPointsThisMonth()).append(",")
                .append("\"checkInStreak\":").append(patient.getCheckInStreak()).append("")
                .append("}");
        return buf.toString();
    }

}
