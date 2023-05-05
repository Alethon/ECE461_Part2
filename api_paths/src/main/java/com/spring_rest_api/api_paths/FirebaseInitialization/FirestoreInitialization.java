package com.spring_rest_api.api_paths.FirebaseInitialization;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import com.google.firebase.FirebaseOptions;
import com.spring_rest_api.api_paths.service.AuthenticateService;
import com.google.firebase.FirebaseApp;
import com.google.auth.oauth2.GoogleCredentials;

@Configuration
public class FirestoreInitialization {

    @PostConstruct
    public void initialization(){
        FileInputStream serviceAccount = null;
        try{

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.getApplicationDefault())
                    .build();

            FirebaseApp.initializeApp(options);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
