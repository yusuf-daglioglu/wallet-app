package com.demo.wallet.controller;

import com.demo.wallet.dto.ApproveTransactionRequest;
import com.demo.wallet.dto.CreateWalletRequest;
import com.demo.wallet.model.Transaction;
import com.demo.wallet.model.Wallet;
import com.demo.wallet.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final WalletService walletService;

    @PostMapping("/create-wallet")
    public Wallet createWallet(@RequestBody @Valid CreateWalletRequest request) throws Exception {

        return walletService.createWallet(
                request.getCustomerId(),
                request.getWalletName(),
                request.getCurrency(),
                request.isShopping(),
                request.isWithdraw()
        );
    }

    @GetMapping("/list")
    public List<Wallet> listWallets(@RequestParam Long customerId) throws Exception {

        return walletService.getWalletsForCustomer(customerId);
    }

    @PostMapping("/transaction/approve")
    public Transaction approveTransaction(@RequestBody ApproveTransactionRequest request) throws Exception {

        return walletService.approveOrDeny(request.getTransactionId(), request.getStatus());
    }
}
