package com.gritacademy.IT_SAKERHET_JAVA23_Ardi_Fetiu_Uppgift2.model;

import jakarta.persistence.*;

@Entity
@Table(name = "time_capsules") // Use a plural form for table names
public class TimeCapsule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String encryptedMessage;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Foreign key column name
    private User user;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEncryptedMessage() {
        return encryptedMessage;
    }

    public void setEncryptedMessage(String encryptedMessage) {
        this.encryptedMessage = encryptedMessage;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "TimeCapsule{" +
                "id=" + id +
                ", encryptedMessage='" + encryptedMessage + '\'' +
                ", user=" + (user != null ? user.getEmail() : "null") + // Display user email if available
                '}';
    }
}
