package com.spring_rest_api.api_paths.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.spring_rest_api.api_paths.entity.Secret;
import com.spring_rest_api.api_paths.entity.User;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

@Service
@DependsOn("firestoreInitialization")
public class AuthenticateService {
    private static final String COLLECTION_NAME = "Users";
    private static final String JWT_SECRET = "GIyoqsMwGPv2YEStDNat1qaXXbOH8lmwkvbUODyzoF8="; // Replace with your own Secret
    private static final long EXPIRATION_TIME = 36000000; // 1 day in milliseconds
    private static final String TOKEN_USAGE_COLLECTION_NAME = "TokenUsage";
    private static final long MAX_TOKEN_USAGE = 1000;

    private static final Logger logger = LoggerFactory.getLogger(AuthenticateService.class);

    public User saveUser(User User,Secret Secret) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference docRef = dbFirestore.collection(COLLECTION_NAME).document(User.getName());
        ApiFuture<DocumentSnapshot> document = docRef.get();
        DocumentSnapshot doc = document.get();
    
        logger.info("Checking if User {} exists in Firestore", User.getName());
        
        if (doc.exists()) {
            logger.warn("User already exists: {}", User.getName());
            logger.warn("Document data for User {}: {}", User.getName(), doc.getData());
            return null; // User already exists
        } else {
            // Set the authentication info to the User object
            User.setUserAuthenticationInfo(new HashMap<String, String>() {{
                put("password", Secret.getPassword());
            }});
            logger.info("Saving Secret: {}", Secret); // Add this log statement
    
            ApiFuture<WriteResult> collectionApiFuture = dbFirestore.collection(COLLECTION_NAME).document(User.getName()).set(User);
            logger.info("User saved: {}", User.getName());
            return User; // Successfully saved User
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


    public String authenticateUser(User User, Secret Secret) throws ExecutionException, InterruptedException, UnsupportedEncodingException {
        Firestore dbfirestore = FirestoreClient.getFirestore();
        DocumentReference docRef = dbfirestore.collection(COLLECTION_NAME).document(User.getName());
        ApiFuture<DocumentSnapshot> document = docRef.get();
        DocumentSnapshot doc = document.get();
    
        if (doc.exists()) {
            String storedPassword = doc.get("userAuthenticationInfo.password", String.class);
            Map<String, String> storedAuthInfo = doc.toObject(User.class).getUserAuthenticationInfo();
            storedPassword = storedAuthInfo.get("password");
            logger.info("Stored UserAuthenticationInfo: {}", storedAuthInfo);
            logger.info("Stored password: {}", storedPassword);
            logger.info("Provided password: {}", Secret.getPassword());
    
            if (Secret.getPassword().equals(storedPassword)) {
                String token = generateJwtToken(User.getName());
                storeTokenUsage(token, User.getName()); // Pass the username as a parameter
                return token;
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

    private void storeTokenUsage(String token, String username) throws ExecutionException, InterruptedException {
        String hashedToken = token;
        Firestore dbFirestore = FirestoreClient.getFirestore();
        CollectionReference tokenUsageCollection = dbFirestore.collection(TOKEN_USAGE_COLLECTION_NAME);
    
        // Find the existing token entry for the User
        Query query = tokenUsageCollection.whereEqualTo("username", username);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
    
        if (!documents.isEmpty()) {
            // Update the existing token entry
            QueryDocumentSnapshot existingDoc = documents.get(0);
            String existingDocId = existingDoc.getId();
            DocumentReference docRef = tokenUsageCollection.document(existingDocId);
            ApiFuture<WriteResult> collectionApiFuture = docRef.update("hashedToken", hashedToken, "usageCount", 1L, "expirationTime", new Date(System.currentTimeMillis() + EXPIRATION_TIME));
        } else {
            // Create a new token entry
            Map<String, Object> data = new HashMap<>();
            data.put("hashedToken", hashedToken);
            data.put("username", username);
            data.put("usageCount", 1L);
            data.put("expirationTime", new Date(System.currentTimeMillis() + EXPIRATION_TIME));
            ApiFuture<DocumentReference> collectionApiFuture = tokenUsageCollection.add(data);
        }
    }
    


    public boolean validateJwtToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).withIssuer("auth0").build();
            DecodedJWT jwt = verifier.verify(token);
    
            // Check token usage count and expiration time
            Firestore dbFirestore = FirestoreClient.getFirestore();
            CollectionReference tokenUsageCollection = dbFirestore.collection(TOKEN_USAGE_COLLECTION_NAME);
    
            Query query = tokenUsageCollection.whereEqualTo("hashedToken", token);
            ApiFuture<QuerySnapshot> querySnapshot = query.get();
            List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
    
            if (!documents.isEmpty()) {
                QueryDocumentSnapshot existingDoc = documents.get(0);
                long currentUsageCount = existingDoc.getLong("usageCount");
                Date expirationTime = existingDoc.getDate("expirationTime");
                Date now = new Date();
    
                if (currentUsageCount >= MAX_TOKEN_USAGE || now.after(expirationTime)) {
                    // Remove token from the database
                    String existingDocId = existingDoc.getId();
                    DocumentReference docRef = tokenUsageCollection.document(existingDocId);
                    ApiFuture<WriteResult> collectionApiFuture = docRef.delete();
    
                    logger.error("Token has exceeded usage limit or expired.");
                    return false;
                } else {
                    // Update the token usage count
                    updateTokenUsage(token);
                    return true;
                }
            } else {
                logger.error("Token not found in token usage collection.");
                return false;
            }
        } catch (JWTVerificationException e) {
            logger.error("JWT verification failed: {}", e.getMessage());
            return false;
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Error validating token: {}", e.getMessage());
            return false;
        }
    }
    

    private void updateTokenUsage(String token) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        CollectionReference tokenUsageCollection = dbFirestore.collection(TOKEN_USAGE_COLLECTION_NAME);

        // Find the existing token entry for the hashed token
        Query query = tokenUsageCollection.whereEqualTo("hashedToken", token);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        if (!documents.isEmpty()) {
            // Update the existing token entry
            QueryDocumentSnapshot existingDoc = documents.get(0);
            String existingDocId = existingDoc.getId();
            long currentUsageCount = existingDoc.getLong("usageCount");
            DocumentReference docRef = tokenUsageCollection.document(existingDocId);

            // Check if usage count is within the limit
            if (currentUsageCount < MAX_TOKEN_USAGE) {
                ApiFuture<WriteResult> collectionApiFuture = docRef.update("usageCount", currentUsageCount + 1);
            } else {
                throw new IllegalStateException("Token usage limit exceeded.");
            }
        } else {
            throw new IllegalStateException("Token not found in token usage collection.");
        }
    }

    // The following is for default username and Secret
    // @PostConstruct
    // @DependsOn("firestoreInitialization")
    public void createDefaultUser() {
        String defaultUsername = "admin";
        String defaultPassword = "admin";
        boolean defaultIsAdmin = true;

        try {
            // Check if the default User already exists
            Firestore dbFirestore = FirestoreClient.getFirestore();
            DocumentReference docRef = dbFirestore.collection(COLLECTION_NAME).document(defaultUsername);
            ApiFuture<DocumentSnapshot> document = docRef.get();
            DocumentSnapshot doc = document.get();

            if (!doc.exists()) {
                // Create a new User with the specified username and password
                User defaultUser = new User();
                defaultUser.setName(defaultUsername);

                Secret userSecret = new Secret();
                userSecret.setPassword(defaultPassword);

                defaultUser.setIsAdmin(defaultIsAdmin);

                saveUser(defaultUser, userSecret);
                logger.info("Default User created: {}", defaultUsername);
            } else {
                logger.info("Default User already exists: {}", defaultUsername);
            }
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Error creating default User: {}", e.getMessage());
        }
    }

}
