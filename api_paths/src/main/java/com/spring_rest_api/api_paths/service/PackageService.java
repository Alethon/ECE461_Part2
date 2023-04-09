package com.spring_rest_api.api_paths.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.spring_rest_api.api_paths.entity.Product;
import org.springframework.stereotype.Service;


import java.util.concurrent.ExecutionException;

@Service
public class PackageService {
 // We will write code here that will be called in the contollers to send data to the db
     private static final String COLLECTION_NAME = "Packages";
     public String savePackage(Product product) throws ExecutionException, InterruptedException {

         Firestore dbfirestore = FirestoreClient.getFirestore();

         DocumentReference docRef = dbfirestore.collection(COLLECTION_NAME).document(product.getMetadata().getID());

         ApiFuture<DocumentSnapshot> document = docRef.get();
         DocumentSnapshot doc = document.get();
         if(doc.exists()){
             if(doc.getData().get("URL") != product.getData().getURL()){
                 ApiFuture<WriteResult> collectionApiFuture = dbfirestore.collection(COLLECTION_NAME).document(product.getMetadata().getID()).update("data.URL",product.getData().getURL());
                 return doc.getData().get("metadata").toString();
             }
             return doc.getData().get("metadata").toString();
         }
         else{
             ApiFuture<WriteResult> collectionApiFuture = dbfirestore.collection(COLLECTION_NAME).document(product.getMetadata().getID()).set(product);
             return doc.getData().get("metadata").toString();
         }

     }
}
