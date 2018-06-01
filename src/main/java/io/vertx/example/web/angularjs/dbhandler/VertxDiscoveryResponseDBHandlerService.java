package io.vertx.example.web.angularjs.dbhandler;

import java.util.List;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.example.util.Runner;
import io.vertx.example.web.angularjs.VertxDemoDiscoveryService;
import io.vertx.ext.bridge.BridgeOptions;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.eventbus.bridge.tcp.TcpEventBusBridge;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.WriteOption;

public class VertxDiscoveryResponseDBHandlerService extends AbstractVerticle {

	private static final String SAMPLE = "sample.*";
	private static final String SAMPLE_DISCOVERY_RESPONSE = "sample.discovery.response";

	private MongoClient mongo;

	@Override
	public void start() throws Exception {

		mongo = MongoClient.createShared(vertx,
				new JsonObject().put("db_name", "test"));
		TcpEventBusBridge bridge = TcpEventBusBridge
				.create(vertx, // Using another verticle for mesaging reading
								// should not be dependent and can start in
								// parallel
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

							JsonObject findDevice = new JsonObject();
							findDevice.put("tofinoid",
									body.getString("tofinoid"));

							mongo.find(
									VertxDemoDiscoveryService.DEVICES_COLLECTION_NAME,
									findDevice,
									new Handler<AsyncResult<List<JsonObject>>>() {

										@Override
										public void handle(
												AsyncResult<List<JsonObject>> event) {
											if (event.result().size() == 0) {
												mongo.insert(
														VertxDemoDiscoveryService.DEVICES_COLLECTION_NAME,
														body,
														new Handler<AsyncResult<String>>() {

															@Override
															public void handle(
																	AsyncResult<String> event) {
																System.out
																		.println("Persited the devices after discovery response");
															}
														});
											} else {
												System.out
												.println("Deviced already existing dropping it");
											}

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

	// Convenience method so you can run it in your IDE
	public static void main(String[] args) {
		Runner.runExample(VertxDiscoveryResponseDBHandlerService.class);
	}

}
