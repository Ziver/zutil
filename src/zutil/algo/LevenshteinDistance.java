/*
 * Copyright (c) 2015 ezivkoc
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

package zutil.algo;

/**
 * Created by Ziver on 2014-10-07.
 */
public class LevenshteinDistance {

    /**
     * Calculates the Levenshtein Distance(Number of character
     * changes to equalize the two strings) for two Strings.
     *
     * @return The number of changes needed to equalize the two Strings
     */
    public static int getDistance(String str1, String str2) {
        return getDistance(str1, str2,
                new int[str1.length()+1][str2.length()+1]);
    }

    /**
     * Calculates the Levenshtein Distance(Number of character
     * changes to equalize the two strings) for two Strings.
     *
     * @param   matrix  is a int matrix that will be used for the dynamic programing algorithm.
     *                  NOTE: matrix must be 1 larger than the largest string
     * @return The number of changes needed to equalize the two Strings
     */
    public static int getDistance(String str1, String str2, int[][] matrix) {
        int len1 = str1.length()+1;
        int len2 = str2.length()+1;
        if(matrix.length < len1 || matrix[0].length < len2)
            throw new IndexOutOfBoundsException("matrix["+matrix.length+"]["+matrix[0].length+"] must be of size ["+len1+"]["+len2+"] or larger");

        // source prefixes can be transformed into empty string by
        // dropping all characters
        for (int i = 0; i < len1; i++) {
            matrix[i][0] = i;
        }
        for (int j = 0; j < len2; j++) {
            matrix[0][j] = j;
        }

        for (int j=1; j < len2; j++) {
            for (int i=1; i < len1; i++) {
                if (str1.charAt(i-1) == str2.charAt(j-1))
                    matrix[i][j] = matrix[i - 1][j - 1]; // no operation required
                else {
                    matrix[i][j] = min(
                            matrix[i - 1][j] + 1,  // a deletion
                            matrix[i][j - 1] + 1,  // an insertion
                            matrix[i - 1][j - 1] + 1); // a substitution
                }
            }
        }
        /*
        System.out.println();
        for (int j=0; j < len2+1; j++) {
            if(j>1)
                System.out.print(str2.charAt(j-2)+" ");
            else
                System.out.print("_ ");
            for (int i=0; i < len1+1; i++) {
                if(j==0) {
                    if (i > 1)
                        System.out.print(str1.charAt(i - 2) + " ");
                    else if (i != 0)
                        System.out.print("_ ");
                }
                else if (i != 0)
                    System.out.print(matrix[i-1][j-1]+" ");
            }
            System.out.println();
        }
        System.out.println("\n\n");*/
        return matrix[len1 - 1][len2 - 1];
    }

    private static int min(int a, int b, int c){
        int i = (a < b) ? a : b;
        return (i < c) ? i : c;
        //return Math.min(i, Math.min(j, k));
    }
}
