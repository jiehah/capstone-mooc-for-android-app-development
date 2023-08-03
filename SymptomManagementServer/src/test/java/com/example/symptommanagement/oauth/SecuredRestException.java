package com.example.symptommanagement.oauth;

/**
 * A custom exception class that represents exceptions thrown by the SecuredRestBuilder.
 * This class provides specific information about the encountered exceptions during OAuth 2.0 authentication.
 * <p>
 * A more robust implementation would probably have additional fields for tracking
 * the type of exception (e.g., bad password, invalid credentials, etc.).
 *
 * @author Jules White
 */
public class SecuredRestException extends RuntimeException {

    /**
     * Constructs a new {@code SecuredRestException} without specifying any additional information.
     */
    public SecuredRestException() {
        super();
    }

    /**
     * Constructs a new {@code SecuredRestException} with the specified detail message and cause.
     *
     * @param message The detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     * @param cause   The cause (which is saved for later retrieval by the {@link #getCause()} method).
     */
    public SecuredRestException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new {@code SecuredRestException} with the specified detail message.
     *
     * @param message The detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     */
    public SecuredRestException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code SecuredRestException} with the specified cause and a detail message
     * of (cause==null ? null : cause.toString()) (which typically contains the class and detail message of cause).
     *
     * @param cause The cause (which is saved for later retrieval by the {@link #getCause()} method).
     */
    public SecuredRestException(Throwable cause) {
        super(cause);
    }
}
