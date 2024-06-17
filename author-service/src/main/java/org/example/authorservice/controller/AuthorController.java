package org.example.authorservice.controller;

import org.example.authorservice.exception.AuthorNotFoundException;
import org.example.authorservice.model.Author;
import org.example.authorservice.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/authors")
public class AuthorController {

    @Autowired
    private AuthorRepository authorRepository;

    @GetMapping
    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    @GetMapping("/{id}")
    public Author getAuthorById(@PathVariable Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException("Author not found with id: " + id));
    }

    @PostMapping
    public Author createAuthor(@RequestBody Author author) {
        return authorRepository.save(author);
    }

    @ExceptionHandler(AuthorNotFoundException.class)
    public ResponseEntity<Object> handleAuthorNotFoundException(AuthorNotFoundException ex) {
        return new ResponseEntity<>("Author not found", HttpStatus.NOT_FOUND);
    }
}