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
