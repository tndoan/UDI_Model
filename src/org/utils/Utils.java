package org.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.object.PointObject;

/**
 * 
 * @author tndoan
 *
 */
public class Utils {
	
	/**
	 * 
	 * @param p1
	 * @param p2
	 * @return 		square Euclidean distance between 2 points
	 */
	public static double calSqDistance(PointObject p1, PointObject p2){
		return calSqDistance(p1.getLat(), p1.getLng(), p2.getLat(), p2.getLng());
	}
	
	/**
	 * 
	 * @param lat1
	 * @param lng1
	 * @param lat2
	 * @param lng2
	 * @return		square Euclidean distance between 2 points given their latitude and longitude
	 */
	public static double calSqDistance(double lat1, double lng1, double lat2, double lng2){
		return (lat1 - lat2) * (lat1 - lat2) + (lng1 - lng2) * (lng1 - lng2);
	}
	
	/**
	 * 
	 * @param friendshipMap
	 * @return				HashMap whose key is user, value is his followers
	 */
	public static HashMap<String, ArrayList<String>> getFollowerMap(HashMap<String, ArrayList<String>> friendshipMap){
		HashMap<String, ArrayList<String>> result = new HashMap<>();
		
		Set<String> users = friendshipMap.keySet();
		
		for(String user : users){
			ArrayList<String> friends = friendshipMap.get(user);
			
			for (String friend : friends){
				ArrayList<String> lOfFollowers = result.get(friend);
				
				if (lOfFollowers == null){
					lOfFollowers = new ArrayList<>();
					result.put(friend, lOfFollowers);
				}
				
				lOfFollowers.add(user);
			}
		}
		
		return result;
	}
}
