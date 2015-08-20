package org.object;

import java.util.ArrayList;

public class UserObject {
	private double scope;
	private ArrayList<String> followers;
	private ArrayList<String> friends;
	private PointObject home;
	
	/**
	 * list of venues which have done tweet by users
	 */
	private ArrayList<String> listOfVenues; 
	
	public UserObject(double scope, ArrayList<String> followers, ArrayList<String> friends, PointObject home){
		this.scope = scope;
		this.followers = followers;
		this.friends = friends;
		this.home = home;
		this.listOfVenues = new ArrayList<>();
	}
	
	public void updateScope(double d){
		scope = d;
	}
	
	public void updateHome(PointObject p){
		home = p;
	}
	
	public void updateHome(double lat, double lng){
		PointObject p = new PointObject(lat, lng);
		this.home = p;
	}

	public double getScope() {
		return scope;
	}

	public ArrayList<String> getFollowers() {
		return followers;
	}

	public ArrayList<String> getFriends() {
		return friends;
	}

	public PointObject getHome() {
		return home;
	}

	public ArrayList<String> getListOfVenues() {
		return listOfVenues;
	}

	public void addVenue(String venue) {
		this.listOfVenues.add(venue);
	}
	
}
