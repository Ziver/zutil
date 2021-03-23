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

package zutil.converter;

import java.util.HashMap;

public class NumberToWordsConverter {

    private static final String ZERO_STRINGS = "zero";
    // Indexes to lookup
    private static final int[] NUMERIC_INDEXES = new int[]{
        9,/*1000 000 000*/
        6,/*1000 000*/
        3,/*1000*/
        2 /*100*/};
    private static final HashMap<Long, String> NUMERIC_STRINGS;
    static{
        NUMERIC_STRINGS = new HashMap<>();
        NUMERIC_STRINGS.put(1L, "one");
        NUMERIC_STRINGS.put(2L, "two");
        NUMERIC_STRINGS.put(3L, "three");
        NUMERIC_STRINGS.put(4L, "four");
        NUMERIC_STRINGS.put(5L, "five");
        NUMERIC_STRINGS.put(6L, "six");
        NUMERIC_STRINGS.put(7L, "seven");
        NUMERIC_STRINGS.put(8L, "eight");
        NUMERIC_STRINGS.put(9L, "nine");
        NUMERIC_STRINGS.put(10L, "ten");
        NUMERIC_STRINGS.put(11L, "eleven");
        NUMERIC_STRINGS.put(12L, "twelve");
        NUMERIC_STRINGS.put(13L, "thirteen");
        NUMERIC_STRINGS.put(14L, "fourteen");
        NUMERIC_STRINGS.put(15L, "fifteen");
        NUMERIC_STRINGS.put(16L, "sixteen");
        NUMERIC_STRINGS.put(17L, "seventeen");
        NUMERIC_STRINGS.put(18L, "eightteen");
        NUMERIC_STRINGS.put(19L, "nineteen");

        NUMERIC_STRINGS.put(20L, "twenty");
        NUMERIC_STRINGS.put(30L, "thirty");
        NUMERIC_STRINGS.put(40L, "forty");
        NUMERIC_STRINGS.put(50L, "fifty");
        NUMERIC_STRINGS.put(60L, "sixty");
        NUMERIC_STRINGS.put(70L, "seventy");
        NUMERIC_STRINGS.put(80L, "eighty");
        NUMERIC_STRINGS.put(90L, "ninety");

        NUMERIC_STRINGS.put(100L, "hundred");
        NUMERIC_STRINGS.put(1_000L, "thousand");
        NUMERIC_STRINGS.put(1000_000L, "million");
        NUMERIC_STRINGS.put(1000_000_000L, "billion");
    }


    /**
     * Given an integer in the range -20 to 20 will return a String with
     * that number converted to words. For example, an input of 15 results in
     * an output of "fifteen". An input of -4 returns "minus four".
     *
     * @param	 num	a number to be converted to words.
     * @return the number as words.
     */
    public String convert(int num) {
        if (num == 0)
            return ZERO_STRINGS;

        long tmpNum = num;
        StringBuilder buffer = new StringBuilder();
        if (tmpNum < 0) { // Negative number
            tmpNum *= -1;
            buffer.append("minus ");
        }

        for (int i : NUMERIC_INDEXES) {
            long pow = (int)Math.pow(10, i);
            if (tmpNum >= pow) {
                long numberAtIndex = tmpNum/pow; // The number at position 3
                tmpNum -= numberAtIndex*pow;
                buffer.append(convert((int)numberAtIndex)).append(" ");
                buffer.append(NUMERIC_STRINGS.get(pow)).append(" ");
            }
        }
        if (tmpNum >= 20) { // second number in the integer
            long numberAtIndex = ((tmpNum % 100)/10)*10; // The number at position 2
            tmpNum -= numberAtIndex;
            buffer.append(NUMERIC_STRINGS.get(numberAtIndex)).append(" ");
        }
        if (NUMERIC_STRINGS.containsKey(tmpNum))
            buffer.append(NUMERIC_STRINGS.get(tmpNum));
        return buffer.toString().trim();
    }


}
