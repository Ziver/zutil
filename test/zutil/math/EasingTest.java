package zutil.math;

import static org.junit.Assert.*;
import org.junit.Test;

public class EasingTest {

    // ----------------------------------------------------
    // Sin
    // ----------------------------------------------------

    @Test
    public void easeInSine() {
        assertEquals(0.0, Easing.easeInSine(0), 0.01);
        assertEquals(0.07, Easing.easeInSine(0.25), 0.01);
        assertEquals(0.29, Easing.easeInSine(0.5), 0.01);
        assertEquals(0.62, Easing.easeInSine(0.75), 0.01);
        assertEquals(1.0, Easing.easeInSine(1), 0.01);
    }

    @Test
    public void easeOutSine() {
        assertEquals(0.0, Easing.easeOutSine(0), 0.01);
        assertEquals(0.38, Easing.easeOutSine(0.25), 0.01);
        assertEquals(0.70, Easing.easeOutSine(0.5), 0.01);
        assertEquals(0.92, Easing.easeOutSine(0.75), 0.01);
        assertEquals(1.0, Easing.easeOutSine(1), 0.01);
    }

    @Test
    public void easeInOutSine() {
        assertEquals(0.0, Easing.easeInOutSine(0), 0.01);
        assertEquals(0.14, Easing.easeInOutSine(0.25), 0.01);
        assertEquals(0.5, Easing.easeInOutSine(0.5), 0.01);
        assertEquals(0.85, Easing.easeInOutSine(0.75), 0.01);
        assertEquals(1.0, Easing.easeInOutSine(1), 0.01);
    }

    // ----------------------------------------------------
    // Quad
    // ----------------------------------------------------

    @Test
    public void easeInQuad() {
        assertEquals(0.0, Easing.easeInQuad(0), 0.01);
        assertEquals(0.06, Easing.easeInQuad(0.25), 0.01);
        assertEquals(0.25, Easing.easeInQuad(0.5), 0.01);
        assertEquals(0.56, Easing.easeInQuad(0.75), 0.01);
        assertEquals(1.0, Easing.easeInQuad(1), 0.01);
    }

    @Test
    public void easeOutQuad() {
        assertEquals(0.0, Easing.easeOutQuad(0), 0.01);
        assertEquals(0.43, Easing.easeOutQuad(0.25), 0.01);
        assertEquals(0.75, Easing.easeOutQuad(0.5), 0.01);
        assertEquals(0.93, Easing.easeOutQuad(0.75), 0.01);
        assertEquals(1.0, Easing.easeOutQuad(1), 0.01);
    }

    @Test
    public void easeInOutQuad() {
        assertEquals(0.0, Easing.easeInOutQuad(0), 0.01);
        assertEquals(0.12, Easing.easeInOutQuad(0.25), 0.01);
        assertEquals(0.50, Easing.easeInOutQuad(0.5), 0.01);
        assertEquals(0.87, Easing.easeInOutQuad(0.75), 0.01);
        assertEquals(1.0, Easing.easeInOutQuad(1), 0.01);
    }

    // ----------------------------------------------------
    // Cubic
    // ----------------------------------------------------

    @Test
    public void easeInCubic() {
        assertEquals(0.0, Easing.easeInCubic(0), 0.01);
        assertEquals(0.01, Easing.easeInCubic(0.25), 0.01);
        assertEquals(0.12, Easing.easeInCubic(0.5), 0.01);
        assertEquals(0.42, Easing.easeInCubic(0.75), 0.01);
        assertEquals(1.0, Easing.easeInCubic(1), 0.01);
    }

    @Test
    public void easeOutCubic() {
        assertEquals(0.0, Easing.easeOutCubic(0), 0.01);
        assertEquals(0.57, Easing.easeOutCubic(0.25), 0.01);
        assertEquals(0.87, Easing.easeOutCubic(0.5), 0.01);
        assertEquals(0.98, Easing.easeOutCubic(0.75), 0.01);
        assertEquals(1.0, Easing.easeOutCubic(1), 0.01);
    }

    @Test
    public void easeInOutCubic() {
        assertEquals(0.0, Easing.easeInOutCubic(0), 0.01);
        assertEquals(0.06, Easing.easeInOutCubic(0.25), 0.01);
        assertEquals(0.50, Easing.easeInOutCubic(0.5), 0.01);
        assertEquals(0.93, Easing.easeInOutCubic(0.75), 0.01);
        assertEquals(1.0, Easing.easeInOutCubic(1), 0.01);
    }

    // ----------------------------------------------------
    // Cubic
    // ----------------------------------------------------

    @Test
    public void easeInQuart() {
        assertEquals(0.0, Easing.easeInQuart(0), 0.01);
        assertEquals(0.00, Easing.easeInQuart(0.25), 0.01);
        assertEquals(0.06, Easing.easeInQuart(0.5), 0.01);
        assertEquals(0.31, Easing.easeInQuart(0.75), 0.01);
        assertEquals(1.0, Easing.easeInQuart(1), 0.01);
    }

