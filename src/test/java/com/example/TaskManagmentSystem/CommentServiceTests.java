package com.example.TaskManagmentSystem;

import com.example.TaskManagmentSystem.dto.entities.Comment;
import com.example.TaskManagmentSystem.dto.entities.Task;
import com.example.TaskManagmentSystem.dto.entities.User;
import com.example.TaskManagmentSystem.repository.CommentRepository;
import com.example.TaskManagmentSystem.repository.TaskRepository;
import com.example.TaskManagmentSystem.repository.UserRepository;
import com.example.TaskManagmentSystem.services.CommentService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class CommentServiceTests {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    void testCreateComment() {
        Comment comment = new Comment();
        comment.setText("Test comment");
        comment.setAuthor(new User(1L));
        comment.setTask(new Task(1L));
        comment.setId(1L);

        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment createdComment = commentService.createComment(comment);

        verify(commentRepository, Mockito.times(1)).save(comment);

        assertEquals("Test comment", createdComment.getText());
        assertEquals(1L, createdComment.getId());
        assertEquals(new User(1), createdComment.getAuthor());
        assertEquals(new Task(1L), createdComment.getTask());
    }

    @Test
    void testGetCommentsByTask() {
        Long taskId = 1L;
        Task task = new Task();
        task.setId(taskId);

        Comment comment1 = new Comment(1L, 1L, "Comment 1");
        Comment comment2 = new Comment(2L, 2L, "Comment 2");

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(commentRepository.findByTask(task)).thenReturn(List.of(comment1, comment2));

        List<Comment> comments = commentService.getCommentsByTask(taskId);

        verify(taskRepository).findById(taskId);
        verify(commentRepository).findByTask(task);

        assertEquals(2, comments.size());

        assertEquals("Comment 1", comments.get(0).getText());
        assertEquals(1L, comments.get(0).getAuthor().getId());
        assertEquals(1L, comments.get(0).getTask().getId());

        assertEquals("Comment 2", comments.get(1).getText());
        assertEquals(2L, comments.get(1).getAuthor().getId());
        assertEquals(2L, comments.get(1).getTask().getId());
    }

    @Test
    void testGetCommentsByTaskWithInvalidTaskId() {
        Long invalidTaskId = 99L;

        when(taskRepository.findById(invalidTaskId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> commentService.getCommentsByTask(invalidTaskId));

        verify(taskRepository).findById(invalidTaskId);

        verify(commentRepository, never()).findByTask(any(Task.class));
    }
}
