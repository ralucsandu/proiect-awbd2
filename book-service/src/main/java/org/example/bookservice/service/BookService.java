package org.example.bookservice.service;

import org.example.bookservice.client.AuthorClient;
import org.example.bookservice.dto.AuthorDto;
import org.example.bookservice.dto.BookDto;
import org.example.bookservice.model.Book;
import org.example.bookservice.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorClient authorClient;

    public BookDto getBookWithAuthors(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
        Set<AuthorDto> authorDtos = book.getAuthorIds().stream()
                .map(authorClient::getAuthorById)
                .collect(Collectors.toSet());

        return convertToDto(book, authorDtos);
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
