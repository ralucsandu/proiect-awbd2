package org.example.bookservice.controller;

import org.example.bookservice.client.AuthorClient;
import org.example.bookservice.dto.AuthorDto;
import org.example.bookservice.dto.BookDto;
import org.example.bookservice.exception.BookNotFoundException;
import org.example.bookservice.model.Book;
import org.example.bookservice.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorClient authorClient;

    @GetMapping
    public List<BookDto> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::convertToBookDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public BookDto getBookById(@PathVariable Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
        return convertToBookDto(book);
    }

    @PostMapping
    public Book createBook(@RequestBody BookDto bookDto) {
        Set<AuthorDto> authors = bookDto.getAuthors();
        Set<Long> authorIds = authors.stream()
                .map(AuthorDto::getId)
                .collect(Collectors.toSet());

        List<AuthorDto> validatedAuthors = authorIds.stream()
                .map(authorClient::getAuthorById)
                .collect(Collectors.toList());

        if (validatedAuthors.size() != authorIds.size()) {
            throw new RuntimeException("Some authors not found");
        }

        Book book = new Book();
        book.setTitle(bookDto.getTitle());
        book.setIsbn(bookDto.getIsbn());
        book.setDescription(bookDto.getDescription());
        book.setPrice(bookDto.getPrice());
        book.setAuthorIds(authorIds);
        return bookRepository.save(book);
    }

    private BookDto convertToBookDto(Book book) {
        Set<AuthorDto> authors = book.getAuthorIds().stream()
                .map(authorClient::getAuthorById)
                .collect(Collectors.toSet());

        BookDto bookDto = new BookDto();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setDescription(book.getDescription());
        bookDto.setPrice(book.getPrice());
        bookDto.setAuthors(authors);
        return bookDto;
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<Object> handleBookNotFoundException(BookNotFoundException ex) {
        return new ResponseEntity<>("Book not found", HttpStatus.NOT_FOUND);
    }

}
