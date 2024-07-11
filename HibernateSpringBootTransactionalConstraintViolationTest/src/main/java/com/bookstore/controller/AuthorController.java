package com.bookstore.controller;

import com.bookstore.entity.Author;
import com.bookstore.service.AuthorService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/author")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @PostMapping
    public void createAuthor() {
        Author author1 = new Author();
        author1.setName("Olivia Goy");
        author1.setGenre("Horror");
        author1.setAge(25);
        try{
            authorService.saveAuthor(author1);
        } catch (Exception e) {
            System.out.println("*******Special catch block required for a transactional annotated method****");
        }
        System.out.println("************END*************");
    }
}
