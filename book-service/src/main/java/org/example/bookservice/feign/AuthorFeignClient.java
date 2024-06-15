package org.example.bookservice.feign;

import org.example.bookservice.dto.AuthorDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "author-service") // name should match the spring.application.name of author-service
public interface AuthorFeignClient {

    @GetMapping("/authors")
    List<AuthorDto> getAllAuthors();

    @GetMapping("/authors/{id}")
    AuthorDto getAuthorById(@PathVariable("id") Long id);
}
