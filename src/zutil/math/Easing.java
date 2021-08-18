package zutil.math;

/**
 * Class contains easing function mostly used in animations and other graphical things.
 *
 * @see <a href="https://easings.net/">Easing.net</a>
 */
public class Easing {

    // ----------------------------------------------------
    // Sin
    // ----------------------------------------------------

    /**
     * @param   x   represents the absolute progress of the animation in the bounds of 0 (beginning of the animation) and 1 (end of animation).
     * @return a double value between 0 and 1 based on the animation progress.
     */
    public static double easeInSine(double x) {
        return 1 - Math.cos((x * Math.PI) / 2.0);
    }

    /**
     * @param   x   represents the absolute progress of the animation in the bounds of 0 (beginning of the animation) and 1 (end of animation).
     * @return a double value between 0 and 1 based on the animation progress.
     */
    public static double easeOutSine(double x) {
        return Math.sin((x * Math.PI) / 2.0);
    }

    /**
     * @param   x   represents the absolute progress of the animation in the bounds of 0 (beginning of the animation) and 1 (end of animation).
     * @return a double value between 0 and 1 based on the animation progress.
     */
    public static double easeInOutSine(double x) {
        return -(Math.cos(x * Math.PI) - 1.0) / 2.0;
    }

    // ----------------------------------------------------
    // Quad
    // ----------------------------------------------------

    /**
     * @param   x   represents the absolute progress of the animation in the bounds of 0 (beginning of the animation) and 1 (end of animation).
     * @return a double value between 0 and 1 based on the animation progress.
     */
    public static double easeInQuad(double x) {
        return x * x;
    }

    /**
     * @param   x   represents the absolute progress of the animation in the bounds of 0 (beginning of the animation) and 1 (end of animation).
     * @return a double value between 0 and 1 based on the animation progress.
     */
    public static double easeOutQuad(double x) {
        return 1 - (1 - x) * (1 - x);
    }

    /**
     * @param   x   represents the absolute progress of the animation in the bounds of 0 (beginning of the animation) and 1 (end of animation).
     * @return a double value between 0 and 1 based on the animation progress.
     */
    public static double easeInOutQuad(double x) {
        return (x < 0.5 ?
                2 * x * x :
                1 - Math.pow(-2 * x + 2, 2) / 2);
    }

    // ----------------------------------------------------
    // Cubic
    // ----------------------------------------------------

    /**
     * @param   x   represents the absolute progress of the animation in the bounds of 0 (beginning of the animation) and 1 (end of animation).
     * @return a double value between 0 and 1 based on the animation progress.
     */
    public static double easeInCubic(double x) {
        return x * x * x;
    }

    /**
     * @param   x   represents the absolute progress of the animation in the bounds of 0 (beginning of the animation) and 1 (end of animation).
     * @return a double value between 0 and 1 based on the animation progress.
     */
    public static double easeOutCubic(double x) {
        return 1 - Math.pow(1 - x, 3);
    }

    /**
     * @param   x   represents the absolute progress of the animation in the bounds of 0 (beginning of the animation) and 1 (end of animation).
     * @return a double value between 0 and 1 based on the animation progress.
     */
    public static double easeInOutCubic(double x) {
        return (x < 0.5 ?
                4 * x * x * x :
                1 - Math.pow(-2 * x + 2, 3) / 2);
    }

    // ----------------------------------------------------
    // Cubic
    // ----------------------------------------------------

    /**
     * @param   x   represents the absolute progress of the animation in the bounds of 0 (beginning of the animation) and 1 (end of animation).
     * @return a double value between 0 and 1 based on the animation progress.
     */
    public static double easeInQuart(double x) {
        return x * x * x * x;
    }

    /**
     * @param   x   represents the absolute progress of the animation in the bounds of 0 (beginning of the animation) and 1 (end of animation).
     * @return a double value between 0 and 1 based on the animation progress.
     */
    public static double easeOutQuart(double x) {
        return 1 - Math.pow(1 - x, 4);
    }

    /**
     * @param   x   represents the absolute progress of the animation in the bounds of 0 (beginning of the animation) and 1 (end of animation).
     * @return a double value between 0 and 1 based on the animation progress.
     */
    public static double easeInOutQuart(double x) {
        return (x < 0.5 ?
                8 * x * x * x * x :
                1 - Math.pow(-2 * x + 2, 4) / 2);
    }

