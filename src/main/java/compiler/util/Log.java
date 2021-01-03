package compiler.util;

import java.util.LinkedList;
import java.util.List;

public class Log {
    public static final Context.Key<Log> logKey = new Context.Key<>();

    private final List<Issue> issues;

    protected Log(Context context) {
        context.put(logKey, this);

        issues = new LinkedList<>();
    }

    public static Log instance(Context context) {
        Log instance = context.get(logKey);
        if (instance == null)
            instance = new Log(context);
        return instance;
    }

    public int entries() {
        return issues.size();
    }

    public static final class Issue {
        private final Position position;
        private final Type type;
        private final String description;

        private Issue(Type type, Position position, String description) {
            this.type = type;
            this.description = description;
            this.position = position;
        }

        public String getDescription() {
            return description;
        }

        public Position getPosition() {
            return position;
        }

        public Type getType() {
            return type;
        }
    }

    public enum Type {
        ERROR,
        WARNING,
        SOURCE_POSITION,

        // CLI errors
        NO_FILE_PATH_GIVEN_IN_CLI,
        INVALID_CLI_FILE_PATH,

        // Lex error
        ILLEGAL_CHARACTER,
        INVALID_IDENTIFIER,
        INVALID_FRACTIONAL_VAL,
        EOF,
        EOF_PARSING_COMMENT,

        // Parsing errors
        SYMBOL_EXPECTED,
        CONDITION_EXPECTED,

        // Semantic errors
        SYMBOL_NOT_DECLARED,
        LHS_EXPR_ERROR,
        DUPLICATE_SYMBOL,
        REASSIGN_CONSTANT,
        RETURN_OUTSIDE_BLOCK;
    }

    public void report(Type err, Position pos, String description) {
        issues.add(
                new Issue(err, pos, description)
        );
    }

    public void printIssues() {
        for (Issue issue : issues) {
            Messages.error(issue.getDescription());
        }
    }
}
