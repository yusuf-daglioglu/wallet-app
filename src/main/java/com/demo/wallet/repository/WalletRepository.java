package com.demo.wallet.repository;

import com.demo.wallet.model.User;
import com.demo.wallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    List<Wallet> findByUser(User user);
}