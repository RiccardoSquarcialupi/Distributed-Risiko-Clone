package app;

import io.vertx.core.Future;

import static org.junit.jupiter.api.Assertions.fail;

public class Utils {
    public static void waitForCompletion(Future<?> fut) {
        while (!fut.isComplete()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        } // Deliberately busy waiting.
        if (fut.failed()) {
            fail(fut.cause());
        }
    }
}
