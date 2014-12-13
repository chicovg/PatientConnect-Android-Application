package com.chicovg.symptommgmt.config;

import com.chicovg.symptommgmt.core.entity.SymptomMgmtUser;
import com.chicovg.symptommgmt.rest.domain.User;
import com.chicovg.symptommgmt.rest.domain.UserType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class AuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthSuccessHandler.class);

    private final ObjectMapper mapper;

    @Autowired
    AuthSuccessHandler(MappingJackson2HttpMessageConverter messageConverter) {
        this.mapper = messageConverter.getObjectMapper();
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_OK);

        User user = new User();
        if(null != authentication){
            Object principal = authentication.getPrincipal();
            if (principal instanceof SymptomMgmtUser) {
                SymptomMgmtUser userDetails = (SymptomMgmtUser) principal;
                user = translateToUser(userDetails);
            }
        }

        PrintWriter writer = response.getWriter();
        mapper.writeValue(writer, user);
        writer.flush();
    }

    private User translateToUser(SymptomMgmtUser symptomMgmtUser){
        User user = new User();
        String username = symptomMgmtUser.getUsername();
        UserType type = symptomMgmtUser.getType();
        long id = symptomMgmtUser.getProfileId();
        if(null!=username && null!=type){
            user.setType(type);
            user.setUsername(username);
            user.setId(id);
        }
        return user;
    }
}
