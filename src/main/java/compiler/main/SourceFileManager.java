/*
 * Copyright (c) 2021. Dhyey Shah, Saurabh Pethani, Romil Nisar
 *
 * Developed by:
 *         Dhyey Shah<dhyeyshah4@gmail.com>
 *         https://github.com/dhyey-shah
 *
 * Contributors:
 *         Saurabh Pethani<spethani28@gmail.com>
 *         https://github.com/SaurabhPethani
 *
 *         Romil Nisar<rnisar7@gmail.com>
 *
 *
 * This file is part of Cafe.
 *
 * Cafe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation,  version 3 of the License.
 *
 * Cafe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cafe.  If not, see <https://www.gnu.org/licenses/>.
 */

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
        File file = new File(path);
        if (file.exists() && !file.isDirectory()) {
            this.file = file;
        } else {
            log.report(INVALID_CLI_FILE_PATH, null,
                    message(INVALID_CLI_FILE_PATH, path));
        }
    }

    public void setSourceFile(File file) {
        this.file = file;
    }

    List<Character> asCharList() {
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
