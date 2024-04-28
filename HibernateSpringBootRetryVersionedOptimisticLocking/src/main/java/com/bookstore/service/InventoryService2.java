package com.bookstore.service;

import com.vladmihalcea.concurrent.Retry;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class InventoryService2 {
    private final RetryFromIncomingTransactionService retryFromIncomingTransactionService;

    public InventoryService2(RetryFromIncomingTransactionService retryFromIncomingTransactionService) {
        this.retryFromIncomingTransactionService = retryFromIncomingTransactionService;
    }

    @Transactional

//    Optimistic version locking works whether the method is annotated with @Transactional or not. Naman was wrong
    @Retry(times = 10, on = OptimisticLockingFailureException.class)
    // This retry doesn't work because the calling method has @Transactional
    // Adding it results in org.springframework.transaction.IllegalTransactionStateException:
    // You shouldn't retry an operation from within an existing Transaction.
    // This is because we can't retry if the current Transaction was already rolled back!
    public void updateQuantity() {
        System.out.println("*********************Inventory Service 2 update quantity**************");
        retryFromIncomingTransactionService.updateQuantity();
    }
}