    // ----------------------------------------------------
    // Quint
    // ----------------------------------------------------

    /**
     * @param   x   represents the absolute progress of the animation in the bounds of 0 (beginning of the animation) and 1 (end of animation).
     * @return a double value between 0 and 1 based on the animation progress.
     */
    public static double easeInQuint(double x) {
        return x * x * x * x * x;
    }

    /**
     * @param   x   represents the absolute progress of the animation in the bounds of 0 (beginning of the animation) and 1 (end of animation).
     * @return a double value between 0 and 1 based on the animation progress.
     */
    public static double easeOutQuint(double x) {
        return 1 - Math.pow(1 - x, 5);
    }

    /**
     * @param   x   represents the absolute progress of the animation in the bounds of 0 (beginning of the animation) and 1 (end of animation).
     * @return a double value between 0 and 1 based on the animation progress.
     */
    public static double easeInOutQuint(double x) {
        return (x < 0.5 ?
                16 * x * x * x * x * x :
                1 - Math.pow(-2 * x + 2, 5) / 2);
    }

    // ----------------------------------------------------
    // Cubic
    // ----------------------------------------------------

    /**
     * @param   x   represents the absolute progress of the animation in the bounds of 0 (beginning of the animation) and 1 (end of animation).
     * @return a double value between 0 and 1 based on the animation progress.
     */
    public static double easeInExpo(double x) {
        return (x == 0 ?
                0 :
                Math.pow(2, 10 * x - 10));
    }

    /**
     * @param   x   represents the absolute progress of the animation in the bounds of 0 (beginning of the animation) and 1 (end of animation).
     * @return a double value between 0 and 1 based on the animation progress.
     */
    public static double easeOutExpo(double x) {
        return (x == 1 ?
                1 :
                1 - Math.pow(2, -10 * x));
    }

    /**
     * @param   x   represents the absolute progress of the animation in the bounds of 0 (beginning of the animation) and 1 (end of animation).
     * @return a double value between 0 and 1 based on the animation progress.
     */
    public static double easeInOutExpo(double x) {
        if (x == 0.0)
            return 0;
        else if (x == 1.0)
            return 1;
        else if (x < 0.5)
            return Math.pow(2, 20 * x - 10) / 2;
        else
            return (2 - Math.pow(2, -20 * x + 10)) / 2;
    }

    // ----------------------------------------------------
    // Circ
    // ----------------------------------------------------

    /**
     * @param   x   represents the absolute progress of the animation in the bounds of 0 (beginning of the animation) and 1 (end of animation).
     * @return a double value between 0 and 1 based on the animation progress.
     */
    public static double easeInCirc(double x) {
        return 1 - Math.sqrt(1 - Math.pow(x, 2));
    }

    /**
     * @param   x   represents the absolute progress of the animation in the bounds of 0 (beginning of the animation) and 1 (end of animation).
     * @return a double value between 0 and 1 based on the animation progress.
     */
    public static double easeOutCirc(double x) {
        return Math.sqrt(1 - Math.pow(x - 1, 2));
    }

    /**
     * @param   x   represents the absolute progress of the animation in the bounds of 0 (beginning of the animation) and 1 (end of animation).
     * @return a double value between 0 and 1 based on the animation progress.
     */
    public static double easeInOutCirc(double x) {
        return (x < 0.5
                ? (1 - Math.sqrt(1 - Math.pow(2 * x, 2))) / 2
                : (Math.sqrt(1 - Math.pow(-2 * x + 2, 2)) + 1) / 2);
    }

    // ----------------------------------------------------
    // Back
    // ----------------------------------------------------

    /**
     * @param   x   represents the absolute progress of the animation in the bounds of 0 (beginning of the animation) and 1 (end of animation).
     * @return a double value between 0 and 1 based on the animation progress.
     */
    public static double easeInBack(double x) {
        double c1 = 1.70158;
        double c3 = c1 + 1;

        return c3 * x * x * x - c1 * x * x;
    }

