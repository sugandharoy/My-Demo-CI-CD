package io.vertx.example.web.angularjs;

import java.util.concurrent.atomic.AtomicInteger;

public class DiscoveryScan {
	
	 private static final AtomicInteger COUNTER = new AtomicInteger();

	private final int id;

	private String startIP;

	private String endIP;
    
	
    public DiscoveryScan() {
      this.id = COUNTER.getAndIncrement();
		// TODO Auto-generated constructor stub
	}


	public DiscoveryScan(String startIP, String endIP) {
	
		this.id = COUNTER.getAndIncrement();
	    this.startIP = startIP;
		this.endIP = endIP;
	}

	

	public static AtomicInteger getCounter() {
		return COUNTER;
	}

	public String getStartIP() {
		return startIP;
	}

	public void setStartIP(String startIP) {
		this.startIP = startIP;
	}

	public String getEndIP() {
		return endIP;
	}

	public void setEndIP(String endIP) {
		this.endIP = endIP;
	}

	public int getId() {
		return id;
	}

}
