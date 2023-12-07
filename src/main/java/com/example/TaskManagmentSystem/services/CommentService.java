package com.example.TaskManagmentSystem.services;

import com.example.TaskManagmentSystem.dto.entities.Comment;
import com.example.TaskManagmentSystem.dto.entities.Task;
import com.example.TaskManagmentSystem.repository.CommentRepository;
import com.example.TaskManagmentSystem.repository.TaskRepository;
import com.example.TaskManagmentSystem.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, TaskRepository taskRepository) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
    }

    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Задача не найдена с идентификатором: " + taskId));

        return commentRepository.findByTask(task);
    }

}