    /**
     * @param   x   represents the absolute progress of the animation in the bounds of 0 (beginning of the animation) and 1 (end of animation).
     * @return a double value between 0 and 1 based on the animation progress.
     */
    public static double easeOutBack(double x) {
        double c1 = 1.70158;
        double c3 = c1 + 1;

        return 1 + c3 * Math.pow(x - 1, 3) + c1 * Math.pow(x - 1, 2);
    }

    /**
     * @param   x   represents the absolute progress of the animation in the bounds of 0 (beginning of the animation) and 1 (end of animation).
     * @return a double value between 0 and 1 based on the animation progress.
     */
    public static double easeInOutBack(double x) {
        double c1 = 1.70158;
        double c2 = c1 * 1.525;

        return x < 0.5
                ? (Math.pow(2 * x, 2) * ((c2 + 1) * 2 * x - c2)) / 2
                : (Math.pow(2 * x - 2, 2) * ((c2 + 1) * (x * 2 - 2) + c2) + 2) / 2;
    }

    // ----------------------------------------------------
    // Elastic
    // ----------------------------------------------------

    /**
     * @param   x   represents the absolute progress of the animation in the bounds of 0 (beginning of the animation) and 1 (end of animation).
     * @return a double value between 0 and 1 based on the animation progress.
     */
    public static double easeInElastic(double x) {
        double c4 = (2 * Math.PI) / 3;

        if (x == 0.0)
            return 0;
        else if (x == 1.0)
            return 1;
        else
            return - Math.pow(2, 10 * x - 10) * Math.sin((x * 10 - 10.75) * c4);
    }

    /**
     * @param   x   represents the absolute progress of the animation in the bounds of 0 (beginning of the animation) and 1 (end of animation).
     * @return a double value between 0 and 1 based on the animation progress.
     */
    public static double easeOutElastic(double x) {
        double c4 = (2 * Math.PI) / 3;

        if (x == 0.0)
            return 0;
        else if (x == 1.0)
            return 1;
        else
            return Math.pow(2, -10 * x) * Math.sin((x * 10 - 0.75) * c4) + 1;
    }

    /**
     * @param   x   represents the absolute progress of the animation in the bounds of 0 (beginning of the animation) and 1 (end of animation).
     * @return a double value between 0 and 1 based on the animation progress.
     */
    public static double easeInOutElastic(double x) {
        double c5 = (2 * Math.PI) / 4.5;

        if (x == 0.0)
            return 0;
        else if (x == 1.0)
            return 1;
        else if (x < 0.5)
            return -(Math.pow(2, 20 * x - 10) * Math.sin((20 * x - 11.125) * c5)) / 2;
        else
            return (Math.pow(2, -20 * x + 10) * Math.sin((20 * x - 11.125) * c5)) / 2 + 1;
    }

    // ----------------------------------------------------
    // Bounce
    // ----------------------------------------------------

    /**
     * @param   x   represents the absolute progress of the animation in the bounds of 0 (beginning of the animation) and 1 (end of animation).
     * @return a double value between 0 and 1 based on the animation progress.
     */
    public static double easeInBounce(double x) {
        return 1 - easeOutBounce(1 - x);
    }

    /**
     * @param   x   represents the absolute progress of the animation in the bounds of 0 (beginning of the animation) and 1 (end of animation).
     * @return a double value between 0 and 1 based on the animation progress.
     */
    public static double easeOutBounce(double x) {
        double n1 = 7.5625;
        double d1 = 2.75;

        if (x < 1 / d1) {
            return n1 * x * x;
        } else if (x < 2 / d1) {
            return n1 * (x -= 1.5 / d1) * x + 0.75;
        } else if (x < 2.5 / d1) {
            return n1 * (x -= 2.25 / d1) * x + 0.9375;
        } else {
            return n1 * (x -= 2.625 / d1) * x + 0.984375;
        }
    }

    /**
     * @param   x   represents the absolute progress of the animation in the bounds of 0 (beginning of the animation) and 1 (end of animation).
     * @return a double value between 0 and 1 based on the animation progress.
     */
    public static double easeInOutBounce(double x) {
        return x < 0.5
                ? (1 - easeOutBounce(1 - 2 * x)) / 2
                : (1 + easeOutBounce(2 * x - 1)) / 2;
    }
}
