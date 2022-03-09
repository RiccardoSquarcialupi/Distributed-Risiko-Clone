import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.ext.web.client.WebClient;

public class ClientWebClientPart extends AbstractVerticle {
    private final WebClient client;

    public ClientWebClientPart(){
        this.client = WebClient.create(Vertx.vertx());
    }

    public void joinLobby(){
        this.client
                .post(8080, "127.0.0.1", "/client/lobby/clients")
                .send()
                .onSuccess(response -> System.out
                        .println("Received response with status code" + response.statusCode()))
                .onFailure(err ->
                        System.out.println("Something went wrong " + err.getMessage()));

    }

}
