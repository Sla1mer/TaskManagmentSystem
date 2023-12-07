package com.example.TaskManagmentSystem.controllers;

import com.example.TaskManagmentSystem.dto.entities.Task;
import com.example.TaskManagmentSystem.dto.exceptions.IncorrectTokenException;
import com.example.TaskManagmentSystem.enums.TaskPriority;
import com.example.TaskManagmentSystem.enums.TaskStatus;
import com.example.TaskManagmentSystem.services.TaskService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@Tag(name = "TaskController", description = "Задачи")
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // Создание новой задачи
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        Task createdTask = taskService.createTask(task);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    // Получение списка всех задач
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(@RequestParam(name = "status", required = false) TaskStatus status,
                                                  @RequestParam(name = "priority", required = false) TaskPriority priority,
                                                  @RequestParam(name = "authorId", required = false) Long authorId,
                                                  @RequestParam(name = "assigned", required = false) Long assigned,
                                                  @RequestParam(name = "page", defaultValue = "0") int page,
                                                  @RequestParam(name = "size", defaultValue = "10") int size) {
        List<Task> tasks = taskService.getAllTasks(status, priority, authorId, assigned, page, size);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    // Получение задачи по идентификатору
    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long taskId) {
        Task task = taskService.getTaskById(taskId);
        return new ResponseEntity<>(task, HttpStatus.OK);
    }

    // Обновление задачи
    @PutMapping("/{taskId}")
    public ResponseEntity<Task> updateTask(@PathVariable Long taskId, @RequestBody Task updatedTask, HttpServletRequest request) throws AccessDeniedException, IncorrectTokenException {
        Task updated = taskService.updateTask(taskId, updatedTask, request);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    // Обновление статуса задачи (для исполнителя)
    @PutMapping("/changeTaskStatus/{taskId}")
    public ResponseEntity<Task> changeTaskStatus(@PathVariable Long taskId, @RequestBody TaskStatus updatedTask, HttpServletRequest request) throws AccessDeniedException, IncorrectTokenException {
        Task updated = taskService.changeTaskStatus(taskId, updatedTask, request);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    // Удаление задачи
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId, HttpServletRequest request) throws AccessDeniedException, IncorrectTokenException {
        taskService.deleteTask(taskId, request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Получение задач пользователя
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Task>> getTasksByUser(@PathVariable Long userId,
                                                     @RequestParam(name = "status", required = false) TaskStatus status,
                                                     @RequestParam(name = "priority", required = false) TaskPriority priority,
                                                     @RequestParam(name = "page", defaultValue = "0") int page,
                                                     @RequestParam(name = "size", defaultValue = "10") int size) {
        List<Task> tasks = taskService.getTasksByUser(userId, status, priority, page, size);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    // Получение задач, назначенных пользователю
    @GetMapping("/assigned/{userId}")
    public ResponseEntity<List<Task>> getAssignedTasksByUser(@PathVariable Long userId,
                                                             @RequestParam(name = "status", required = false) TaskStatus status,
                                                             @RequestParam(name = "priority", required = false) TaskPriority priority,
                                                             @RequestParam(name = "page", defaultValue = "0") int page,
                                                             @RequestParam(name = "size", defaultValue = "10") int size) {
        List<Task> assignedTasks = taskService.getAssignedTasksByUser(userId, status, priority, page, size);
        return new ResponseEntity<>(assignedTasks, HttpStatus.OK);
    }

}

