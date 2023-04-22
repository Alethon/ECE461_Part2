package com.spring_rest_api.api_paths.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.spring_rest_api.api_paths.entity.User;
import com.spring_rest_api.api_paths.entity.UserAuthenticationInfo;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.concurrent.ExecutionException;

@Service
public class AuthenticateService {
    private static final String COLLECTION_NAME = "Users";
    private static final String JWT_SECRET = "GIyoqsMwGPv2YEStDNat1qaXXbOH8lmwkvbUODyzoF8="; // Replace with your own secret
    private static final long EXPIRATION_TIME = 36000000; // 1 day in milliseconds

    private static final Logger logger = LoggerFactory.getLogger(AuthenticateService.class);

    public User saveUser(User user, UserAuthenticationInfo authInfo) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference docRef = dbFirestore.collection(COLLECTION_NAME).document(user.getName());
        ApiFuture<DocumentSnapshot> document = docRef.get();
        DocumentSnapshot doc = document.get();
    
        if (doc.exists()) {
            logger.warn("User already exists: {}", user.getName());
            logger.warn("Document data for user {}: {}", user.getName(), doc.getData());
            return null; // User already exists
        } else {
            // Set the authentication info to the user object
            user.setUserAuthenticationInfo(authInfo);
            logger.info("Saving UserAuthenticationInfo: {}", authInfo); // Add this log statement

            ApiFuture<WriteResult> collectionApiFuture = dbFirestore.collection(COLLECTION_NAME).document(user.getName()).set(user);
            logger.info("User saved: {}", user.getName());
            return user; // Successfully saved user
        }
    }
    

    public void removeUser(String username) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference docRef = dbFirestore.collection(COLLECTION_NAME).document(username);
        ApiFuture<DocumentSnapshot> document = docRef.get();
        DocumentSnapshot doc = document.get();

        if (doc.exists()) {
            ApiFuture<WriteResult> writeResult = docRef.delete();
        } else {
            throw new IllegalArgumentException("User not found.");
        }
    }


    public String authenticateUser(User user, UserAuthenticationInfo authInfo) throws ExecutionException, InterruptedException, UnsupportedEncodingException {
        Firestore dbfirestore = FirestoreClient.getFirestore();
        DocumentReference docRef = dbfirestore.collection(COLLECTION_NAME).document(user.getName());
        ApiFuture<DocumentSnapshot> document = docRef.get();
        DocumentSnapshot doc = document.get();
    
        if (doc.exists()) {
            String storedPassword = doc.getString("authInfo.password");
            UserAuthenticationInfo storedAuthInfo = doc.toObject(User.class).getUserAuthenticationInfo();
            logger.info("Stored UserAuthenticationInfo: {}", storedAuthInfo);
            logger.info("Stored password: {}", storedPassword);
            logger.info("Provided password: {}", authInfo.getPassword());
    
            if (authInfo.getPassword().equals(storedPassword)) {
                return generateJwtToken(user.getName());
            } else {
                throw new IllegalArgumentException("Invalid password.");
            }
        } else {
            throw new IllegalArgumentException("User not found.");
        }
    }

    private String generateJwtToken(String username) throws UnsupportedEncodingException {
        Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + EXPIRATION_TIME);

        return JWT.create()
                .withIssuer("auth0")
                .withSubject(username)
                .withIssuedAt(now)
                .withExpiresAt(expirationDate)
                .sign(algorithm);
    }
}
