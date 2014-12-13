package com.chicovg.symptommgmt.rest.controller;

import com.chicovg.symptommgmt.core.service.DoctorService;
import com.chicovg.symptommgmt.rest.api.SymptomMgmtApi;
import com.chicovg.symptommgmt.rest.domain.Doctor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Created by victorguthrie on 10/15/14.
 */
@Controller
@RequestMapping(SymptomMgmtApi.DOCTOR_PATH)
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @RequestMapping(value = "/{id}", method = GET)
    public ResponseEntity<Doctor> getDoctor(@PathVariable Long id) {
        Doctor doctor = doctorService.findDoctorById(id);
        if(null==doctor){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(doctor, HttpStatus.OK);
    }
}
