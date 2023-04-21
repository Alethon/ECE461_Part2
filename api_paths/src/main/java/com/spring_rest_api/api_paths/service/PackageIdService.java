package com.spring_rest_api.api_paths.service;

import com.spring_rest_api.api_paths.entity.Product;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.common.util.concurrent.ExecutionError;
import com.google.firebase.cloud.FirestoreClient;
import com.google.gson.Gson;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PackageIdService {
    private static final String COLLECTION_NAME = "Packages";
    private CollectionReference collectionReference = FirestoreClient.getFirestore().collection(COLLECTION_NAME);
    private Gson gson = new Gson();     // using .toString doesn't keep the quotes on the JSON

    // Returns a String representation of the document found in Packages
    // Returns null if the document can't be found
    public String getPackage(String id) throws ExecutionException, InterruptedException{
        DocumentReference docRef = collectionReference.document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        return (document.exists()) ? gson.toJson(document.getData()) : null;
    }


    // Returns true if document found has been deleted from Packages collection
    public boolean deletePackage(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = collectionReference.document(id);
        DocumentSnapshot document = docRef.get().get();
        if (!document.exists()) {return false;}
        // Warning: Deleting a document does not delete its subcollections!
        ApiFuture<WriteResult> writeRes = docRef.delete();
        return true;
    }


    // Check if package has matching metadata and then updates the package.
    // public boolean stringJson_To_Map(String old_doc_string, Product new_package) throws ExecutionException, InterruptedException {
    //     ObjectMapper mapper = new ObjectMapper();

    //     try {
    //         Map<String, Object> map = mapper.readValue(old_doc_string, Map.class);
    //         System.out.println(map);
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }

    //     return true;
    // }
}