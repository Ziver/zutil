/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Ziver Koc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package zutil;

import zutil.io.file.FileUtil;
import zutil.parser.DataNode;
import zutil.parser.json.JSONParser;

import java.io.*;
import java.util.*;

/**
 * Utility class for MIME type definitions
 */
public class MimeTypeUtil {

    // Static variables

    private static final ArrayList<MimeType> mimes = new ArrayList<MimeType>();
    private static final HashMap<String, MimeType> mimesByExtension = new HashMap<>();

    // Initialize mime types
    static {
        try {
            readMimeFile("zutil/data/mime.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readMimeFile(String path) throws IOException {
        DataNode json = JSONParser.read(FileUtil.getContent(path));

        for (Iterator<String> it = json.keyIterator(); it.hasNext();) {
            String primaryType = it.next();

            for (Iterator<String> it2 = json.get(primaryType).keyIterator(); it2.hasNext();) {
                String subType = it2.next();
                DataNode mimeJson = json.get(primaryType).get(subType);

                addMimeType(new MimeType(
                        primaryType,
                        subType,
                        mimeJson.getString("description"),
                        mimeJson.getList("extensions")));
            }
        }
    }

    private static void addMimeType(MimeType mime) {
        mimes.add(mime);

        for (String extension : mime.getExtensions()) {
            mimesByExtension.put(extension, mime);
        }
    }


    public static MimeType getMimeByExtension(String extension) {
        return mimesByExtension.get(extension);
    }


    public static class MimeType{
        private final String primaryType;
        private final String subType;
        private final String description;
        private final String[] extensions;

        private MimeType(String primary, String subType, String description, List extensions) {
            this.primaryType = primary;
            this.subType = subType;
            this.description = description;
            this.extensions = (String[]) extensions.toArray(new String[0]);
        }

        public String getPrimaryType() {
            return primaryType;
        }

        public String getSubType() {
            return subType;
        }

        public String getDescription() {
            return description;
        }

        public String[] getExtensions() {
            return extensions;
        }

        public String toString() {
            return primaryType + "/" + subType;
        }
    }
}
