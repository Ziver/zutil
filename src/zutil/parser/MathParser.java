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

package zutil.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class parses a string with math
 * and solves it
 *
 * @author Ziver
 */
public class MathParser {

    public static MathNode parse(String functionString) {
        StringBuffer functionStringBuffer = new StringBuffer(functionString + (char) 0);
        MathNode node = new MathNode();

        parse(functionStringBuffer, new StringBuffer(), null, node);
        return node;
    }

    private static void parse(StringBuffer functionString, StringBuffer temp, MathOperation previous, MathNode rootNode) {
        if (functionString.length() <= 0) {
            return;
        }
        char c = functionString.charAt(0);
        functionString.deleteCharAt(0);
        MathOperation current = null;

        if (!Character.isWhitespace(c)) {
            if (Character.isDigit(c)) {
                temp.append(c);
            } else {
                Math container = new MathNumber();
                if (temp.length() > 0) {
                    ((MathNumber) container).num = Double.parseDouble(temp.toString());
                    temp.delete(0, temp.length());
                }

                if (rootNode.math == null) {
                    previous = getOperation(c);
                    previous.math1 = container;
                    rootNode.math = previous;
                } else {
                    if (c == '(') {
                        MathNode parentheses = new MathNode();
                        parse(functionString, temp, previous, parentheses);
                        previous.math2 = parentheses;
                        container = parentheses;

                        // get the next operation
                        c = functionString.charAt(0);
                        functionString.deleteCharAt(0);
                    }

                    current = getOperation(c);
                    current.math1 = container;
                    previous.math2 = current;

                    if (c == ')') {
                        return;
                    }
                }
            }
        }

        if (current != null)
            parse(functionString, temp, current, rootNode);
        else
            parse(functionString, temp, previous, rootNode);
    }

    private static MathOperation getOperation(char c) {
        switch (c) {
            case '+':
                return new MathAddition();
            case '-':
                return new MathSubtraction();
            case '*':
                return new MathMultiplication();
            case '/':
                return new MathDivision();
            case '%':
                return new MathModulus();
            case '^':
                return new MathPow();
            case ')':
            case (char) 0:
                return new EmptyMath();
            default:
                return null;
        }
    }

    public static void main(String[] args) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                System.out.print("<< Math: ");
                MathNode math = parse(in.readLine());
                System.out.println(">> = " + math.exec());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static abstract class Math {
        public abstract double exec();

        public abstract String toString();
    }

    static class MathNode extends Math {
        Math math;

        public double exec() {
            return math.exec();
        }

        public String toString() {
            return "( " + math.toString() + ")";
        }
    }

    static class MathNumber extends Math {
        double num;

        public double exec() {
            return num;
        }

        public String toString() {
            return "" + num;
        }
    }

    static abstract class MathOperation extends Math {
        Math math1;
        Math math2;

        public abstract double exec();
    }

    static class MathAddition extends MathOperation {
        public double exec() {
            return math1.exec() + math2.exec();
        }

        public String toString() {
            return math1.toString() + " + " + math2.toString();
        }
    }

    static class MathSubtraction extends MathOperation {
        public double exec() {
            return math1.exec() - math2.exec();
        }

        public String toString() {
            return math1.toString() + " - " + math2.toString();
        }
    }

    static class MathMultiplication extends MathOperation {
        public double exec() {
            return math1.exec() * math2.exec();
        }

        public String toString() {
            return math1.toString() + " * " + math2.toString();
        }
    }

    static class MathDivision extends MathOperation {
        public double exec() {
            return math1.exec() / math2.exec();
        }

        public String toString() {
            return math1.toString() + " / " + math2.toString();
        }
    }

    static class MathModulus extends MathOperation {
        public double exec() {
            return math1.exec() % math2.exec();
        }

        public String toString() {
            return math1.toString() + " % " + math2.toString();
        }
    }

    static class MathPow extends MathOperation {
        public double exec() {
            double ret = 1;
            double tmp1 = math1.exec();
            double tmp2 = math2.exec();
            for (int i = 0; i < tmp2; i++) {
                ret *= tmp1;
            }
            return ret;
        }

        public String toString() {
            return math1.toString() + "^" + math2.toString();
        }
    }

    static class EmptyMath extends MathOperation {
        public double exec() {
            return math1.exec();
        }

        public String toString() {
            return math1.toString();
        }
    }

}