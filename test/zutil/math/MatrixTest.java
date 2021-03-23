/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Ziver Koc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package zutil.math;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class MatrixTest {

    @Test
    public void scalarAdd() {
        assertArrayEquals(new double[][]{{4,5},{-2,11}},
                Matrix.add(new double[][]{{2,3},{-4,9}}, 2));
    }

    @Test
    public void scalarSubtraction() {
        assertArrayEquals(new double[][]{{0,1},{-6,7}},
                Matrix.subtract(new double[][]{{2,3},{-4,9}}, 2));
    }

    @Test
    public void scalarMultiply() {
        assertArrayEquals(new double[][]{{4,6},{-8,18}},
                Matrix.multiply(new double[][]{{2,3},{-4,9}}, 2));
    }

    @Test
    public void scalarDivision() {
        assertArrayEquals(new double[][]{{1,2},{-2,5}},
                Matrix.divide(new double[][]{{2,4},{-4,10}}, 2));
    }



    @Test
    public void elementalAdd() {
        assertArrayEquals(new double[][]{{3,5},{-1,13}},
                Matrix.Elemental.add(new double[][]{{2,3},{-4,9}}, new double[][]{{1,2},{3,4}}));
    }

    @Test
    public void elementalSubtract() {
        assertArrayEquals(new double[][]{{1,1},{-7,5}},
                Matrix.Elemental.subtract(new double[][]{{2,3},{-4,9}}, new double[][]{{1,2},{3,4}}));
    }

    @Test
    public void elementalMultiply() {
        assertArrayEquals(new double[][]{{2,6},{-12,36}},
                Matrix.Elemental.multiply(new double[][]{{2,3},{-4,9}}, new double[][]{{1,2},{3,4}}));
    }

    @Test
    public void elementalVectorPow() {
        assertArrayEquals(
                new double[]{4,9,16,81},
                Matrix.Elemental.pow(new double[]{2,3,-4,9}, 2),
                0.0);
    }

    @Test
    public void elementalMatrixPow() {
        assertArrayEquals(new double[][]{{4,9},{16,81}},
                Matrix.Elemental.pow(new double[][]{{2,3},{-4,9}}, 2));
    }



    @Test
    public void vectorAddition() {
        assertArrayEquals(
                new double[]{3,5,-1,13},
                Matrix.add(new double[]{2,3,-4,9}, new double[]{1,2,3,4}),
                0.0
        );
    }

    @Test
    public void vectorMatrixAddition() {
        assertArrayEquals(
                new double[][]{{2,3,4,5},{2,3,4,5},{2,3,4,5},{2,3,4,5}},
                Matrix.add(new double[][]{{1,2,3,4},{1,2,3,4},{1,2,3,4},{1,2,3,4}}, new double[]{1,1,1,1})
        );
    }

    @Test
    public void vectorSubtraction() {
        assertArrayEquals(
                new double[]{1,1,-7,5},
                Matrix.subtract(new double[]{2,3,-4,9}, new double[]{1,2,3,4}),
                0.0
        );
    }

    @Test
    public void vectorMatrixSubtraction() {
        assertArrayEquals(
                new double[][]{{0,1,2,3},{0,1,2,3},{0,1,2,3},{0,1,2,3}},
                Matrix.subtract(new double[][]{{1,2,3,4},{1,2,3,4},{1,2,3,4},{1,2,3,4}}, new double[]{1,1,1,1})
        );
    }

    @Test
    public void vectorMultiply() {
        assertArrayEquals(
                new double[]{0.1, 0.4, 0.9, 1.6},
                Matrix.Elemental.multiply(
                        new double[]{1, 2, 3, 4},
                        new double[]{0.1, 0.2, 0.3, 0.4}),
                0.001);
    }

    @Test
    public void vectorMatrixMultiply() {
        assertArrayEquals(
                new double[]{1.4, 1.9, 2.4, 2.9},
                Matrix.multiply(
                        new double[][]{{1, 2, 3}, {1, 3, 4}, {1, 4, 5}, {1, 5, 6}},
                        new double[]{0.1, 0.2, 0.3}),
                0.001);
    }

    @Test
    public void vectorMatrixDivision() {
        assertArrayEquals(
                new double[]{4,1},
                Matrix.divide(new double[][]{{2,4},{-4,10}}, new double[]{1,2}),
                0.0
        );
    }

    @Test
    public void vectorMatrixElementalMultiply() {
        assertArrayEquals(
                new double[][]{{1, 4, 9}, {1, 6, 12}, {1, 8, 15}, {1, 10, 18}},
                Matrix.Elemental.multiply(
                        new double[][]{{1, 2, 3}, {1, 3, 4}, {1, 4, 5}, {1, 5, 6}},
                        new double[]{1, 2, 3}));
    }

    @Test
    public void vectorMatrixElementalDivision() {
        assertArrayEquals(
                new double[][]{{2,2},{-4,5}},
                Matrix.Elemental.divide(
                        new double[][]{{2,4},{-4,10}},
                        new double[]{1,2}));
    }

    @Test
    public void vectorSum() {
        assertEquals(
                20.0,
                Matrix.sum(new double[]{1,2,0,3,5,9}),
                0.02
        );
    }



    @Test
    public void matrixMultiply() {
        assertArrayEquals(
                new double[][]{{486,410.4,691.6},{314,341.6,416.4},{343.5,353.4,463.6},{173,285.2,190.8}},
                Matrix.multiply(
                        new double[][]{{1,2104},{1,1416},{1,1534},{1,852}},
                        new double[][]{{-40,200,-150},{0.25,0.1,0.4}})
        );
    }

    @Test
    public void matrixTranspose() {
        assertArrayEquals(
                new double[][]{{1,3},{2,5},{0,9}},
                Matrix.transpose(
                        new double[][]{{1,2,0},{3,5,9}})
        );
    }

    @Test
    public void matrixSum() {
        assertEquals(
                20.0,
                Matrix.sum(new double[][]{{1,2,0},{3,5,9}}),
                0.02
        );
    }



    @Test
    public void identity() {
        assertArrayEquals(
                new double[][]{{1}},
                Matrix.identity(1));

        assertArrayEquals(
                new double[][]{{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}},
                Matrix.identity(4));
    }

    @Test
    public void getColumn() {
        assertArrayEquals(
                new double[]{2,3,4,1},
                Matrix.getColumn(new double[][]{{1,2,3,4},{2,3,4,1},{3,4,1,2},{4,1,2,3}}, 1),
                0.0
        );
    }
}