package org.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.object.PointObject;
import org.object.TweetObject;

public class ReadFiles {
	
	/**
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static HashMap<String, PointObject> readProfile(String filename) throws IOException{
		HashMap<String, PointObject> result = new HashMap<>();

		int counter = 1;
		File file = new File(filename);
	    FileReader fr = new FileReader(file);
	    BufferedReader br = new BufferedReader(fr);
	    String line;
	    while((line = br.readLine()) != null){
	    	if (!line.trim().equals("?")){
	    		// if we know the location of user
	    		String[] comp = line.trim().split(",");
	    		double lat = Double.parseDouble(comp[0]);
	    		double lng = Double.parseDouble(comp[1]);
	    		PointObject p = new PointObject(lat, lng);
	    		result.put(Integer.toString(counter), p);
	    	}
	        counter++;
	    }
	    br.close();
	    fr.close();
		
		return result;
	}
	
	/**
	 * 
	 * @param filename
	 * @return hash map whose key is user id, value is list of users who he follows
	 * @throws IOException
	 */
	public static HashMap<String, ArrayList<String>> readFriendship(String filename) throws IOException{
		HashMap<String, ArrayList<String>> result = new HashMap<>();
		
		File file = new File(filename);
	    FileReader fr = new FileReader(file);
	    BufferedReader br = new BufferedReader(fr);
	    String line;
	    while((line = br.readLine()) != null){
	    	String[] comp = line.trim().split(" ");
	    	String user1 = comp[0];
	    	String user2 = comp[1];
	    	
	    	ArrayList<String> l = result.get(user1);
	    	if ( l == null){
	    		l = new ArrayList<>();
	    		result.put(user1, l);
	    	}
	    	
	    	l.add(user2);
	    }
	    br.close();
	    fr.close();
		
		return result;
	}
	
	/**
	 * 
	 * @param filename
	 * @return
	 * @throws IOException 
	 */
	public static HashMap<String, ArrayList<TweetObject>> readTweet(String filename) throws IOException{
		HashMap<String, ArrayList<TweetObject>> result = new HashMap<>();
		
		int counter = 1;
		File file = new File(filename);
	    FileReader fr = new FileReader(file);
	    BufferedReader br = new BufferedReader(fr);
	    String line;
	    while((line = br.readLine()) != null){
	    	String[] comp = line.trim().split(",");
	    	
	    	ArrayList<TweetObject> l = new ArrayList<>();
	    	for (int i = 0; i < comp.length; i++){
	    		int numTweet = Integer.parseInt(comp[i]);
	    		if (numTweet != 0){
	    			TweetObject to = new TweetObject(Integer.toString(i + 1), numTweet);
	    			l.add(to);
	    		}
	    	}
	    	
	    	if (l.size() != 0)
	    		result.put(Integer.toString(counter), l);
	    	
	    	counter++;
	    }
	    br.close();
	    fr.close();
		
		return result;
	}
}
