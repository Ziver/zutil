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
