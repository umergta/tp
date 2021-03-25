package seedu.module.model.task;

import static seedu.module.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import seedu.module.model.tag.Tag;
import seedu.module.model.task.exceptions.InvalidRecurrenceTypeException;

/**
 * Represents a Task in the module book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Task {

    // Identity fields
    private final Name name;
    private final Deadline deadline;
    private final Module module;
    private boolean isRecurringTask;
    //stores the recurrence

    // Data fields
    private final Description description;
    private final Workload workload;
    private final DoneStatus doneStatus;
    private Recurrence taskRecurrence;
    private final Set<Tag> tags = new HashSet<>();

    /**
     * Every field must be present and not null.
     */
    public Task(Name name, Deadline deadline, Module module, Description description,
                Workload workload, DoneStatus doneStatus, Recurrence taskRecurrence, Set<Tag> tags) {
        requireAllNonNull(name, deadline, module, description, workload, taskRecurrence, doneStatus, tags);
        this.name = name;
        this.deadline = deadline;
        this.module = module;
        this.description = description;
        this.workload = workload;
        this.doneStatus = doneStatus;
        this.taskRecurrence = taskRecurrence;
        this.isRecurringTask = true;
        this.tags.addAll(tags);
    }

    /**
     * Overloaded constructor for non-recurring tasks.
     * All fields must be present and not null
     */
    public Task(Name name, Deadline deadline, Module module, Description description, Workload workload,
                DoneStatus doneStatus, Set<Tag> tags) {
        requireAllNonNull(name, deadline, module, description, workload, doneStatus, tags);
        this.name = name;
        this.deadline = deadline;
        this.module = module;
        this.description = description;
        this.workload = workload;
        this.doneStatus = doneStatus;
        this.isRecurringTask = false;
        this.tags.addAll(tags);
    }

    public Name getName() {
        return name;
    }

    public Deadline getDeadline() {
        return deadline;
    }

    public Module getModule() {
        return module;
    }

    public Description getDescription() {
        return description;
    }

    public Workload getWorkload() {
        return workload;
    }

    public DoneStatus getDoneStatus() {
        return doneStatus;
    }

    public Recurrence getTaskRecurrence() {
        return taskRecurrence;
    }

    public boolean isRecurringTask() {
        return isRecurringTask;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns true if both tasks have the same name and same module code.
     * This defines a weaker notion of equality between two tasks.
     */
    public boolean isSameTask(Task otherTask) {
        if (otherTask == this) {
            return true;
        }

        return otherTask != null
                && otherTask.getName().equals(getName())
                && otherTask.getModule().equals(getModule());
    }

    /**
     * Returns true if both tasks have the same identity and data fields.
     * This defines a stronger notion of equality between two tasks.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Task)) {
            return false;
        }

        Task otherTask = (Task) other;
        return otherTask.getName().equals(getName())
                && otherTask.getDeadline().equals(getDeadline())
                && otherTask.getModule().equals(getModule())
                && otherTask.getDescription().equals(getDescription())
                && otherTask.getWorkload().equals(getWorkload())
                && otherTask.getDoneStatus().equals(getDoneStatus())
                && otherTask.getTaskRecurrence().equals(getTaskRecurrence())
                && otherTask.getTags().equals(getTags());
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, deadline, module, description, workload, doneStatus, taskRecurrence, tags);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Name: ")
                .append(getName())
                .append("; Deadline: ")
                .append(getDeadline())
                .append("; Module: ")
                .append(getModule())
                .append("; Description: ")
                .append(getDescription())
                .append("; Workload: ")
                .append(getWorkload())
                .append("; Completion Status: ")
                .append(getDoneStatus());

        if (isRecurringTask()) {
            builder.append("; Recurrence: ").append(getTaskRecurrence());
        }

        Set<Tag> tags = getTags();
        if (!tags.isEmpty()) {
            builder.append("; Tags: ");
            tags.forEach(builder::append);
        }
        return builder.toString();
    }

    /**
     * Comparator of tasks using deadline as reference
     */
    public static class DeadlineComparator implements Comparator<Task> {

        public int compare(Task t1, Task t2) {
            return t1.getDeadline().compareTo(t2.getDeadline());
        }

    }

    /**
     * Returns true if recurringTasks are the same.
     * Recurring tasks should have same name, description, workload, tags
     * This defines a weaker notion of equality between two equal tasks but more than tasks return true for isSameTask
     */
    public boolean equalRecurringTask(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Task)) {
            return false;
        }

        Task otherTask = (Task) other;
        return otherTask.getName().equals(getName())
                && otherTask.getModule().equals(getModule())
                && otherTask.getDescription().equals(getDescription())
                && otherTask.getWorkload().equals(getWorkload())
                && otherTask.getTags().equals(getTags());
    }

}
