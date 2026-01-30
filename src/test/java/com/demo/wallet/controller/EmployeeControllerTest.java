package com.demo.wallet.controller;

import com.demo.wallet.dto.ApproveTransactionRequest;
import com.demo.wallet.dto.CreateWalletRequest;
import com.demo.wallet.model.Transaction;
import com.demo.wallet.model.Wallet;
import com.demo.wallet.service.WalletService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(EmployeeController.class)
@Import(TestSecurityConfig.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateWallet() throws Exception {

        final CreateWalletRequest request = new CreateWalletRequest();
        request.setCustomerId(1L);
        request.setWalletName("Main Wallet");
        request.setCurrency(Wallet.Currency.USD);
        request.setShopping(true);
        request.setWithdraw(false);

        final Wallet wallet = new Wallet();
        wallet.setId(10L);
        wallet.setWalletName("Main Wallet");
        wallet.setCurrency(Wallet.Currency.USD);

        Mockito.when(walletService.createWallet(anyLong(), anyString(), any(), anyBoolean(), anyBoolean()))
                .thenReturn(wallet);

        mockMvc.perform(post("/employee/create-wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(wallet.getId()))
                .andExpect(jsonPath("$.walletName").value(wallet.getWalletName()));
    }

    @Test
    void testListWallets() throws Exception {

        final Wallet wallet1 = new Wallet();
        wallet1.setId(1L);
        wallet1.setWalletName("Wallet A");

        final Wallet wallet2 = new Wallet();
        wallet2.setId(2L);
        wallet2.setWalletName("Wallet B");

        final List<Wallet> wallets = Arrays.asList(wallet1, wallet2);

        Mockito.when(walletService.getWalletsForCustomer(1L)).thenReturn(wallets);

        mockMvc.perform(get("/employee/list")
                        .param("customerId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].walletName").value("Wallet A"));
    }

    @Test
    void testApproveTransaction() throws Exception {

        final ApproveTransactionRequest request = new ApproveTransactionRequest();
        request.setTransactionId(100L);
        request.setStatus(Transaction.TransactionStatus.APPROVED);

        final Transaction transaction = new Transaction();
        transaction.setId(100L);
        transaction.setStatus(Transaction.TransactionStatus.APPROVED);

        Mockito.when(walletService.approveOrDeny(100L, Transaction.TransactionStatus.APPROVED)).thenReturn(transaction);

        mockMvc.perform(post("/employee/transaction/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }
}