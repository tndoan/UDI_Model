package org.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.object.PointObject;
import org.object.UserObject;
import org.object.VenueObject;
import org.utils.Utils;

public class GlobalProfiling extends UDIModel{

	public GlobalProfiling(String uFile, String fFile, String tFile,
			String vFile) throws IOException {
		super(uFile, fFile, tFile, vFile);
	}
	
	public void learnParameters() {
		// set of all users who do not have home location
		ArrayList<String> unknownHomeUsers = new ArrayList<>();
		
		Set<String> users = uMap.keySet();
		for (String user : users){
			UserObject uObj = uMap.get(user);
			
			if(uObj.getHome() == null)
				unknownHomeUsers.add(user);
		}
		
		double llh = 0.0;
		
		// key is venue id, value is current predicted home location of user
		HashMap<String, PointObject> predictedLoc = new HashMap<>();
		
		// init home location for each users in unknownHomeUsers
		// I initialize home locations of all users to Ho Chi Minh City, Vietnam :P
		for (String user : unknownHomeUsers){
			//10.738095, 106.654537
			UserObject u = uMap.get(user);
			u.updateHome(10.738095, 106.654537);
			predictedLoc.put(user, new PointObject(10.738095, 106.654537));
		}
		
		// outter loop
		boolean convergence = false;
		
		while (!convergence){
			// estimate influence scopes of all users
			updateInfluenceScopeOfAllUsers();
			
			// estimate influence scope of each venue
			updateScopeOfAllVenues();
			
			// Inner loop
			boolean conv = false;
			while (!conv){
				double distance = 0.0; 
				
				// update location of unknown-home users
				for (String user : unknownHomeUsers){
					PointObject oldPoint = predictedLoc.get(user);
					
					PointObject p = updateLocOfUser(user);
					
					distance += Utils.calSqDistance(oldPoint, p);
					
					predictedLoc.put(user, p);
				}
				
				// check convergence. All the home locations are stable
				if (distance < 10.0)
					conv = true;
			}
			
			// update home location of each unknown-home users
			for (String user : unknownHomeUsers){
				UserObject uObj = uMap.get(user);
				uObj.updateHome(predictedLoc.get(user));
			}
			
			// check convergence. terminate if the loglikelihood is stable			
			System.out.println("LLH:" + calculateLLH());
			double new_llh = calculateLLH();
			if (Math.abs(new_llh - llh) / Math.abs(llh) < 0.01){
				convergence = true;
			} else {
				llh = new_llh;
			}
			
		}
	}
	
	/**
	 * calculate log likelihood of whole graph
	 * @return	log likelihood
	 */
	private double calculateLLH(){
		double llh = 0.0;
		
		Set<String> users = uMap.keySet();
		
		for (String user : users){
			double llh_comp = 0.0;
			UserObject uObj = uMap.get(user);
			ArrayList<String> venues = uObj.getListOfVenues();
			
			for (String vId : venues) {
				VenueObject v = vMap.get(vId);
				double scope = v.getScope();
				llh_comp += ((double) v.getNumberOfTweet(user))
						* ( - Math.log(scope) - Utils.calSqDistance(v.getLocation(), uObj.getHome()) 
								/ (2.0 * scope));
			}
			
			double uScope = uObj.getScope();
			PointObject home = uObj.getHome();
			ArrayList<String> followers = uObj.getFollowers();
			
			if (followers != null){
				for (String follower : followers){
					UserObject fObj = uMap.get(follower);
					llh_comp += -Math.log(uScope) - Utils.calSqDistance(home, fObj.getHome()) / (2.0 * uScope);
				}
			}
			llh += llh_comp;
		}
		System.out.println("===============");
		return llh;
	}

	/**
	 * update the location of user
	 * @param user	user id
	 */
	private PointObject updateLocOfUser(String user) {
		UserObject uObj = uMap.get(user);
		double scope = uObj.getScope();
		
		double n1 = 0.0; double d1 = 0.0;
		double n2 = 0.0; double d2 = 0.0;
		
		// for each of his followers
		ArrayList<String> followers = uObj.getFollowers();
		if (followers != null) {
			// we do not care about the case of non-followers user
			for (String follower : followers) {
				UserObject fObj = uMap.get(follower);
				
				n1 += fObj.getHome().getLat() / scope;
				d1 += 1.0 / scope;
				
				n2 += fObj.getHome().getLng() / scope;
				d2 += 1.0 / scope;
			}
		}
		
		// for each of his friend
		ArrayList<String> friends = uObj.getFriends();
		if (friends != null){
			// we do not care about case of non-friend users
			for (String friend : friends){
				UserObject fObj = uMap.get(friend);
				
				n1 += fObj.getHome().getLat() / fObj.getScope();
				d1 += 1.0 / fObj.getScope();
				
				n2 += fObj.getHome().getLng() / fObj.getScope();
				d2 += 1.0 / fObj.getScope();
			}
		}
		
		// for each venue that user has tweeted
		ArrayList<String> venues = uObj.getListOfVenues();
		if (venues != null){
			for (String venue : venues){
				VenueObject vObj = vMap.get(venue);
				
				n1 += ((double)vObj.getNumberOfTweet(user)) * vObj.getLocation().getLat() / vObj.getScope();
				d1 += ((double)vObj.getNumberOfTweet(user)) / vObj.getScope();
				
				n2 += ((double)vObj.getNumberOfTweet(user)) * vObj.getLocation().getLng() / vObj.getScope();
				d2 += ((double)vObj.getNumberOfTweet(user)) / vObj.getScope();
			}
		}
		
		double lat = n1 / d1;
		double lng = n2 / d2;
		
		return new PointObject(lat, lng);
	}

	/**
	 * update the influence scope of all venues
	 */
	private void updateScopeOfAllVenues() {
		Set<String> venues = vMap.keySet();
		
		for (String venueId : venues){
			VenueObject vObj = vMap.get(venueId);
			
			double numerator = 0.0;
			double denominator = 0.0;
			PointObject loc = vObj.getLocation();
			
			Set<String> users = vObj.getAllUsers();
			for (String user : users){
				double numTweets = (double)vObj.getNumberOfTweet(user);
				numerator += numTweets * Utils.calSqDistance(loc, uMap.get(user).getHome());
				denominator += numTweets;
			}
			
			denominator *= (double) 2.0;
			
//			if (numerator == 0.0)
//				numerator = 1e-10;
			
			vObj.updateScope(numerator / denominator);
		}
	}

	/**
	 * we update the influence scope of all users
	 */
	private void updateInfluenceScopeOfAllUsers(){
		Set<String> users = uMap.keySet();
		
		for (String user : users){
			UserObject uObj = uMap.get(user);
			
			ArrayList<String> followers = uObj.getFollowers();
			PointObject home = uObj.getHome();
			
			if (followers == null){
				uObj.updateScope(1e-6);
				continue; // user does not have any followers
			}
			
			double scope = 0.0;
			for (String follower : followers){
				UserObject fObj = uMap.get(follower);
				scope += Utils.calSqDistance(home, fObj.getHome());
				
			}
			scope /= (double) (2 * followers.size());
			uObj.updateScope(scope);
		}
	}

}
