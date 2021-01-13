/*
 * Copyright (c) 2021. Dhyey Shah, Saurabh Pethani, Romil Nisar
 *
 * Developed by:
 *         Dhyey Shah<dhyeyshah4@gmail.com>
 *         https://github.com/dhyey-shah
 *
 * Contributors:
 *         Saurabh Pethani<spethani28@gmail.com>
 *         https://github.com/SaurabhPethani
 *
 *         Romil Nisar<rnisar7@gmail.com>
 *
 *
 * This file is part of Cafe.
 *
 * Cafe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation,  version 3 of the License.
 *
 * Cafe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cafe.  If not, see <https://www.gnu.org/licenses/>.
 */

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
        SUCCESS,
        SOURCE_POSITION,

        // CLI errors
        NO_FILE_PATH_GIVEN_IN_CLI,
        INVALID_CLI_FILE_PATH,
        MODULE_NOT_FOUND,

        // Lex error
        ILLEGAL_CHARACTER,
        INVALID_IDENTIFIER,
        INVALID_FRACTIONAL_VAL,
        EOF,
        EOF_PARSING_COMMENT,

        // Parsing errors
        SYMBOL_EXPECTED,
        INVALID_EXPRESSION,
        INVALID_IMPORT_FILE,

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
