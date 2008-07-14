package se.slackers.locality.exception;

/**
 * Encapsules an exception in a RuntimeException.
 * @author bysse
 *
 */
public class EncapsuledExceptionRuntimException extends RuntimeException {
	private static final long serialVersionUID = 794221656425404393L;
	
	private Exception exception = null;
	
	public EncapsuledExceptionRuntimException(Exception exception) {
		this.exception = exception;
	}
	
	public void rethrow() throws Exception {
		throw this.exception;
	}
}
