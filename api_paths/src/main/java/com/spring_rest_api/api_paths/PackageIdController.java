package com.spring_rest_api.api_paths;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.firestore.*;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.google.api.core.ApiFuture;


import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;


@RestController
public class PackageIdController {
    
	@GetMapping("/package/{id}")
	public String packageId(@PathVariable String id) throws ExecutionException, InterruptedException {
        String collection = "Packages";
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference cR = db.collection(collection);
        DocumentReference docRef = cR.document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        return document.getData().toString();

        // // List<ApiFuture<QuerySnapshot>> futures = new ArrayList<>();
        // Query query = cR.whereEqualTo("ID", id);

        // ApiFuture<QuerySnapshot> querySnapshot = query.get();

        // System.out.println("QuerySnapshot: " + querySnapshot.get().getDocuments().size());

        // for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
        //     System.out.println(document.getId());
        // }

		// return String.format("PackageId %s!", id);        
	}

    @PutMapping("/package/{id}")
    public void putMethodName(@PathVariable String id) {
        System.out.println("Put! %s" + id);
    }

    @DeleteMapping("/package/{id}")
    public void deleteMethodName(@PathVariable String id) {
        System.out.println("Delete! %s" + id);
    }

}
