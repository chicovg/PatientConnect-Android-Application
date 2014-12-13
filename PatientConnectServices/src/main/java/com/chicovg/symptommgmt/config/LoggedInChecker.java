package com.chicovg.symptommgmt.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Component
public class LoggedInChecker {
    /*public SymptomMgmtUser getLoggedInUser() {
        SymptomMgmtUser user = null;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();

            // principal can be "anonymousUser" (String)

        }

        return user;
    }*/
}
