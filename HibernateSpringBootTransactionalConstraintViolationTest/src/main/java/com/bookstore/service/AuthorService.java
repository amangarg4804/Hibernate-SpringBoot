package com.bookstore.service;

import com.bookstore.entity.Author;
import com.bookstore.repository.AuthorRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Transactional(rollbackOn = Exception.class)
    public void saveAuthor(Author author) {
        try{
            authorRepository.save(author);
            System.out.println("**************Hello**********************");
        } catch (Exception e) {
            System.out.println("********Error saving author******: "  + e.getMessage());
        }
        System.out.println("**************Same method after catch block **********************");
    }
}
