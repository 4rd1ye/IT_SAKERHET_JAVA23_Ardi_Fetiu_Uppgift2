package com.gritacademy.IT_SAKERHET_JAVA23_Ardi_Fetiu_Uppgift2.controller;

import com.gritacademy.IT_SAKERHET_JAVA23_Ardi_Fetiu_Uppgift2.model.LoginRequest;
import com.gritacademy.IT_SAKERHET_JAVA23_Ardi_Fetiu_Uppgift2.model.SaveRequest;
import com.gritacademy.IT_SAKERHET_JAVA23_Ardi_Fetiu_Uppgift2.model.UpdateMessageRequest;
import com.gritacademy.IT_SAKERHET_JAVA23_Ardi_Fetiu_Uppgift2.model.User;
import com.gritacademy.IT_SAKERHET_JAVA23_Ardi_Fetiu_Uppgift2.service.UserService;
import com.gritacademy.IT_SAKERHET_JAVA23_Ardi_Fetiu_Uppgift2.config.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        return ResponseEntity.ok(userService.registerUser(user));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
        System.out.println("Login attempt for user: " + loginRequest.getEmail());

        String token = userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());

        if (token != null) {
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Collections.singletonMap("error", "Invalid credentials"));
    }

    @PostMapping("/encrypt")
    public ResponseEntity<String> encryptMessage(@RequestBody SaveRequest saveRequest) {
        try {
            // Encrypt the message only once in the service
            String encryptedMessage = EncryptionUtil.encryptMessage(saveRequest.getMessage());
            return ResponseEntity.ok(userService.saveMessage(encryptedMessage, saveRequest.getEmail()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Encryption error: " + e.getMessage());
        }
    }

    @GetMapping("/decrypt")
    public ResponseEntity<List<String>> decryptMessage(@RequestParam("email") String email) {
        try {
            List<String> decryptedMessages = userService.findMessages(email);
            if (decryptedMessages.isEmpty() || decryptedMessages.contains("No messages found!")) {
                return ResponseEntity.ok(Collections.singletonList("No messages to decrypt."));
            }
            return ResponseEntity.ok(decryptedMessages);  // Return decrypted messages
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonList("Decryption error: " + e.getMessage()));
        }
    }

    @PostMapping("/updateMessage/{timeCapsuleId}")
    public ResponseEntity<String> updateMessage(@PathVariable Long timeCapsuleId, @RequestBody UpdateMessageRequest updateMessageRequest) {
        String email = updateMessageRequest.getEmail();

        try {
            String encryptedMessage = EncryptionUtil.encryptMessage(updateMessageRequest.getMessage());
            String result = userService.updateMessage(timeCapsuleId, encryptedMessage, email);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Encryption failed: " + e.getMessage());
        }
    }
}
