package org.example.bookservice.controller;

import org.example.bookservice.dto.AuthorDto;
import org.example.bookservice.feign.AuthorFeignClient;
import org.example.authorservice.model.Author;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookController {

    @Autowired
    private AuthorFeignClient authorClient;

    @GetMapping("/books/{bookId}/author")
    public AuthorDto getAuthorByBookId(@PathVariable Long bookId) {
        // Assuming you have a method to get the authorId by bookId
        Long authorId = getAuthorIdByBookId(bookId);
        return authorClient.getAuthorById(authorId);
    }

    private Long getAuthorIdByBookId(Long bookId) {
        // Mock implementation, replace with actual logic
        return 1L;
    }
}
