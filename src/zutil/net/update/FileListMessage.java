/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Ziver Koc
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

package zutil.net.update;

import zutil.io.file.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to store the files
 * and there hashes
 *
 * @author Ziver
 */
class FileListMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private ArrayList<FileInfo> fileList;
    private long totalSize;

    private FileListMessage() {
    }

    /**
     * Returns a ArrayList of FileInfo object for all the files in the specified folder
     *
     * @param path is the path to scan
     **/
    public FileListMessage(String path) throws IOException {
        fileList = new ArrayList<>();

        List<File> files = FileUtil.search(FileUtil.find(path));
        long totalSize = 0;
        for (File file : files) {
            FileInfo fileInfo = new FileInfo(path, file);
            fileList.add(fileInfo);
            totalSize += fileInfo.getSize();
        }
        this.totalSize = totalSize;
    }


    public long getTotalSize() {
        return totalSize;
    }

    public ArrayList<FileInfo> getFileList() {
        return fileList;
    }

    /**
     * Compares files and returns the ones that differ from this file list
     *
     * @param comp is the file list to compare with
     * @return a list of files that diff
     */
    public FileListMessage getDiff(FileListMessage comp) {
        FileListMessage diff = new FileListMessage();

        long diffSize = 0;
        diff.fileList = new ArrayList<>();
        for (FileInfo file : this.fileList) {
            if (!comp.fileList.contains(file)) {
                diff.fileList.add(file);
                diffSize += file.getSize();
            }
        }
        diff.totalSize = diffSize;

        return diff;
    }


    public boolean equals(Object comp) {
        if (comp instanceof FileListMessage) {
            FileListMessage tmp = (FileListMessage) comp;
            return fileList.equals(tmp.fileList) && totalSize == tmp.totalSize;
        }
        return false;
    }
}
