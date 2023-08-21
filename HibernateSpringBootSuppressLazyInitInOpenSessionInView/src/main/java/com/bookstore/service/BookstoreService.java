package com.bookstore.service;

import com.bookstore.entity.Author;
import com.bookstore.repository.AuthorRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class BookstoreService {

    private final AuthorRepository authorRepository;

    public BookstoreService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Author fetchAuthorWithBooks() {
        Author author = authorRepository.findByNameWithBooks("Joana Nimar");

        return author;
    }

    //@Transactional
    // Adding transactional annotation results in exception and transaction is rolled back ,
    // org.hibernate.HibernateException: A collection
    // with cascade="all-delete-orphan" was no longer referenced by the owning entity instance:
    // com.bookstore.entity.Author.books
    public Author fetchAuthorWithoutBooks() {
        Author author = authorRepository.findByName("Joana Nimar");

        // explicitly set Books of the Author to null
        // in order to avoid fetching them from the database
        author.setBooks(null);

        // or, to an empty collection
        // author.setBooks(Collections.emptyList());
        return author;
    }
}
