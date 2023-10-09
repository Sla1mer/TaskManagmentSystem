package com.example.PracticeAuthBackend.repo;

import com.example.PracticeAuthBackend.dto.entities.UserDto;
import org.springframework.data.repository.CrudRepository;

public interface UserDtoRepo extends CrudRepository<UserDto, Long> {

    UserDto findByLoginAndPassword(String login, String password);
    UserDto findByLogin(String login);

}
