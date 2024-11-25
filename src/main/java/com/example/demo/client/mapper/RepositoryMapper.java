package com.example.demo.client.mapper;

import com.example.demo.client.model.dto.GitHubRepository;
import com.example.demo.client.model.entity.Repository;
import com.example.demo.client.model.dto.RepositoryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class RepositoryMapper {

    @Mapping(source = "owner.login", target = "owner")
    public abstract Repository toRepository(GitHubRepository gitHubRepository);

    public abstract RepositoryDto toDto(Repository repository);

}
