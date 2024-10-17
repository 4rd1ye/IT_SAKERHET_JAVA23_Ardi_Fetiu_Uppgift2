package com.gritacademy.IT_SAKERHET_JAVA23_Ardi_Fetiu_Uppgift2.service;

import com.gritacademy.IT_SAKERHET_JAVA23_Ardi_Fetiu_Uppgift2.config.EncryptionUtil;
import com.gritacademy.IT_SAKERHET_JAVA23_Ardi_Fetiu_Uppgift2.model.TimeCapsule;
import com.gritacademy.IT_SAKERHET_JAVA23_Ardi_Fetiu_Uppgift2.model.User;
import com.gritacademy.IT_SAKERHET_JAVA23_Ardi_Fetiu_Uppgift2.repository.UserRepository;
import com.gritacademy.IT_SAKERHET_JAVA23_Ardi_Fetiu_Uppgift2.repository.TimeCapsuleRepository;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TimeCapsuleRepository timeCapsuleRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final String SECRET_KEY = "h7Ui63Mzqj61G87j";  // Ensure this is Base64 encoded for safe key usage

    public String registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return "User already exists!";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "User registered successfully!";
    }

    public String saveMessage(String encryptedMessage, String email) {
        TimeCapsule timeCapsule = new TimeCapsule();
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return "User not found!";
        }

        timeCapsule.setUser(user);
        timeCapsule.setEncryptedMessage(encryptedMessage);
        timeCapsuleRepository.save(timeCapsule);

        return "Message saved successfully!";
    }

    public List<String> findMessages(String email) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return Collections.singletonList("User not found!");
        }

        List<TimeCapsule> timeCapsules = timeCapsuleRepository.findByUser(user);
        List<String> decryptedMessages = new ArrayList<>();

        for (TimeCapsule timeCapsule : timeCapsules) {
            try {
                // Decrypt each message
                String decryptedMessage = EncryptionUtil.decryptMessage(timeCapsule.getEncryptedMessage());
                decryptedMessages.add(decryptedMessage);
            } catch (Exception e) {
                decryptedMessages.add("Error decrypting message: " + e.getMessage());
            }
        }

        return decryptedMessages.isEmpty() ? Collections.singletonList("No messages found!") : decryptedMessages;
    }

    public String loginUser(String email, String password) {
        try {
            User foundUser = userRepository.findByEmail(email);
            if (foundUser == null) {
                return "User not found";
            }
            if (passwordEncoder.matches(password, foundUser.getPassword())) {
                return generateToken(foundUser);
            } else {
                return "Invalid password";
            }
        } catch (Exception e) {
            throw new RuntimeException("Error logging in: " + e.getMessage());
        }
    }

    // Generate token for User object
    public String generateToken(User user) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] apiKeySecretBytes = Base64.getDecoder().decode(SECRET_KEY);  // Decode the Base64 key
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        JwtBuilder builder = Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setIssuer("Demo")
                .signWith(signatureAlgorithm, signingKey)
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10));  // 10 hours expiry

        return builder.compact();
    }

    public String updateMessage(Long timeCapsuleId, String message, String email) {
        Optional<TimeCapsule> optionalTimeCapsule = timeCapsuleRepository.findById(timeCapsuleId);

        if (optionalTimeCapsule.isPresent()) {
            TimeCapsule timeCapsule = optionalTimeCapsule.get();
            if (timeCapsule.getUser().getEmail().equals(email)) {
                timeCapsule.setEncryptedMessage(message);
                timeCapsuleRepository.save(timeCapsule);
                return "Message updated successfully!";
            } else {
                return "Unauthorized: You do not have permission to update this message.";
            }
        } else {
            return "Message not found!";
        }
    }
}
