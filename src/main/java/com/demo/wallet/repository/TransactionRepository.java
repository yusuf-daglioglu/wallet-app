package com.demo.wallet.repository;

import com.demo.wallet.model.Transaction;
import com.demo.wallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByWallet(Wallet wallet);
}