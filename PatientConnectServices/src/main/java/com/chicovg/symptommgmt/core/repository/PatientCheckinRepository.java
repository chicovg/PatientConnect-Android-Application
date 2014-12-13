package com.chicovg.symptommgmt.core.repository;

import com.chicovg.symptommgmt.core.entity.PatientCheckinPo;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by victorguthrie on 11/14/14.
 */
public interface PatientCheckinRepository extends CrudRepository<PatientCheckinPo, Long> {

}
