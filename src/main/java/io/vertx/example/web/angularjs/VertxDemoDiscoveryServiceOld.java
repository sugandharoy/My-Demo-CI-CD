package io.vertx.example.web.angularjs;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.example.util.Runner;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.net.Inet4Address;
import java.util.LinkedList;
import java.util.List;

/*
 * @author <a href="mailto:pmlopes@gmail.com">Paulo Lopes</a>
 */
public class VertxDemoDiscoveryServiceOld extends AbstractVerticle {

	// Convenience method so you can run it in your IDE
	public static void main(String[] args) {
		Runner.runExample(VertxDemoDiscoveryServiceOld.class);
	}

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
//		loadData(mongo);
		DiscoveryResponseListener listener = new DiscoveryResponseListener(vertx);
		new Thread(listener).start();

		Router router = Router.router(vertx);

		router.route().handler(BodyHandler.create());

		// define some REST API
		router.get("/api/discovery").handler(
				ctx -> {
					mongo.find(
							"devices",
							new JsonObject(),
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

								System.out.println(" DB Look Up" + lookup.result());
								for (JsonObject o : lookup.result()) {
									json.add(o);
								}

								ctx.response().putHeader(
										HttpHeaders.CONTENT_TYPE,
										"application/json");
								ctx.response().end(json.encode());
							});
				});

		router.get("/api/discovery/:id").handler(
				ctx -> {
//					mongo.findOne(
//							"users",
//							new JsonObject().put("_id",
//									ctx.request().getParam("id")), null,
//							lookup -> {
//								// error handling
//							if (lookup.failed()) {
//								ctx.fail(500);
//								return;
//							}
//
//							JsonObject user = lookup.result();
//
//							if (user == null) {
//								ctx.fail(404);
//							} else {
//								ctx.response().putHeader(
//										HttpHeaders.CONTENT_TYPE,
//										"application/json");
//								ctx.response().end(user.encode());
//							}
//						});
				});

		router.post("/api/discovery")
				.handler(ctx -> {
					 JsonObject discoveryJSON = ctx.getBodyAsJson();
					// ctx.setUser(newUser);
						JsonArray json = new JsonArray();
						// JsonObject dummyDevice1= new
						// JsonObject().put("tofinoid", "TOFINOID");
						// dummyDevice1.put("hostname", "9.9.9.7");
						// dummyDevice1.put("startip",
						// ctx.getBodyAsJson().getString("startip"));
						// dummyDevice1.put("endip",
						// ctx.getBodyAsJson().getString("endip"));
						//
						// JsonObject dummyDevice2 = new
						// JsonObject().put("tofinoid", "TOFINOID1");
						// dummyDevice2.put("hostname", "9.9.9.8");
						// dummyDevice2.put("startip",
						// ctx.getBodyAsJson().getString("startip"));
						// dummyDevice2.put("endip",
						// ctx.getBodyAsJson().getString("endip"));
						// json.add(dummyDevice1);
						// json.add(dummyDevice2);
						
						// get the start and END IP fro object came from UI
						
						String startIP = discoveryJSON.getString("startip");
						String endIP = discoveryJSON.getString("endip");


						try {
							StartDiscoveryUtils.sendStartDiscoveryData(vertx, 
									(Inet4Address) Inet4Address
											.getByName(startIP),
									(Inet4Address) Inet4Address
											.getByName(endIP), DEFAULT_SERVER_PORT,
									(Inet4Address) Inet4Address
											.getByName("localhost"), // change the source and target in actual
									(Inet4Address) Inet4Address
											.getByName("localhost"));
						} catch (Exception e) {
							e.printStackTrace();
							System.out
									.println(" Got Exception while sending the packet to on premise client");
						}

						// commenting for now

						// for(int i=0; i<json.size(); i++){
						// JsonObject newUser=json.getJsonObject(i);
						//
						// mongo.findOne("users", new
						// JsonObject().put("tofinoid",
						// newUser.getString("tofinoid")), null, lookup -> {
						// // error handling
						// if (lookup.failed()) {
						// ctx.fail(500);
						// return;
						// }
						//
						// JsonObject user = lookup.result();
						//
						// if (user != null) {
						// // already exists
						// ctx.fail(500);
						// } else {
						// mongo.insert("users", newUser, insert -> {
						// // error handling
						// if (insert.failed()) {
						// ctx.fail(500);
						// return;
						// }
						//
						// // add the generated id to the user object
						// newUser.put("_id", insert.result());
						//
						// });
						//
						// }
						// });

						// }
						try {
							Thread.sleep(4000);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						ctx.response().putHeader(HttpHeaders.CONTENT_TYPE,
								"application/json");
						ctx.response().end(json.encode());
					});

		router.put("/api/discovery/:id").handler(
				ctx -> {
//					mongo.findOne("users", new JsonObject().put("_id", ctx
//							.request().getParam("id")), null, lookup -> {
//						// error handling
//							if (lookup.failed()) {
//								ctx.fail(500);
//								return;
//							}
//
//							JsonObject user = lookup.result();
//
//							if (user == null) {
//								// does not exist
//							ctx.fail(404);
//						} else {
//
//							// update the user properties
//							JsonObject update = ctx.getBodyAsJson();
//
//							user.put("tofinoid", update.getString("tofinoid"));
//							// user.put("firstName",
//							// update.getString("firstName"));
//							// user.put("lastName",
//							// update.getString("lastName"));
//							// user.put("address", update.getString("address"));
//
//							mongo.replace("users",
//									new JsonObject().put("_id", ctx.request()
//											.getParam("id")),
//									user,
//									replace -> {
//										// error handling
//									if (replace.failed()) {
//										ctx.fail(500);
//										return;
//									}
//
//									ctx.response().putHeader(
//											HttpHeaders.CONTENT_TYPE,
//											"application/json");
//									ctx.response().end(user.encode());
//								});
//						}
//					});
				});

		router.delete("/api/discovery/:id").handler(
				ctx -> {
//					mongo.findOne("users", new JsonObject().put("_id", ctx
//							.request().getParam("id")), null, lookup -> {
//						// error handling
//							if (lookup.failed()) {
//								ctx.fail(500);
//								return;
//							}
//
//							JsonObject user = lookup.result();
//
//							if (user == null) {
//								// does not exist
//							ctx.fail(404);
//						} else {
//
//							mongo.remove("users", new JsonObject().put("_id",
//									ctx.request().getParam("id")), remove -> {
//								// error handling
//									if (remove.failed()) {
//										ctx.fail(500);
//										return;
//									}
//
//									ctx.response().setStatusCode(204);
//									ctx.response().end();
//								});
//						}
//					});
				});

		// Create a router endpoint for the static content.
		router.route().handler(StaticHandler.create());

		vertx.createHttpServer().requestHandler(router::accept).listen(9800);
	}

	private void loadData(MongoClient db) {
		db.dropCollection(
				"users",
				drop -> {
					if (drop.failed()) {
						throw new RuntimeException(drop.cause());
					}

					List<JsonObject> users = new LinkedList<>();

					users.add(new JsonObject().put("tofinoid", "pmlopes")
							.put("hostname", "Paulo").put("startip", "Lopes")
							.put("endip", "The Netherlands"));

					users.add(new JsonObject().put("tofinoid", "timfox")
							.put("hostname", "Tim").put("startip", "Fox")
							.put("endip", "The Moon"));

					for (JsonObject user : users) {
						db.insert("users", user, res -> {
							System.out.println("inserted " + user.encode());
						});
					}
				});
	}
}
