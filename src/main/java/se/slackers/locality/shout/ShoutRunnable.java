package se.slackers.locality.shout;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import se.slackers.locality.data.FrameStorage;
import se.slackers.locality.data.FrameStorageEntry;
import se.slackers.locality.data.MetadataManager;
import se.slackers.locality.exception.EncapsuledExceptionRuntimException;
import se.slackers.locality.exception.FrameHasNotBeenLoadedException;
import se.slackers.locality.exception.FrameIsTooOldException;
import se.slackers.locality.exception.FrameStorageIsEmptyException;
import se.slackers.locality.media.Frame;
import se.slackers.locality.media.queue.MediaQueue;
import se.slackers.locality.net.HttpRequest;
import se.slackers.locality.shout.manager.ShoutRequestManager;

public class ShoutRunnable implements Runnable {
	private static Logger log = Logger.getLogger(ShoutRunnable.class);
	private static final int metadataInterval = 65536;
	
	private Socket socket;
	private ShoutRequestManager requestManager;
	private MetadataManager metadataManager;
	
	protected boolean exit;
	private List<WeakReference<ClientListener>> clientConnectionListeners = new ArrayList<WeakReference<ClientListener>>();

	public ShoutRunnable(ShoutRequestManager requestManager, Socket clientSocket) {
		this.socket = clientSocket;
		this.requestManager = requestManager;
		this.metadataManager = new MetadataManager();
	}	

	public void run() {
		log.info(Thread.currentThread().getName() + " started");
		try {
			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();
			
			HttpRequest request = readRequest(in);
			log.info("Received request: "+request);

			MediaQueue mediaQueue = null;
			try {
				mediaQueue = requestManager.processRequest(request);
			} catch (SecurityException e) {
				writeNegativeResponse(out);
				
				//TODO: Change the execution flow of this method
				throw new IOException("Change this code please");
			}
			
			sendStartStreamResponse(mediaQueue, out);
						
			mediaQueue.startProcessor();
			mediaQueue.getMediaQueueProcessor().addMediaQueueProcessorListener(metadataManager);
			
			metadataManager.setMetadata(mediaQueue.getMediaQueueProcessor().getCurrentMetadata());
			addClientConnectionListener(mediaQueue.getMediaQueueProcessor());
			
			fireClientStartsStreaming();

			FrameStorage storage = mediaQueue.getFrameStorage();

			FrameStorageEntry entry = null;
			long time = System.currentTimeMillis();
			int bytesSent = 0;
			int total = 0;
			
			while (true) {			
				try {
					entry = storage.find(time);		
					time = entry.getStopTime();
					
					// grab the next frame to send
					Frame frame = entry.getFrame();
					
					// Check if we have to send metadata
					if (bytesSent + frame.getSize() >= metadataInterval) {
						
						int sendBefore = metadataInterval - bytesSent;
						if (sendBefore > 0) {
							out.write(frame.getData(), 0, sendBefore);
						}
						
						byte [] metadata = metadataManager.getMetaData(mediaQueue);
						out.write(metadata);

						out.write(frame.getData(), sendBefore, frame.getSize()-sendBefore);
						bytesSent -= metadataInterval;							
					} else {
						// we don't need to send any meta data
						//log.debu
						out.write(entry.getFrame().getData(), 0, entry.getFrame().getSize());
					}
					
					bytesSent += frame.getSize();
					total += frame.getSize();
					
				} catch (FrameStorageIsEmptyException e) {
					// sleep for a while and let the MediaQueueProcessor fill the storage
					sleep(250);
				} catch (FrameIsTooOldException e) {
					// the client is lagging behind. Reset the time to the current system time.
					time = System.currentTimeMillis();
				} catch (FrameHasNotBeenLoadedException e) {
					// the client is too far ahead.
					//log.info("Client is too far ahead, sleeping 250 Ms");
					sleep(250);
				}
			}
			
		} catch(IOException e) {
			e.printStackTrace();
			// TODO: Fix error handling
		} catch(RuntimeException e) {
			e.printStackTrace();
			// TODO: Fix error handling			
		}		

		fireClientStopsStreaming();
		
		setExit(true);
		log.info(Thread.currentThread().getName() + " stopped");
	}

	private void writeNegativeResponse(OutputStream out) throws IOException {
		out.write( (new String("404 Not found")).getBytes() );
	}

	private void sendStartStreamResponse(MediaQueue mediaQueue, OutputStream out) throws IOException {
		StringBuilder response = new StringBuilder();
		response.append("HTTP/1.1 200 OK\r\nContent-Type: audio/mpeg\r\n");

		// add the stream name
		response.append("icy-name: ");
		response.append(mediaQueue.getName());
		response.append("\r\n");
		
		// add metadata information
		response.append("icy-metadata:1\r\n");
		response.append("icy-metaint:");
		response.append(metadataInterval);
		response.append("\r\n");
		
		response.append("\r\n");
		
		out.write(response.toString().getBytes());
	}

	/**
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	private HttpRequest readRequest(InputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();
		byte [] buffer = new byte[4096];
		int bytes;
		
		byte endSequence[] = new byte[] {13,10,13,10}; 
		
		while ( (bytes=in.read(buffer)) > 0) {
			sb.append(new String(buffer, 0, bytes));
			
			if (bytes > endSequence.length) {
				boolean foundSequence = true;
				for (int i=0;i<endSequence.length;i++) {
					foundSequence |= (endSequence[i] == buffer[bytes-i-1]);						
				}
				
				if (foundSequence) {
					break;
				}
			}
		}
		
		return new HttpRequest( sb.toString() );
	}
	
	/**
	 * Starts the client thread with the default name 'ShoutClientThread'.
	 *
	 */
	public void start() {
		start("ShoutClientThread");		
	}

	/**
	 * Starts the client thread.
	 * @param threadName Name of the thread
	 */
	public void start(String threadName) {
		Thread thread = new Thread(this, threadName);
		thread.start();		
	}

	public synchronized void setExit(boolean exit) {
		this.exit = exit;
	}

	public synchronized boolean isExit() {
		return exit;
	}
	
	public void addClientConnectionListener(ClientListener listener) {
		clientConnectionListeners .add(new WeakReference<ClientListener>(listener));		
	}

	public void removeClientConnectionListener(ClientListener listener) {
		clientConnectionListeners.remove(listener);		
	}	

	/**
	 * 
	 */
	private void fireClientStopsStreaming() {
		Iterator<WeakReference<ClientListener>> iterator = clientConnectionListeners.iterator();
		while (iterator.hasNext()) {
			WeakReference<ClientListener> ref = iterator.next();
			
			if (null == ref.get()) {
				iterator.remove();
			} else {
				ref.get().clientStopsStreaming();
			}
		}		
	}

	/**
	 * 
	 */
	private void fireClientStartsStreaming() {
		Iterator<WeakReference<ClientListener>> iterator = clientConnectionListeners.iterator();
		while (iterator.hasNext()) {
			WeakReference<ClientListener> ref = iterator.next();
			
			if (null == ref.get()) {
				iterator.remove();
			} else {
				ref.get().clientStartStreaming();
			}
		}
	}
	
	/**
	 * Sleep for a number of milliseconds and encapsule any exception in a EncapsuledExceptionRuntimException.
	 * @param ms Number of milliseconds to sleep
	 */
	private void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			throw new EncapsuledExceptionRuntimException(e);
		}
	}
}
