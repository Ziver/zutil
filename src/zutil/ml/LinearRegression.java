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

package zutil.ml;

import zutil.log.LogUtil;
import zutil.math.Matrix;

import java.util.logging.Logger;

/**
 * Implementation of a Linear Regression algorithm for predicting
 * numerical values depending on specific input
 */
public class LinearRegression {
    private static final Logger logger = LogUtil.getLogger();

    /**
     * Method for calculating a hypothesis value fr a specific input value x.
     * <br><br>
     * <i>
     *     h(x) = theta0 * x0 + theta1 * x1 + ... + thetan * xn =&gt; transpose(theta) * x
     * </i>
     */
    protected static double[] calculateHypothesis(double[][] x, double[] theta){
        return Matrix.multiply(x, theta);
    }

    /**
     * Linear Regression cost method.
     * <p><br>
     * <i>
     *      J(O) = 1 / (2 * m) * Σ { ( h(Xi) - Yi )^2 }
     * </i><br>
     * m = learning data size (rows)
     * @return a number indicating the error rate
     */
    protected static double calculateCost(double[][] x, double[] y, double[] theta){
        double[] hypothesis = calculateHypothesis(x, theta);
        double[] normalized = Matrix.subtract(hypothesis, y);

        return 1.0 / (2.0 * x.length) * Matrix.sum(
                Matrix.Elemental.pow(normalized,2));
    }

    private static double calculateDiff(double[] vector1, double[] vector2){
        return Math.abs(Matrix.sum(vector1) - Matrix.sum(vector2));
    }

    /**
     * Will try to find the best theta value.
     */
    public static double[] gradientDescent(double[][] x, double[] y, double[] theta, double alpha){
        double[] newTheta = theta.clone();
        double[] prevTheta = new double[newTheta.length];
        double thetaDiff = 0;
        int i = 0;

        do {
            logger.fine("Gradient Descent iteration " + i + ", diff to previous iteration: " + thetaDiff);
            System.arraycopy(newTheta, 0, prevTheta, 0, newTheta.length);
            newTheta = gradientDescentIteration(x, y, newTheta, alpha);
            ++i;
        } while ((thetaDiff=calculateDiff(prevTheta, newTheta)) > 0.0001);

        return newTheta;
    }

    /**
     * Gradient Descent algorithm
     * <p><br>
     * <i>
     *     Oj = Oj - α * (1 / m) *  Σ { ( h(Xi) - Yi ) * Xij }
     * </i>
     * <br>
     * @return the theta that was found to minimize the cost function
     */
    public static double[] gradientDescentIteration(double[][] x, double[] y, double[] theta, double alpha){
        double[] newTheta = new double[theta.length];
        double m = y.length;
        double[] hypothesis = calculateHypothesis(x, theta);
        double[] normalized = Matrix.subtract(hypothesis, y);

        for (int j= 0; j < theta.length; j++) {
            newTheta[j] = theta[j] - (alpha/m) * Matrix.sum(
                    Matrix.Elemental.multiply(normalized, Matrix.getColumn(x, j)));
        }

        return newTheta;
    }
}
