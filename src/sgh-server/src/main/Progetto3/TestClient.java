package Progetto3;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonObject;

public class TestClient extends AbstractVerticle {
	
	public static void main(String[] args) {		
	
		String host = "03a2866e.ngrok.io";
		int port = 80;

		Vertx vertx = Vertx.vertx();
		HttpClient client = vertx.createHttpClient();
		int um = 40;
		JsonObject item = new JsonObject().put("umidita", um);
		
		client.post(port, host, "/api/data", response -> {
			System.out.println("Received response with status code " + response.statusCode());
			response.bodyHandler(bodyHandler -> {
				System.out.println(bodyHandler.toString());
			});
		}).putHeader("content-type", "application/json").end(item.encodePrettily());
	}
	
}