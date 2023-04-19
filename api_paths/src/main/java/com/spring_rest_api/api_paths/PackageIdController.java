package com.spring_rest_api.api_paths;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring_rest_api.api_paths.service.PackageIdService;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;


@RestController
public class PackageIdController {

    @Autowired
    PackageIdService packageIdService;
    
	@GetMapping("/package/{id}")
	public ResponseEntity<String> packageId(@PathVariable String id) throws ExecutionException, InterruptedException {
        String document_string = packageIdService.getPackage(id);
        HttpStatus status = (document_string == "Package does not exist.") ? 
            HttpStatus.NOT_FOUND : HttpStatus.OK;

        return ResponseEntity.status(status).body(document_string);
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
