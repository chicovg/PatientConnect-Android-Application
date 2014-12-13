package com.chicovg.symptommgmt.core.service;

import com.chicovg.symptommgmt.core.entity.SymptomMgmtUser;
import com.chicovg.symptommgmt.core.exceptions.UserNameExistsException;
import com.chicovg.symptommgmt.core.repository.SymptomMgmtUserDetailsRepository;
import com.chicovg.symptommgmt.rest.domain.User;
import com.chicovg.symptommgmt.rest.domain.UserType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by victorguthrie on 10/16/14.
 */
public class SymptomMgmtUserServiceTest {

    @Mock
    private SymptomMgmtUserDetailsRepository symptomMgmtUserDetailsRepository;

    @InjectMocks
    private SymptomMgmtUserService symptomUserDetailsService;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testLoadUserByUsername(){
        String username = "doctor";
        when(symptomMgmtUserDetailsRepository.getUserByName(username)).thenReturn(doctor(username, 1l));

        symptomUserDetailsService.loadUserByUsername(username);
        verify(symptomMgmtUserDetailsRepository).getUserByName(username);
    }

    @Test
    public void testLoadUserByUsernameNotFound(){
        String username = "user";
        when(symptomMgmtUserDetailsRepository.getUserByName(username)).thenReturn(null);

        try{
            symptomUserDetailsService.loadUserByUsername(username);
        }catch (UsernameNotFoundException unfe){
            assertTrue(true);
        }
        verify(symptomMgmtUserDetailsRepository).getUserByName(username);
    }

    @Test
    public void testCreateUser(){
        String username = "newTestUser";
        when(symptomMgmtUserDetailsRepository.getUserByName(username)).thenReturn(null);
        when(symptomMgmtUserDetailsRepository.createNewUser(any(SymptomMgmtUser.class))).thenReturn(patient(username, 1l));

        User newPatient = user(username, "pass", UserType.PATIENT);
        User createdUser = new User();

        try {
            createdUser = symptomUserDetailsService.createUser(newPatient);
        } catch (UserNameExistsException e) {
            fail("Exception thrown "+e);
        }

        assertEquals(newPatient.getUsername(), createdUser.getUsername());
        assertEquals(newPatient.getType(), createdUser.getType());
        assertEquals(1L, createdUser.getId());
        assertNull(createdUser.getPassword());

        verify(symptomMgmtUserDetailsRepository).getUserByName(username);
        verify(symptomMgmtUserDetailsRepository).createNewUser(any(SymptomMgmtUser.class));
    }

    @Test
    public void testCreateUserExists(){
        String username = "newTestUser";
        when(symptomMgmtUserDetailsRepository.getUserByName(username)).thenReturn(patient(username, 1l));

        User newPatient = user(username, "pass", UserType.PATIENT);

        try {
            symptomUserDetailsService.createUser(newPatient);
        } catch (UserNameExistsException e) {
            assertTrue(true);
        }

        verify(symptomMgmtUserDetailsRepository).getUserByName(username);
    }

    private static SymptomMgmtUser doctor(String name, Long id){
        return new SymptomMgmtUser(name, new ShaPasswordEncoder().encodePassword("password", null), id, UserType.DOCTOR);
    }

    private static SymptomMgmtUser patient(String name, Long id) {
        return new SymptomMgmtUser(name, new ShaPasswordEncoder().encodePassword("password", null), id, UserType.PATIENT);
    }

    private static User user(String name, String password, UserType type){
        User user = new User();
        user.setUsername(name);
        user.setPassword(password);
        user.setType(type);

        return user;
    }
}
