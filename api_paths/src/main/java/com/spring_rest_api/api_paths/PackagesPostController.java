package com.spring_rest_api.api_paths;

import com.spring_rest_api.api_paths.entity.Product;
import com.spring_rest_api.api_paths.service.PackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

@RestController
public class PackagesPostController {

    private final Logger logger;

    @Autowired
    private PackageService packageService;

    public PackagesPostController() {
        this.logger = LoggerFactory.getLogger(this.getClass());
    }
    
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
