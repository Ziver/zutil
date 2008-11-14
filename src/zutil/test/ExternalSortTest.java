package zutil.test;

import java.io.File;

import zutil.algo.sort.ExternalSort;


public class ExternalSortTest {
	public static void main(String[] args){		
		try {
			File file = new File("C:\\Users\\Ziver\\Desktop\\IndexFile.txt");
			File sortedFile = new File("C:\\Users\\Ziver\\Desktop\\SortedIndexFile.txt");

			ExternalSort sort = new ExternalSort(file, sortedFile);
			sort.sort();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
