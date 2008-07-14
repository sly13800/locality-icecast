package se.slackers.locality.net;
import junit.framework.TestCase;

import org.junit.Test;

import se.slackers.locality.net.HttpRequest;



public class HttpRequestTest extends TestCase {
	
	@Test
	public void testRequest() throws Exception {
		String httpRequest = "GET /media.mp3 HTTP/1.0\n\rHost: localhost\n\rUser-Agent: xmms/1.2.10\n\rIcy-MetaData:1\n\r\n\r";		
		HttpRequest request = new HttpRequest(httpRequest);		
		assertEquals("/media.mp3", request.getRequestPath());
	}

}
