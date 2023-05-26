package com.example.journalactionbackend.repo;

import com.example.journalactionbackend.dto.entities.RefreshTokenDto;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenDtoRepo extends CrudRepository<RefreshTokenDto, Long> {

    RefreshTokenDto findByUserId(long userId);
    RefreshTokenDto findByToken(String token);

}
