package se.slackers.jss.mediastream;

import java.io.InputStream;

/**
 * BaseClass for classes that stream audio data.
 * @author bysse
 *
 */
abstract public class MediaStream {	
	private InputStream inputStream;

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}	
}
