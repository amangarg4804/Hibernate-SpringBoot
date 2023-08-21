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

            ExecutorService executor2 = Executors.newFixedThreadPool(2);
            executor2.execute(bookstoreService2);
            // Thread.sleep(2000); -> adding a sleep here will break the transactions concurrency
            executor2.execute(bookstoreService2);

            executor2.shutdown();
            try {
                executor2.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        };
    }
}
