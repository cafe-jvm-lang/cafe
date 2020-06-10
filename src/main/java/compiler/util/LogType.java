package compiler.util;

public class LogType {

	public static enum Type {
		ERROR, WARNING
	}

	public static abstract class Issues<T> {
		Position pos;
		Type type;
		T issue;

		public Issues(Position pos, Type type, T issue) {
			this.pos = pos;
			this.type = type;
			this.issue = issue;
		}

		@Override
		public String toString() {
			if(pos == null) {
				return issue.toString();
			}
			String error = issue.toString() + " at " + pos.toString();
			return error;
		}
	}

	public static final class Error extends Issues<Errors> {
		public Error(Position pos, Errors err) {
			super(pos, Type.ERROR, err);
		}
	}

	public static final class Warning extends Issues<Warnings> {

		public Warning(Position pos, Warnings warn) {
			super(pos, Type.WARNING, warn);
		}
	}
	
	public static enum Errors {
		
		NO_FILE_PATH_GIVEN_IN_CLI("No file path provided in arguments"),
		INVALID_CLI_FILE_PATH("File not found or invalid file path"),
		
		SEMICOLON_MISSING("SemiColon ';' missing"),
		
		// Lex error
		ILLEGAL_CHARACTER("Illegal character"),
		INVALID_IDENTIFIER("Invalid identifier"),
		INVALID_FRACTIONAL_VAL("Invalid fractional value"),
		EOF_PARSING_COMMENT("Reached EOF while parsing comment");
		
		Errors(String desc) {
			this.desc = desc;
		}

		@Override
		public String toString() {
			return desc;
		}

		String desc;
	}

	public static enum Warnings {
		WARNING_1("Warning desciption");

		Warnings(String desc) {
			this.desc = desc;
		}

		@Override
		public String toString() {
			return desc;
		}

		String desc;
	}
}
