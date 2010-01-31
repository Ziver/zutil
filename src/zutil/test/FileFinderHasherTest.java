package zutil.test;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import zutil.FileFinder;
import zutil.Hasher;

public class FileFinderHasherTest {
	public static void main(String[] args) throws URISyntaxException{
		String relativePath = "zutil/test";
		
		File path = FileFinder.find(relativePath);
		List<File> files = FileFinder.search(path);
		for(int i=0; i<files.size(); i++){
			try {
				System.out.println(
						FileFinder.relativePath(files.get(i), relativePath)+
						": "+Hasher.hash(files.get(i),"MD5"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
