package com.example.TaskManagmentSystem.dto.entities;

import com.example.TaskManagmentSystem.enums.TaskPriority;
import com.example.TaskManagmentSystem.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private User assignee;

    public Task(String title, String description, TaskStatus status, TaskPriority priority, long authorId, long assigneeId) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.author = new User(authorId);
        this.assignee = new User(assigneeId);
    }

    public Task(long id) {
        this.id = id;
    }
}
