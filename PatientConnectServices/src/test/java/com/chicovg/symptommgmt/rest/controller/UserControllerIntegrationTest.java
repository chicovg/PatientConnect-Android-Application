package com.chicovg.symptommgmt.rest.controller;

import com.chicovg.symptommgmt.core.exceptions.UserNameExistsException;
import com.chicovg.symptommgmt.core.service.SymptomMgmtUserService;
import com.chicovg.symptommgmt.rest.api.SymptomMgmtApi;
import com.chicovg.symptommgmt.rest.domain.User;
import com.chicovg.symptommgmt.rest.domain.UserType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * Created by victorguthrie on 10/16/14.
 */
public class UserControllerIntegrationTest {

    MockMvc mockMvc;

    @InjectMocks
    private UserController userController;

    @Mock
    private SymptomMgmtUserService symptomMgmtUserService;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        this.mockMvc = standaloneSetup(userController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter()).build();
    }

    @Test
    public void testIsCurrentUserLoggedIn() throws Exception {
        String username = "doctor_who";
        Long id = 1L;

        User userResponse = user(username, id, UserType.DOCTOR);

        when(symptomMgmtUserService.getCurrentUser()).thenReturn(userResponse);

        mockMvc.perform(get(SymptomMgmtApi.USER_PATH + "/" + SymptomMgmtApi.LOGIN_PATH)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value(username));
        Mockito.verify(symptomMgmtUserService).getCurrentUser();
    }

    @Test
    public void testRegister() throws Exception {
        String username = "doctor_who";
        Long id = 1L;

        User userRequest = user(username, "password", id, UserType.DOCTOR);
        User userResponse = user(username, id, UserType.DOCTOR);

        when(symptomMgmtUserService.createUser(Mockito.any(User.class))).thenReturn(userResponse);

        mockMvc.perform(post(SymptomMgmtApi.USER_PATH)
                .content(userToJson(userRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value(username));
        Mockito.verify(symptomMgmtUserService).createUser(Mockito.any(User.class));
    }


    @Test
    public void testRegisterUserNameExists() throws Exception {
        String username = "doctor_who";
        Long id = 1L;

        User userRequest = user(username, "password", id, UserType.DOCTOR);

        when(symptomMgmtUserService.createUser(Mockito.any(User.class))).thenThrow(new UserNameExistsException(username));

        mockMvc.perform(post(SymptomMgmtApi.USER_PATH)
                .content(userToJson(userRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        Mockito.verify(symptomMgmtUserService).createUser(Mockito.any(User.class));
    }


    private static User user(String name, String password, Long id, UserType type){
        User user = new User();
        user.setId(id);
        user.setUsername(name);
        user.setPassword(password);
        user.setType(type);

        return user;
    }

    private static User user(String name, Long id, UserType type){
        User user = new User();
        user.setId(id);
        user.setUsername(name);
        user.setType(type);

        return user;
    }

    private static String userToJson(User user){
        StringBuffer buf = new StringBuffer();
        buf.append("{").append("\"id\":").append(user.getId()).append(",")
                .append("\"username\":\"").append(user.getUsername()).append("\",")
                .append("\"password\":\"").append(user.getPassword()).append("\",")
                .append("\"type\":\"").append(user.getType().toString()).append("\"")
                .append("}");
        return buf.toString();
    }
}
