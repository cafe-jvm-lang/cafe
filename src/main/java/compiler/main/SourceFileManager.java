package compiler.main;

import compiler.util.Context;
import compiler.util.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static compiler.util.Log.Type.INVALID_CLI_FILE_PATH;
import static compiler.util.Messages.message;

/**
 * A temporary class to register source file
 * Status: incomplete
 *
 * @author Dhyey
 */
public class SourceFileManager {
    public static final Context.Key<SourceFileManager> fileKey = new Context.Key<>();
    private String fileName;
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

    public void setSourceFile(String path) {
        File f = new File(path);
        if (f.exists() && !f.isDirectory()) {
            this.file = f;
            this.fileName = f.getName();
        } else {
            log.report(INVALID_CLI_FILE_PATH, null,
                    message(INVALID_CLI_FILE_PATH, path));
        }
    }

    File getSourceFile() {
        return file;
    }

    String getSourceFileName() {
        return this.fileName;
    }

    List<Character> getSourceFileCharList() {
        char ch;
        List<Character> list = new ArrayList<>(100);
        BufferedReader br;

        try {
            br = new BufferedReader(new FileReader(file));

            int c = br.read();
            while (c != -1) {
                ch = (char) c;
                list.add(ch);
                c = br.read();
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
