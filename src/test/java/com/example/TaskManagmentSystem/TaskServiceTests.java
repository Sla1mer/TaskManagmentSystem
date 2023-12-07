package com.example.TaskManagmentSystem;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.TaskManagmentSystem.config.UserAuthenticationProvider;
import com.example.TaskManagmentSystem.dto.entities.Task;
import com.example.TaskManagmentSystem.dto.entities.User;
import com.example.TaskManagmentSystem.dto.exceptions.IncorrectTokenException;
import com.example.TaskManagmentSystem.enums.TaskPriority;
import com.example.TaskManagmentSystem.enums.TaskStatus;
import com.example.TaskManagmentSystem.repository.TaskRepository;
import com.example.TaskManagmentSystem.repository.UserRepository;
import com.example.TaskManagmentSystem.services.TaskService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.nio.file.AccessDeniedException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class TaskServiceTests {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserAuthenticationProvider userAuthenticationProvider;

    @InjectMocks
    private TaskService taskService;

    @Test
    void testCreateTask() {
        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("This is a test task");
        task.setStatus(TaskStatus.В_ОЖИДАНИИ);
        task.setPriority(TaskPriority.ВЫСОКИЙ);

        when(taskRepository.save(task)).thenReturn(task);

        Task createdTask = taskService.createTask(task);

        verify(taskRepository, Mockito.times(1)).save(task);

        assertEquals("Test Task", createdTask.getTitle());
        assertEquals("This is a test task", createdTask.getDescription());
        assertEquals(TaskStatus.В_ОЖИДАНИИ, createdTask.getStatus());
        assertEquals(TaskPriority.ВЫСОКИЙ, createdTask.getPriority());
    }

    @Test
    void testCreateTaskWithInvalidStatus() {
        Task invalidStatusTask = new Task();
        invalidStatusTask.setStatus(null);

        assertThrows(IllegalArgumentException.class, () -> taskService.createTask(invalidStatusTask));

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testCreateTaskWithInvalidPriority() {

        Task invalidPriorityTask = new Task();
        invalidPriorityTask.setPriority(null);

        assertThrows(IllegalArgumentException.class, () -> taskService.createTask(invalidPriorityTask));

        verify(taskRepository, never()).save(any(Task.class));
    }

    // От лица автора
    @Test
    void testUpdateTask() throws IncorrectTokenException, AccessDeniedException {
        Long taskId = 1L;
        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setTitle("Existing Task");
        existingTask.setDescription("This is an existing task");
        existingTask.setStatus(TaskStatus.В_ОЖИДАНИИ);
        existingTask.setPriority(TaskPriority.ВЫСОКИЙ);
        existingTask.setAuthor(new User(1));
        existingTask.setAssignee(new User(2));

        DecodedJWT decodedJWT = mock(DecodedJWT.class);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(userAuthenticationProvider.getTokenFromHeader(any(HttpServletRequest.class))).thenReturn("test-token");
        when(userAuthenticationProvider.verifyToken("test-token")).thenReturn(decodedJWT);
        when(taskRepository.save(existingTask)).thenReturn(existingTask);

        when(decodedJWT.getSubject()).thenReturn("1");

        Task updatedTask = new Task();
        updatedTask.setTitle("Updated Task");
        updatedTask.setDescription("This is an updated task");
        updatedTask.setStatus(TaskStatus.В_ПРОЦЕССЕ);
        updatedTask.setPriority(TaskPriority.НИЗКИЙ);
        existingTask.setAssignee(new User(3));

        Task result = taskService.updateTask(taskId, updatedTask, mock(HttpServletRequest.class));

        verify(taskRepository, times(1)).save(existingTask);

        assertEquals("Updated Task", result.getTitle());
        assertEquals("This is an updated task", result.getDescription());
        assertEquals(TaskStatus.В_ПРОЦЕССЕ, result.getStatus());
        assertEquals(TaskPriority.НИЗКИЙ, result.getPriority());
    }

    // От лица исполнителя
    @Test
    void testUpdateTaskAssignee() throws IncorrectTokenException, AccessDeniedException {
        Long taskId = 1L;
        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setTitle("Existing Task");
        existingTask.setDescription("This is an existing task");
        existingTask.setStatus(TaskStatus.В_ОЖИДАНИИ);
        existingTask.setPriority(TaskPriority.ВЫСОКИЙ);
        existingTask.setAuthor(new User(1));
        existingTask.setAssignee(new User(2));

        DecodedJWT decodedJWT = mock(DecodedJWT.class);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(userAuthenticationProvider.getTokenFromHeader(any(HttpServletRequest.class))).thenReturn("test-token");
        when(userAuthenticationProvider.verifyToken("test-token")).thenReturn(decodedJWT);
        when(taskRepository.save(existingTask)).thenReturn(existingTask);

        when(decodedJWT.getSubject()).thenReturn("2");

        Task updatedTask = new Task();
        updatedTask.setTitle("Updated Task");
        updatedTask.setDescription("This is an updated task");
        updatedTask.setStatus(TaskStatus.В_ПРОЦЕССЕ);
        updatedTask.setPriority(TaskPriority.НИЗКИЙ);
        existingTask.setAssignee(new User(3));

        assertThrows(AccessDeniedException.class, () -> taskService.updateTask(taskId, updatedTask, mock(HttpServletRequest.class)));

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testUpdateTaskWithInvalidStatus() throws IncorrectTokenException, AccessDeniedException {
        Long taskId = 1L;
        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setTitle("Existing Task");
        existingTask.setDescription("This is an existing task");
        existingTask.setStatus(TaskStatus.В_ОЖИДАНИИ);
        existingTask.setPriority(TaskPriority.ВЫСОКИЙ);
        existingTask.setAuthor(new User(1));
        existingTask.setAssignee(new User(2));

        DecodedJWT decodedJWT = mock(DecodedJWT.class);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(userAuthenticationProvider.getTokenFromHeader(any(HttpServletRequest.class))).thenReturn("test-token");
        when(userAuthenticationProvider.verifyToken("test-token")).thenReturn(decodedJWT);
        when(taskRepository.save(existingTask)).thenReturn(existingTask);

        when(decodedJWT.getSubject()).thenReturn("1");

        Task updatedTask = new Task();
        updatedTask.setTitle("Updated Task");
        updatedTask.setDescription("This is an updated task");
        updatedTask.setStatus(null);
        updatedTask.setPriority(TaskPriority.НИЗКИЙ);
        existingTask.setAssignee(new User(3));

        assertThrows(IllegalArgumentException.class, () -> taskService.updateTask(taskId, updatedTask, mock(HttpServletRequest.class)));

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testUpdateTaskWithInvalidPriority() throws IncorrectTokenException, AccessDeniedException {
        Long taskId = 1L;
        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setTitle("Existing Task");
        existingTask.setDescription("This is an existing task");
        existingTask.setStatus(TaskStatus.В_ОЖИДАНИИ);
        existingTask.setPriority(TaskPriority.ВЫСОКИЙ);
        existingTask.setAuthor(new User(1));
        existingTask.setAssignee(new User(2));

        DecodedJWT decodedJWT = mock(DecodedJWT.class);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(userAuthenticationProvider.getTokenFromHeader(any(HttpServletRequest.class))).thenReturn("test-token");
        when(userAuthenticationProvider.verifyToken("test-token")).thenReturn(decodedJWT);
        when(taskRepository.save(existingTask)).thenReturn(existingTask);

        when(decodedJWT.getSubject()).thenReturn("1");

        Task updatedTask = new Task();
        updatedTask.setTitle("Updated Task");
        updatedTask.setDescription("This is an updated task");
        updatedTask.setStatus(TaskStatus.В_ПРОЦЕССЕ);
        updatedTask.setPriority(null);
        existingTask.setAssignee(new User(3));

        assertThrows(IllegalArgumentException.class, () -> taskService.updateTask(taskId, updatedTask, mock(HttpServletRequest.class)));

        verify(taskRepository, never()).save(any(Task.class));
    }


    @Test
    void testGetTaskById() {
        Long taskId = 1L;
        Task task = new Task("Test Task",
                "This is a test task",
                TaskStatus.В_ОЖИДАНИИ,
                TaskPriority.ВЫСОКИЙ,
                1L,
                2L);

        task.setId(taskId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        Task result = taskService.getTaskById(taskId);

        verify(taskRepository, Mockito.times(1)).findById(taskId);

        assertEquals(task, result);
    }

    @Test
    void testGetTaskByIdWithEntityNotFoundException() {
        Long taskId = 1L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            taskService.getTaskById(taskId);
        });

        Mockito.verify(taskRepository, Mockito.times(1)).findById(taskId);

        assertEquals("Задача не найдена с идентификатором: " + taskId, exception.getMessage());
    }

}
