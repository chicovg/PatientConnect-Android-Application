package com.chicovg.symptommgmt.core.service;

import com.chicovg.symptommgmt.core.entity.DoctorPo;
import com.chicovg.symptommgmt.core.repository.DoctorRepository;
import com.chicovg.symptommgmt.rest.domain.Doctor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.chicovg.symptommgmt.core.service.DoctorTranslator.toDoctor;
import static com.chicovg.symptommgmt.core.service.DoctorTranslator.toDoctorPo;

/**
 * Created by victorguthrie on 10/29/14.
 */
@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    public Doctor saveDoctor(Doctor doctor){
        DoctorPo po = doctorRepository.save(toDoctorPo(doctor));
        return toDoctor(po);
    }

    public Doctor findDoctorById(Long doctorId){
        DoctorPo po = doctorRepository.findOne(doctorId);
        return toDoctor(po);
    }

}
