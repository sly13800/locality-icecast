package se.slackers.locality.shout;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

import se.slackers.locality.shout.manager.ShoutRequestManager;

/**
 * 
 * @author bysse
 *
 */
public class ShoutServer implements Runnable {
	private static Logger log = Logger.getLogger(ShoutServer.class);
	private ServerSocket serverSocket;
	private ShoutThreadPool threadPool;
	private ShoutRequestManager requestManager;
	
	protected int serverPort = 8001;
	protected boolean exitServer;

	public void start() {
		Thread thread = new Thread(this, "SoutServer");		
		thread.start();
	}

	public void run() {
		log.info("Server started");		
		
		//.. create a listening socket
		try {
			serverSocket = new ServerSocket(serverPort);
		} catch (IOException e) {
			log.error(e);
			log.info("Server stopped");
			return;
		}
		
		//.. create the threads
		threadPool = new ShoutThreadPool(5, "ShoutThread-");
		
		//.. main loop
		setExitServer(false);
		while (!isExitServer()) {
			try {
				Socket clientSocket = serverSocket.accept();
				
				ShoutRunnable client = new ShoutRunnable(requestManager, clientSocket);
				threadPool.startShoutRunnable(client);
			} catch (IOException e) {
				e.printStackTrace();
				log.error(e);
				
				sleep(1000);
			}
		}
		
		threadPool.shutdown();
		
		setExitServer(true);
		log.info("Server stopped");
	}

	private void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			log.error(e);
		}
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public synchronized boolean isExitServer() {
		return exitServer;
	}

	public synchronized void setExitServer(boolean exitServer) {
		this.exitServer = exitServer;
	}
		
	public void setRequestManager(ShoutRequestManager requestManager) {
		this.requestManager = requestManager;
	}	
}
