package uk.gov.hmcts.reform.dev.models;

/**
 * Enumeration defining the possible states of a task in the HMCTS case
 * management system.
 * This enum provides type-safe status values and includes display names
 * for user-friendly presentation in the frontend application.
 */
public enum TaskStatus {

    /**
     * Task has been created but work has not yet started.
     */
    TODO("To Do"),

    /**
     * Task is currently being worked on by a caseworker.
     */
    IN_PROGRESS("In Progress"),

    /**
     * Task has been finished and requires no further action.
     */
    COMPLETED("Completed");

    /**
     * Used in user interfaces, reports, and API responses.
     */
    private final String displayName;

    /**
     * Constructs a TaskStatus enum value with the specified display name.
     * 
     * @param displayName The user-friendly name to display in UI components
     */
    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the user-friendly display name for this status.
     * 
     * @return The formatted display name (e.g., "To Do", "In Progress",
     *         "Completed")
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Converts a string value to the corresponding TaskStatus enum.
     * This method is useful for API endpoints that receive status as strings
     * and need to validate and convert them to enum values.
     * 
     * @param value The string representation of the status
     * @return The corresponding TaskStatus enum value
     * @throws IllegalArgumentException if the value doesn't match any enum constant
     */
    public static TaskStatus fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Status value cannot be null or empty");
        }

        try {
            return TaskStatus.valueOf(value.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid task status: " + value
                    + ". Valid values are: TODO, IN_PROGRESS, COMPLETED");
        }
    }
}