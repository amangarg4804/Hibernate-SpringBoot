package com.bookstore.service;

import com.bookstore.entity.Inventory;
import com.bookstore.repository.InventoryRepository;
import javax.transaction.Transactional;

import com.vladmihalcea.concurrent.Retry;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public Inventory firstTransactionFetchesAndReturn() {
        Inventory firstInventory = inventoryRepository.findById(1L).orElseThrow();

        return firstInventory;
    }       
    
    @Transactional
    public void secondTransactionFetchesAndReturn() {
        Inventory secondInventory = inventoryRepository.findById(1L).orElseThrow();

        secondInventory.setQuantity(secondInventory.getQuantity() - 1);
    }
    @Retry(times = 2, on = OptimisticLockingFailureException.class)
    // In case the entity is detached and modified by another transaction like in this case,
    // the retry annotation does its job of retrying the operation specified no of times,
    // but the operation itself still fails with OptimisticLockingFailureException
    // Do not attempt to retry the transaction that uses merge(). Each retry will just
    //fetch from the database the entity whose version doesn’t match the version of
    //the detached entity, resulting in an Optimistic Locking exception.
    public void thirdTransactionMergesAndUpdates(Inventory firstInventory) {
        System.out.println("***********************Retrying*************************");
        // if we add this line,i.e., if we select the entity before calling save, then save operation succeeds,
        // otherwise it throws OptimisticLockingFailureException
//        firstInventory = inventoryRepository.findById(1L).orElseThrow();
        inventoryRepository.save(firstInventory); // calls EntityManager#merge() behind the scene
        
        // this ends up in optimistic locking exception
    }
}
