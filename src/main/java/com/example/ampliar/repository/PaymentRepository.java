package com.example.ampliar.repository;

import com.example.ampliar.model.PaymentModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository  extends JpaRepository<PaymentModel, Long> {}
