package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositoryRepository extends JpaRepository<com.example.demo.model.entity.Repository, Long> {

    Optional<com.example.demo.model.entity.Repository> findByOwnerAndRepositoryName(String owner, String repositoryName);

    boolean existsByOwnerAndRepositoryName(String owner, String repositoryName);

}
