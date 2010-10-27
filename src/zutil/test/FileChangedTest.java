package zutil.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;

import zutil.io.file.FileChangeListener;
import zutil.io.file.FileUtil;
import zutil.io.file.FileWatcher;

public class FileChangedTest implements FileChangeListener{
	public static void main(String[] args) throws URISyntaxException, FileNotFoundException{
		FileWatcher watcher = new FileWatcher(FileUtil.find("test.txt"));
		watcher.setListener(new FileChangedTest());
		
		while(true){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void fileChangedEvent(File file) {
		System.out.println(file);		
	}
}
