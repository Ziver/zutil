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
    public void vectorMultiply(){
        assertArrayEquals(
                new double[]{8,14},
                MatrixMath.multiply(new double[][]{{2,3},{-4,9}}, new double[]{1,2}),
                0.0
        );
    }

    @Test
    public void vectorDivision(){
        assertArrayEquals(
                new double[]{4,1},
                MatrixMath.divide(new double[][]{{2,4},{-4,10}}, new double[]{1,2}),
                0.0
        );
    }



    @Test
    public void matrixMultiply(){
        assertArrayEquals(
                new double[][]{{486,410.4,691.6},{314,341.6,416.4},{343.5,353.4,463.6},{173,285.2,190.8}},
                MatrixMath.multiply(
                        new double[][]{{1,2104},{1,1416},{1,1534},{1,852}},
                        new double[][]{{-40,200,-150},{0.25,0.1,0.4}})
        );
    }

    @Test
    public void matrixTranspose(){
        assertArrayEquals(
                new double[][]{{1,3},{2,5},{0,9}},
                MatrixMath.transpose(
                        new double[][]{{1,2,0},{3,5,9}})
        );
    }



    @Test
    public void identity(){
        assertArrayEquals(
                new double[][]{{1}},
                MatrixMath.identity(1));

        assertArrayEquals(
                new double[][]{{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}},
                MatrixMath.identity(4));
    }
}