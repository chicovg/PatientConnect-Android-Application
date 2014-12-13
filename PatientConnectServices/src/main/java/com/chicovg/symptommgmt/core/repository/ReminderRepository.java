package com.chicovg.symptommgmt.core.repository;

import com.chicovg.symptommgmt.core.entity.PatientReminderPo;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by victorguthrie on 10/11/14.
 */
public interface ReminderRepository extends CrudRepository<PatientReminderPo, Long> {
}
