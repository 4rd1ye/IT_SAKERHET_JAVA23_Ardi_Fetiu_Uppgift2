package com.gritacademy.IT_SAKERHET_JAVA23_Ardi_Fetiu_Uppgift2.repository;

import com.gritacademy.IT_SAKERHET_JAVA23_Ardi_Fetiu_Uppgift2.model.TimeCapsule;
import com.gritacademy.IT_SAKERHET_JAVA23_Ardi_Fetiu_Uppgift2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

}
