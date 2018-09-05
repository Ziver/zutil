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
     *     h(x) = theta0 * x0 + theta1 * x1 + ... + thetan * xn => transpose(theta) * x
     * </i>
     */
    protected static double[] calculateHypothesis(double[][] x, double[] theta){
        return Matrix.multiply(x, theta);
    }

    /**
     * Linear Regression cost method.
     * <br /><br />
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

    /**
     * Calculates the gradiant of the current provided theta.
     */
    protected static double calculateGradiant(double[][] x, double[] y, double[] theta){
        int m = y.length; // number of training examples
        double[] hypothesis = calculateHypothesis(x, theta);
        double[] normalized = Matrix.subtract(hypothesis, y);

        return 1/m * Matrix.sum(
                Matrix.Elemental.multiply(Matrix.transpose(x), normalized));

    }

    /**
     * Will try to find the best theta value.
     */
    public static double[] gradientDescent(double[][] x, double[] y, double[] theta, double alpha){
        double[] newTheta = theta.clone();
        double gradient;

        for (int i=0; (gradient = calculateGradiant(x, y, newTheta)) != 0; i++) {
            logger.fine("Gradient Descent iteration " + i + ", gradiant: " + gradient);
            newTheta = gradientDescentIteration(x, y, newTheta, alpha);
        }

        return newTheta;
    }

    /**
     * Gradient Descent algorithm
     * <br /><br />
     * <i>
     *     Oj = Oj - α * (1 / m) *  Σ { ( h(Xi) - Yi ) * Xij }
     * </i><br />
     *
     * @return the theta that was found to minimize the cost function
     */
    public static double[] gradientDescentIteration(double[][] x, double[] y, double[] theta, double alpha){
        double[] newTheta = new double[theta.length];
        double m = y.length;
        double[] hypothesis = calculateHypothesis(x, theta);
        double[] normalized = Matrix.subtract(hypothesis, y);

        for (int j= 0; j < theta.length; j++) {
            newTheta[j] = theta[j] - alpha * (1.0/m) * Matrix.sum(
                    Matrix.Elemental.multiply(normalized, Matrix.getColumn(x, j)));
        }

        return newTheta;
    }
}
