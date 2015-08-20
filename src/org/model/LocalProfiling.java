package org.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import org.object.PointObject;
import org.object.UserObject;
import org.object.VenueObject;
import org.utils.Utils;

/**
 * 
 * @author tndoan
 *
 */
public class LocalProfiling extends UDIModel{

	public LocalProfiling(String uFile, String fFile, String tFile, String vFile)
			throws IOException {
		super(uFile, fFile, tFile, vFile);
	}
	
	/**
	 * 
	 * @param id	user id
	 * @return
	 */
	public UserObject getUserLocation(String id){
		UserObject uObj = uMap.get(id);
		PointObject home = uObj.getHome();
		if (home != null) // if home has been updated, home location and influence scope are also learned
			return uObj;
		
		// if user does not have home, we need to learn it
		ArrayList<String> friends = uObj.getFriends();
//		double llh = 0.0;
		
		// update influence scope of friend
		updateFriends(friends);
		
		
		// update the influence scope of venue
		ArrayList<String> venues = uObj.getListOfVenues();
		updateVenues(venues);
		
		// update the location and influence scope of user
		boolean convergence = false;
		int iter = 0;
		
		double oLat = 0.0; double oLng = 0.0;
		
		while (!convergence){ // iterate until convergence
			iter++;
			double scope = uObj.getScope();
			
			// update latitude and longitude
			double n1 = 0.0; double n2 = 0.0;
			double d1 = 0.0; double d2 = 0.0;

			
			ArrayList<String> allVenues = uObj.getListOfVenues();
			for (String venue : allVenues){
				VenueObject vObj = vMap.get(venue);
				n1 += ((double) vObj.getNumberOfTweet(id)) * vObj.getLocation().getLat() / vObj.getScope();
				d1 += ((double) vObj.getNumberOfTweet(id)) / vObj.getScope();
				n1 += ((double) vObj.getNumberOfTweet(id)) * vObj.getLocation().getLng() / vObj.getScope();
				d1 += ((double) vObj.getNumberOfTweet(id)) / vObj.getScope();
			}
			
			ArrayList<String> followers = uObj.getFollowers();
			
			for (String f : followers) {
				UserObject fObj = uMap.get(f);
				PointObject fHome = fObj.getHome();
				if (fHome == null)
					continue;
				
				n1 += fHome.getLat() / scope;
				d1 += 1.0 / scope;
				
				n2 += fHome.getLng() / scope;
				d2 += 1.0 / scope;
			}
			
			for (String f : friends) {
				UserObject fObj = uMap.get(f);
				PointObject fHome = fObj.getHome();
				if (fHome == null)
					continue;
				
				n1 += fHome.getLat() / fObj.getScope();
				d1 += 1.0 / fObj.getScope();
				
				n2 += fHome.getLng() / fObj.getScope();
				d2 += 1.0 / fObj.getScope();
			}
				
			double lat = n1 / d1;
			double lng = n2 / d2;
			
			// update scope			
			double numerator = 0.0;
			double denominator = 0.0;
			
			for (String user : followers){
				UserObject fObj = uMap.get(user);
				PointObject fHome = fObj.getHome();
				if (fHome == null)
					continue; // we dont care 
				
				denominator += 1.0;
				numerator += Utils.calSqDistance(lat, lng, fHome.getLat(), fHome.getLng());
			}
			
			scope = numerator / (denominator * 2.0);
			uObj.updateScope(scope);
			uObj.updateHome(lat, lng);
			
			// check convergence
			double new_llh = calculateLLH(id);
//			if (Math.abs(llh - new_llh) < 0.001) {
//				convergence = true;
//			} else 
//				llh = new_llh;
			
			System.out.println(Utils.calSqDistance(lat, lng, oLat, oLng));
			if (Utils.calSqDistance(lat, lng, oLat, oLng) < 10){
				convergence = true;
			} else {
				oLat = lat;
				oLng = lng;
			}
				
			System.out.println(new_llh);
			System.out.println(scope);
			System.out.println(iter);
			System.out.println("===============");
		}
		
		return uObj;
	}
	
	/**
	 * update the influence scope of friends of user. However, we ignore friends who do not have exact location.
	 * @param friends	list of friends
	 */
	private void updateFriends(ArrayList<String> friends){
		for (String friend : friends){
			UserObject fObj = uMap.get(friend);
			PointObject fHome = fObj.getHome();
			if (fHome == null)
				// we dont care about friend without home location
				continue;
			
			ArrayList<String> fFollowers = fObj.getFollowers();
			
			double scope = 0; // it is square of sigma
			int count = 0; // count numbers of users who have home location
			for (String follower : fFollowers) {
				UserObject followerObj = uMap.get(follower);
				PointObject foFHome =  followerObj.getHome();
				if (foFHome == null)
					continue; // we dont care followers who dont have home location
				count++;
				
				scope += Utils.calSqDistance(fHome, foFHome);
			}
			
			scope /= (double) (2 * count);
			
			fObj.updateScope(scope);
		}
	}
	
	/**
	 * update the influence scope of venues in the given list
	 * @param venues	list of venues
	 */
	private void updateVenues(ArrayList<String> venues){
		for (String venue : venues){
			VenueObject vObj = vMap.get(venue);
			
			PointObject vHome = vObj.getLocation();
			Set<String> setOfUsers = vObj.getAllUsers();
			
			double numerator = 0.0;
			double denominator = 0.0;
			
			for (String user : setOfUsers) {
				UserObject uuObj = uMap.get(user);
				PointObject uuHome = uuObj.getHome();
				if (uuHome == null)
					continue;
				
				double w = (double)vObj.getNumberOfTweet(user);
				numerator += w * Utils.calSqDistance(uuHome, vHome);
				denominator += w;
			}
			
			double scope = numerator / denominator;
			vObj.updateScope(scope);
		}
	}
	
	/**
	 * Calculate the likelihood of this user. Constants are omitted (such as \pi)
	 * @param userId	user id
	 * @return			value of log likelihood
	 */
	private double calculateLLH(String userId){
		double result = 0.0;
		
		UserObject uObj = uMap.get(userId);
		double uScope = uObj.getScope();
		
		ArrayList<String> followers = uObj.getFollowers();
		for(String follower : followers) {
			
			UserObject fObj = uMap.get(follower);
			if (fObj.getHome() == null)
				continue; // we dont care
			
			result += - Math.log(uScope) - Utils.calSqDistance(uObj.getHome(), fObj.getHome()) / (2.0 * uScope);
		}
		
		ArrayList<String> friends = uObj.getFriends();
		for (String friend : friends){
			UserObject fObj = uMap.get(friend);
			if (fObj.getHome() == null)
				continue;
			double scope = fObj.getScope();
			result += - Math.log(scope) - Utils.calSqDistance(fObj.getHome(), uObj.getHome()) / (2.0 * scope);
		}
		
		ArrayList<String> venues = uObj.getListOfVenues();
		for(String venue : venues){
			VenueObject vObj = vMap.get(venue);
			double scope = vObj.getScope();
			double w = (double) vObj.getNumberOfTweet(userId);
			double v = - Math.log(scope) - Utils.calSqDistance(vObj.getLocation(), uObj.getHome()) / (2.0 * scope * scope);
			result += w * v;
		}
		
		return result;
	}

}