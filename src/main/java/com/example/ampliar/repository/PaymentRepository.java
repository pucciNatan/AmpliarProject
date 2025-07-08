package com.example.ampliar.repository;

import com.example.ampliar.models.PayerModel;
import com.example.ampliar.models.PaymentModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository  extends JpaRepository<PaymentModel, Long> {}
