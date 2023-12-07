package com.example.TaskManagmentSystem.services;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.TaskManagmentSystem.config.UserAuthenticationProvider;
import com.example.TaskManagmentSystem.dto.entities.Task;
import com.example.TaskManagmentSystem.dto.entities.User;
import com.example.TaskManagmentSystem.dto.exceptions.IncorrectTokenException;
import com.example.TaskManagmentSystem.enums.TaskPriority;
import com.example.TaskManagmentSystem.enums.TaskStatus;
import com.example.TaskManagmentSystem.repository.TaskRepository;
import com.example.TaskManagmentSystem.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final UserAuthenticationProvider userAuthenticationProvider;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserRepository userRepository, UserAuthenticationProvider userAuthenticationProvider) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.userAuthenticationProvider = userAuthenticationProvider;
    }

    public Task createTask(Task task) {

        if (!isValidStatus(task.getStatus())) {
            throw new IllegalArgumentException("Недопустимое значение статуса задачи");
        }

        if (!isValidPriority(task.getPriority())) {
            throw new IllegalArgumentException("Недопустимое значение приоритета задачи");
        }

        return taskRepository.save(task);
    }

    public Task updateTask(Long taskId, Task updatedTask, HttpServletRequest request) throws IncorrectTokenException, AccessDeniedException {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Задача не найдена с идентификатором: " + taskId));

        String jwtToken = userAuthenticationProvider.getTokenFromHeader(request);

        DecodedJWT decoded = userAuthenticationProvider.verifyToken(jwtToken);

        // Проверяем, имеет ли текущий пользователь право изменять статус этой задачи
        if (!(existingTask.getAuthor().getId() == Integer.parseInt(decoded.getSubject()))) {
            throw new AccessDeniedException("Вы не имеете права изменять статус этой задачи");
        }

        if (!isValidStatus(updatedTask.getStatus())) {
            throw new IllegalArgumentException("Недопустимое значение статуса задачи");
        }

        if (!isValidPriority(updatedTask.getPriority())) {
            throw new IllegalArgumentException("Недопустимое значение приоритета задачи");
        }

        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setStatus(updatedTask.getStatus());
        existingTask.setPriority(updatedTask.getPriority());
        existingTask.setAssignee(updatedTask.getAssignee());

        return taskRepository.save(existingTask);
    }

    public Task changeTaskStatus(Long taskId, TaskStatus newStatus, HttpServletRequest request) throws AccessDeniedException, IncorrectTokenException {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Задача не найдена с идентификатором: " + taskId));

        String jwtToken = userAuthenticationProvider.getTokenFromHeader(request);

        DecodedJWT decoded = userAuthenticationProvider.verifyToken(jwtToken);

        // Проверяем, имеет ли текущий пользователь право изменять статус этой задачи
        if (!(existingTask.getAssignee().getId() == Integer.parseInt(decoded.getSubject()))) {
            throw new AccessDeniedException("Вы не имеете права изменять статус этой задачи");
        }

        if (!isValidStatus(newStatus)) {
            throw new IllegalArgumentException("Недопустимое значение статуса задачи");
        }

        existingTask.setStatus(newStatus);
        return taskRepository.save(existingTask);
    }

    private boolean isValidStatus(TaskStatus status) {
        // Проверка наличия статуса в Enum
        for (TaskStatus validStatus : TaskStatus.values()) {
            if (validStatus.equals(status)) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidPriority(TaskPriority priority) {
        // Проверка наличия приоритета в Enum
        for (TaskPriority validPriority: TaskPriority.values()) {
            if (validPriority.equals(priority)) {
                return true;
            }
        }
        return false;
    }

    public void deleteTask(Long taskId, HttpServletRequest request) throws IncorrectTokenException, AccessDeniedException {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Задача не найдена с идентификатором: " + taskId));

        String jwtToken = userAuthenticationProvider.getTokenFromHeader(request);

        DecodedJWT decoded = userAuthenticationProvider.verifyToken(jwtToken);

        // Проверяем, имеет ли текущий пользователь право изменять статус этой задачи
        if (!(existingTask.getAuthor().getId() == Integer.parseInt(decoded.getSubject()))) {
            throw new AccessDeniedException("Вы не имеете права удалять эту задачу");
        }

        taskRepository.deleteById(taskId);
    }

    // Получение списка всех задач с фильтрацией и пагинацией
    public List<Task> getAllTasks(TaskStatus status, TaskPriority priority, Long authorId, Long assigned, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Specification<Task> specification = Specification.where(null);

        if (status != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("status"), status));
        }

        if (priority != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("priority"), priority));
        }

        if (authorId != null) {
            User author = userRepository.findById(authorId)
                    .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден с идентификатором: " + authorId));
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("author"), author));
        }

        if (assigned != null) {
            User assignedUser = userRepository.findById(assigned)
                    .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден с идентификатором: " + authorId));
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("assignee"), assignedUser));
        }

        return taskRepository.findAll(specification, pageable).getContent();
    }


    public Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Задача не найдена с идентификатором: " + taskId));
    }

    public List<Task> getTasksByUser(Long userId, TaskStatus status, TaskPriority priority, int page, int size) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден с идентификатором: " + userId));

        Pageable pageable = PageRequest.of(page, size);

        Specification<Task> specification = Specification.where((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("author"), user));

        if (status != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("status"), status));
        }

        if (priority != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("priority"), priority));
        }

        return taskRepository.findAll(specification, pageable).getContent();
    }

    public List<Task> getAssignedTasksByUser(Long userId, TaskStatus status, TaskPriority priority, int page, int size) {
        User assignee = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден с идентификатором: " + userId));

        Pageable pageable = PageRequest.of(page, size);

        Specification<Task> specification = Specification.where((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("assignee"), assignee));

        if (status != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("status"), status));
        }

        if (priority != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("priority"), priority));
        }

        return taskRepository.findAll(specification, pageable).getContent();
    }



}
