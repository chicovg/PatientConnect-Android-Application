package com.chicovg.symptommgmt.core.repository;

import com.chicovg.symptommgmt.core.entity.PatientMedicationPo;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by victorguthrie on 11/25/14.
 */
public interface MedicationRepository extends CrudRepository<PatientMedicationPo, Long> {
}
