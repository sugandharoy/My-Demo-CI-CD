package io.vertx.example.web.angularjs;

import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
//import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public class VertxDemoDiscoveryRequestLoadBalancerVerticle {

	public static int DEFAULT_CLIENT_PORT = 9232;

	public static int DEFAULT_SERVER_PORT_1 = 9234;
	// public static int DEFAULT_SERVER_PORT_2 = 9235;

	AtomicInteger deployCount = new AtomicInteger();
	AtomicInteger undeployCount = new AtomicInteger();
	AtomicInteger deployHandlerCount = new AtomicInteger();
	AtomicInteger undeployHandlerCount = new AtomicInteger();

	public static void main(String... args) throws Throwable {

		Vertx vertx = Vertx.vertx();
		System.out.println("hello >>" + vertx.toString());

		// JsonObject config = new JsonObject().put("server.port",
		// DEFAULT_SERVER_PORT_1);
		// config.put("did", "1");
		DeploymentOptions options = new DeploymentOptions();
		options.setInstances(1);
		// options.setHa(false);

		System.out.println("Options: " + options);
		vertx.deployVerticle(VertxDemoDiscoveryService.class.getName(),
				options, new Handler<AsyncResult<String>>() {

					@Override
					public void handle(AsyncResult<String> event) {

						if (event.failed()) {
							System.out.println(" Bulk Deployment got failed");

						} else {
							System.out.println("Result is " + event.result());
						}

					}
				});

		Thread.sleep(2000);

		Iterator<String> iterator = vertx.deploymentIDs().iterator();
		while (iterator.hasNext()) {
			System.out.println(" Deployment ID " + iterator.next());
		}

		/*
		 * config = new JsonObject().put("server.port", DEFAULT_SERVER_PORT_1);
		 * config.put("did", "2"); options = new
		 * DeploymentOptions().setConfig(config);
		 * vertx.deployVerticle(DiscoveryRequestListener
		 * .class.getName(),options);
		 */
	}

}
