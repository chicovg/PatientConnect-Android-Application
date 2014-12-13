package com.chicovg.symptommgmt.core.repository;

import com.chicovg.symptommgmt.core.entity.SymptomMgmtUser;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by victorguthrie on 11/1/14.
 */
public interface UserRepository extends CrudRepository<SymptomMgmtUser, String> {
}
