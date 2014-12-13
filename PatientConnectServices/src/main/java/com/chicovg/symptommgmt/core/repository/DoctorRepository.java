package com.chicovg.symptommgmt.core.repository;

import com.chicovg.symptommgmt.core.entity.DoctorPo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by victorguthrie on 10/11/14.
 */
@Repository
public interface DoctorRepository extends CrudRepository<DoctorPo, Long> {
}
