package com.spring_rest_api.api_paths;

import com.spring_rest_api.api_paths.entity.Product;
import com.spring_rest_api.api_paths.service.PackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
public class PackagesPostController {

    @Autowired
    PackageService packageService;
    
    @PostMapping("/packages")
    public void packages_plurual() {
        System.out.println("Packages!");
    }

    @PostMapping("/package")
    public ResponseEntity<String> package_single(@RequestBody Product product) throws ExecutionException, InterruptedException {
        System.out.println(product.getMetadata().getName());
        //packageService.savePackage(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(packageService.savePackage(product));
    }

}
