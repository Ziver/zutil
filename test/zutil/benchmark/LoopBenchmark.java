package zutil.benchmark;

import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import org.junit.Rule;
import org.junit.Test;
import zutil.StringUtil;

public class LoopBenchmark {
    public static final int TEST_EXECUTIONS = 500;

    @Rule
    public BenchmarkRule benchmarkRun = new BenchmarkRule();


    private int[] matrix  = new int[100_000];
    private int[] matrix2 = new int[50_000];


    @Test
    public void oneLoop() {
        for(int k=0; k<TEST_EXECUTIONS; k++) {
            for (int i = 0; i < Math.max(matrix.length, matrix.length); i++) {
                if (i < matrix.length)
                    matrix[i] = i;
                if (i < matrix2.length)
                    matrix2[i] = i;
            }
        }
    }

    @Test
    public void twoLoops(){
        for(int k=0; k<TEST_EXECUTIONS; k++) {
            for (int i = 0; i < matrix.length; i++) {
                matrix[i] = i;
            }
            for (int j = 0; j < matrix2.length; j++) {
                matrix2[j] = j;
            }
        }
    }
}