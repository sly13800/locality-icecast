package se.slackers.locality.util;

import java.util.HashMap;
import java.util.Map;

public class OneShot {
	private static Map<String, Boolean> map = new HashMap<String, Boolean>();
	
	public static boolean test(String id) {
		if (map.containsKey(id)) {
			return false;
		}		
		map.put(id, Boolean.TRUE);		
		return true;
	}
}
