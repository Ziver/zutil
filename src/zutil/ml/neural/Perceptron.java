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

package zutil.ml.neural;

/**
 * This class represents one "nuron" in a neural network.
 * <p>
 * <b>From Wikipedia:</b> https://en.wikipedia.org/wiki/Perceptron
 * <blockquote>
 *     The perceptron is an algorithm for supervised learning of
 *     binary classifiers (functions that can decide whether an
 *     input, represented by a vector of numbers, belongs to some
 *     specific class or not). It is a type of linear classifier,
 *     i.e. a classification algorithm that makes its predictions based
 *     on a linear predictor function combining a set of weights with
 *     the feature vector.
 * </blockquote>
 */
public class Perceptron {

    /** Internal classes **/

    public static class InputConnection {
        float weight;
    }
    public static class OutputConnection {
        InputConnection target;
    }

    /** Class Fields **/

    private InputConnection[] inputs;
    private OutputConnection[] outputs;


    public Perceptron(int inputCount, int outputCount){
        inputs = new InputConnection[inputCount];
        outputs = new OutputConnection[outputCount];
    }


    public InputConnection[] getInputs() {
        return inputs;
    }
    public OutputConnection[] getOutputs() {
        return outputs;
    }
}
