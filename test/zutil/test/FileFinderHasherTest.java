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

package zutil.test;

import zutil.Hasher;
import zutil.io.file.FileUtil;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

public class FileFinderHasherTest {
	public static void main(String[] args) throws URISyntaxException{
		String relativePath = "zutil/test";
		
		File path = FileUtil.find(relativePath);
		List<File> files = FileUtil.search(path);
		for(int i=0; i<files.size(); i++){
			try {
				System.out.println(
						FileUtil.relativePath(files.get(i), relativePath)+
						": "+Hasher.hash(files.get(i),"MD5"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
