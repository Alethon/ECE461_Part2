package com.spring_rest_api.api_paths.entity;

import com.google.cloud.firestore.annotation.PropertyName;

public class Data {

    public String Content;

    public String JSProgram;

    public String URL;

    @PropertyName("URL")
    public String getURL() {
        return URL;
    }

    @PropertyName("URL")
    public void setURL(String URL) {
        this.URL = URL;
    }

    @PropertyName("Content")
    public String getContent() {
        return Content;
    }

    @PropertyName("Content")
    public void setContent(String content) {
        Content = content;
    }

    @PropertyName("JSProgram")
    public String getJSProgram() {
        return JSProgram;
    }

    @PropertyName("JSProgram")
    public void setJSProgram(String JSProgram) {
        this.JSProgram = JSProgram;
    }
}