    @Test
    public void easeOutQuart() {
        assertEquals(0.0, Easing.easeOutQuart(0), 0.01);
        assertEquals(0.68, Easing.easeOutQuart(0.25), 0.01);
        assertEquals(0.93, Easing.easeOutQuart(0.5), 0.01);
        assertEquals(0.99, Easing.easeOutQuart(0.75), 0.01);
        assertEquals(1.0, Easing.easeOutQuart(1), 0.01);
    }

    @Test
    public void easeInOutQuart() {
        assertEquals(0.0, Easing.easeInOutQuart(0), 0.01);
        assertEquals(0.03, Easing.easeInOutQuart(0.25), 0.01);
        assertEquals(0.50, Easing.easeInOutQuart(0.5), 0.01);
        assertEquals(0.96, Easing.easeInOutQuart(0.75), 0.01);
        assertEquals(1.0, Easing.easeInOutQuart(1), 0.01);
    }

    // ----------------------------------------------------
    // Quint
    // ----------------------------------------------------

    @Test
    public void easeInQuint() {
        assertEquals(0.0, Easing.easeInQuint(0), 0.01);
        assertEquals(0.00, Easing.easeInQuint(0.25), 0.01);
        assertEquals(0.03, Easing.easeInQuint(0.5), 0.01);
        assertEquals(0.23, Easing.easeInQuint(0.75), 0.01);
        assertEquals(1.0, Easing.easeInQuint(1), 0.01);
    }

    @Test
    public void easeOutQuint() {
        assertEquals(0.0, Easing.easeOutQuint(0), 0.01);
        assertEquals(0.76, Easing.easeOutQuint(0.25), 0.01);
        assertEquals(0.96, Easing.easeOutQuint(0.5), 0.01);
        assertEquals(0.99, Easing.easeOutQuint(0.75), 0.01);
        assertEquals(1.0, Easing.easeOutQuint(1), 0.01);
    }

    @Test
    public void easeInOutQuint() {
        assertEquals(0.0, Easing.easeInOutQuint(0), 0.01);
        assertEquals(0.01, Easing.easeInOutQuint(0.25), 0.01);
        assertEquals(0.50, Easing.easeInOutQuint(0.5), 0.01);
        assertEquals(0.98, Easing.easeInOutQuint(0.75), 0.01);
        assertEquals(1.0, Easing.easeInOutQuint(1), 0.01);
    }

    // ----------------------------------------------------
    // Cubic
    // ----------------------------------------------------

    @Test
    public void easeInExpo() {
        assertEquals(0.0, Easing.easeInExpo(0), 0.01);
        assertEquals(0.00, Easing.easeInExpo(0.25), 0.01);
        assertEquals(0.03, Easing.easeInExpo(0.5), 0.01);
        assertEquals(0.17, Easing.easeInExpo(0.75), 0.01);
        assertEquals(1.0, Easing.easeInExpo(1), 0.01);
    }

    @Test
    public void easeOutExpo() {
        assertEquals(0.0, Easing.easeOutExpo(0), 0.01);
        assertEquals(0.82, Easing.easeOutExpo(0.25), 0.01);
        assertEquals(0.97, Easing.easeOutExpo(0.5), 0.01);
        assertEquals(0.99, Easing.easeOutExpo(0.75), 0.01);
        assertEquals(1.0, Easing.easeOutExpo(1), 0.01);
    }

    @Test
    public void easeInOutExpo() {
        assertEquals(0.0, Easing.easeInOutExpo(0), 0.01);
        assertEquals(0.01, Easing.easeInOutExpo(0.25), 0.01);
        assertEquals(0.50, Easing.easeInOutExpo(0.5), 0.01);
        assertEquals(0.98, Easing.easeInOutExpo(0.75), 0.01);
        assertEquals(1.0, Easing.easeInOutExpo(1), 0.01);
    }

    // ----------------------------------------------------
    // Circ
    // ----------------------------------------------------

    @Test
    public void easeInCirc() {
        assertEquals(0.0, Easing.easeInCirc(0), 0.01);
        assertEquals(0.03, Easing.easeInCirc(0.25), 0.01);
        assertEquals(0.13, Easing.easeInCirc(0.5), 0.01);
        assertEquals(0.33, Easing.easeInCirc(0.75), 0.01);
        assertEquals(1.0, Easing.easeInCirc(1), 0.01);
    }

    @Test
    public void easeOutCirc() {
        assertEquals(0.0, Easing.easeOutCirc(0), 0.01);
        assertEquals(0.66, Easing.easeOutCirc(0.25), 0.01);
        assertEquals(0.86, Easing.easeOutCirc(0.5), 0.01);
        assertEquals(0.96, Easing.easeOutCirc(0.75), 0.01);
        assertEquals(1.0, Easing.easeOutCirc(1), 0.01);
    }

