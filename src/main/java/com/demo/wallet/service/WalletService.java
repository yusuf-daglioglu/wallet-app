package com.demo.wallet.service;

import com.demo.wallet.model.Transaction;
import com.demo.wallet.model.Wallet;

import java.util.List;

public interface WalletService {

    Wallet createWallet(Long customerId,
                        String name,
                        Wallet.Currency currency,
                        boolean forShopping,
                        boolean forWithdraw) throws Exception;

    List<Wallet> getWalletsForCustomer(Long customerId) throws Exception;

    Transaction deposit(Long walletId,
                        long amount,
                        Transaction.OppositePartyType oppositePartyType) throws Exception;

    Transaction withdraw(Long walletId,
                         long amount,
                         Transaction.OppositePartyType oppositePartyType) throws Exception;

    List<Transaction> getTransactions(Long walletId) throws Exception;

    Transaction approveOrDeny(Long transactionId,
                              Transaction.TransactionStatus newStatus) throws Exception;
}
