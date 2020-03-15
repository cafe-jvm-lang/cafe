package compiler.codegen;

public class ConstantPoolTable {
	private ByteVector constantPool;
	private int constantPoolCount = 1;

	/**
	 * A custom hash table with seperate chaining to avoid repetitive values in
	 * constant pool.
	 */
	private Entry[] entries;
	private int entryCount = 0;

	public ConstantPoolTable(ClassWriter classWriter) {
		constantPool = new ByteVector();
		entries = new Entry[256];
	}

	int getConstantPoolLength() {
		return -1;
	}

	int addConstantUtf8(final String value) {
		int hashCode = hash(Constant.CONSTANT_UTF8_TAG, value);
		Entry entry = get(hashCode);
		while (entry != null) {
			if (entry.tag == Constant.CONSTANT_UTF8_TAG && entry.value.equals(value) && entry.hashCode == hashCode)
				return entry.index;
			entry = entry.next;
		}

		constantPool.putByte(Constant.CONSTANT_UTF8_TAG).putUTF8(value);
		return -1;
	}

	Entry put(final Entry entry) {
		// Check if total keys > load factor
		if (entryCount > (entries.length * 3) / 4) {
			// Increase the size of hash table
			int currCap = entries.length;
			int newCap = currCap * 2 + 1;
			Entry[] newEntries = new Entry[newCap];
			// Copy all keys to newEntry
			for (int i = currCap - 1; i >= 0; --i) { // Check here, --i or i-- ?
				Entry currEntry = entries[i];
				while (currEntry != null) {
					int newCurrEntryInd = currEntry.hashCode % newCap;
					// Doubts here
					Entry nextEntry = currEntry.next;
					currEntry.next = newEntries[newCurrEntryInd];
					newEntries[newCurrEntryInd] = currEntry;
					currEntry = nextEntry;
				}
			}
			entries = newEntries;
		}

		entryCount++;
		int index = entry.hashCode % entries.length;
		entry.next = entries[index];
		return entries[index] = entry;
	}

	Entry get(final int hashCode) {
		return entries[hashCode % entries.length];
	}

	private static int hash(final int tag, final int value) {
		// 0x7FFFFFFF always gives a positive int value when '&' with int.
		return 0x7FFFFFFF & (tag + value);
	}

	private static int hash(final int tag, final String value) {
		// 0x7FFFFFFF always gives a positive int value when '&' with int.
		return 0x7FFFFFFF & (tag + value.hashCode());
	}
}