    @Test
    public void easeInOutCirc() {
        assertEquals(0.0, Easing.easeInOutCirc(0), 0.01);
        assertEquals(0.06, Easing.easeInOutCirc(0.25), 0.01);
        assertEquals(0.50, Easing.easeInOutCirc(0.5), 0.01);
        assertEquals(0.93, Easing.easeInOutCirc(0.75), 0.01);
        assertEquals(1.0, Easing.easeInOutCirc(1), 0.01);
    }

    // ----------------------------------------------------
    // Back
    // ----------------------------------------------------

    @Test
    public void easeInBack() {
        assertEquals(0.0, Easing.easeInBack(0), 0.01);
        assertEquals(-0.06, Easing.easeInBack(0.25), 0.01);
        assertEquals(-0.09, Easing.easeInBack(0.5), 0.01);
        assertEquals(0.18, Easing.easeInBack(0.75), 0.01);
        assertEquals(1.0, Easing.easeInBack(1), 0.01);
    }

    @Test
    public void easeOutBack() {
        assertEquals(0.0, Easing.easeOutBack(0), 0.01);
        assertEquals(0.81, Easing.easeOutBack(0.25), 0.01);
        assertEquals(1.08, Easing.easeOutBack(0.5), 0.01);
        assertEquals(1.06, Easing.easeOutBack(0.75), 0.01);
        assertEquals(1.0, Easing.easeOutBack(1), 0.01);
    }

    @Test
    public void easeInOutBack() {
        assertEquals(0.0, Easing.easeInOutBack(0), 0.01);
        assertEquals(-0.09, Easing.easeInOutBack(0.25), 0.01);
        assertEquals(0.50, Easing.easeInOutBack(0.5), 0.01);
        assertEquals(1.09, Easing.easeInOutBack(0.75), 0.01);
        assertEquals(1.0, Easing.easeInOutBack(1), 0.01);
    }

    // ----------------------------------------------------
    // Elastic
    // ----------------------------------------------------

    @Test
    public void easeInElastic() {
        assertEquals(0.0, Easing.easeInElastic(0), 0.01);
        assertEquals(0.00, Easing.easeInElastic(0.25), 0.01);
        assertEquals(-0.01, Easing.easeInElastic(0.5), 0.01);
        assertEquals(0.08, Easing.easeInElastic(0.75), 0.01);
        assertEquals(1.0, Easing.easeInElastic(1), 0.01);
    }

    @Test
    public void easeOutElastic() {
        assertEquals(0.0, Easing.easeOutElastic(0), 0.01);
        assertEquals(0.91, Easing.easeOutElastic(0.25), 0.01);
        assertEquals(1.01, Easing.easeOutElastic(0.5), 0.01);
        assertEquals(1.00, Easing.easeOutElastic(0.75), 0.01);
        assertEquals(1.0, Easing.easeOutElastic(1), 0.01);
    }

    @Test
    public void easeInOutElastic() {
        assertEquals(0.0, Easing.easeInOutElastic(0), 0.01);
        assertEquals(0.01, Easing.easeInOutElastic(0.25), 0.01);
        assertEquals(0.50, Easing.easeInOutElastic(0.5), 0.01);
        assertEquals(0.98, Easing.easeInOutElastic(0.75), 0.01);
        assertEquals(1.0, Easing.easeInOutElastic(1), 0.01);
    }

    // ----------------------------------------------------
    // Bounce
    // ----------------------------------------------------

    @Test
    public void easeInBounce() {
        assertEquals(0.0, Easing.easeInBounce(0), 0.01);
        assertEquals(0.02, Easing.easeInBounce(0.25), 0.01);
        assertEquals(0.23, Easing.easeInBounce(0.5), 0.01);
        assertEquals(0.52, Easing.easeInBounce(0.75), 0.01);
        assertEquals(1.0, Easing.easeInBounce(1), 0.01);
    }

    @Test
    public void easeOutBounce() {
        assertEquals(0.0, Easing.easeOutBounce(0), 0.01);
        assertEquals(0.47, Easing.easeOutBounce(0.25), 0.01);
        assertEquals(0.76, Easing.easeOutBounce(0.5), 0.01);
        assertEquals(0.97, Easing.easeOutBounce(0.75), 0.01);
        assertEquals(1.0, Easing.easeOutBounce(1), 0.01);
    }

    @Test
    public void easeInOutBounce() {
        assertEquals(0.0, Easing.easeInOutBounce(0), 0.01);
        assertEquals(0.11, Easing.easeInOutBounce(0.25), 0.01);
        assertEquals(0.50, Easing.easeInOutBounce(0.5), 0.01);
        assertEquals(0.88, Easing.easeInOutBounce(0.75), 0.01);
        assertEquals(1.0, Easing.easeInOutBounce(1), 0.01);
    }
}