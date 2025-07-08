package com.example.ampliar.repository;

import com.example.ampliar.models.AppointmentModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<AppointmentModel, Long>{}
