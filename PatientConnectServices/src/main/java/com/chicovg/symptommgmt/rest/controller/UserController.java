package com.chicovg.symptommgmt.rest.controller;

import com.chicovg.symptommgmt.core.exceptions.UserNameExistsException;
import com.chicovg.symptommgmt.core.service.SymptomMgmtUserService;
import com.chicovg.symptommgmt.rest.api.SymptomMgmtApi;
import com.chicovg.symptommgmt.rest.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.security.PermitAll;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by victorguthrie on 10/12/14.
 */
@Controller
@RequestMapping(SymptomMgmtApi.USER_PATH)
public class UserController {

    @Autowired
    private SymptomMgmtUserService symptomMgmtUserService;

    /**
     *
     * @return the current user if logged in OR an empty user object is not logged in
     */
    @RequestMapping(value = SymptomMgmtApi.LOGIN_PATH, method = GET, produces = {"application/json"})
    @PermitAll
    public ResponseEntity<User> getCurrentUser() {
        User user = symptomMgmtUserService.getCurrentUser();
        return new ResponseEntity<User>(user, HttpStatus.OK);
    }

    @RequestMapping(method = POST, produces = {"application/json"}, consumes = {"application/json"})
    @PermitAll
    public ResponseEntity<User> register(@RequestBody User user){
        try{
            User newUser = symptomMgmtUserService.createUser(user);
            return new ResponseEntity<User>(newUser, HttpStatus.OK);
        } catch (UserNameExistsException e) {
            return new ResponseEntity<User>(user, HttpStatus.BAD_REQUEST);
        }
    }

}
