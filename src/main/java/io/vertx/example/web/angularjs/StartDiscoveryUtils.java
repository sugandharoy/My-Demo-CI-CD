package io.vertx.example.web.angularjs;

import java.net.Inet4Address;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import io.vertx.ext.eventbus.bridge.tcp.impl.protocol.FrameHelper;

public class StartDiscoveryUtils {

	private static final String SOURCE2 = "source";
	private static final String END_IP = "endIP";
	private static final String START_IP = "startIP";
	private static final String SAMPLE_DISCOVERY_REQUEST = "sample.discovery.request";

	public static void sendStartDiscoveryData(Vertx vertx, Inet4Address address1,
			Inet4Address address2, int port, Inet4Address targetHost,
			Inet4Address source) {
		Vertx vertx1 = Vertx.vertx(); // using a diff vertex

		// Send a request and get a response
		NetClient client = vertx1.createNetClient();

		// NetClient client = vertx.createNetClient(new NetClientOptions()
		// .setSsl(false)
		// .setTrustAll(true)
		// .setKeyStoreOptions(
		// new JksOptions().setPath("client.keystore")
		// .setPassword("wibble")));
		// final Async async = context.async();

		// vertx.eventBus().localConsumer("test", (Message<JsonObject> msg) -> {
		// client.close();
		// // async.complete();
		// });

		client.connect(port, targetHost.getHostAddress(),
				new Handler<AsyncResult<NetSocket>>() {

					@Override
					public void handle(AsyncResult<NetSocket> arg0) {
						if (arg0.failed()) {
							System.out.println(" Could not make connection");
						}
						NetSocket socket = arg0.result();
						if (socket != null) {
							FrameHelper.sendFrame(
									"send",
									SAMPLE_DISCOVERY_REQUEST,
									new JsonObject()
											.put(START_IP,
													address1.getHostAddress())
											// check
											// if
											// host
											// name
											// to be
											// used
											.put(END_IP,
													address2.getHostAddress())
											.put(SOURCE2,
													source.getHostAddress()),
									socket);

						}

					}
				});

		// client.connect(8000, "localhost", conn -> {
		// // context.assertFalse(conn.failed());
		//
		// NetSocket socket = conn.result();
		//
		// FrameHelper.sendFrame("send", "sample.dumb.inbox",
		// new JsonObject().put("value", "vert.x").put("check", "got"), socket);
		// });

	}

	public static void main(String args[]) {

		Vertx vertx = Vertx.vertx();

		// Send a request and get a response
		NetClient client = vertx.createNetClient();

		// NetClient client = vertx.createNetClient(new NetClientOptions()
		// .setSsl(false)
		// .setTrustAll(true)
		// .setKeyStoreOptions(
		// new JksOptions().setPath("client.keystore")
		// .setPassword("wibble")));
		// final Async async = context.async();

		// vertx.eventBus().localConsumer("test", (Message<JsonObject> msg) -> {
		// client.close();
		// // async.complete();
		// });

		client.connect(9232, "10.11.246.244",
				new Handler<AsyncResult<NetSocket>>() {

					@Override
					public void handle(AsyncResult<NetSocket> arg0) {
						if (arg0.failed()) {
							System.out.println(" Could not make connection");
						}
						NetSocket socket = arg0.result();
						if (socket != null) {
							FrameHelper.sendFrame("send", "sample.dumb.inbox",
									new JsonObject().put("value", "vert.x")
											.put("check", "got"), socket);

						}

					}
				});

		// client.connect(8000, "localhost", conn -> {
		// // context.assertFalse(conn.failed());
		//
		// NetSocket socket = conn.result();
		//
		// FrameHelper.sendFrame("send", "sample.dumb.inbox",
		// new JsonObject().put("value", "vert.x").put("check", "got"), socket);
		// });
	}

}
