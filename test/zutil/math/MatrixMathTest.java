package zutil.math;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class MatrixMathTest {

    @Test
    public void scalarAdd(){
        assertArrayEquals(new double[][]{{4,5},{-2,11}},
                MatrixMath.add(new double[][]{{2,3},{-4,9}}, 2));
    }

    @Test
    public void scalarSubtraction(){
        assertArrayEquals(new double[][]{{0,1},{-6,7}},
                MatrixMath.subtract(new double[][]{{2,3},{-4,9}}, 2));
    }

    @Test
    public void scalarMultiply(){
        assertArrayEquals(new double[][]{{4,6},{-8,18}},
                MatrixMath.multiply(new double[][]{{2,3},{-4,9}}, 2));
    }

    @Test
    public void scalarDivision(){
        assertArrayEquals(new double[][]{{1,2},{-2,5}},
                MatrixMath.divide(new double[][]{{2,4},{-4,10}}, 2));
    }



    @Test
    public void elementalAdd(){
        assertArrayEquals(new double[][]{{3,5},{-1,13}},
                MatrixMath.add(new double[][]{{2,3},{-4,9}}, new double[][]{{1,2},{3,4}}));
    }

    @Test
    public void elementalSubtract(){
        assertArrayEquals(new double[][]{{1,1},{-7,5}},
                MatrixMath.subtract(new double[][]{{2,3},{-4,9}}, new double[][]{{1,2},{3,4}}));
    }

    @Test
    public void elementalMultiply(){
        assertArrayEquals(new double[][]{{2,6},{-12,36}},
                MatrixMath.multiply(new double[][]{{2,3},{-4,9}}, new double[][]{{1,2},{3,4}}));
    }

    @Test
    public void elementalDivision(){
        assertArrayEquals(new double[][]{{2,2},{-3,3}},
                MatrixMath.divide(new double[][]{{2,4},{-9,12}}, new double[][]{{1,2},{3,4}}));
    }
}