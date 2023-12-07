package com.example.TaskManagmentSystem.repository;

import com.example.TaskManagmentSystem.dto.entities.Comment;
import com.example.TaskManagmentSystem.dto.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTask(Task task);

}
