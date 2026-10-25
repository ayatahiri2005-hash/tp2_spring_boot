package com.example.securite.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.securite.model.Medicament;

public interface MedicamentRepository extends JpaRepository<Medicament, Long> {
}
