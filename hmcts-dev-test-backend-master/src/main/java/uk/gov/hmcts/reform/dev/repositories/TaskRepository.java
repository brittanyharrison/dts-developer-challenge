package uk.gov.hmcts.reform.dev.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.models.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Task entity database operations.
 * 
 * Provides CRUD operations and custom queries for managing tasks
 * in the HMCTS case management system. Spring Data JPA automatically
 * implements basic operations like save, findById, findAll, delete.
 * 
 * @author Your Name
 * @version 1.0
 * @since 2024
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Finds all tasks with the specified status.
     * Useful for filtering tasks by their current state.
     * 
     * @param status The status to filter by (TODO, IN_PROGRESS, COMPLETED)
     * @return List of tasks matching the status
     */
    List<Task> findByStatus(TaskStatus status);

    /**
     * Finds tasks that are overdue (due date has passed).
     * Important for identifying tasks that need urgent attention.
     * 
     * @param currentTime The current timestamp to compare against
     * @return List of overdue tasks
     */
    List<Task> findByDueDateTimeBefore(LocalDateTime currentTime);

    /**
     * Finds tasks due within a specified time range.
     * Useful for showing upcoming deadlines.
     * 
     * @param startTime Start of the time range
     * @param endTime End of the time range
     * @return List of tasks due within the range
     */
    List<Task> findByDueDateTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Finds tasks by title containing the search term (case-insensitive).
     * Enables searching functionality in the user interface.
     * 
     * @param title The search term to look for in task titles
     * @return List of tasks with matching titles
     */
    List<Task> findByTitleContainingIgnoreCase(String title);

    /**
     * Custom query to find tasks ordered by due date and status priority.
     * Shows most urgent tasks first (overdue, then by due date, prioritizing incomplete tasks).
     * 
     * @return List of tasks ordered by urgency
     */
    @Query("SELECT t FROM Task t ORDER BY " +
           "CASE WHEN t.status = 'COMPLETED' THEN 1 ELSE 0 END, " +
           "t.dueDateTime ASC")
    List<Task> findAllOrderedByUrgency();

    /**
     * Counts tasks by status for dashboard statistics.
     * Provides quick overview of task distribution.
     * 
     * @param status The status to count
     * @return Number of tasks with the specified status
     */
    long countByStatus(TaskStatus status);
}