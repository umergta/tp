package seedu.module.logic.parser;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

import static seedu.module.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.module.logic.parser.CliSyntax.PREFIX_DEADLINE;
import static seedu.module.logic.parser.CliSyntax.PREFIX_DESCRIPTION;
import static seedu.module.logic.parser.CliSyntax.PREFIX_MODULE;
import static seedu.module.logic.parser.CliSyntax.PREFIX_RECURRENCE;
import static seedu.module.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.module.logic.parser.CliSyntax.PREFIX_TASK_NAME;

import seedu.module.commons.core.index.Index;
import seedu.module.commons.exceptions.IllegalValueException;
import seedu.module.logic.commands.Command;
import seedu.module.logic.commands.RecurCommand;
import seedu.module.logic.parser.exceptions.ParseException;
import seedu.module.model.task.Recurrence;
import seedu.module.model.task.exceptions.InvalidRecurrenceTypeException;


public class RecurCommandParser implements Parser<Command> {

    @Override
    public Command parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argumentMultimap = ArgumentTokenizer.tokenize(args, PREFIX_TASK_NAME,
                PREFIX_DEADLINE, PREFIX_MODULE, PREFIX_DESCRIPTION, PREFIX_RECURRENCE, PREFIX_TAG);
        Index index;
        try {
            index = ParserUtil.parseIndex(argumentMultimap.getPreamble());
        } catch (IllegalValueException e) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, RecurCommand.MESSAGE_USAGE), e);
        }

        RecurCommand recurCommand = new RecurCommand(index);
        parseRecurrence(argumentMultimap.getValue(PREFIX_RECURRENCE)).ifPresent(recurCommand::setRecurrence);

        return recurCommand;
    }

    private static Optional<Recurrence> parseRecurrence(Optional<String> recurrenceStr)
            throws InvalidRecurrenceTypeException {
        if (recurrenceStr.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(new Recurrence(recurrenceStr.get()));
        }
    }
}
