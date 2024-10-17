package com.gritacademy.IT_SAKERHET_JAVA23_Ardi_Fetiu_Uppgift2.repository;

import com.gritacademy.IT_SAKERHET_JAVA23_Ardi_Fetiu_Uppgift2.model.TimeCapsule;
import com.gritacademy.IT_SAKERHET_JAVA23_Ardi_Fetiu_Uppgift2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimeCapsuleRepository extends JpaRepository<TimeCapsule, Long> {
    List<TimeCapsule> findByUser(User user); // Find all time capsules for a given user
}
