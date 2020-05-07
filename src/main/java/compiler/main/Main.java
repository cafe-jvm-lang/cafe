package compiler.main;

/**
 * @author Dhyey
 * @version 1.0
 */
public class Main {

	public enum Result {
		OK(0), // Compilation completed with no errors.
		ERROR(1); // Completed but reported errors.

		Result(int exitCode) {
			this.exitCode = exitCode;
		}

		public boolean isOK() {
			return (exitCode == 0);
		}

		public final int exitCode;
	}

	/**
	 * Entry point into the compiler
	 * @param args expects a file-name.txt file
	 */
	public static void main(String[] args) {
		
	}
}
