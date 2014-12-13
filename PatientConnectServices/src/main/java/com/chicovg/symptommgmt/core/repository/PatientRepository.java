package com.chicovg.symptommgmt.core.repository;

import com.chicovg.symptommgmt.core.entity.PatientPo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * Created by victorguthrie on 10/11/14.
 */
@Repository
public interface PatientRepository extends CrudRepository<PatientPo, Long> {

    public Collection<PatientPo> findByFirstNameContainingOrLastNameContaining(String firstName, String lastName);

}
