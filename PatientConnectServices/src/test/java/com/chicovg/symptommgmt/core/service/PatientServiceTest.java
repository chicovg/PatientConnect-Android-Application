package com.chicovg.symptommgmt.core.service;

import com.chicovg.symptommgmt.core.entity.PatientPo;
import com.chicovg.symptommgmt.core.repository.PatientRepository;
import com.chicovg.symptommgmt.rest.domain.Patient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Created by victorguthrie on 10/30/14.
 */
public class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSavePatient(){
        when(patientRepository.save(any(PatientPo.class))).thenReturn(new PatientPo());
        Patient patient = patientService.savePatient(new Patient());
        assertNotNull(patient);
        verify(patientRepository).save(any(PatientPo.class));
    }

    @Test
    public void testFindPatientById(){
        when(patientRepository.findOne(1L)).thenReturn(new PatientPo());
        Patient patient = patientService.findPatientById(1L);
        verify(patientRepository).findOne(1L);
    }

}
