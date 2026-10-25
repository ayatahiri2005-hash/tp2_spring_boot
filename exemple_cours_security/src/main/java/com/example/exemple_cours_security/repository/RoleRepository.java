package com.example.exemple_cours_security.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.exemple_cours_security.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	Role findByName(String name);
}
