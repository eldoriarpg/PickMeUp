package de.eldoria.pickmeup.scheduler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ThrowBarHandlerTest {
    @Test
    public void calcTest() {
        for (int i = 0; i < 60; i++) {
            Assertions.assertEquals(ThrowBarHandler.calculateIndex(i), ThrowBarHandler.calculateIndex(60 - i));
        }
        Assertions.assertEquals(0, ThrowBarHandler.calculateIndex(0));
        Assertions.assertEquals(0, ThrowBarHandler.calculateIndex(1));
        Assertions.assertEquals(28, ThrowBarHandler.calculateIndex(29));
        Assertions.assertEquals(30, ThrowBarHandler.calculateIndex(30));
        Assertions.assertEquals(28, ThrowBarHandler.calculateIndex(31));
        Assertions.assertEquals(0, ThrowBarHandler.calculateIndex(60));
        Assertions.assertEquals(0, ThrowBarHandler.calculateIndex(59));
    }

    @Test
    public void forceStaysBoundedWhenHeldLong() {
        for (int ticks = 0; ticks < 10_000; ticks++) {
            double force = ThrowBarHandler.calculateForce(ticks);
            Assertions.assertTrue(force >= 0 && force <= 1, "force " + force + " at " + ticks + " ticks");
            Assertions.assertEquals(ThrowBarHandler.calculateForce(ticks % 60), force);
        }
    }
}
