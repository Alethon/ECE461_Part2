package com.spring_rest_api.api_paths.entity;

public class User {

    public String name;
    public boolean isAdmin;
    private UserAuthenticationInfo userAuthenticationInfo;


    public UserAuthenticationInfo getUserAuthenticationInfo() {
        return userAuthenticationInfo;
    }

    public void setUserAuthenticationInfo(UserAuthenticationInfo userAuthenticationInfo) {
        this.userAuthenticationInfo = userAuthenticationInfo;
    }

    public User(String name,boolean isAdmin) {
        this.name = name;
        this.isAdmin = isAdmin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

}
