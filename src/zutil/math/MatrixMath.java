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
     * @return a new matrix with the result
     */
    public static double[][] add(double[][] matrix, double num){
        double[][] result = new double[matrix.length][matrix[0].length];

        for (int y=0; y < matrix.length; ++y) {
            for (int x=0; x < matrix[y].length; ++x){
                result[y][x] = matrix[y][x] + num;
            }
        }
        return result;
    }

    /**
     * Scalar subtraction, every element in the matrix will
     * be subtracted by the provided number
     *
     * @return a new matrix with the result
     */
    public static double[][] subtract(double[][] matrix, double num){
        double[][] result = new double[matrix.length][matrix[0].length];

        for (int y=0; y < matrix.length; ++y) {
            for (int x=0; x < matrix[y].length; ++x){
                result[y][x] = matrix[y][x] - num;
            }
        }
        return result;
    }

    /**
     * Scalar multiplication, every element in the matrix will
     * be multiplied by the provided number
     *
     * @return a new matrix with the result
     */
    public static double[][] multiply(double[][] matrix, double num){
        double[][] result = new double[matrix.length][matrix[0].length];

        for (int y=0; y < matrix.length; ++y) {
            for (int x=0; x < matrix[y].length; ++x){
                result[y][x] = matrix[y][x] * num;
            }
        }
        return result;
    }

    /**
     * Scalar division, every element in the matrix will
     * be multiplied by the provided number
     *
     * @return a new matrix with the result
     */
    public static double[][] divide(double[][] matrix, double num){
        return multiply(matrix, 1/num);
    }

    /***********************************************************************
     *                      Elemental
     **********************************************************************/

    /**
     * Element addition, each element in matrix1 will be
     * added with the corresponding element in matrix2.
     *
     * @return a new matrix with the result
     */
    public static double[][] add(double[][] matrix1, double[][] matrix2){
        elementalPreCheck(matrix1, matrix2);
        double[][] result = new double[matrix1.length][matrix1[0].length];

        for (int y=0; y < matrix1.length; ++y) {
            for (int x=0; x < matrix1[y].length; ++x){
                result[y][x] = matrix1[y][x] + matrix2[y][x];
            }
        }
        return result;
    }

    /**
     * Element subtraction, each element in matrix1 will be
     * subtracted with the corresponding element in matrix2.
     *
     * @return a new matrix with the result
     */
    public static double[][] subtract(double[][] matrix1, double[][] matrix2){
        elementalPreCheck(matrix1, matrix2);
        double[][] result = new double[matrix1.length][matrix1[0].length];

        for (int y=0; y < matrix1.length; ++y) {
            for (int x=0; x < matrix1[y].length; ++x){
                result[y][x] = matrix1[y][x] - matrix2[y][x];
            }
        }
        return result;
    }

    private static void elementalPreCheck(double[][] matrix1, double[][] matrix2) {
        if (matrix1.length != matrix2.length || matrix1[0].length != matrix2[0].length)
            throw new IllegalArgumentException("Matrices need to be of same dimension: " +
                    "matrix1 " + matrix1.length + "x" + matrix1[0].length + ", " +
                    "matrix2 " + matrix2.length + "x" + matrix2[0].length + ", ");
    }

    /***********************************************************************
     *                      Vector
     **********************************************************************/

    /**
     * Matrix Vector multiplication, each element column in the matrix will be
     * multiplied with the corresponding element row in the vector.
     *
     * @return a new vector with the result
     */
    public static double[] multiply(double[][] matrix, double[] vector){
        vectorPreCheck(matrix, vector);
        double[] result = new double[matrix.length];

        for (int y=0; y < matrix.length; ++y) {
            for (int x=0; x < matrix[y].length; ++x){
                result[y] += matrix[y][x] * vector[x];
            }
        }
        return result;
    }

    /**
     * Matrix Vector division, each element column in the matrix will be
     * divided with the corresponding element row in the vector.
     *
     * @return a new vector with the result
     */
    public static double[] divide(double[][] matrix, double[] vector){
        vectorPreCheck(matrix, vector);
        double[] result = new double[matrix.length];

        for (int y=0; y < matrix.length; ++y) {
            for (int x=0; x < matrix[y].length; ++x){
                result[y] += matrix[y][x] / vector[x];
            }
        }
        return result;
    }

    private static void vectorPreCheck(double[][] matrix, double[] vector) {
        if (matrix[0].length != vector.length)
            throw new IllegalArgumentException("Matrix columns need to have same length as vector length: " +
                    "matrix " + matrix.length + "x" + matrix[0].length + ", " +
                    "vector " + vector.length + "x1");
    }

    /***********************************************************************
     *                      Matrix
     **********************************************************************/

    /**
     * Element multiplication, each element in matrix1 will be
     * multiplied with the corresponding element in matrix2.
     *
     * @return a new matrix with the result
     */
    public static double[][] multiply(double[][] matrix1, double[][] matrix2){
        matrixPreCheck(matrix1, matrix2);
        double[][] result = new double[matrix1.length][matrix2[0].length];

        for (int y=0; y < result.length; ++y) {
            for (int x=0; x < result[y].length; ++x){

                for (int i=0; i<matrix1[0].length; ++i) {
                    result[y][x] += matrix1[y][i] * matrix2[i][x];
                }
            }
        }
        return result;
    }

    /**
     * @return a new matrix with the transpose of the input matrix.
     */
    public static double[][] transpose(double[][] matrix){
        double[][] result = new double[matrix[0].length][matrix.length];

        for (int y=0; y < result.length; ++y) {
            for (int x=0; x < result[y].length; ++x){
                result[y][x] = matrix[x][y];
            }
        }
        return result;
    }


    private static void matrixPreCheck(double[][] matrix1, double[][] matrix2) {
        if (matrix1[0].length != matrix2.length)
            throw new IllegalArgumentException("Matrix1 columns need to match Matrix2 rows: " +
                    "matrix1 " + matrix1.length + "x" + matrix1[0].length + ", " +
                    "matrix2 " + matrix2.length + "x" + matrix2[0].length + ", ");
    }

    /***********************************************************************
     *                      Util Methods
     **********************************************************************/

    /**
     * @return a identity matrix (n x n) where the diagonal elements have the value 1
     */
    public static double[][] identity(int n){
        double[][] result = new double[n][n];

        for (int i=0; i < n; ++i) {
            result[i][i] = 1;
        }
        return result;
    }

}
