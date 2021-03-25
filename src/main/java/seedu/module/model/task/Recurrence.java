package seedu.module.model.task;

import static java.util.Objects.requireNonNull;
import static seedu.module.commons.util.AppUtil.checkArgument;
import static seedu.module.model.task.RecurrenceType.*;

import seedu.module.model.task.exceptions.InvalidRecurrenceTypeException;

public class Recurrence {
    public static final String MESSAGE_CONSTRAINTS = "recurrence can only be daily, weekly or monthly";
    private final RecurrenceType taskRecurrence;
    public final String value;

    /**
     * Constructs a {@code Workload}.
     *
     * @param taskRecurrence A valid type of recurrence for a task.
     */
    public Recurrence(String taskRecurrence) {
        requireNonNull(taskRecurrence);
        checkArgument(isValidRecurrence(taskRecurrence), MESSAGE_CONSTRAINTS);
        this.taskRecurrence = RecurrenceType.valueOf(taskRecurrence);
        this.value = taskRecurrence;
    }

    /**
     * Checks if workload can be converted to int of range 1 to 3 inclusive.
     *
     * @param recurrenceString The recurrence in string form.
     * @return true if above condition is fulfilled, false otherwise.
     * @throws InvalidRecurrenceTypeException if the recurrenceType is not daily, monthly or weekly.
     */
    public static boolean isValidRecurrence(String recurrenceString) {
        requireNonNull(recurrenceString);
        try {
            RecurrenceType taskRecurrence = RecurrenceType.valueOf(recurrenceString);
            boolean isValidRecurrence = false;
            for (RecurrenceType x : RecurrenceType.values()) {
                if (taskRecurrence.equals(x)) {
                    isValidRecurrence = true;
                    break;
                }
            }
            return isValidRecurrence;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public RecurrenceType getRecurrenceType() {
        return this.taskRecurrence;
    }

    public String displayUi() {
        String returnString;
        switch (taskRecurrence) {
        case daily:
            returnString = "daily";
            break;
        case weekly:
            returnString = "weekly";
            break;
        case monthly:
            returnString = "monthly";
            break;
        default:
            System.err.println("error in display UI of recurrence");
            throw new IllegalArgumentException("error in display UI of recurrence");
        }
        return returnString;
    }
    @Override
    public String toString() {
        return this.taskRecurrence.toString();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Recurrence) // instanceof handles nulls
                && taskRecurrence.equals(((Recurrence) other).taskRecurrence); // state check
    }

    @Override
    public int hashCode() {
        return taskRecurrence.hashCode();
    }
}
