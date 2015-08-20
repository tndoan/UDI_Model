package org.object;

import java.util.HashMap;
import java.util.Set;

public class VenueObject {
	private double scope;
	private HashMap<String, Integer> tweetMap;
	private PointObject location;
	
	public VenueObject(double scope, HashMap<String, Integer> tweetMap, PointObject location){
		this.scope = scope;
		this.tweetMap = tweetMap;
		this.location = location;
	}
	
	public void updateScope(double scope){
		this.scope = scope;
	}
	
	/**
	 * get all users who have done check-ins in this venue
	 * @return	set of users
	 */
	public Set<String> getAllUsers(){
		return tweetMap.keySet();
	}
	
	/**
	 * 
	 * @param userId	id of user
	 * @return			number of times this user has done check-in to this venue
	 */
	public int getNumberOfTweet(String userId){
		Integer num = tweetMap.get(userId);
		if (num == null)
			return 0;
		return num;
	}

	public double getScope() {
		return scope;
	}

	public PointObject getLocation() {
		return location;
	}
}
