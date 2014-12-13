package com.chicovg.symptommgmt.core.repository;

import com.chicovg.symptommgmt.core.entity.MessagePo;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by victorguthrie on 10/11/14.
 */
public interface MessageRepository extends CrudRepository<MessagePo, Long> {
}
