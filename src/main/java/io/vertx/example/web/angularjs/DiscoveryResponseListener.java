package io.vertx.example.web.angularjs;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.BridgeOptions;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.eventbus.bridge.tcp.TcpEventBusBridge;
import io.vertx.ext.mongo.MongoClient;

public class DiscoveryResponseListener implements Runnable {

	private static final String SAMPLE = "sample.*";
	private static final String SAMPLE_DISCOVERY_RESPONSE = "sample.discovery.response";
	private Vertx vertx = null;

	private MongoClient mongo;

	public DiscoveryResponseListener(Vertx vertx) {
		super();
		this.vertx = vertx;
		mongo = MongoClient.createShared(vertx,
				new JsonObject().put("db_name", "test"));
	}

	private void startDiscoveryResponseListener() {
//		Vertx vertx = Vertx.vertx();
		TcpEventBusBridge bridge = TcpEventBusBridge
				.create(vertx, // Using another verticle for mesaging reading should not be dependent and can start in parallel
						new BridgeOptions().addInboundPermitted(
								new PermittedOptions().setAddressRegex(SAMPLE))
								.addOutboundPermitted(
										new PermittedOptions()
												.setAddressRegex(SAMPLE)));

		vertx.eventBus()
				.consumer(
						SAMPLE_DISCOVERY_RESPONSE,
						message -> {
							JsonObject body = (JsonObject) message.body();
							System.out.println(body.encodePrettily());
							System.out
									.println(" I got the message now persisting in data base");

							mongo.insert("devices", body,
									new Handler<AsyncResult<String>>() {

										@Override
										public void handle(
												AsyncResult<String> event) {
											System.out
													.println("Persited the devices after discovery response");
										}
									});
						});

		// MessageProducer<Object> tickPublisher =
		// vertx.eventBus().publisher("sample.clock.ticks");
		// vertx.setPeriodic(1000L, id -> {
		// tickPublisher.send(new JsonObject().put("tick", id));
		// });
		// vertx.eventBus().send(arg0, arg1, new
		// Handler<AsyncResult<Message<T>>>() {
		//
		// @Override
		// public <T> void handle(AsyncResult<Message<T>> arg0) {
		// // TODO Auto-generated method stub
		//
		// }
		// });

		// vertx.eventBus().consumer("sample.echo", message -> {
		// JsonObject body = (JsonObject) message.body();
		// System.out.println("Echoing: " + body.encodePrettily());
		// message.reply(body);
		// });

		bridge.listen(
				VertxDemoDiscoveryService.DEFAULT_CLIENT_PORT,
				result -> {
					if (result.failed()) {
						throw new RuntimeException(result.cause());
					} else {
						System.out
								.println("TCP Event Bus bridge running on port "
										+ VertxDemoDiscoveryService.DEFAULT_CLIENT_PORT);
					}
				});
	}

	@Override
	public void run() {
		startDiscoveryResponseListener();
	}

	public static void main(String... args) throws Throwable {

	}
}