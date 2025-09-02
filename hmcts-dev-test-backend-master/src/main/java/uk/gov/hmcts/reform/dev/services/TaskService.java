package uk.gov.hmcts.reform.dev.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.models.TaskStatus;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for Task business logic operations.
 * 
 * Handles all business logic related to task management including
 * CRUD operations, validation, and business rules. This service
 * acts as an intermediary between controllers and the data layer.
 * 
 * @author Your Name
 * @version 1.0
 * @since 2024
 */
@Service
public class TaskService {

    private final TaskRepository taskRepository;

    /**
     * Constructs TaskService with required dependencies.
     * 
     * @param taskRepository Repository for task database operations
     */
    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Creates a new task with validation.
     * Sets the status to TODO if not specified.
     * 
     * @param task The task to create
     * @return The saved task with generated ID
     * @throws IllegalArgumentException if task data is invalid
     */
    public Task createTask(Task task) {
        validateTask(task);
        
        // Set default status if not provided
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.TODO);
        }
        
        return taskRepository.save(task);
    }

    /**
     * Retrieves all tasks ordered by urgency.
     * 
     * @return List of all tasks, most urgent first
     */
    public List<Task> getAllTasks() {
        return taskRepository.findAllOrderedByUrgency();
    }

    /**
     * Finds a task by its ID.
     * 
     * @param id The task ID to search for
     * @return Optional containing the task if found
     */
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    /**
     * Updates an existing task.
     * 
     * @param id The ID of the task to update
     * @param updatedTask The updated task data
     * @return The updated task
     * @throws RuntimeException if task not found
     */
    public Task updateTask(Long id, Task updatedTask) {
        Optional<Task> existingTask = taskRepository.findById(id);
        
        if (existingTask.isEmpty()) {
            throw new RuntimeException("Task not found with id: " + id);
        }
        
        validateTask(updatedTask);
        
        Task task = existingTask.get();
        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());
        task.setStatus(updatedTask.getStatus());
        task.setDueDateTime(updatedTask.getDueDateTime());
        
        return taskRepository.save(task);
    }

    /**
     * Deletes a task by ID.
     * 
     * @param id The ID of the task to delete
     * @throws RuntimeException if task not found
     */
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

    /**
     * Finds tasks by status.
     * 
     * @param status The status to filter by
     * @return List of tasks with the specified status
     */
    public List<Task> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    /**
     * Finds overdue tasks.
     * 
     * @return List of tasks past their due date
     */
    public List<Task> getOverdueTasks() {
        return taskRepository.findByDueDateTimeBefore(LocalDateTime.now());
    }

    /**
     * Searches tasks by title.
     * 
     * @param searchTerm The term to search for in task titles
     * @return List of tasks with matching titles
     */
    public List<Task> searchTasks(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllTasks();
        }
        return taskRepository.findByTitleContainingIgnoreCase(searchTerm.trim());
    }

    /**
     * Gets task statistics by status.
     * 
     * @return Array containing counts: [TODO, IN_PROGRESS, COMPLETED]
     */
    public long[] getTaskStatistics() {
        return new long[]{
            taskRepository.countByStatus(TaskStatus.TODO),
            taskRepository.countByStatus(TaskStatus.IN_PROGRESS),
            taskRepository.countByStatus(TaskStatus.COMPLETED)
        };
    }

    /**
     * Validates task data before saving.
     * 
     * @param task The task to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Task title is required");
        }
        
        if (task.getDueDateTime() == null) {
            throw new IllegalArgumentException("Due date is required");
        }
        
        if (task.getDueDateTime().isBefore(LocalDateTime.now().minusDays(1))) {
            throw new IllegalArgumentException("Due date cannot be more than 1 day in the past");
        }
    }
}