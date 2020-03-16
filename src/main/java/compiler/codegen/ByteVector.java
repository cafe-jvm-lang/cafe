package compiler.codegen;

public class ByteVector {
	byte[] data;
	int length;

	public ByteVector() {
		data = new byte[64];
	}
	
	public ByteVector(final int initCapacity) {
		data = new byte[initCapacity];
	}

	ByteVector putByte(final int byteValue) {
		int currLen = length;
		if (currLen + 1 > data.length)
			enlarge(1);
		byte[] currData = data;
		currData[currLen++] = (byte) byteValue;
		length = currLen;
		return this;
	}
	
	/**
	 * Puts short (2 bytes) into this vector
	 * 
	 * @param shortValue
	 * @return this byte vector
	 */
	ByteVector putShort(final int shortValue) {
		int currLen = length;
		if (currLen + 2 > data.length)
			enlarge(2);
		byte[] currData = data;
		// Shift and Mask
		currData[currLen++] = (byte) (shortValue >>> 8);
		currData[currLen++] = (byte) shortValue;
		length = currLen;
		return this;
	}

	/**
	 * Puts int (4 bytes) into this vector.
	 * 
	 * @param intValue
	 * @return this vector
	 */
	ByteVector putInt(final int intValue) {
		int currLen = length;
		if (currLen + 4 > data.length)
			enlarge(4);
		byte[] currData = data;
		currData[currLen++] = (byte) (intValue >>> 24);
		currData[currLen++] = (byte) (intValue >>> 16);
		currData[currLen++] = (byte) (intValue >>> 8);
		currData[currLen++] = (byte) intValue;
		length = currLen;
		return this;
	}

	ByteVector putLong(final long longValue) {
		int currLen = length;
		if (currLen + 8 > data.length)
			enlarge(8);

		byte[] currData = data;

		// long = 64 bits
		// Save higher 32 bits into int and perform shift/mask op.
		// Save lower 32 bits into int and perform shift/mask op.
		int intValue = (int) (longValue >>> 32);
		currData[currLen++] = (byte) (intValue >>> 24);
		currData[currLen++] = (byte) (intValue >>> 16);
		currData[currLen++] = (byte) (intValue >>> 8);
		currData[currLen++] = (byte) intValue;
		intValue = (int) longValue;
		currData[currLen++] = (byte) (intValue >>> 24);
		currData[currLen++] = (byte) (intValue >>> 16);
		currData[currLen++] = (byte) (intValue >>> 8);
		currData[currLen++] = (byte) intValue;
		length = currLen;
		return this;
	}
	
	/**
	 * Insert into this vector:
	 * <ul>
	 * <li>u1</l1>
	 * <li>u2</li>
	 * </ul>
	 * 
	 * @param byteValue
	 * @param shortValue
	 * @return this vector
	 */
	ByteVector put12(final int byteValue, final int shortValue) {
		int currLen = length;
		if (currLen + 3 > data.length)
			enlarge(3);
		byte[] currData = data;
		currData[currLen++] = (byte) byteValue;
		currData[currLen++] = (byte) (shortValue >>> 8);
		currData[currLen++] = (byte) shortValue;
		length = currLen;
		return this;
	}
	
	/**
	 * Insert into this vector:
	 * <ul>
	 * <li>u1</l1>
	 * <li>u2</li>
	 * </ul>
	 * 
	 * @param byteValue
	 * @param shortValue
	 * @return this vector
	 */
	ByteVector put122(final int byteValue, final int shortValue1, final int shortValue2) {
		int currLen = length;
		if (currLen + 5 > data.length)
			enlarge(5);
		byte[] currData = data;
		currData[currLen++] = (byte) byteValue;
		currData[currLen++] = (byte) (shortValue1 >>> 8);
		currData[currLen++] = (byte) shortValue1;
		currData[currLen++] = (byte) (shortValue2 >>> 8);
		currData[currLen++] = (byte) shortValue2;
		length = currLen;
		return this;
	}
	
	/**
	 * puts String into this vector. If encoded UTF-8 len exceeds 65535 (2^16), throws error. 
	 * @param stringValue
	 * @return this vector
	 */
	ByteVector putUTF8(final String stringValue) {
		int charLen = stringValue.length();
		
		// If no. of characters > 65535, than however UTF-8 encoded length, wont fit in 2 bytes.
		if(charLen > 65535) {
			throw new IllegalArgumentException("UTF8 string too large");
		}
		
		byte[] currData = data;
		int currLen = length;
		
		if(currLen + 2 /* length of encoded str*/ + charLen > data.length) {
			enlarge(2 + charLen);
		}
		
		// Since charLen < 65535, only lower 16bits(2 bytes) of charLen contains string length, so shift/mask
		// 2 times and store length in 2 bytes. (following UTF8_info struct of JVM).
		currData[currLen++] = (byte)(charLen >>> 8);
		currData[currLen++] = (byte) charLen;
		for(int i=0;i<charLen;i++) {  // Check here, i++ or ++i ?
			char charValue = stringValue.charAt(i);
			
			// Range taken from spec: 
			// https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html#jvms-4.4.7
			if(charValue >= '\u0001' && charValue <= '\u007F') { // a range till 1 byte only
				// 1 char = 16 bits, but since code-points fall in above range, it consumes only 8bits.
				// so extracting 8bits from char.
				currData[currLen++] = (byte) charValue; 
			}
			else {
				// if code-point range > above range, we need to encode the string, as given in specification.
				
				// skipping for now
			}
		}
		
		length = currLen;
		return this;
	}
	
	/**
	 * Copies given array into this vector.
	 * 
	 * @param byteArray
	 * @param offset
	 * @param byteArrayLength
	 * @return this vector
	 */
	ByteVector putByteArray( final byte[] byteArray, final int offset, final int byteArrayLength) {
		if(length + byteArrayLength > data.length) 
			enlarge(byteArrayLength);
		
		if(byteArray != null)
			System.arraycopy(byteArray, offset, data, length, byteArrayLength);
		
		length += byteArrayLength;
		return this;
	}

	/**
	 * 
	 * @param size ( min no. of bytes to be increased)
	 */
	private void enlarge(final int size) {
		int doubleCapacity = 2 * data.length;
		int minimalCapacity = length + size;

		byte[] newData = new byte[doubleCapacity > minimalCapacity ? doubleCapacity : minimalCapacity];
		System.arraycopy(data, 0, newData, 0, length);
		data = newData;
	}
}
