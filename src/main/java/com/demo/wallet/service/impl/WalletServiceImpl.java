package com.demo.wallet.service.impl;


import com.demo.wallet.model.Transaction;
import com.demo.wallet.model.User;
import com.demo.wallet.model.Wallet;
import com.demo.wallet.repository.TransactionRepository;
import com.demo.wallet.repository.UserRepository;
import com.demo.wallet.repository.WalletRepository;
import com.demo.wallet.service.SecurityService;
import com.demo.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepo;
    private final TransactionRepository transactionRepo;
    private final UserRepository customerRepo;
    private final SecurityService securityService;

    final long MAX_AMOUNT_FOR_APPROVAL = 1000;

    @Override
    public Wallet createWallet(Long customerId,
                               final String name,
                               final Wallet.Currency currency,
                               final boolean forShopping,
                               final boolean forWithdraw) throws Exception {

        if(customerId == null){
            customerId = securityService.getCurrentUserId();
        }

        final User customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new Exception("Customer not found"));

        final Wallet wallet = new Wallet();
        wallet.setWalletName(name);
        wallet.setCurrency(currency);
        wallet.setActiveForShopping(forShopping);
        wallet.setActiveForWithdraw(forWithdraw);
        wallet.setUser(customer);

        log.info("wallet saved {}", wallet.getId());

        return walletRepo.save(wallet);
    }

    @Override
    public List<Wallet> getWalletsForCustomer(final Long customerId) throws Exception {

        final User customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new Exception("Customer not found"));

        return walletRepo.findByUser(customer);
    }

    @Override
    public Transaction deposit(final Long walletId,
                               final long amount,
                               final Transaction.OppositePartyType oppositePartyType) throws Exception {

        final Wallet wallet = walletRepo.findById(walletId)
                .orElseThrow(() -> new Exception("Wallet not found"));

        if( ! wallet.getUser().getId().equals(securityService.getCurrentUserId()) ){
            log.trace("someone triggered a transaction for someone else wallet");
            throw new Exception("Wallet is not yours!");
        }

        final Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setAmount(amount);
        transaction.setTransactionType(Transaction.TransactionType.DEPOSIT);
        transaction.setOppositePartyType(oppositePartyType);

        if (amount > MAX_AMOUNT_FOR_APPROVAL) {
            transaction.setStatus(Transaction.TransactionStatus.PENDING);
            wallet.setBalance(wallet.getBalance() + amount);
        } else {
            transaction.setStatus(Transaction.TransactionStatus.APPROVED);
            wallet.setBalance(wallet.getBalance() + amount);
            wallet.setUsableBalance(wallet.getUsableBalance() + amount);
        }

        walletRepo.save(wallet);
        return transactionRepo.save(transaction);
    }

    @Override
    public Transaction withdraw(final Long walletId,
                                final long amount,
                                final Transaction.OppositePartyType oppositePartyType) throws Exception {

        final Wallet wallet = walletRepo.findById(walletId)
                .orElseThrow(() -> new Exception("Wallet not found"));

        if( ! wallet.getUser().getId().equals(securityService.getCurrentUserId()) ){
            log.trace("someone triggered a transaction for someone else wallet");
            throw new Exception("Wallet is not yours!");
        }

        if (!wallet.isActiveForWithdraw()) {
            log.trace("Withdraw not allowed for this wallet. this is not an error. this depends on user configuration.");
            throw new Exception("Withdraw not allowed for this wallet");
        }

        if (wallet.getUsableBalance() < amount) {
            throw new Exception("Insufficient balance");
        }

        final Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setAmount(amount);
        transaction.setTransactionType(Transaction.TransactionType.WITHDRAW);
        transaction.setOppositePartyType(oppositePartyType);

        if (amount > MAX_AMOUNT_FOR_APPROVAL) {
            transaction.setStatus(Transaction.TransactionStatus.PENDING);
            wallet.setUsableBalance(wallet.getUsableBalance() - amount);
        } else {
            transaction.setStatus(Transaction.TransactionStatus.APPROVED);
            wallet.setBalance(wallet.getBalance() - amount);
            wallet.setUsableBalance(wallet.getUsableBalance() - amount);
        }

        walletRepo.save(wallet);
        return transactionRepo.save(transaction);
    }

    @Override
    public List<Transaction> getTransactions(final Long walletId) throws Exception {

        final Wallet wallet = walletRepo.findById(walletId)
                .orElseThrow(() -> new Exception("Wallet not found"));

        if( ! wallet.getUser().getId().equals(securityService.getCurrentUserId()) ){
            throw new Exception("Wallet is not yours!");
        }

        return transactionRepo.findByWallet(wallet);
    }

    @Override
    public Transaction approveOrDeny(final Long transactionId,
                                     final Transaction.TransactionStatus newStatus) throws Exception {

        final Transaction transaction = transactionRepo.findById(transactionId)
                .orElseThrow(() -> new Exception("Transaction not found"));

        if (transaction.getStatus() != Transaction.TransactionStatus.PENDING) {
            throw new Exception("Transaction already progressed");
        }

        final Wallet wallet = transaction.getWallet();

        if (transaction.getTransactionType() == Transaction.TransactionType.DEPOSIT) {
            if (newStatus == Transaction.TransactionStatus.APPROVED) {
                wallet.setUsableBalance(wallet.getUsableBalance() + transaction.getAmount());
            } else if (newStatus == Transaction.TransactionStatus.DENIED) {
                wallet.setBalance(wallet.getBalance() - transaction.getAmount());
            }
        } else if (transaction.getTransactionType() == Transaction.TransactionType.WITHDRAW) {
            if (newStatus == Transaction.TransactionStatus.APPROVED) {
                wallet.setBalance(wallet.getBalance() - transaction.getAmount());
            } else if (newStatus == Transaction.TransactionStatus.DENIED) {
                wallet.setUsableBalance(wallet.getUsableBalance() + transaction.getAmount());
            }
        }

        transaction.setStatus(newStatus);
        walletRepo.save(wallet);
        return transactionRepo.save(transaction);
    }

}
