package compiler.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CharReader {

	/**
	 * Input Buffer
	 */
	protected List<Character> buff;

	/**
	 * Input Buffer pointer
	 */
	protected int bp = -1;

	/**
	 * Input Buffer Length
	 */
	private int buffLen;

	/**
	 * Saved buffer
	 */
	protected List<Character> sbuff;

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
//
//	protected CharReader(CharSequence input) {
//		this(input.toString().toCharArray());
//	}

	protected CharReader(List<Character> input) {
		buff = input;
		buffLen = buff.size();
		scanChar();

		sbuff = new ArrayList<>(sbuffCapacity);
	}

	protected void scanChar() {
		if (bp < buffLen-1)
			ch = buff.get(++bp);
		else
			ch = Character.MIN_VALUE;
	}

	protected void putChar(char c) {
		sbuff.add(c);
	}

	/**
	 * @param clearSavedBuffer - if true, clears sbuff
	 * @return saved buffer char array
	 */
	protected List<Character> getSavedBuffer(boolean clearSavedBuffer) {
		if (clearSavedBuffer) {
			clearSavedBufer();
		}
		return sbuff;
	}

	protected String getSavedBufferAsString(boolean clearSavedBuffer) {
		String s = sbuff.stream().map(e->e.toString()).collect(Collectors.joining());
		if (clearSavedBuffer) {
			clearSavedBufer();
		}
		return s;
	}

	private void clearSavedBufer() {
		sbuff = new ArrayList<>(sbuffCapacity);
	}

}
