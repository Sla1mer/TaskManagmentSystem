package com.example.TaskManagmentSystem.dto.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    private String text;

    public Comment(long authorId, long taskId, String text) {
        this.author = new User(authorId);
        this.task = new Task(taskId);
        this.text = text;
    }
}
