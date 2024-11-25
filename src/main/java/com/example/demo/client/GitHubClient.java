package com.example.demo.client;

import com.example.demo.client.model.dto.GitHubRepository;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "githubClient", url = "https://api.github.com")
public interface GitHubClient {

    @GetMapping("/repos/{owner}/{repo}")
    GitHubRepository getDetails(@PathVariable("owner") String owner, @PathVariable("repo") String repo);
}
