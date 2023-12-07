package com.example.TaskManagmentSystem.repository;

import com.example.TaskManagmentSystem.dto.entities.Task;
import com.example.TaskManagmentSystem.dto.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    List<Task> findByAuthor(User author);

    List<Task> findByAssignee(User assignee);
}
