package compiler.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import compiler.util.Context;
import compiler.util.Log;
import compiler.util.LogType.Errors;
import compiler.util.Position;

/**
 * A temporary class to register source file
 * Status: incomplete
 * @author Dhyey
 *
 */
public class SourceFileManager {
	public static final Context.Key<SourceFileManager> fileKey = new Context.Key<>();

	private File file;
	private Log log;

	public static SourceFileManager instance(Context context) {
		SourceFileManager instance = context.get(fileKey);
		if (instance == null) {
			instance = new SourceFileManager(context);
		}
		return instance;
	}

	private SourceFileManager(Context context) {
		context.put(fileKey, this);
		log = Log.instance(context);
	}

	void addSourceFile(String path) {
		File f = new File(path);
		if (f.exists() && !f.isDirectory()) {
			this.file = f;
		} else {
			log.error(new Position(0, 1, 1), Errors.INVALID_CLI_FILE_PATH);
		}
	}

	File getSourceFile() {
		return file;
	}

	List<Character> getSourceFileCharList() {
		char ch;
		List<Character> list = new ArrayList<>(100);
		BufferedReader br;

		try {
			br = new BufferedReader(new FileReader(file));

			ch = (char) br.read();
			while (ch != -1) {
				list.add(ch);
				ch = (char) br.read();
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return list;
	}
}
