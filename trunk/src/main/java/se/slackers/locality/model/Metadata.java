package se.slackers.locality.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds some basic information about a media file.
 * @author bysse
 *
 */
public class Metadata {
	private Map<MetadataType, String> data = new HashMap<MetadataType, String>();
	
	public static Metadata create(String artist, String album, String title) {
		Metadata info = new Metadata();
		
		info.set(MetadataType.ARTIST, artist);
		info.set(MetadataType.ALBUM, album);
		info.set(MetadataType.TITLE, title);
		
		return info;
	}

	public void set(MetadataType type, String value) {
		data.put(type, value);
	}
	
	public boolean has(MetadataType type) {
		return data.containsKey(type);
	}
	
	public String get(MetadataType type) {
		return data.get(type);
	}
	
	@Override
	public String toString() {
		StringBuffer builder = new StringBuffer();
		
		builder.append("[");
		for (MetadataType type : data.keySet()) {
			builder.append(data.get(type));
			builder.append(", ");
		}
		if (builder.length() > 0) {
			builder.setLength(builder.length()-1);
		}
		builder.append("]");
		
		return builder.toString();
	}
}
