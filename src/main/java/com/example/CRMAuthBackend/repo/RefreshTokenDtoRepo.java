package com.example.CRMAuthBackend.repo;

import com.example.CRMAuthBackend.dto.entities.RefreshTokenDto;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenDtoRepo extends CrudRepository<RefreshTokenDto, Long> {

    RefreshTokenDto findByUserId(long userId);
    RefreshTokenDto findByToken(String token);

}
