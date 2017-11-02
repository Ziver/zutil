package zutil.math;

/**
 * Some basic matrix match functions.
 * Matrix definition: double[y][x].
 */
public class MatrixMath {

    /***********************************************************************
     *                      Scalar
     **********************************************************************/
    /**
     * Scalar addition, every element in the matrix will
     * be added by the provided number
     *
     * @return same reference as matrix with the resulting addition
     */
    public static double[][] add(double[][] matrix, double num){
        for (int y=0; y < matrix.length; ++y) {
            for (int x=0; x < matrix[y].length; ++x){
                matrix[y][x] = matrix[y][x] + num;
            }
        }
        return matrix;
    }

    /**
     * Scalar subtraction, every element in the matrix will
     * be subtracted by the provided number
     *
     * @return same reference as matrix with the resulting subtraction
     */
    public static double[][] subtract(double[][] matrix, double num){
        for (int y=0; y < matrix.length; ++y) {
            for (int x=0; x < matrix[y].length; ++x){
                matrix[y][x] = matrix[y][x] - num;
            }
        }
        return matrix;
    }

    /**
     * Scalar multiplication, every element in the matrix will
     * be multiplied by the provided number
     *
     * @return same reference as matrix with the resulting multiplication
     */
    public static double[][] multiply(double[][] matrix, double num){
        for (int y=0; y < matrix.length; ++y) {
            for (int x=0; x < matrix[y].length; ++x){
                matrix[y][x] = matrix[y][x] * num;
            }
        }
        return matrix;
    }

    /**
     * Scalar division, every element in the matrix will
     * be multiplied by the provided number
     *
     * @return same reference as matrix with the resulting division
     */
    public static double[][] divide(double[][] matrix, double num){
        for (int y=0; y < matrix.length; ++y) {
            for (int x=0; x < matrix[y].length; ++x){
                matrix[y][x] = matrix[y][x] / num;
            }
        }
        return matrix;
    }

    /***********************************************************************
     *                      Elemental
     **********************************************************************/

    /**
     * Element addition, each element in matrix1 will be
     * added with the corresponding element in matrix2.
     *
     * @return same reference as matrix1 with the resulting addition
     */
    public static double[][] add(double[][] matrix1, double[][] matrix2){
        for (int y=0; y < matrix1.length; ++y) {
            for (int x=0; x < matrix1[y].length; ++x){
                matrix1[y][x] = matrix1[y][x] + matrix2[y][x];
            }
        }
        return matrix1;
    }

    /**
     * Element subtraction, each element in matrix1 will be
     * subtracted with the corresponding element in matrix2.
     *
     * @return same reference as matrix1 with the resulting subtraction
     */
    public static double[][] subtract(double[][] matrix1, double[][] matrix2){
        for (int y=0; y < matrix1.length; ++y) {
            for (int x=0; x < matrix1[y].length; ++x){
                matrix1[y][x] = matrix1[y][x] - matrix2[y][x];
            }
        }
        return matrix1;
    }

    /**
     * Element multiplication, each element in matrix1 will be
     * multiplied with the corresponding element in matrix2.
     *
     * @return same reference as matrix1 with the resulting multiplication
     */
    public static double[][] multiply(double[][] matrix1, double[][] matrix2){
        for (int y=0; y < matrix1.length; ++y) {
            for (int x=0; x < matrix1[y].length; ++x){
                matrix1[y][x] = matrix1[y][x] * matrix2[y][x];
            }
        }
        return matrix1;
    }

    /**
     * Element division, each element in matrix1 will be
     * dicided with the corresponding element in matrix2.
     *
     * @return same reference as matrix1 with the resulting division
     */
    public static double[][] divide(double[][] matrix1, double[][] matrix2){
        for (int y=0; y < matrix1.length; ++y) {
            for (int x=0; x < matrix1[y].length; ++x){
                matrix1[y][x] = matrix1[y][x] / matrix2[y][x];
            }
        }
        return matrix1;
    }
}
