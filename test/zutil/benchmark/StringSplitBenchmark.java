package zutil.benchmark;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

/**
 * Created by Ziver on 2016-07-07.
 */
public class StringSplitBenchmark {
    public static final int TEST_EXECUTIONS = 200000;

    @Rule
    public BenchmarkRule benchmarkRun = new BenchmarkRule();

    private static String delimiter = ",";
    private static String str = "aaa,aaaaaa,aaaaaa,aa,a,a,a,bb,bbb,aaaaaaaaa,a,a,aaa";


    @Test
    public void stringSplit(){
        for(int i=0; i<TEST_EXECUTIONS; i++)
            assertSplit(str.split(delimiter));
    }

    public static Pattern pattern = Pattern.compile(delimiter);
    @Test
    public void patternSplit(){
        for(int i=0; i<TEST_EXECUTIONS; i++)
            assertSplit(pattern.split(str));
    }

    @Test
    public void substring(){
        for(int i=0; i<TEST_EXECUTIONS; i++) {
            ArrayList<String> splitList = new ArrayList<>();
            int from = 0, to = 0;
            while (to >= 0) {
                to = str.indexOf(delimiter, from + 1);
                if (to < 0)
                    splitList.add(str.substring(from));
                else
                    splitList.add(str.substring(from, to));
                from = to;
            }
            assertSplit(splitList.toArray(new String[splitList.size()]));
        }
    }

    public void assertSplit(String[] str){
        assertEquals(13,str.length);
    }
}
