package com.demo.wallet.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String walletName;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    private boolean activeForShopping;

    private boolean activeForWithdraw;

    private long balance = 0;

    private long usableBalance = 0;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public enum Currency {
        TRY, USD, EUR
    }
}