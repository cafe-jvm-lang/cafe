package compiler.util;

public class Position {
	public final int line;
	public final int start;
	public final int end;

	public Position(int line, int start, int end) {
		this.line = line;
		this.start = start;
		this.end = end;
	}

	@Override
	public String toString() {
		String pos = "line #" + line + " <col:" + start + ">";
		return pos;
	}
}
