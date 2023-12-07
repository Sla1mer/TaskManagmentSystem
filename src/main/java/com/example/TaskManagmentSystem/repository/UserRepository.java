package com.example.TaskManagmentSystem.repository;

import com.example.TaskManagmentSystem.dto.entities.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

    User findByEmailAndPassword(String email, String password);
    User findByEmail(String numberPhone);
}
