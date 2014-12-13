package com.chicovg.symptommgmt.core.service;

import com.chicovg.symptommgmt.core.entity.SymptomMgmtUser;
import com.chicovg.symptommgmt.core.exceptions.UserNameExistsException;
import com.chicovg.symptommgmt.core.repository.UserRepository;
import com.chicovg.symptommgmt.rest.domain.User;
import com.chicovg.symptommgmt.rest.domain.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Created by victorguthrie on 10/15/14.
 */
@Service
public class SymptomMgmtUserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SymptomMgmtUser userDetails = userRepository.findOne(username);
        if(null!=userDetails){
            return userDetails;
        }else{
            throw new UsernameNotFoundException(username+" not found!");
        }
    }

    public User getCurrentUser(){

        User user = new User();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(null != authentication){
            Object principal = authentication.getPrincipal();
            if (principal instanceof SymptomMgmtUser) {
                SymptomMgmtUser userDetails = (SymptomMgmtUser) principal;
                user = translateToUser(userDetails);
            }
        }

        return user;
    }

    public User createUser(User user) throws UserNameExistsException{
        String username = user.getUsername();
        SymptomMgmtUser symptomMgmtUser = userRepository.findOne(username);
        if(null!=symptomMgmtUser){
            throw new UserNameExistsException(username);
        }
        symptomMgmtUser = userRepository.save(translateToSymptomMgmtUser(user));
        return translateToUser(symptomMgmtUser);
    }

    private User translateToUser(SymptomMgmtUser symptomMgmtUser){
        User user = new User();
        String username = symptomMgmtUser.getUsername();
        UserType type = symptomMgmtUser.getType();
        Long id = symptomMgmtUser.getProfileId();
        if(null!=username && null!=type && null!=id){
            user.setType(type);
            user.setUsername(username);
            user.setId(id);
        }
        return user;
    }

    private SymptomMgmtUser translateToSymptomMgmtUser(User user){
        return new SymptomMgmtUser(user.getUsername(), user.getPassword(), user.getId(), user.getType());
    }
}
