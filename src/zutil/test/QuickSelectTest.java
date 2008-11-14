package zutil.test;

import java.util.Arrays;

import zutil.algo.QuickSelect;
import zutil.algo.sort.sortable.SortableIntArray;

public class QuickSelectTest {
	public static void main(String[] args){
		int[] array = {1,3,4,6,3,2,98,5,7,8,543,2,4,5,8,9,5,2,3,5,7,5,3,2,6,8,5,324,8,6};
		//int[] array = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,17,18,19,20};
		
		long time = System.currentTimeMillis();
		int median = (Integer)QuickSelect.find(new SortableIntArray(array), array.length/2);
		System.out.println("QuickSelection("+(System.currentTimeMillis()-time)+"ms): "+median);
		
		time = System.currentTimeMillis();
		Arrays.sort(array);
		System.out.println("RightAnswer("+(System.currentTimeMillis()-time)+"ms): "+array[array.length/2]);
		
		System.out.println("Sorted Array("+array.length+"): ");
		for(int i=0; i<array.length ;i++){
			System.out.println(array[i] +",");
		}
	}
}
