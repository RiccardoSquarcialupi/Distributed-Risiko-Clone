import io.vertx.core.AbstractVerticle;
import io.vertx.core.Launcher;

public class ClientLauncher extends AbstractVerticle{

    public static void main(final String[] args) {
        Launcher.executeCommand("run", ClientLauncher.class.getName());
    }
}
