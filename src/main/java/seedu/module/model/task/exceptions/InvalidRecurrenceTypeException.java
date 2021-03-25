package seedu.module.model.task.exceptions;

/**
 * Signals that the recurrenceType is invalid. A recurrence is valid if it's daily, weekly or monthly.
 */
public class InvalidRecurrenceTypeException extends RuntimeException {
    public InvalidRecurrenceTypeException() {
        super("Recurrence type invalid. Task can only recur daily, weekly or monthly");
    }
}
