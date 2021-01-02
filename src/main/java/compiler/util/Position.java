package compiler.util;

public class Position {
    private final int startLine;
    private final int startColumn;
    private final int endLine;
    private final int endColumn;

    private Position(int startColumn, int startLine, int endColumn, int endLine) {
        this.startColumn = startColumn;
        this.startLine = startLine;
        this.endColumn = endColumn;
        this.endLine = endLine;
    }

    public static Position of(int column, int line) {
        return new Position(column, line, column, line);
    }

    public static Position of(int startColumn, int startLine, int endColumn, int endLine) {
        return new Position(startColumn, startLine, endColumn, endLine);
    }

    public int getStartColumn() {
        return startColumn;
    }

    public int getStartLine() {
        return startLine;
    }

    public int getEndColumn() {
        return endColumn;
    }

    public int getEndLine() {
        return endLine;
    }
}
