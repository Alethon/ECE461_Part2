package com.spring_rest_api.api_paths.service;

import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import javax.annotation.Nonnull;

public abstract class DbCollectionService {
    protected final Firestore dbFirestore;
    protected final CollectionReference collectionRef;

    @Nonnull
    protected abstract String getCollectionName();

    protected DbCollectionService() {
        this.dbFirestore = FirestoreClient.getFirestore();
        this.collectionRef = this.dbFirestore.collection(this.getCollectionName());
    }
}
