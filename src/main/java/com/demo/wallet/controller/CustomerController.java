package com.demo.wallet.controller;

import com.demo.wallet.dto.CreateWalletRequest;
import com.demo.wallet.model.Transaction;
import com.demo.wallet.model.Wallet;
import com.demo.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final WalletService walletService;

    @PostMapping("/create-wallet")
    public Wallet createWallet(@RequestBody CreateWalletRequest request) throws Exception {

        if (request.getCustomerId() != null) {
            throw new Exception("Bad request (customer-id should be empty)");
        }

        return walletService.createWallet(
                null,
                request.getWalletName(),
                request.getCurrency(),
                request.isShopping(),
                request.isWithdraw()
        );
    }

    @PostMapping("/deposit")
    public Transaction deposit(@RequestParam Long walletId,
                               @RequestParam long amount,
                               @RequestParam Transaction.OppositePartyType oppositePartyType) throws Exception {

        return walletService.deposit(walletId, amount, oppositePartyType);
    }

    @PostMapping("/withdraw")
    public Transaction withdraw(@RequestParam Long walletId,
                                @RequestParam long amount,
                                @RequestParam Transaction.OppositePartyType oppositePartyType) throws Exception {

        return walletService.withdraw(walletId, amount, oppositePartyType);
    }

    @GetMapping("/{walletId}/transactions")
    public List<Transaction> getTransactions(@PathVariable Long walletId) throws Exception {

        return walletService.getTransactions(walletId);
    }
}
