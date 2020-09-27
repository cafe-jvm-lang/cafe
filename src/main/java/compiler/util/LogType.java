package compiler.util;

public class LogType {

	public enum Type {
		ERROR, WARNING
	}

	public static abstract class Issues<T> {
		Position pos;
		Type type;
		T issue;
		String val;

		public Issues(Position pos, Type type, T issue, String val) {
			this.pos = pos;
			this.type = type;
			this.issue = issue;
			this.val = val;
		}

		@Override
		public String toString() {
			String msg = val+ ": " + issue.toString();
			if(pos == null) {
				return msg;
			}
			return msg + " at " + pos.toString();
		}
	}

	public static final class Error extends Issues<Errors> {
		public Error(Position pos, Errors err, String val) {
			super(pos, Type.ERROR, err, val);
		}
	}

	public static final class Warning extends Issues<Warnings> {

		public Warning(Position pos, Warnings warn, String val)
		{
			super(pos, Type.WARNING, warn,val);
		}
	}
	
	public enum Errors {
		
		NO_FILE_PATH_GIVEN_IN_CLI("No file path provided in arguments"),
		INVALID_CLI_FILE_PATH("File not found or invalid file path"),
		
		SEMICOLON_MISSING("SemiColon ';' missing"),
		
		// Lex error
		ILLEGAL_CHARACTER("Illegal character"),
		INVALID_IDENTIFIER("Invalid identifier"),
		INVALID_FRACTIONAL_VAL("Invalid fractional value"),
		EOF("Unexpected end of file"),
		EOF_PARSING_COMMENT("Reached EOF while parsing comment"),
		
		// Semantic errors
		SYMBOL_NOT_DECLARED("Symbol is not declared"),
		LHS_EXPR_ERROR("Invalid LHS expression"),
		DUPLICATE_SYMBOL("Duplicate Symbol");

		Errors(String desc) {
			this.desc = desc;
		}

		@Override
		public String toString() {
			return desc;
		}

		String desc;
	}

	public enum Warnings {
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
