package zutil.test;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import zutil.Hasher;
import zutil.io.file.FileUtil;

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
