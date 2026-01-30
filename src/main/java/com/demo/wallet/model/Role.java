package com.demo.wallet.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {

    CUSTOMER,
    EMPLOYEE;

    @Override
    public String getAuthority() {
        return name();
    }
}
