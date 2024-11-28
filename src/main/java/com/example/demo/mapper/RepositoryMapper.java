package com.example.demo.mapper;

import com.example.demo.model.dto.GitHubRepository;
import com.example.demo.model.entity.Repository;
import com.example.demo.model.dto.RepositoryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class RepositoryMapper {

    @Mapping(source = "owner.login", target = "owner")
    public abstract Repository toRepository(GitHubRepository gitHubRepository);

    public abstract RepositoryDto toDto(Repository repository);

}
