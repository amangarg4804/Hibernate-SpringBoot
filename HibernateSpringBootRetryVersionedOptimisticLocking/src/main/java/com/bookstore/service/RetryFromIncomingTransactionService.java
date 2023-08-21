package com.bookstore.service;

import com.bookstore.entity.Inventory;
import com.bookstore.repository.InventoryRepository;
import com.vladmihalcea.concurrent.Retry;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
public class RetryFromIncomingTransactionService {

    private final InventoryRepository inventoryRepository;

    public RetryFromIncomingTransactionService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }
    @Retry(times = 10, on = OptimisticLockingFailureException.class)
    public void updateQuantity() {
        System.out.println("*********************Retrying from incoming transaction**************");
        Inventory inventory = inventoryRepository.findById(1L).get();
        inventory.setQuantity(inventory.getQuantity() - 2);
    }
}
