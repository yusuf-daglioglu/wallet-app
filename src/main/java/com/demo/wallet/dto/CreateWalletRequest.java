package com.demo.wallet.dto;

import com.demo.wallet.model.Wallet;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateWalletRequest {

    // Optionally validations can be added to each field depending on the business/analysis.
    // example:
    @NotBlank(message = "Wallet name cannot be null or empty.")
    private String walletName;

    private Long customerId;

    private Wallet.Currency currency;

    private boolean shopping;

    private boolean withdraw;
}