package com.spring_rest_api.api_paths.entity;

public class AuthenticationRequest {

    private User user;
    private UserAuthenticationInfo userAuthenticationInfo;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserAuthenticationInfo getUserAuthenticationInfo() {
        return userAuthenticationInfo;
    }

    public void setUserAuthenticationInfo(UserAuthenticationInfo userAuthenticationInfo) {
        this.userAuthenticationInfo = userAuthenticationInfo;
    }
}

