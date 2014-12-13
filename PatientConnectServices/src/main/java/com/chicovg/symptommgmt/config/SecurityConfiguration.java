package com.chicovg.symptommgmt.config;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.savedrequest.NullRequestCache;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by victorguthrie on 10/13/14.
 */
//@Configuration
//@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    // This anonymous inner class' onAuthenticationSuccess() method is invoked
    // whenever a client successfully logs in. The class just sends back an
    // HTTP 200 OK status code to the client so that they know they logged
    // in correctly. The class does not redirect the client anywhere like the
    // default handler does with a HTTP 302 response. The redirect has been
    // removed to be friendlier to mobile clients and Retrofit.
    private static final AuthenticationSuccessHandler NO_REDIRECT_SUCCESS_HANDLER = new AuthenticationSuccessHandler() {
        @Override
        public void onAuthenticationSuccess(HttpServletRequest request,
                                            HttpServletResponse response, Authentication authentication)
                throws IOException, ServletException {
            response.setStatus(HttpStatus.SC_OK);
        }
    };

    // Normally, the logout success handler redirects the client to the login page. We
    // just want to let the client know that it succcessfully logged out and make the
    // response a bit of JSON so that Retrofit can handle it. The handler sends back
    // a 200 OK response and an empty JSON object.
    private static final LogoutSuccessHandler JSON_LOGOUT_SUCCESS_HANDLER = new LogoutSuccessHandler() {
        @Override
        public void onLogoutSuccess(HttpServletRequest request,
                                    HttpServletResponse response, Authentication authentication)
                throws IOException, ServletException {
            response.setStatus(HttpStatus.SC_OK);
            response.setContentType("application/json");
            response.getWriter().write("{}");
        }
    };

    /**
     * This method is used to inject access control policies into Spring
     * security to control what resources / paths / http methods clients have
     * access to.
     */
    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.requestCache().requestCache(new NullRequestCache());

        // Allow all clients to access the login page and use
        // it to login
        http.formLogin()
                .loginProcessingUrl("/login")
                .successHandler(NO_REDIRECT_SUCCESS_HANDLER)
                .permitAll();

        // Make sure that clients can logout too!!
        http.logout()
                .logoutUrl("logout")
                .logoutSuccessHandler(JSON_LOGOUT_SUCCESS_HANDLER)
                .permitAll();

        http.authorizeRequests().anyRequest().authenticated();
    }

    /**
     *
     * This method is used to setup the users that will be able to login to the
     * system. This is a VERY insecure setup that is using two hardcoded users /
     * passwords and should never be used for anything other than local testing
     * on a machine that is not accessible via the Internet. Even if you use
     * this code for testing, at the bare minimum, you should change the
     * passwords listed below.
     *
     * @param auth
     * @throws Exception
     */
    @Autowired
    protected void registerAuthentication(
            final AuthenticationManagerBuilder auth) throws Exception {

        // This example creates a simple in-memory UserDetailService that
        // is provided by Spring
        auth.inMemoryAuthentication()
                .withUser("coursera")
                .password("changeit")
                .authorities("admin","user")
                .and()
                .withUser("student")
                .password("changeit")
                .authorities("user");
    }

}
