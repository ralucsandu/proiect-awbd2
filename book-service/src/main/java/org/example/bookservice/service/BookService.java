package org.example.bookservice.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.example.bookservice.client.AuthorClient;
import org.example.bookservice.dto.AuthorDto;
import org.example.bookservice.dto.BookDto;
import org.example.bookservice.model.Book;
import org.example.bookservice.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorClient authorClient;

    @CircuitBreaker(name = "bookServiceCircuitBreaker", fallbackMethod = "fallbackGetBookWithAuthors")
    public BookDto getBookWithAuthors(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
        Set<AuthorDto> authorDtos = book.getAuthorIds().stream()
                .map(authorClient::getAuthorById)
                .collect(Collectors.toSet());

        return convertToDto(book, authorDtos);
    }

    @CircuitBreaker(name = "bookServiceCircuitBreaker", fallbackMethod = "fallbackGetAllBooks")
    public List<BookDto> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(book -> {
                    Set<AuthorDto> authors = book.getAuthorIds().stream()
                            .map(authorClient::getAuthorById)
                            .collect(Collectors.toSet());
                    return convertToDto(book, authors);
                })
                .collect(Collectors.toList());
    }

    @CircuitBreaker(name = "bookServiceCircuitBreaker", fallbackMethod = "fallbackCreateBook")
    public Book createBook(Book book) {
        validateAuthors(book.getAuthorIds());

        return bookRepository.save(book);
    }

    private void validateAuthors(Set<Long> authorIds) {
        List<AuthorDto> validatedAuthors = authorIds.stream()
                .map(authorClient::getAuthorById)
                .collect(Collectors.toList());

        if (validatedAuthors.size() != authorIds.size()) {
            throw new RuntimeException("Some authors not found");
        }
    }

    public BookDto fallbackGetBookWithAuthors(Long bookId, Throwable throwable) {
        BookDto bookDto = new BookDto();
        bookDto.setId(bookId);
        bookDto.setTitle("Unknown");
        bookDto.setIsbn("N/A");
        bookDto.setDescription("Service is currently unavailable");
        bookDto.setPrice((double) 0);
        bookDto.setAuthors(Collections.emptySet());
        return bookDto;
    }

    public List<BookDto> fallbackGetAllBooks(Throwable throwable) {
        return Collections.emptyList();
    }

    public Book fallbackCreateBook(Book book, Throwable throwable) {
        throw new RuntimeException("Failed to create book. Service is currently unavailable.");
    }

    private BookDto convertToDto(Book book, Set<AuthorDto> authorDtos) {
        BookDto bookDto = new BookDto();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setDescription(book.getDescription());
        bookDto.setPrice(book.getPrice());
        bookDto.setAuthors(authorDtos);
        return bookDto;
    }
}