package com.spring_rest_api.api_paths.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.spring_rest_api.api_paths.entity.PagQuery;

// import ch.qos.logback.classic.Logger;

// import ch.qos.logback.core.boolex.Matcher;

@Service
public class PackagesQueryService {
    private final String COLLECTION_NAME = "Packages";
    private final String NameField = "metadata.Name";
    private final String VersionField = "metadata.Version";
    private final String types[] = {"Exact", "Bounded range", "Carat", "Tilde"};
    private CollectionReference collectionReference = FirestoreClient.getFirestore().collection(COLLECTION_NAME);
    
    private final Logger logger = LoggerFactory.getLogger(PackagesQueryService.class);

    // Check if a String can be converted in int
    public int checkValidQueryVar(String input) {
        int offset;
        try {
            offset = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
        return offset;
    }

    // Will Check if the Queries are valid before actually executing them
    public boolean checkValidQuery(List<PagQuery> list_pagQueries) {
        for (PagQuery pq : list_pagQueries) {
            if (type_of_query(pq.get_Version()) == -1) {
                return false;
            }
        }
        return true;
    }

    // Check type of query based on swagger
    // We'll check if it's valid and how it affects the query
    private int type_of_query(String version_query) {
        for (int i = 0; i < this.types.length; i++) {
            Pattern pattern = Pattern.compile(types[i]);
            Matcher match = pattern.matcher(version_query);
            if (match.find()) {
                return i;
            }
        }
        return -1;
    }

    private List<String> get_nums_from_string(String v_query) {
        List<String> result = new ArrayList<String>();

        // Why double backslash on this regex even though it's not normal
        // https://stackoverflow.com/questions/22218350/invalid-escape-sequence-valid-ones-are-b-t-n-f-r-in-java
        Pattern pattern = Pattern.compile("[0-9]\\.[0-9]\\.[0-9]");
        Matcher match = pattern.matcher(v_query);
        while (match.find()) {
            result.add(match.group());
        }

        return result;
    }

    public ArrayList<Map<String, Object>> pagnitatedqueries(List<PagQuery> pagQuerys) throws ExecutionException, InterruptedException {
        // Note, there is no OR query for Java on Firestore

        // We add all the packages we find here
        ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        // We're going to have to go through each different query, since they're seperate queries
        for(PagQuery pags : pagQuerys) {
            Query query = collectionReference.whereEqualTo(NameField, pags.get_Name());
            
            String version_query = pags.get_Version();

            Integer type_of_query = this.type_of_query(version_query);
            logger.info("query type: {}", type_of_query);

            // Return list of version numbers found, group is reference to regex group
            List<String> nums_found = this.get_nums_from_string(version_query);
            logger.info("version strings: {}", nums_found);

            // Single number section
            if (nums_found.size() == 1) {
                String comparison = "";
                if (type_of_query == 2){
                    comparison += "^";
                } else if (type_of_query == 3) {
                    comparison += "~";
                }
                comparison += nums_found.get(0);
                query = query.whereEqualTo(VersionField, comparison);
                logger.info("version comparison: {}", comparison);
            // Double number section
            } else if (nums_found.size() == 2) {
                query = query.whereEqualTo(VersionField, nums_found.get(0) + "-" + nums_found.get(1));
            } else {
                // This condition will probably never hit unless the query is invalid
                query = null;
            }
            
            if (query != null) {
                ApiFuture<QuerySnapshot> future = query.get();
                for (DocumentSnapshot document : future.get().getDocuments()) {
                    Map<String,Object> metaData = (Map<String, Object>) document.getData().get("metadata");
                    result.add(metaData);
                }
            }
        }

        return result;
    }
}
