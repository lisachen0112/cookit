package dev.lschen.cookit.exception;

public class OperationNotPermittedException extends RuntimeException {
    public OperationNotPermittedException(String message) {
            super(message);
        }
}
