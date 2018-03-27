package zutil.ml;

import zutil.math.Matrix;

/**
 * Implementation of a Linear Regression algorithm for "predicting"
 * numerical values depending on specific input
 */
public class LinearRegression {


    /**
     * Method for calculating a hypothesis value fr a specific input value x.
     * <br><br>
     * <i>
     *     h(x) = theta0 * x0 + theta1 * x1 + ... + thetan * xn => transpose(theta) * x
     * </i>
     */
    protected static double[] calculateHypotesis(double[][] x, double[] theta){
        return Matrix.multiply(x, theta);
    }

    /**
     * Linear Regresion cost method.
     * <br /><br />
     * <i>
     *      J(O) = 1 / (2 * m) * Σ { ( h(xi) - yi )^2 }
     * </i><br>
     * m = learning data size (rows)
     * @return a number indicating the error rate
     */
    protected static double calculateCost(double[][] x, double[] y, double[] theta){
        return 1 / (2 * x.length) * Matrix.sum(
                Matrix.Elemental.pow(
                        Matrix.subtract(calculateHypotesis(x, theta), y),
                        2));
    }
}
