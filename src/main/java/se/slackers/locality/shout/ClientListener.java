package se.slackers.locality.shout;

public interface ClientListener {
	/**
	 * Called whenever a new ShoutRunnable is started to handle a client connection.
	 */
	public void clientStartStreaming();
	
	/**
	 * Called when a a ShoutRunnable is stopped.
	 */
	public void clientStopsStreaming();
	
}
