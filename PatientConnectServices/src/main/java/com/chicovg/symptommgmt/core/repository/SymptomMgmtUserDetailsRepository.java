package com.chicovg.symptommgmt.core.repository;

import com.chicovg.symptommgmt.core.entity.SymptomMgmtUser;
import com.chicovg.symptommgmt.rest.domain.UserType;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by victorguthrie on 10/15/14.
 */
@Repository
public class SymptomMgmtUserDetailsRepository {

    // TODO this will come from a DB table, users pre-loaded
    private Map<String, SymptomMgmtUser> userMap;
    private long docSequence;
    private long patSequence;

    @PostConstruct
    public void createUsers(){
        userMap = new HashMap<String, SymptomMgmtUser>();
        userMap.put("doctor1", new SymptomMgmtUser("doctor1", new ShaPasswordEncoder().encodePassword("password", null), 0L, UserType.DOCTOR));
        userMap.put("patient1", new SymptomMgmtUser("patient1", new ShaPasswordEncoder().encodePassword("password", null), 0L,  UserType.PATIENT));
    }

    public SymptomMgmtUser getUserByName(String name) {
        return userMap.get(name);
    }

    public SymptomMgmtUser createNewUser(SymptomMgmtUser user){
        userMap.put(user.getUsername(), user);
        if(UserType.DOCTOR.equals(user.getType())){
            user.setProfileId(docSequence++);
        }else{
            user.setProfileId(patSequence++);
        }
        return user;
    }

}
