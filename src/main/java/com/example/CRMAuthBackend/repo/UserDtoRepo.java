package com.example.CRMAuthBackend.repo;

import com.example.CRMAuthBackend.dto.entities.UserDto;
import org.springframework.data.repository.CrudRepository;

public interface UserDtoRepo extends CrudRepository<UserDto, Long> {

    UserDto findByEmailAndPassword(String numberPhone, String password);
    UserDto findByEmail(String numberPhone);
}
