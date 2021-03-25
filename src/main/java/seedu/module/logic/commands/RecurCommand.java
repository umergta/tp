package seedu.module.logic.commands;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import seedu.module.commons.core.Messages;
import seedu.module.commons.core.index.Index;
import seedu.module.logic.commands.exceptions.CommandException;
import seedu.module.model.Model;
import seedu.module.model.tag.Tag;
import seedu.module.model.task.*;
import seedu.module.model.task.Module;
import seedu.module.model.task.exceptions.InvalidRecurrenceTypeException;
import static seedu.module.model.Model.PREDICATE_SHOW_ALL_TASKS;
import static seedu.module.commons.util.CollectionUtil.requireAllNonNull;

/**
 * Makes a task repeat at a given interval of
 */
public class RecurCommand extends Command {
    public static final String COMMAND_WORD = "recur";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Make a task recur either daily, weekly or monthly "
            + "to the task identified by the index number used in the last person listing. "
            + "If recurrence specified, it is overwritten by the input.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "r/ [RECURRENCE] (must be daily, weekly or monthly)\n"
            + "Example: " + COMMAND_WORD + " 1 r/ monthly";

    public static final String MESSAGE_ADD_RECURRENCE_SUCCESS = "New recurrence to task added successfully: %1$s";
    public static final String MESSAGE_INVALID_RECURRENCE = "Recurrence can only be daily, weekly or monthly.";
    public static final String MESSAGE_DUPLICATE_RECURRENCE = "This task is already recurring: %1$s";

    private Index index;
    private Recurrence recurrence;

    /**
     * Creates a new RecurCommand object.
     * @param index of the task for which recurrence needs to be added to.
     */
    public RecurCommand(Index index) {
        requireAllNonNull(index);
        this.index = index;
        this.recurrence = null;
    }

    /**
     * Sets the {@rcode recurrence} of a task
     * @param recurrence the specified recurrence
     */
    public void setRecurrence(Recurrence recurrence) {
        this.recurrence = recurrence;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        assert model != null;
        List<Task> lastShownList = model.getFilteredTaskList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX);
        }
        Task taskToAddRecurrenceTo = lastShownList.get(index.getZeroBased());
        //task with recurrence
        Task taskWithRecurrence = createRecurringTask(taskToAddRecurrenceTo);
        //check if the task already has this recurring task
        if (taskToAddRecurrenceTo.equals(taskWithRecurrence) && model.hasTask(taskWithRecurrence)) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_RECURRENCE,
                    taskWithRecurrence.getTaskRecurrence()));
        }
        //logic of adding the recurrence to the task
        //adds all recurring tasks into the module book
        //instead, add to hashmap, recompute moduleBook and show the tasks
        setRecurrencesOfTasks(model, taskWithRecurrence);
        model.updateFilteredTaskList(PREDICATE_SHOW_ALL_TASKS);

        return new CommandResult(String.format(MESSAGE_ADD_RECURRENCE_SUCCESS, taskWithRecurrence));
    }

    private Task createRecurringTask(Task taskToAddRecurrence) {
        assert taskToAddRecurrence != null;

        Name name = taskToAddRecurrence.getName();
        Module module = taskToAddRecurrence.getModule();
        Deadline deadline = taskToAddRecurrence.getDeadline();
        Description description = taskToAddRecurrence.getDescription();
        Workload workload = taskToAddRecurrence.getWorkload();
        DoneStatus doneStatus = taskToAddRecurrence.getDoneStatus();
        Recurrence newRecurrence = this.recurrence;
        Set<Tag> tags = taskToAddRecurrence.getTags();

        Task recurringTask = new Task(name, deadline, module, description, workload, doneStatus, newRecurrence, tags);
        return recurringTask;
    }

    public static void setRecurrencesOfTasks(Model model, Task taskChanged) {
        requireAllNonNull(model, taskChanged);

        List<Task> lastShownList = model.getFilteredTaskList();
        for (Task taskInModuleBook : lastShownList) {
            boolean isSameRecurringTask = taskInModuleBook.equalRecurringTask(taskChanged);
            //change the first instance of the task
            if (isSameRecurringTask) {
                taskChanged.getTaskRecurrence();
                model.setTask(taskInModuleBook, taskChanged);

                List<Task> listOfNewRecurringTasks = listOfNewRecurringTasksForYear(taskChanged);
                model.addTasks(listOfNewRecurringTasks);
                break;
            }
        }
    }

    private static Task makeNextRecurringTask(Task previousRecurringTask) {
        DoneStatus newDoneStatus = new DoneStatus(false);

        Recurrence recurrence = previousRecurringTask.getTaskRecurrence();
        Deadline lastDeadline = previousRecurringTask.getDeadline();
        Deadline nextRecurringDeadline = setNextDeadline(lastDeadline, recurrence);

        Task nextRecurringTask = new Task(previousRecurringTask.getName(),
                nextRecurringDeadline,
                previousRecurringTask.getModule(),
                previousRecurringTask.getDescription(),
                previousRecurringTask.getWorkload(),
                newDoneStatus,
                recurrence,
                previousRecurringTask.getTags());

        return nextRecurringTask;
    }

    private static Deadline setNextDeadline(Deadline previousDeadline, Recurrence recurrence) {
        Deadline nextRecurringDeadline = previousDeadline;
        switch (recurrence.getRecurrenceType()) {
        case daily:
            //change date to day + 1
            nextRecurringDeadline = previousDeadline.setNewDate(previousDeadline.getTime().plusDays(1));
            break;
        case weekly:
            //change date to day + 7
            nextRecurringDeadline = previousDeadline.setNewDate(previousDeadline.getTime().plusDays(7));
            break;
        case monthly:
            //change date to month + 1
            nextRecurringDeadline = previousDeadline.setNewDate(previousDeadline.getTime().plusMonths(1));
            break;
        default:
            throw new InvalidRecurrenceTypeException();
        }
        return nextRecurringDeadline;
    }

    private static List<Task> listOfNewRecurringTasksForYear(Task recurringTask) {
        LocalDateTime endOfYear = recurringTask.getDeadline().getTime().plusYears(1);
        List<Task> newRecurringTasks = new ArrayList<>();
        Task nextTask = makeNextRecurringTask(recurringTask);

        LocalDateTime nextDeadlineDateTime = nextTask.getDeadline().time;

        while (endOfYear.isAfter(nextDeadlineDateTime)) {
            newRecurringTasks.add(nextTask);
            nextTask = makeNextRecurringTask(nextTask);

            nextDeadlineDateTime = nextTask.getDeadline().time;
        }
        return newRecurringTasks;
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof RecurCommand)) {
            return false;
        }

        // state check
        RecurCommand e = (RecurCommand) other;
        return index.equals(e.index)
                && recurrence.equals(e.recurrence);
    }
}
