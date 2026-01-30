package com.demo.wallet.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long amount;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    private OppositePartyType oppositePartyType;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    public enum TransactionType {
        DEPOSIT, WITHDRAW
    }

    public enum OppositePartyType {
        IBAN, PAYMENT
    }

    public enum TransactionStatus {
        PENDING, APPROVED, DENIED
    }
}