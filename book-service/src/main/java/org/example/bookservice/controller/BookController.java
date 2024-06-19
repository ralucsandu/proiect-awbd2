package org.example.bookservice.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.example.bookservice.client.AuthorClient;
import org.example.bookservice.dto.AuthorDto;
import org.example.bookservice.dto.BookDto;
import org.example.bookservice.exception.BookNotFoundException;
import org.example.bookservice.model.Book;
import org.example.bookservice.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public List<EntityModel<BookDto>> getAllBooks() {
        List<BookDto> books = bookService.getAllBooks();
        return books.stream()
                .map(book -> EntityModel.of(book,
                        linkTo(methodOn(BookController.class).getBookWithAuthors(book.getId())).withSelfRel(),
                        linkTo(methodOn(BookController.class).getAllBooks()).withRel("books")))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<BookDto>> getBookWithAuthors(@PathVariable Long id) {
        BookDto bookDto = bookService.getBookWithAuthors(id);
        EntityModel<BookDto> bookResource = EntityModel.of(bookDto,
                linkTo(methodOn(BookController.class).getBookWithAuthors(id)).withSelfRel(),
                linkTo(methodOn(BookController.class).getAllBooks()).withRel("books"));
        return ResponseEntity.ok(bookResource);
    }

    @PostMapping
    public ResponseEntity<EntityModel<Book>> createBook(@RequestBody BookDto bookDto) {
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

        EntityModel<Book> bookResource = EntityModel.of(createdBook,
                linkTo(methodOn(BookController.class).getBookWithAuthors(createdBook.getId())).withSelfRel(),
                linkTo(methodOn(BookController.class).getAllBooks()).withRel("books"));

        return ResponseEntity.created(bookResource.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(bookResource);
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<Object> handleBookNotFoundException(BookNotFoundException ex) {
        return new ResponseEntity<>("Book not found", HttpStatus.NOT_FOUND);
    }
}
