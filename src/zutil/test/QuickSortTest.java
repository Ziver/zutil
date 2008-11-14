package zutil.test;
import zutil.algo.sort.QuickSort;
import zutil.algo.sort.sortable.SortableIntArray;
import junit.framework.*;

public class QuickSortTest extends TestCase {
	public static boolean debug = false;
	//the size of the arrays to be tested
	private static final int[] cases = new int[]{10000,100000,1000000,10000000};
	//the type of array to be tested
	// 0 = random
	// 1 = mirror
	// 2 = sorted
	private static final int[] types = new int[]{0,1,2};
	//the strings for the diffrent arrays
	private static final String[] typesS = new String[]{"Random array","Mirrored array","Sorted array"};
	//the pivots that will be tested
	// 0 = random
	// 1 = median
	// 2 = middle
	private static final int[] pivots = new int[]{0,1,2};
	//the strings for the pivots
	private static final String[] pivotsS = new String[]{"Random pivot","Median pivot","Half pivot"};
	//the current array size index of cases
	private static int currentCase = 0;
	//the current type of arrays 
	private static int typeCase = 0;
	//the current pivot to use
	private static int pivotCase = 0;
	//the current state of using insertionsort in quicksort
	private static boolean insertSort;
	//the temp array that will be sorted
	private int[] array;
	
	
	/**
	 *The main method to run the test. was going to use junit but did
	 *no find a way to loop the test like in this method.
	 */
	public static void main(String[] args){
		QuickSortTest test = new QuickSortTest("Test");
		insertSort = true;
		//the insertion sort tests loop
		for(int z=0; z<2 ; insertSort=false,z++){
			System.out.println("****************  Whit insertionSort: "+insertSort+"  *****************");
			// the pivots tests loop
			for(pivotCase=0; pivotCase<pivots.length ;pivotCase++){
				System.out.println("**********  "+pivotsS[pivots[pivotCase]]+"  ***********");
				//the array size tests loop
				for(typeCase=0; typeCase<types.length ;typeCase++){
					currentCase = 0;
					System.out.println("****  "+typesS[types[typeCase]]+"  ****");
					//the array size loop
					for(currentCase=0; currentCase<cases.length ;currentCase++){
						try{
							test.setUp();
							test.TestSort();
							test.tearDown();
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	public QuickSortTest(String name){
		super(name);		
	}

    /**
     * setUp() method that initializes common objects
     */
	public void setUp() throws Exception{
		super.setUp();
		array = new int[cases[currentCase]];

		switch(types[typeCase]){
			//Randome Array test
			case 0:
				for(int i=0; i<array.length ;i++){
					array[i] = (int)(Math.random()*array.length*10);
				}
				break;
			//Mirrored Array test
			case 1:
				for(int i=0; i<array.length ;i++){
					array[i] = array.length-i;			
				}
				break;
			//Sorted Array test
			case 2:
				for(int i=0; i<array.length ;i++){
					array[i] = i;			
				}
				break;					
		}
	}
	
    /**
     * tearDown() method that cleanup the common objects
     */
    public void tearDown() throws Exception {
        super.tearDown();
    }
	
	/**
	 *Tests if the array is sorted 
	 */
	public void TestSort() {
		long time = System.currentTimeMillis();
		if(debug){
			for(int i=0; i<array.length ;i++)
				System.out.print(array[i]+", ");
			System.out.println("");
		}
		QuickSort.sort(new SortableIntArray(array),pivotCase,insertSort);
		//Sort.insertionSort(array);
		System.out.print("*");
		//the time to sort
		System.out.println(array.length+" elements: "+
				((double)(System.currentTimeMillis()-time)/1000)+" sec("+
				(System.currentTimeMillis()-time)+" ms)");
		if(debug){
			for(int i=0; i<array.length ;i++)
				System.out.print(array[i]+", ");
			System.out.println("");
		}
		//checking if sorted correct
		for(int i=1; i<array.length ; i++){
			if(array[i-1] > array[i]){
				fail("Array not sorted!! ("+array[i-1]+" > "+array[i]+")");
			}
		}
	}	
} 