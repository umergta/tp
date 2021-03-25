package seedu.module.logic.parser;

import static seedu.module.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.module.logic.parser.CliSyntax.*;

import java.util.Set;
import java.util.stream.Stream;

import seedu.module.logic.commands.AddCommand;
import seedu.module.logic.parser.exceptions.ParseException;
import seedu.module.model.tag.Tag;
import seedu.module.model.task.*;
import seedu.module.model.task.Module;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_TASK_NAME, PREFIX_MODULE,
                    PREFIX_DESCRIPTION, PREFIX_DEADLINE, PREFIX_WORKLOAD, PREFIX_RECURRENCE, PREFIX_TAG);

        if (!arePrefixesPresent(argMultimap, PREFIX_TASK_NAME, PREFIX_MODULE, PREFIX_DESCRIPTION,
                PREFIX_DEADLINE, PREFIX_WORKLOAD)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_TASK_NAME).get());
        Module module = ParserUtil.parseModule(argMultimap.getValue(PREFIX_MODULE).get());
        Description description = ParserUtil.parseDescription(argMultimap.getValue(PREFIX_DESCRIPTION).get());
        Deadline deadline = ParserUtil.parseDeadline(argMultimap.getValue(PREFIX_DEADLINE).get());
        Workload workload = ParserUtil.parseWorkload(argMultimap.getValue(PREFIX_WORKLOAD).get());
        DoneStatus newTaskDoneStatus = new DoneStatus(false);
        Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));
        Task task;
        if (argMultimap.getValue(PREFIX_RECURRENCE).isEmpty()) {
            task = new Task(name, deadline, module, description, workload, newTaskDoneStatus, tagList);
        } else {
            Recurrence recurrence = ParserUtil.parseRecurrence(argMultimap.getValue(PREFIX_RECURRENCE).get());
            task = new Task(name, deadline, module, description, workload, newTaskDoneStatus, recurrence, tagList);
        }

        return new AddCommand(task);
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}
