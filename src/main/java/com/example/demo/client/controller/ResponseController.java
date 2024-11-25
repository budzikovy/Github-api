package com.example.demo.client.controller;

import com.example.demo.client.model.dto.RepositoryDto;
import com.example.demo.client.service.ResponseService;
import com.example.demo.client.model.entity.Repository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/repositories")
@AllArgsConstructor
public class ResponseController {

    private final ResponseService responseService;

    @GetMapping("/{owner}/{repositoryName}")
    public Repository getDetails(@PathVariable String owner, @PathVariable String repositoryName) {
        return responseService.getDetails(owner, repositoryName);
    }

    @PostMapping("/{owner}/{repositoryName}")
    public RepositoryDto saveRepository(@PathVariable String owner, @PathVariable String repositoryName, @RequestBody(required = false) Repository repository) {
        return responseService.saveRepository(owner, repositoryName, repository);
    }

    @GetMapping("/local/{owner}/{repositoryName}")
    public RepositoryDto getRepositoryDetails(@PathVariable String owner, @PathVariable String repositoryName) {
        return responseService.getRepositoryDetails(owner, repositoryName);
    }

    @PutMapping("/local/{owner}/{repositoryName}")
    public RepositoryDto editRepository(@PathVariable String owner, @PathVariable String repositoryName, @RequestBody Repository updatedRepository) {
        return responseService.editRepository(owner, repositoryName, updatedRepository);
    }

    @DeleteMapping("/local/{owner}/{repositoryName}")
    public RepositoryDto deleteRepositoryByOwnerAndName(@PathVariable String owner, @PathVariable String repositoryName) {
        return responseService.deleteRepository(owner, repositoryName);
    }

}
