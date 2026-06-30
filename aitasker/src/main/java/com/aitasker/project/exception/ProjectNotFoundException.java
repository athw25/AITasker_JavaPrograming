package com.aitasker.project.exception;

import com.aitasker.exception.ResourceNotFoundException;

/** Raised when a project cannot be found. */
public class ProjectNotFoundException extends ResourceNotFoundException {
    public ProjectNotFoundException(Long id) {
        super("Project not found with id: " + id);
    }
}
