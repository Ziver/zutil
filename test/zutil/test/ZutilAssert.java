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

package zutil.test;

import org.junit.Assert;
import org.junit.internal.ArrayComparisonFailure;
import org.junit.internal.InexactComparisonCriteria;

/**
 * Some additional assert functions that are missing from JUnit
 */
public class ZutilAssert extends Assert {

    private ZutilAssert() {}

    /**
     * Asserts that two short arrays are equal. If they are not, an
     * {@link AssertionError} is thrown.
     *
     * @param   expected   double array with expected values.
     * @param   actual     double array with actual values
     */
    public static void assertArrayEquals(double[][] expected, double[][] actual, double delta) {
        ZutilAssert.assertArrayEquals(null, expected, actual, delta);
    }

    /**
     * Asserts that two int arrays are equal. If they are not, an
     * {@link AssertionError} is thrown with the given message.
     *
     * @param   message   the identifying message for the {@link AssertionError} (<code>null</code>
     * okay)
     * @param   expected  double array with expected values.
     * @param   actual    double array with actual values
     */
    public static void assertArrayEquals(String message, double[][] expected,
                                         double[][] actual, double delta) throws ArrayComparisonFailure {
        // If both arrays are referencing the same object or null
        if (expected == actual)
            return;

        // Check array lengths
        if (expected.length != actual.length)
            fail(message + ". The array lengths of the first dimensions do not match.");

        // Check all sub arrays
        new InexactComparisonCriteria(delta).arrayEquals(message, expected, actual);
    }
}
