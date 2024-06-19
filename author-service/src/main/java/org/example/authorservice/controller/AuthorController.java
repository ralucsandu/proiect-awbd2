package org.example.authorservice.controller;

import org.example.authorservice.exception.AuthorNotFoundException;
import org.example.authorservice.model.Author;
import org.example.authorservice.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/authors")
public class AuthorController {

    @Autowired
    private AuthorRepository authorRepository;

    @GetMapping
    public List<EntityModel<Author>> getAllAuthors() {
        List<Author> authors = authorRepository.findAll();
        return authors.stream()
                .map(author -> {
                    String wikipediaUrl = "https://ro.wikipedia.org/wiki/" + author.getName().replace(" ", "_");
                    return EntityModel.of(author,
                            linkTo(methodOn(AuthorController.class).getAuthorById(author.getId())).withSelfRel(),
                            linkTo(methodOn(AuthorController.class).getAllAuthors()).withRel("authors"),
                            Link.of(wikipediaUrl).withRel("wikipedia"));
                })
                .collect(Collectors.toList());
    }


    @GetMapping("/{id}")
    public EntityModel<Author> getAuthorById(@PathVariable Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException("Author not found with id: " + id));

        String wikipediaUrl = "https://ro.wikipedia.org/wiki/" + author.getName().replace(" ", "_");

        return EntityModel.of(author,
                linkTo(methodOn(AuthorController.class).getAuthorById(id)).withSelfRel(),
                linkTo(methodOn(AuthorController.class).getAllAuthors()).withRel("authors"),
                Link.of(wikipediaUrl).withRel("wikipedia"));
    }


    @PostMapping
    public ResponseEntity<EntityModel<Author>> createAuthor(@RequestBody Author author) {
        Author createdAuthor = authorRepository.save(author);

        String wikipediaUrl = "https://ro.wikipedia.org/wiki/" + createdAuthor.getName().replace(" ", "_");

        EntityModel<Author> authorResource = EntityModel.of(createdAuthor,
                linkTo(methodOn(AuthorController.class).getAuthorById(createdAuthor.getId())).withSelfRel(),
                linkTo(methodOn(AuthorController.class).getAllAuthors()).withRel("authors"),
                Link.of(wikipediaUrl).withRel("wikipedia"));

        return ResponseEntity.created(authorResource.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(authorResource);
    }


    @ExceptionHandler(AuthorNotFoundException.class)
    public ResponseEntity<Object> handleAuthorNotFoundException(AuthorNotFoundException ex) {
        return new ResponseEntity<>("Author not found", HttpStatus.NOT_FOUND);
    }
}
