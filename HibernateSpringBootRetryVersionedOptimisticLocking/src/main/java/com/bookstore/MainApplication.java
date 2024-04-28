package com.bookstore;

import com.bookstore.service.BookstoreService;
import com.bookstore.service.BookstoreService2;
import com.vladmihalcea.concurrent.aop.OptimisticConcurrencyControlAspect;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class MainApplication {

    private final BookstoreService bookstoreService;
    private final BookstoreService2 bookstoreService2;

    public MainApplication(BookstoreService bookstoreService, BookstoreService2 bookstoreService2) {
        this.bookstoreService = bookstoreService;
        this.bookstoreService2 = bookstoreService2;
    }

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    @Bean
    public OptimisticConcurrencyControlAspect
            optimisticConcurrencyControlAspect() {

        return new OptimisticConcurrencyControlAspect();
    }

    @Bean
    public ApplicationRunner init() {
        return args -> {

            ExecutorService executor = Executors.newFixedThreadPool(2);
            executor.execute(bookstoreService);
            // Thread.sleep(2000); -> adding a sleep here will break the transactions concurrency
            executor.execute(bookstoreService);

            executor.shutdown();
            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            // 1. The bookstoreService reads the value quantity as 10
            // 2. First transaction updates qty to 8
            // 3. 2nd transaction fails on OptimisticLockingException
            // 4. 2nd transaction is retried because of @retry annotation
            // 5. 2nd transaction updates qty to 6

            ExecutorService executor2 = Executors.newFixedThreadPool(2);
            executor2.execute(bookstoreService2);
            // Thread.sleep(2000); -> adding a sleep here will break the transactions concurrency
            executor2.execute(bookstoreService2);

            // Two cases for bookstoreService2
            // Case 1: When there is no Retry annotation at all.
            // 1. The bookstoreService2 reads the quantity as 6
            // 2. First transaction updates qty to 4
            // 3. 2nd transaction fails on OptimisticLockingException
            // 4. 2nd transaction is not retried because the retry annotation is at a wrong place.
            // It must NOT be on a method annotated with @Transactional
            // It must NOT be on a method that the method annotated with @Transactional calls

            // Two cases for bookstoreService2
            // Case 2: When there is Retry annotation either on the method in InventoryService2 or on the method in RetryFromIncomingTransactionService .
            // 1. The bookstoreService2 reads the quantity as 6
            // 2. Both transactions fail because it is not allowed to have a @Retry annotation within a transaction
            // 3. Quantity remains 6
            executor2.shutdown();
            try {
                executor2.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        };
    }
}
