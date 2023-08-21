package com.bookstore.event;

import com.bookstore.entity.BookReview;
import com.bookstore.entity.ReviewStatus;
import com.bookstore.repository.BookReviewRepository;

import java.util.Random;
import java.util.logging.Logger;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class CheckReviewEventHandler {

    private static final Logger logger = Logger.getLogger(CheckReviewEventHandler.class.getName());

    public final BookReviewRepository bookReviewRepository;

    public CheckReviewEventHandler(BookReviewRepository bookReviewRepository) {
        this.bookReviewRepository = bookReviewRepository;
    }

    //    @Async
    @TransactionalEventListener
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
    /** case 1: if we don't use @transactional annotation. By default, this method runs after the previous transaction
     *  has already commited. it will still use the previous transaction but changes won't be propagated to DB
     case 2: using @Transactional won't fix the situation. Using TransactionPhase.AFTER_COMPLETION won't fix the situation
     case 3: using Propagation.REQUIRES_NEW fixes the situation. It's not a cost free operation.
     It now creates two long running transactionsinstead of 1
     case 4: Without @Transactional using TransactionPhase.BEFORE_COMMIT: updates book review + only one long running
     transaction But if you really need to commit the previous transaction this is not an option
     case 5: Using auto-commit=false and provider_disables_autocommit=true and Propagation.REQUIRES_NEW: This time,
     the transaction required via Propagation.REQUIRES_NEW is delayed until you call bookReviewRepository.save(bookReview);.
     This means that the long process of checking the book review will hold open a single database connection instead of two.
     This is a little bit better, but still not acceptable.
     case 6: In all above cases,i.e,synchronous the postReview method blocks till review event is done.
     **/
    public void handleCheckReviewEvent(CheckReviewEvent event) {

        BookReview bookReview = event.getBookReview();

        logger.info(() -> "Starting checking of review: " + bookReview.getId());

        try {
            // simulate a check out of review grammar, content, acceptance 
            // policies, reviewer email, etc via artificial delay of 5s for demonstration purposes
            String content = bookReview.getContent(); // check content           
            String email = bookReview.getEmail(); // validate email
            Thread.sleep(40000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            // log exception
        }

        if (new Random().nextBoolean()) {
            bookReview.setStatus(ReviewStatus.ACCEPT);
            logger.info(() -> "Book review " + bookReview.getId() + " was accepted ...");
        } else {
            bookReview.setStatus(ReviewStatus.REJECT);
            logger.info(() -> "Book review " + bookReview.getId() + " was rejected ...");
        }

        bookReviewRepository.save(bookReview);

        logger.info(() -> "Checking review " + bookReview.getId() + " done!");
    }
}
