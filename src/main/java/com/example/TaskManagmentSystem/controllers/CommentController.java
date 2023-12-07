package com.example.TaskManagmentSystem.controllers;

import com.example.TaskManagmentSystem.dto.entities.Comment;
import com.example.TaskManagmentSystem.services.CommentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@Tag(name = "CommentController", description = "Комментарии")
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/create")
    public ResponseEntity<Comment> createComment(@RequestBody Comment comment) throws ExecutionException, InterruptedException {
        Comment createdComment = commentService.createComment(comment).get();
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<Comment>> getCommentsByTask(@PathVariable Long taskId) throws ExecutionException, InterruptedException {
        List<Comment> comments = commentService.getCommentsByTask(taskId).get();
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }
}

