package se.slackers.locality.media.reader;

public class ByteUtils {
	/**
	 * Converts a byte to a string of ones and zeroes.
	 * @param b
	 * @return
	 */
	public static String byte2String(byte b) {
		StringBuffer sb = new StringBuffer();

		int value = 0x80;
		for (int i = 0; i < 8; i++) {
			if ((b & value) == 0) {
				sb.append("0");
			} else {
				sb.append("1");
			}
			value >>= 1;
		}

		return sb.toString();
	}
}
