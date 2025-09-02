package uk.gov.hmcts.reform.dev.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.models.TaskStatus;
import uk.gov.hmcts.reform.dev.services.TaskService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Task management operations.
 * 
 * Provides HTTP endpoints for creating, reading, updating, and deleting tasks.
 * All endpoints return JSON responses and follow RESTful conventions.
 * 
 * Base URL: /api/tasks
 * 
 * @author Your Name
 * @version 1.0
 * @since 2024
 */
@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "http://localhost:3100") // Allow frontend to call this API
public class TaskController {

    private final TaskService taskService;

    /**
     * Constructs TaskController with required dependencies.
     * 
     * @param taskService Service for task business logic
     */
    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Creates a new task.
     * 
     * POST /api/tasks
     * 
     * @param task The task data to create
     * @return ResponseEntity containing the created task
     */
    @PostMapping
    public ResponseEntity<?> createTask(@Valid @RequestBody Task task) {
        try {
            Task createdTask = taskService.createTask(task);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * Retrieves all tasks.
     * 
     * GET /api/tasks
     * 
     * @return ResponseEntity containing list of all tasks
     */
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    /**
     * Retrieves a specific task by ID.
     * 
     * GET /api/tasks/{id}
     * 
     * @param id The task ID
     * @return ResponseEntity containing the task or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        Optional<Task> task = taskService.getTaskById(id);
        if (task.isPresent()) {
            return ResponseEntity.ok(task.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Task not found with id: " + id);
        }
    }

    /**
     * Updates an existing task.
     * 
     * PUT /api/tasks/{id}
     * 
     * @param id The task ID to update
     * @param task The updated task data
     * @return ResponseEntity containing the updated task
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @Valid @RequestBody Task task) {
        try {
            Task updatedTask = taskService.updateTask(id, task);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
            } else {
                return ResponseEntity.badRequest().body("Error: " + e.getMessage());
            }
        }
    }

    /**
     * Deletes a task.
     * 
     * DELETE /api/tasks/{id}
     * 
     * @param id The task ID to delete
     * @return ResponseEntity with appropriate status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.ok("Task deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }

    /**
     * Retrieves tasks filtered by status.
     * 
     * GET /api/tasks/status/{status}
     * 
     * @param status The status to filter by (TODO, IN_PROGRESS, COMPLETED)
     * @return ResponseEntity containing filtered tasks
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getTasksByStatus(@PathVariable String status) {
        try {
            TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
            List<Task> tasks = taskService.getTasksByStatus(taskStatus);
            return ResponseEntity.ok(tasks);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body("Invalid status. Valid values: TODO, IN_PROGRESS, COMPLETED");
        }
    }

    /**
     * Retrieves overdue tasks.
     * 
     * GET /api/tasks/overdue
     * 
     * @return ResponseEntity containing overdue tasks
     */
    @GetMapping("/overdue")
    public ResponseEntity<List<Task>> getOverdueTasks() {
        List<Task> overdueTasks = taskService.getOverdueTasks();
        return ResponseEntity.ok(overdueTasks);
    }

    /**
     * Searches tasks by title.
     * 
     * GET /api/tasks/search?q={searchTerm}
     * 
     * @param searchTerm The term to search for
     * @return ResponseEntity containing matching tasks
     */
    @GetMapping("/search")
    public ResponseEntity<List<Task>> searchTasks(@RequestParam(name = "q") String searchTerm) {
        List<Task> tasks = taskService.searchTasks(searchTerm);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Gets task statistics for dashboard.
     * 
     * GET /api/tasks/stats
     * 
     * @return ResponseEntity containing task counts by status
     */
    @GetMapping("/stats")
    public ResponseEntity<TaskStatsResponse> getTaskStatistics() {
        long[] stats = taskService.getTaskStatistics();
        TaskStatsResponse response = new TaskStatsResponse(stats[0], stats[1], stats[2]);
        return ResponseEntity.ok(response);
    }

    /**
     * Response class for task statistics.
     */
    public static class TaskStatsResponse {
        public final long todoCount;
        public final long inProgressCount;
        public final long completedCount;

        public TaskStatsResponse(long todoCount, long inProgressCount, long completedCount) {
            this.todoCount = todoCount;
            this.inProgressCount = inProgressCount;
            this.completedCount = completedCount;
        }
    }
}