package com.bookstore.service;

import com.bookstore.entity.Author;
import com.bookstore.repository.AuthorRepository;
import java.util.Random;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InsertSecondAuthorService {

    private final AuthorRepository authorRepository;

    public InsertSecondAuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

//    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void insertSecondAuthor() {

        Author author = new Author();
        author.setName("Alicia Tom");
        System.out.println("***************saving second author************");
        authorRepository.save(author);
        System.out.println("***************saved second author************");
        author.setName("Aman Garg");
        // Notes on setting name to Aman Garg
        // Case 1: method is annotated with  @Transactional(propagation = Propagation.NOT_SUPPORTED). In this case, there is no existing transaction. save method is called outside any transaction.
        // the save method implementation starts a new transaction which ends as soon as save method is done executing.
        // This means that setName method above is being run without any transaction, so the changes don't get propagated to database.
        // if we check database, we see the name is Alicia Tom and not Aman Garg

        // Case2: method is not annotated with  @Transactional(propagation = Propagation.NOT_SUPPORTED). Which means that it participates in existing transaction
        // that was started by insertFirstAuthor() method. We have commented out the code that throws the exception so that transaction successfully commits.
        // In this case, both author names are set to Aman Garg (because we also set name in insertFirstAuthor method)

        //NOTES on transactional propagation and rollback:
        // Case 1: When method is annotated with @Transactional(propagation = Propagation.NOT_SUPPORTED)
        // note that since Alicia Tom is saved outside of transaction created by insertFirstAuthor method,
        // the exception thrown below doesn't rollback insertion of Alicia Tom,
        // it only rolls back the insertFirstAuthor transaction

        // Case 2: When method is not annotated with @Transactional. The transaction created by insertFirstAuthor
        // method doesn't get suspended. The save method above participates in same transaction and doesn't create a new transaction
        // If there is an exception from below code, since it is only one transaction, both Alicia tom and Joana Nimar inserts are rolled back


        if (new Random().nextBoolean()) {
//            throw new RuntimeException("DummyException: this should cause "
//                    + "rollback of the insert triggered in insertFirstAuthor() !");
        }
    }
}
