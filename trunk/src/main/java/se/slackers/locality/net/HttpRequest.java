package se.slackers.locality.net;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import se.slackers.locality.exception.IllegalRequestException;

public class HttpRequest {
	private static final Pattern getRegexp = Pattern.compile("get\\s+([^\\s]+).*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private String request;
	
	public HttpRequest(String request) {
		this.request = request;
	}
	
	public String getRequestPath() throws IllegalRequestException {
		Matcher matcher = getRegexp.matcher(request);
		
		if  (matcher.matches()) {
			return matcher.group(1);
		}
		
		throw new IllegalRequestException("Illegal request ["+request+"]");
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return request;
	}
}
