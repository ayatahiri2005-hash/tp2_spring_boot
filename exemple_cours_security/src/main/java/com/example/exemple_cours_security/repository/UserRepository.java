package com.example.exemple_cours_security.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.exemple_cours_security.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);

}