import java.io.IOException;

public class Launcher {
    public static void main(String[] args) throws IOException {
        ClientServerPart.main();
        System.out.println("Starting Client?");
        ClientWebClientPart.main();
    }
}
