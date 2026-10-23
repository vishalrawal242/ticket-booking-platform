// The package line must match the folder the file is in.
package com.vishal.ticketbooking.exception;

// The class name MUST be spelled exactly like the file name:
// ResourceNotFoundException.java -> class ResourceNotFoundException
public class ResourceNotFoundException extends RuntimeException {

    // The constructor receives an error message and passes it up to RuntimeException.
    public ResourceNotFoundException(String message) {
        super(message);
    }
}