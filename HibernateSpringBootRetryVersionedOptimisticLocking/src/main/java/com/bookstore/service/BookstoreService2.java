package com.bookstore.service;

import com.vladmihalcea.concurrent.Retry;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
public class BookstoreService2 implements Runnable {

    private final InventoryService2 inventoryService2;

    public BookstoreService2(InventoryService2 inventoryService2) {
        this.inventoryService2 = inventoryService2;
    }
     
    @Override
    // The retry annotation should have been here for the retry to work
    public void run() {
        inventoryService2.updateQuantity();
    }
}
