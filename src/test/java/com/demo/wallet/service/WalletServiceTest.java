package com.demo.wallet.service;

import com.demo.wallet.model.Transaction;
import com.demo.wallet.model.User;
import com.demo.wallet.model.Wallet;
import com.demo.wallet.repository.TransactionRepository;
import com.demo.wallet.repository.UserRepository;
import com.demo.wallet.repository.WalletRepository;
import com.demo.wallet.service.impl.WalletServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepo;

    @Mock
    private TransactionRepository transactionRepo;

    @Mock
    private UserRepository customerRepo;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private WalletServiceImpl walletService;

    @Test
    void test_CreateWallet_ForHimself() throws Exception {

        /*
         GIVEN
        */
        final String walletName = "Gift Wallet for Test";
        final Wallet.Currency currency = Wallet.Currency.USD;
        final boolean forShopping = true;
        final boolean forWithdraw = false;

        /*
         WHEN
        */
        final User user1 = new User();
        user1.setId(1L);
        user1.setName("Jack");

        final Wallet savedWallet = new Wallet();
        savedWallet.setId(1L);
        savedWallet.setWalletName(walletName);
        savedWallet.setCurrency(currency);
        savedWallet.setActiveForShopping(forShopping);
        savedWallet.setActiveForWithdraw(forWithdraw);
        savedWallet.setUser(user1);

        when(customerRepo.findById(1L)).thenReturn(Optional.of(user1));
        when(walletRepo.save(any(Wallet.class))).thenReturn(savedWallet);
        when(securityService.getCurrentUserId()).thenReturn(1L);

        /*
         EXECUTION
        */
        final Wallet result = walletService.createWallet(null, walletName, currency, forShopping, forWithdraw);

        /*
         THEN
        */
        assertNotNull(result);
        assertEquals(walletName, result.getWalletName());
        assertEquals(currency, result.getCurrency());
        assertTrue(result.isActiveForShopping());
        assertFalse(result.isActiveForWithdraw());
        assertEquals(user1, result.getUser());
        verify(walletRepo, times(1)).save(any(Wallet.class));
    }

    @Test
    void test_CreateWallet_Success() throws Exception {

        /*
         GIVEN
        */
        final String walletName = "Gift Wallet for Test";
        final Wallet.Currency currency = Wallet.Currency.USD;
        final boolean forShopping = true;
        final boolean forWithdraw = false;

        /*
         WHEN
        */
        final User user1 = new User();
        user1.setId(1L);
        user1.setName("Jack");

        final Wallet savedWallet = new Wallet();
        savedWallet.setId(1L);
        savedWallet.setWalletName(walletName);
        savedWallet.setCurrency(currency);
        savedWallet.setActiveForShopping(forShopping);
        savedWallet.setActiveForWithdraw(forWithdraw);
        savedWallet.setUser(user1);

        when(customerRepo.findById(1L)).thenReturn(Optional.of(user1));
        when(walletRepo.save(any(Wallet.class))).thenReturn(savedWallet);

        /*
         EXECUTION
        */
        final Wallet result = walletService.createWallet(1L, walletName, currency, forShopping, forWithdraw);

        /*
         THEN
        */
        assertNotNull(result);
        assertEquals(walletName, result.getWalletName());
        assertEquals(currency, result.getCurrency());
        assertTrue(result.isActiveForShopping());
        assertFalse(result.isActiveForWithdraw());
        assertEquals(user1, result.getUser());
        verify(walletRepo, times(1)).save(any(Wallet.class));
    }

    @Test
    void test_CreateWallet_CustomerNotFound() {

        /*
         WHEN
        */
        when(customerRepo.findById(1L)).thenReturn(Optional.empty());

        /*
         EXECUTION
        */
        final Exception exception = assertThrows(Exception.class, () -> {
            walletService.createWallet(1L, "Special Test Wallet", Wallet.Currency.TRY, true, false);
        });

        /*
         THEN
        */
        assertEquals("Customer not found", exception.getMessage());
        verify(walletRepo, never()).save(any(Wallet.class));
    }

    @Test
    void test_GetWalletsForCustomer_Success() throws Exception {

        /*
         GIVEN
        */
        final Long customerId = 1L;
        final User user = new User();
        user.setId(customerId);

        /*
         WHEN
        */
        final Wallet wallet1 = new Wallet();
        final Wallet wallet2 = new Wallet();
        final List<Wallet> wallets = Arrays.asList(wallet1, wallet2);

        when(customerRepo.findById(customerId)).thenReturn(Optional.of(user));
        when(walletRepo.findByUser(user)).thenReturn(wallets);

        /*
         EXECUTION
        */
        final List<Wallet> result = walletService.getWalletsForCustomer(customerId);

        /*
         THEN
        */
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(customerRepo).findById(customerId);
        verify(walletRepo).findByUser(user);
    }

    @Test
    void test_GetWalletsForCustomer_CustomerNotFound() {

        /*
         GIVEN
        */
        final Long customerId = 1L;

        /*
         WHEN
        */
        when(customerRepo.findById(customerId)).thenReturn(Optional.empty());

        /*
         EXECUTION
        */
        final Exception exception = assertThrows(Exception.class, () -> {
            walletService.getWalletsForCustomer(customerId);
        });

        /*
         THEN
        */
        assertEquals("Customer not found", exception.getMessage());
        verify(customerRepo).findById(customerId);
        verify(walletRepo, never()).findByUser(any());
    }

    @Test
    public void test_Deposit_WalletNotYours() {

        /*
         GIVEN
        */
        final Long walletId = 1L;

        /*
         WHEN
        */
        final Long walletOwnerId = 100L;
        final Long currentUserId = 200L;

        final User walletOwner = new User();
        walletOwner.setId(walletOwnerId);

        final Wallet wallet = new Wallet();
        wallet.setUser(walletOwner);

        when(walletRepo.findById(walletId)).thenReturn(Optional.of(wallet));
        when(securityService.getCurrentUserId()).thenReturn(currentUserId);

        /*
         EXECUTION
        */
        final Exception exception = assertThrows(Exception.class, () ->
             walletService.deposit(walletId, 500L, Transaction.OppositePartyType.IBAN)
        );

        /*
         THEN
        */
        assert(exception.getMessage().equals("Wallet is not yours!"));
    }

    @Test
    void test_Deposit_Approved() throws Exception {

        /*
         GIVEN
        */
        final long amount = 500L;

        /*
         WHEN
        */
        final Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(1000L);
        wallet.setUsableBalance(1000L);

        final User user = new User();
        user.setId(99L);
        wallet.setUser(user);

        when(walletRepo.findById(1L)).thenReturn(Optional.of(wallet));
        when(walletRepo.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepo.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(securityService.getCurrentUserId()).thenReturn(99L);

        /*
         EXECUTION
        */
        final Transaction tx = walletService.deposit(1L, amount, Transaction.OppositePartyType.PAYMENT);

        /*
         THEN
        */
        assertNotNull(tx);
        assertEquals(Transaction.TransactionType.DEPOSIT, tx.getTransactionType());
        assertEquals(Transaction.TransactionStatus.APPROVED, tx.getStatus());
        assertEquals(1500L, wallet.getBalance());
        assertEquals(1500L, wallet.getUsableBalance());
    }

    @Test
    void test_Deposit_Pending() throws Exception {

        /*
         GIVEN
        */
        final long amount = 2000L;

        /*
         WHEN
        */
        final Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(1000L);
        wallet.setUsableBalance(1000L);

        final User user = new User();
        user.setId(99L);
        wallet.setUser(user);

        when(walletRepo.findById(1L)).thenReturn(Optional.of(wallet));
        when(walletRepo.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepo.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(securityService.getCurrentUserId()).thenReturn(99L);

        /*
         EXECUTION
        */
        final Transaction tx = walletService.deposit(1L, amount, Transaction.OppositePartyType.IBAN);

        /*
         THEN
        */
        assertNotNull(tx);
        assertEquals(Transaction.TransactionType.DEPOSIT, tx.getTransactionType());
        assertEquals(Transaction.TransactionStatus.PENDING, tx.getStatus());
        assertEquals(3000L, wallet.getBalance());
        assertEquals(1000L, wallet.getUsableBalance()); // unchanged
    }

    @Test
    void test_Deposit_WalletNotFound() {

        /*
         WHEN
        */
        when(walletRepo.findById(99L)).thenReturn(Optional.empty());

        /*
         EXECUTION
        */
        final Exception exception = assertThrows(Exception.class, () -> {
            walletService.deposit(99L, 100L, Transaction.OppositePartyType.IBAN);
        });

        /*
         THEN
        */
        assertEquals("Wallet not found", exception.getMessage());
    }

    @Test
    public void test_Withdraw_ApprovedTransaction() throws Exception {

        /*
         WHEN
        */
        final Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(2000);
        wallet.setUsableBalance(1500);
        wallet.setActiveForWithdraw(true);

        final User user = new User();
        user.setId(99L);
        wallet.setUser(user);

        when(walletRepo.findById(1L)).thenReturn(Optional.of(wallet));
        when(transactionRepo.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(securityService.getCurrentUserId()).thenReturn(99L);

        /*
         EXECUTION
        */
        final Transaction result = walletService.withdraw(1L, 500, Transaction.OppositePartyType.IBAN);

        /*
         THEN
        */
        assertEquals(Transaction.TransactionStatus.APPROVED, result.getStatus());
        assertEquals(1000, wallet.getUsableBalance());
        assertEquals(1500, wallet.getBalance());
    }

    @Test
    public void test_Withdraw_PendingTransaction() throws Exception {

        /*
         WHEN
        */
        final Wallet wallet = new Wallet();
        wallet.setId(2L);
        wallet.setBalance(5000);
        wallet.setUsableBalance(4000);
        wallet.setActiveForWithdraw(true);

        final User user = new User();
        user.setId(99L);
        wallet.setUser(user);

        when(walletRepo.findById(2L)).thenReturn(Optional.of(wallet));
        when(transactionRepo.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(securityService.getCurrentUserId()).thenReturn(99L);

        /*
         EXECUTION
        */
        final Transaction result = walletService.withdraw(2L, 2000, Transaction.OppositePartyType.IBAN);

        /*
         THEN
        */
        assertEquals(Transaction.TransactionStatus.PENDING, result.getStatus());
        assertEquals(2000, wallet.getUsableBalance());
    }

    @Test
    public void test_Withdraw_WalletNotFound() {

        /*
         WHEN
        */
        when(walletRepo.findById(3L)).thenReturn(Optional.empty());

        /*
         EXECUTION
        */
        final Exception exception = assertThrows(Exception.class, () -> {
            walletService.withdraw(3L, 500, Transaction.OppositePartyType.IBAN);
        });

        /*
         THEN
        */
        assertEquals("Wallet not found", exception.getMessage());
    }

    @Test
    public void test_Withdraw_WalletInactive() {

        /*
         WHEN
        */
        final Wallet wallet = new Wallet();
        wallet.setId(4L);
        wallet.setActiveForWithdraw(false);

        final User user = new User();
        user.setId(99L);
        wallet.setUser(user);

        when(walletRepo.findById(4L)).thenReturn(Optional.of(wallet));
        when(securityService.getCurrentUserId()).thenReturn(99L);

        /*
         EXECUTION
        */
        final Exception exception = assertThrows(Exception.class, () -> {
            walletService.withdraw(4L, 500, Transaction.OppositePartyType.IBAN);
        });

        /*
         THEN
        */
        assertEquals("Withdraw not allowed for this wallet", exception.getMessage());
    }

    @Test
    public void test_Withdraw_InsufficientBalance() {

        /*
         WHEN
        */
        final Wallet wallet = new Wallet();
        wallet.setId(5L);
        wallet.setUsableBalance(100);
        wallet.setActiveForWithdraw(true);

        final User user = new User();
        user.setId(99L);
        wallet.setUser(user);

        when(walletRepo.findById(5L)).thenReturn(Optional.of(wallet));
        when(securityService.getCurrentUserId()).thenReturn(99L);

         /*
         EXECUTION
        */
        final Exception exception = assertThrows(Exception.class, () -> {
            walletService.withdraw(5L, 500, Transaction.OppositePartyType.IBAN);
        });

        /*
         THEN
        */
        assertEquals("Insufficient balance", exception.getMessage());
    }

    @Test
    public void test_ApproveOrDeny_DepositTransaction() throws Exception {

        /*
         GIVEN
        */
        final Long transactionId = 1L;
        final long amount = 100;

        /*
         WHEN
        */
        final Wallet wallet = new Wallet();
        wallet.setBalance(1000);
        wallet.setUsableBalance(500);

        final Transaction tx = new Transaction();
        tx.setId(transactionId);
        tx.setAmount(amount);
        tx.setStatus(Transaction.TransactionStatus.PENDING);
        tx.setTransactionType(Transaction.TransactionType.DEPOSIT);
        tx.setWallet(wallet);

        when(transactionRepo.findById(transactionId)).thenReturn(Optional.of(tx));
        when(walletRepo.save(any(Wallet.class))).thenReturn(wallet);
        when(transactionRepo.save(any(Transaction.class))).thenReturn(tx);

        /*
         EXECUTION
        */
        final Transaction result = walletService.approveOrDeny(transactionId, Transaction.TransactionStatus.APPROVED);

         /*
         THEN
        */
        assertEquals(Transaction.TransactionStatus.APPROVED, result.getStatus());
        assertEquals(600.0, wallet.getUsableBalance());
        verify(walletRepo, times(1)).save(wallet);
        verify(transactionRepo, times(1)).save(tx);
    }

    @Test
    public void test_ApproveOrDeny_Deny_WithdrawTransaction() throws Exception {

         /*
         GIVEN
        */
        final Long transactionId = 2L;
        final long amount = 200;

         /*
         WHEN
        */
        final Wallet wallet = new Wallet();
        wallet.setBalance(1000);
        wallet.setUsableBalance(300);

        final Transaction tx = new Transaction();
        tx.setId(transactionId);
        tx.setAmount(amount);
        tx.setStatus(Transaction.TransactionStatus.PENDING);
        tx.setTransactionType(Transaction.TransactionType.WITHDRAW);
        tx.setWallet(wallet);

        when(transactionRepo.findById(transactionId)).thenReturn(Optional.of(tx));
        when(walletRepo.save(any(Wallet.class))).thenReturn(wallet);
        when(transactionRepo.save(any(Transaction.class))).thenReturn(tx);

         /*
         EXECUTION
        */
        final Transaction result = walletService.approveOrDeny(transactionId, Transaction.TransactionStatus.DENIED);

         /*
         THEN
        */
        assertEquals(Transaction.TransactionStatus.DENIED, result.getStatus());
        assertEquals(500.0, wallet.getUsableBalance()); // 300 + 200
        verify(walletRepo, times(1)).save(wallet);
        verify(transactionRepo, times(1)).save(tx);
    }

    @Test
    public void test_ApproveOrDeny_TransactionNotFound() {

         /*
         GIVEN
        */
        final Long transactionId = 3L;

         /*
         WHEN
        */
        when(transactionRepo.findById(transactionId)).thenReturn(Optional.empty());

         /*
         EXECUTION
        */
        final Exception exception = assertThrows(Exception.class, () -> {
            walletService.approveOrDeny(transactionId, Transaction.TransactionStatus.APPROVED);
        });

         /*
         THEN
        */
        assertEquals("Transaction not found", exception.getMessage());
    }

    @Test
    public void test_ApproveOrDeny_AlreadyProgressed() {

         /*
         GIVEN
        */
        final Long transactionId = 4L;

         /*
         WHEN
        */
        final Transaction tx = new Transaction();
        tx.setId(transactionId);
        tx.setStatus(Transaction.TransactionStatus.APPROVED);

        when(transactionRepo.findById(transactionId)).thenReturn(Optional.of(tx));

        /*
         EXECUTION
        */
        final Exception exception = assertThrows(Exception.class, () -> {
            walletService.approveOrDeny(transactionId, Transaction.TransactionStatus.DENIED);
        });

         /*
         THEN
        */
        assertEquals("Transaction already progressed", exception.getMessage());
    }

    @Test
    public void test_GetTransactions_Success() throws Exception {

        /*
         GIVEN
        */
        final Long walletId = 1L;

         /*
         WHEN
        */
        final Wallet wallet = new Wallet();
        wallet.setId(walletId);

        final User user = new User();
        user.setId(99L);
        wallet.setUser(user);

        final Transaction tx1 = new Transaction();
        final Transaction tx2 = new Transaction();
        final List<Transaction> transactions = Arrays.asList(tx1, tx2);

        when(walletRepo.findById(walletId)).thenReturn(Optional.of(wallet));
        when(transactionRepo.findByWallet(wallet)).thenReturn(transactions);
        when(securityService.getCurrentUserId()).thenReturn(99L);

        /*
         EXECUTION
        */
        final List<Transaction> result = walletService.getTransactions(walletId);

        /*
         THEN
        */
        assertEquals(2, result.size());
        verify(walletRepo, times(1)).findById(walletId);
        verify(transactionRepo, times(1)).findByWallet(wallet);
    }

    @Test
    void test_GetTransactions_WalletNotYours() {

        /*
         GIVEN
        */
        final Long walletId = 100L;

        /*
         WHEN
        */
        final User walletOwner;
        walletOwner = new User();
        walletOwner.setId(1L);

        final Wallet wallet;
        wallet = new Wallet();
        wallet.setId(100L);
        wallet.setUser(walletOwner);

        final Long currentUserId = 2L;

        when(walletRepo.findById(walletId)).thenReturn(Optional.of(wallet));
        when(securityService.getCurrentUserId()).thenReturn(currentUserId);

        /*
         EXECUTION
        */
        final Exception exception = assertThrows(Exception.class, () -> {
            walletService.getTransactions(walletId);
        });

         /*
         THEN
        */
        assert(exception.getMessage().contains("Wallet is not yours!"));
        verify(walletRepo).findById(walletId);
        verify(securityService).getCurrentUserId();
    }

    @Test
    public void test_GetTransactions_WalletNotFound() {

        /*
         GIVEN
        */
        final Long walletId = 1L;

        /*
         WHEN
        */
        when(walletRepo.findById(walletId)).thenReturn(Optional.empty());

        /*
         EXECUTION
        */
        final Exception exception = assertThrows(Exception.class, () -> {
            walletService.getTransactions(walletId);
        });

        /*
         THEN
        */
        assertEquals("Wallet not found", exception.getMessage());
        verify(walletRepo, times(1)).findById(walletId);
        verify(transactionRepo, never()).findByWallet(any());
    }
}