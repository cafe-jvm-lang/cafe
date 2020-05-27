package compiler.parser;

public class CharReader {

	/**
	 * Input Buffer
	 */
	protected char[] buff;

	/**
	 * Input Buffer pointer
	 */
	private int bp = -1;

	/**
	 * Input Buffer Length
	 */
	private int buffLen;

	/**
	 * Saved buffer
	 */
	protected char[] sbuff;

	/**
	 * Saved Buffer pointer
	 */
	private int sp = -1;

	/**
	 * Saved Buffer max capacity
	 */
	private int sbuffCapacity = 128;

	/**
	 * Current character
	 */
	protected char ch;

	protected CharReader(CharSequence input) {
		this(input.toString().toCharArray());
	}

	protected CharReader(char[] input) {
		buff = input;
		buffLen = buff.length;
		scanChar();

		sbuff = new char[sbuffCapacity];
	}

	protected void scanChar() {
		if (bp < buffLen)
			ch = buff[++bp];
	}

	protected void putChar(char c) {
		if (sp > sbuffCapacity) {
			sbuffCapacity *= 2;
			char[] newSbuff = new char[sbuffCapacity];
			System.arraycopy(sbuff, 0, newSbuff, 0, sbuffCapacity);
			sbuff = newSbuff;
		}
		sbuff[++sp] = c;
	}

	/**
	 * @param clearSavedBuffer - if true, clears sbuff
	 * @return saved buffer char array
	 */
	protected char[] getSavedBuffer(boolean clearSavedBuffer) {
		if (clearSavedBuffer) {
			clearSavedBufer();
		}
		return sbuff;
	}

	protected String getSavedBufferString(boolean clearSavedBuffer) {
		if (clearSavedBuffer) {
			clearSavedBufer();
		}
		return new String(sbuff);
	}

	private void clearSavedBufer() {
		sbuffCapacity = 128;
		sbuff = new char[sbuffCapacity];
		sp = -1;
	}

}
