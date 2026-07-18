package io.dev.project_service.exceptions;

/**
 * Thrown when a user attempts to modify a project they don't own.
 */
public class ProjectAccessDeniedException extends RuntimeException {

    public ProjectAccessDeniedException(String message) {
        super(message);
    }
}
