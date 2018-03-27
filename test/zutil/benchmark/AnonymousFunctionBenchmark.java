package zutil.benchmark;

import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import org.junit.Rule;
import org.junit.Test;

public class AnonymousFunctionBenchmark {
    public static final int TEST_EXECUTIONS = 500;

    @Rule
    public BenchmarkRule benchmarkRun = new BenchmarkRule();


    private int[] array  = new int[100_000];


    @Test
    public void functionLoop() {
        for(int k=0; k<TEST_EXECUTIONS; k++) {
            for (int i = 0; i < array.length; i++) {
                array[i] = new CalcFunc(){
                    public int calc(int i){
                        return i+1;
                    }
                }.calc(i);
            }
        }
    }

    @Test
    public void preFunctionLoop() {
        CalcFunc func = new CalcFunc(){
            public int calc(int i){
                return i+1;
            }
        };

        for(int k=0; k<TEST_EXECUTIONS; k++) {
            for (int i = 0; i < array.length; i++) {
                array[i] = func.calc(i);
            }
        }
    }

    @Test
    public void rawLoops(){
        for(int k=0; k<TEST_EXECUTIONS; k++) {
            for (int i = 0; i < array.length; i++) {
                array[i] = i;
            }
        }
    }

    private interface CalcFunc{
        int calc(int i);
    }
}