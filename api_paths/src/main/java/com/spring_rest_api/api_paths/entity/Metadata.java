package com.spring_rest_api.api_paths.entity;

import com.google.cloud.firestore.annotation.PropertyName;

public class Metadata {

    public String Name;

    public String ID;
    public String Version;

    @PropertyName("Name")
    public String getName() {
        return Name;
    }

    @PropertyName("Name")
    public void setName(String name) {
        Name = name;
    }

    @PropertyName("ID")
    public String getID() {
        return ID;
    }
    @PropertyName("ID")
    public void setID(String ID) {
        this.ID = ID;
    }

    @PropertyName("Version")
    public String getVersion() {
        return Version;
    }

    @PropertyName("Version")
    public void setVersion(String version) {
        Version = version;
    }
}
