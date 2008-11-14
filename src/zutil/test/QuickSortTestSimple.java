package zutil.test;
import zutil.algo.sort.QuickSort;
import zutil.algo.sort.sortable.SortableIntArray;
import junit.framework.*;

public class QuickSortTestSimple extends TestCase {

	public static void main(String[] args){
		int[] array = new int[1000];
		
		for(int i=0; i<array.length ;i++){
			array[i] = (int)(Math.random()*10000);
		}
		
		for(int i=0; i<array.length ;i++){
			System.out.println(array[i]);
		}
		
		//quicksort(array, 0, array.length-1);
		
		QuickSort.sort(new SortableIntArray(array));
		
		System.out.println("----------------------------------");
		for(int i=0; i<array.length ;i++){
			System.out.println(array[i]);
		}
		
		System.out.println("----------------------------------");
		for(int i=1; i<array.length ; i++){
			if(array[i-1] > array[i]){
				System.out.println("Array not sorted!! ("+array[i-1]+" > "+array[i]+")");
			}
		}
	}

	static void quicksort (int[] a, int lo, int hi)	{
		//  lo is the lower index, hi is the upper index
		//  of the region of array a that is to be sorted
		    int i=lo, j=hi, h;
		    int x=a[(lo+hi)/2];

		    //  partition
		    do
		    {    
		        while (a[i]<x){
		        	i++; 
		        }
		        while (a[j]>x){
		        	j--;
		        }
		        if (i<=j)
		        {
		            h=a[i]; a[i]=a[j]; a[j]=h;
		            i++; j--;
		        }
		    } while (i<=j);

		    //  recursion
		    if (lo<j) quicksort(a, lo, j);
		    if (i<hi) quicksort(a, i, hi);
		}
	
} 