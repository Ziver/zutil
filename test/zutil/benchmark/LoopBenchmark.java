package zutil.benchmark;

import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import org.junit.Rule;
import org.junit.Test;

public class LoopBenchmark {
    public static final int TEST_EXECUTIONS = 500;

    @Rule
    public BenchmarkRule benchmarkRun = new BenchmarkRule();


    private int[] array1 = new int[100_000];
    private int[] array2 = new int[50_000];


    @Test
    public void writeArrayOneLoop() {
        for(int k=0; k<TEST_EXECUTIONS; k++) {
            for (int i = 0; i < Math.max(array1.length, array1.length); i++) {
                if (i < array1.length)
                    array1[i] = i;
                if (i < array2.length)
                    array2[i] = i;
            }
        }
    }

    @Test
    public void writeArraySeparateLoops(){
        for(int k=0; k<TEST_EXECUTIONS; k++) {
            for (int i = 0; i < array1.length; i++) {
                array1[i] = i;
            }
            for (int j = 0; j < array2.length; j++) {
                array2[j] = j;
            }
        }
    }


    @Test
    public void readArrayLoop() {
        int sum = 0;
        for(int k=0; k<TEST_EXECUTIONS; k++) {
            for (int i = 0; i < array1.length; i++) {
                sum += array1[i];
            }
        }
    }

    @Test
    public void readArrayForeach() {
        int sum = 0;
        for(int k=0; k<TEST_EXECUTIONS; k++) {
            for (int i : array1) {
                sum += array1[i];
            }
        }
    }
}