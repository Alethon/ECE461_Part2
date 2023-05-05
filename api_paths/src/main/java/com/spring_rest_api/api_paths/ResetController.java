package com.spring_rest_api.api_paths;


import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import com.spring_rest_api.api_paths.service.ResetService;

import com.spring_rest_api.api_paths.service.AuthenticateService;

@RestController
public class ResetController {

    private final ResponseEntity<String> unAuthError = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You do not have permission to reset the registry.");
    private final ResponseEntity<String> badRequestError = ResponseEntity.badRequest().body("There is missing field(s) in the AuthenticationToken or it is formed improperly, or the AuthenticationToken is invalid.");

    @Autowired
    AuthenticateService authenticateService;

    @Autowired
    ResetService resetService;


    @DeleteMapping(value = "/reset")
    public ResponseEntity<String> reset(@RequestHeader("X-Authorization") String token) {
        try {
            if(!authenticateService.validateAuthHeaderForUser(token))
                return badRequestError;

            if(!authenticateService.validateAuthHeaderForAdmin(token)) 
                return unAuthError;

            // if (resetService.checkAdminToken(token) == false)
            //     return unAuthError;
            
            boolean result = resetService.clearCollection();
            boolean result1 = resetService.clearTokenUsageCollection();
            boolean result2 = resetService.clearUsersCollection();
            if ((result | result1 | result2) == false)
                return badRequestError;
            authenticateService.createDefaultUser();
            String successMsg = "Registry is reset.";
            return ResponseEntity.ok(successMsg);
        } catch (Exception e) {
            System.err.println("Something failed in reset");
            return badRequestError;
        }
    }

}
