import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.ext.web.client.WebClient;

public class ClientWebClientPart extends AbstractVerticle {
    public static void main(){
        Vertx vertx = Vertx.vertx();
        WebClient client = WebClient.create(Vertx.vertx());
        // Send a GET request
        client
                .post(8080, "127.0.0.1", "/client/lobby/clients")
                .send()
                .onSuccess(response -> System.out
                        .println("Received response with status code" + response.statusCode()))
                .onFailure(err ->
                        System.out.println("Something went wrong " + err.getMessage()));
    }
}
