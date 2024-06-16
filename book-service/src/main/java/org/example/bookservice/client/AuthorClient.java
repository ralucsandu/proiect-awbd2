package org.example.bookservice.client;

import org.example.bookservice.dto.AuthorDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "author-service")
public interface AuthorClient {

    @GetMapping("/authors")
    List<AuthorDto> getAllAuthors();

    @GetMapping("/authors/{id}")
    AuthorDto getAuthorById(@PathVariable("id") Long id);
}
