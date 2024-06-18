package org.example.bookservice.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.example.bookservice.client.AuthorClient;
import org.example.bookservice.dto.AuthorDto;
import org.example.bookservice.dto.BookDto;
import org.example.bookservice.exception.BookNotFoundException;
import org.example.bookservice.model.Book;
import org.example.bookservice.repository.BookRepository;
import org.example.bookservice.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorClient authorClient;

    @GetMapping
    public List<BookDto> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookWithAuthors(@PathVariable Long id) {
        BookDto bookDto = bookService.getBookWithAuthors(id);
        return ResponseEntity.ok(bookDto);
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody BookDto bookDto) {
        Set<AuthorDto> authors = bookDto.getAuthors();
        Set<Long> authorIds = authors.stream()
                .map(AuthorDto::getId)
                .collect(Collectors.toSet());

        Book book = new Book();
        book.setTitle(bookDto.getTitle());
        book.setIsbn(bookDto.getIsbn());
        book.setDescription(bookDto.getDescription());
        book.setPrice(bookDto.getPrice());
        book.setAuthorIds(authorIds);

        Book createdBook = bookService.createBook(book);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<Object> handleBookNotFoundException(BookNotFoundException ex) {
        return new ResponseEntity<>("Book not found", HttpStatus.NOT_FOUND);
    }
}