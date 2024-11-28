package com.example.demo.controller;

import com.example.demo.model.dto.RepositoryDto;
import com.example.demo.service.RepositoryService;
import com.example.demo.model.entity.Repository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@AllArgsConstructor
public class RepositoryController {

    private final RepositoryService responseService;

    @GetMapping("/repositories/{owner}/{repositoryName}")
    public Repository getDetails(@PathVariable String owner, @PathVariable String repositoryName) {
        return responseService.getDetails(owner, repositoryName);
    }

    @PostMapping("/repositories/{owner}/{repositoryName}")
    public RepositoryDto saveRepository(@PathVariable String owner, @PathVariable String repositoryName, @RequestBody(required = false) Repository repository) {
        return responseService.saveRepository(owner, repositoryName, repository);
    }

    @GetMapping("/local/repositories/{owner}/{repositoryName}")
    public RepositoryDto getRepositoryDetails(@PathVariable String owner, @PathVariable String repositoryName) {
        return responseService.getRepositoryDetails(owner, repositoryName);
    }

    @PutMapping("/repositories/{owner}/{repositoryName}")
    public RepositoryDto editRepository(@PathVariable String owner, @PathVariable String repositoryName, @RequestBody Repository updatedRepository) {
        return responseService.editRepository(owner, repositoryName, updatedRepository);
    }

    @DeleteMapping("/repositories/{owner}/{repositoryName}")
    public RepositoryDto deleteRepositoryByOwnerAndName(@PathVariable String owner, @PathVariable String repositoryName) {
        return responseService.deleteRepository(owner, repositoryName);
    }

}
