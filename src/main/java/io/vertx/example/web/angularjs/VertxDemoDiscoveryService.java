package io.vertx.example.web.angularjs;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.ReadStream;
import io.vertx.example.util.Runner;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.net.Inet4Address;
import java.util.LinkedList;
import java.util.List;

/*
 * sharad.mishra
 */
public class VertxDemoDiscoveryService extends AbstractVerticle {

	public static final String DEVICES_COLLECTION_NAME = "discoveredDevices";

	// Convenience method so you can run it in your IDE
	public static void main(String[] args) {
		Runner.runExample(VertxDemoDiscoveryService.class);
	}
	
	
	
	
	
	
//sugandha first commit
	private MongoClient mongo;

	public static int DEFAULT_CLIENT_PORT = 9232;

	public static int DEFAULT_SERVER_PORT = 9234;

	@Override
	public void start() throws Exception {

		// Create a mongo client using all defaults (connect to localhost and
		// default port) using the database name "demo".
		mongo = MongoClient.createShared(vertx,
				new JsonObject().put("db_name", "test"));

		// the load function just populates some data on the storage
		// loadData(mongo);

		// DiscoveryResponseListener listener = new DiscoveryResponseListener(
		// vertx);
		// new Thread(listener).start();

		final JsonArray jsonArrayForDevices = new JsonArray(); // This array is
																// also being
																// used to
																// populate prev
																// post result
		// and we are avoiding a db hit.

		Router router = Router.router(vertx);

		router.route().handler(BodyHandler.create());

		router.get("/api/disco").handler(
				ctx -> {
					System.out.println("jsonArrayget size = "
							+ jsonArrayForDevices.size());
					ctx.response().putHeader(HttpHeaders.CONTENT_TYPE,
							"application/json");
					ctx.response().end(jsonArrayForDevices.encode());

				});

		// define some REST API
		router.get("/api/discovery").handler(
				ctx -> {
					System.out.println(" I came to fetch all devices " + this.toString());
					mongo.find(DEVICES_COLLECTION_NAME, new JsonObject(),
							lookup -> {
								// error handling
							if (lookup.failed()) {
								ctx.fail(500);
								return;
							}

							// now convert the list to a JsonArray because
							// it will be easier to encode the final object
							// as the response.
							final JsonArray json = new JsonArray();
							System.out.println("result of DB look up "
									+ lookup.result().size());

							for (JsonObject o : lookup.result()) {
								json.add(o);
							}

							ctx.response().putHeader(HttpHeaders.CONTENT_TYPE,
									"application/json");
							ctx.response().end(json.encode());
						});
				});

		router.post("/api/discovery")
				.handler(ctx -> {
					JsonObject discoveryJSON = ctx.getBodyAsJson();
					// ctx.setUser(newUser);
						jsonArrayForDevices.clear();

						// get the start and END IP fro object came from UI

						String startIP = discoveryJSON.getString("startip");
						String endIP = discoveryJSON.getString("endip");

						try {
							StartDiscoveryUtils.sendStartDiscoveryData(vertx,
									(Inet4Address) Inet4Address
											.getByName(startIP),
									(Inet4Address) Inet4Address
											.getByName(endIP),
									DEFAULT_SERVER_PORT,
									(Inet4Address) Inet4Address
											.getByName("localhost"), // change
																		// the
																		// source
																		// and
																		// target
																		// in
																		// actual
									(Inet4Address) Inet4Address
											.getByName("localhost"));
						} catch (Exception e) {
							e.printStackTrace();
							System.out
									.println(" Got Exception while sending the packet to on premise client");
						}

						try {
							Thread.sleep(2000);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						checkDBAndFillResponse(jsonArrayForDevices, ctx,
								startIP, endIP);

						System.out.println(" Checked DB and found "
								+ jsonArrayForDevices);

					});

		router.put("/api/discovery/:id").handler(ctx -> {
			// mongo.findOne("users", new JsonObject().put("_id", ctx
			// .request().getParam("id")), null, lookup -> {
			// // error handling
			// if (lookup.failed()) {
			// ctx.fail(500);
			// return;
			// }
			//
			// JsonObject user = lookup.result();
			//
			// if (user == null) {
			// // does not exist
			// ctx.fail(404);
			// } else {
			//
			// // update the user properties
			// JsonObject update = ctx.getBodyAsJson();
			//
			// user.put("tofinoid", update.getString("tofinoid"));
			// // user.put("firstName",
			// // update.getString("firstName"));
			// // user.put("lastName",
			// // update.getString("lastName"));
			// // user.put("address", update.getString("address"));
			//
			// mongo.replace("users",
			// new JsonObject().put("_id", ctx.request()
			// .getParam("id")),
			// user,
			// replace -> {
			// // error handling
			// if (replace.failed()) {
			// ctx.fail(500);
			// return;
			// }
			//
			// ctx.response().putHeader(
			// HttpHeaders.CONTENT_TYPE,
			// "application/json");
			// ctx.response().end(user.encode());
			// });
			// }
			// });
			});

		router.delete("/api/discovery/:id").handler(ctx -> {
			// mongo.findOne("users", new JsonObject().put("_id", ctx
			// .request().getParam("id")), null, lookup -> {
			// // error handling
			// if (lookup.failed()) {
			// ctx.fail(500);
			// return;
			// }
			//
			// JsonObject user = lookup.result();
			//
			// if (user == null) {
			// // does not exist
			// ctx.fail(404);
			// } else {
			//
			// mongo.remove("users", new JsonObject().put("_id",
			// ctx.request().getParam("id")), remove -> {
			// // error handling
			// if (remove.failed()) {
			// ctx.fail(500);
			// return;
			// }
			//
			// ctx.response().setStatusCode(204);
			// ctx.response().end();
			// });
			// }
			// });
			});

		// Create a router endpoint for the static content.
		router.route().handler(StaticHandler.create());

		vertx.createHttpServer().requestHandler(router::accept).listen(9298);
	}

	private void checkDBAndFillResponse(JsonArray jsonArray,
			RoutingContext ctx, String staString, String end) {

		JsonObject put = new JsonObject().put("startip", staString).put(
				"endip", end);
		System.out.println(" Searching " + put);

		// final CountDownLatch latch = new CountDownLatch(1);

		// useStreamToFindData(jsonArray, put);

		mongo.find(DEVICES_COLLECTION_NAME, put,
				new Handler<AsyncResult<List<JsonObject>>>() {

					@Override
					public void handle(AsyncResult<List<JsonObject>> event) {
						for (JsonObject obj : event.result()) {
							jsonArray.add(obj);
							System.out.println("Showing " + jsonArray);
						}
						// // latch.countDown();
						ctx.response().putHeader(HttpHeaders.CONTENT_TYPE,
								"application/json");
						System.out.println(" Again" + jsonArray);

						ctx.response().end(jsonArray.encode());

					}
				});
		// while (latch.getCount() != 0) {
		// System.out.println(" waiting for DB Query");

		// }

	}

	private void useStreamToFindData(JsonArray jsonArray, JsonObject put) {
		ReadStream<JsonObject> objects = mongo.findBatch(
				DEVICES_COLLECTION_NAME, put);

		objects.handler(new Handler<JsonObject>() {

			@Override
			public void handle(JsonObject event) {
				jsonArray.add(event);
				System.out.println(" Adding after result " + event);
			}
		});

		try {
			Thread.sleep(2000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void loadData(MongoClient db) {
		db.dropCollection(
				DEVICES_COLLECTION_NAME,
				drop -> {
					if (drop.failed()) {
						throw new RuntimeException(drop.cause());
					}

					List<JsonObject> devices = new LinkedList<>();

					devices.add(new JsonObject()
							.put("tofinoid", "AA:AA:AA:AB:BS:AA")
							.put("hostname", "4.4.4.9")
							.put("startip", "4.4.4.19")
							.put("endip", "4.4.4.11"));

					for (JsonObject device : devices) {
						db.insert(DEVICES_COLLECTION_NAME, device, res -> {
							System.out.println("inserted " + device.encode());
						});
					}
				});
	}
}
