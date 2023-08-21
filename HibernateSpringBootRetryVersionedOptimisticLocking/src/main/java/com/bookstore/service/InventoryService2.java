package com.bookstore.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class InventoryService2 {
    private final RetryFromIncomingTransactionService retryFromIncomingTransactionService;

    public InventoryService2(RetryFromIncomingTransactionService retryFromIncomingTransactionService) {
        this.retryFromIncomingTransactionService = retryFromIncomingTransactionService;
    }

    @Transactional
    public void updateQuantity() {
        retryFromIncomingTransactionService.updateQuantity();
    }
}

