package com.example.ampliar.repository;

import com.example.ampliar.model.PatientModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<PatientModel, Long>{}
