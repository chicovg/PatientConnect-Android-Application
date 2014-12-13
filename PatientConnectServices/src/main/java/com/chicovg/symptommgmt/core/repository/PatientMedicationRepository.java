package com.chicovg.symptommgmt.core.repository;

import com.chicovg.symptommgmt.core.entity.PatientMedicationPo;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by victorguthrie on 11/15/14.
 */
public interface PatientMedicationRepository extends CrudRepository<PatientMedicationPo, Long> {

}
