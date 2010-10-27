package zutil.test;
import zutil.algo.sort.MergeSort;
import zutil.algo.sort.QuickSort;
import zutil.algo.sort.SimpleSort;
import zutil.algo.sort.sortable.SortableIntArray;

@SuppressWarnings("unused")
public class SortTestSimple {
	public static final int SIZE = 10000;
	public static final int MAX_INT = 10000;

	public static void main(String[] args){
		int[] array = new int[SIZE];
		
		for(int i=0; i<array.length ;i++){
			array[i] = (int)(Math.random()*MAX_INT);
		}
		
		for(int i=0; i<array.length ;i++){
			System.out.print(array[i]+", ");
		}
		
		long time = System.currentTimeMillis();
		//SimpleSort.bubbleSort( new SortableIntArray(array) );
		//SimpleSort.selectionSort( new SortableIntArray(array) );
		//SimpleSort.insertionSort( new SortableIntArray(array) );
		//QuickSort.sort( new SortableIntArray(array) );
		//MergeSort.sort( array );
		MergeSort.sort( new SortableIntArray(array) );
		time = System.currentTimeMillis() - time;
		
		System.out.println("\n--------------------------------------------");
		System.out.print(array[0]+", ");
		int error = -1;
		for(int i=1; i<array.length ;i++){
			System.out.print(array[i]+", ");
			if(array[i-1] > array[i]){
				error = i;
			}
		}
		
		if(error >= 0){
			System.out.println("\nArray not sorted!! ("+array[error-1]+" > "+array[error]+")");
		}
		System.out.println("\nTime: "+time+" ms");		
	}
} 