package com.chicovg.symptommgmt.core.entity;

import com.chicovg.symptommgmt.rest.domain.UserType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by victorguthrie on 10/15/14.
 */
@Entity
public class SymptomMgmtUser implements UserDetails {

    @Id
    private String username;
    private String password;
    private Long profileId;
    private UserType type;

    public SymptomMgmtUser() {
    }

    public SymptomMgmtUser(String username, String password, Long profileId, UserType type){
        this.username = username;
        this.password = password;
        this.profileId = profileId;
        this.type = type;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities =  new LinkedList<>();
        authorities.add(new SimpleGrantedAuthority(type.toString()));
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
