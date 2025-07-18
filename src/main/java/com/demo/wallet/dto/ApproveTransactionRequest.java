package com.demo.wallet.dto;

import com.demo.wallet.model.Transaction;
import lombok.Data;

@Data
public class ApproveTransactionRequest {

    private Long transactionId;

    private Transaction.TransactionStatus status;
}