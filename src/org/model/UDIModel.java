package org.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.object.PointObject;
import org.object.TweetObject;
import org.object.UserObject;
import org.object.VenueObject;
import org.utils.ReadFiles;
import org.utils.Utils;

public class UDIModel {

	/**
	 * key is venue id, value is venue object
	 */
	protected HashMap<String, VenueObject> vMap;
	
	/**
	 * key is user id, value is user object
	 */
	protected HashMap<String, UserObject> uMap;
	
	/**
	 * 
	 * @param uFile
	 * @param fFile
	 * @param tFile
	 * @param vFile
	 * @throws IOException
	 */
	public UDIModel(String uFile, String fFile, String tFile, String vFile) throws IOException {
		HashMap<String, PointObject> userProfiles = ReadFiles.readProfile(uFile);
		HashMap<String, ArrayList<String>> friendship = ReadFiles.readFriendship(fFile);
		HashMap<String, ArrayList<TweetObject>> tweetsData = ReadFiles.readTweet(tFile);
		HashMap<String, PointObject> venueProfiles = ReadFiles.readProfile(vFile);
		HashMap<String, ArrayList<String>> followersMap = Utils.getFollowerMap(friendship);
		
		// init
		uMap = new HashMap<>();
		vMap = new HashMap<>();
		
		// construct user map
		Set<String> setOfUsers = userProfiles.keySet();
		
		for (String user : setOfUsers){
			PointObject home = userProfiles.get(user); // user home
			
			ArrayList<String> friends = friendship.get(user);
			ArrayList<String> followers = followersMap.get(user);
			
			UserObject uObj = new UserObject(10000.0, followers, friends, home);
			
			uMap.put(user, uObj);
		}
		
		// construct venue map
		Set<String> setOfVenues = venueProfiles.keySet(); // set of all venues. Assume that they all have location
		for (String venue : setOfVenues){
			PointObject vLoc = venueProfiles.get(venue); // location of venue
			
			HashMap<String, Integer> map = new HashMap<>();
			ArrayList<TweetObject> l = tweetsData.get(venue);
			for (TweetObject to : l){
				int numTweets = to.getNumTweet();
				String uId = to.getUserId();
				map.put(uId, numTweets);
				UserObject u = uMap.get(uId);
				u.addVenue(venue);
			}
			
			VenueObject vo = new VenueObject(1.0, map, vLoc);
			vMap.put(venue, vo);
		}
	}
	
	/**
	 * get the home location of user
	 * @param userId	user id
	 * @return			Point object of user
	 */
	public PointObject getHome(String userId){
		UserObject uObj = uMap.get(userId);
		return uObj.getHome();
	}
	
	/**
	 * 
	 * @param userId
	 * @return			influence scope of user
	 */
	public double getUserScope(String userId){
		return uMap.get(userId).getScope();
	}
	
	/**
	 * 
	 * @param venueId
	 * @return			influence scope of venue
	 */
	public double getVenueScope(String venueId){
		return vMap.get(venueId).getScope();
	}
	
}
