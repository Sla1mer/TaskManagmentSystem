package com.example.TaskManagmentSystem.repository;

import com.example.TaskManagmentSystem.dto.entities.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenDtoRepository extends CrudRepository<RefreshToken, Long> {

    RefreshToken findByUserId(long userId);
    RefreshToken findByToken(String token);

}
