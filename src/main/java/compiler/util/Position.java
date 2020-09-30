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

	public Position(int line, int start) {
		this.start = start;
		this.line = line;
		this.end = -1;
	}

	public Position(int start) {
		this.start = start;
		this.line = -1;
		this.end = -1;
	}

	@Override
	public String toString() {
		String pos = "line #" + line + " <col:" + start + ">";
		if (line == -1 && end == -1) {
			pos = "Position: " + start + " ";
		}
		return pos;
	}
}
