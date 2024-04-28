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
//    @Retry(times = 10, on = OptimisticLockingFailureException.class)
    // This retry doesn't work because the calling method has @Transactional
    // Adding it results in org.springframework.transaction.IllegalTransactionStateException:
    // You shouldn't retry an operation from within an existing Transaction.
    // This is because we can't retry if the current Transaction was already rolled back!
    public void updateQuantity() {
        System.out.println("*********************Retrying from incoming transaction**************");
        Inventory inventory = inventoryRepository.findById(1L).get();
        inventory.setQuantity(inventory.getQuantity() - 2);
//        inventoryRepository.save(inventory); this line is only required if there is no existing transaction
    }
}
