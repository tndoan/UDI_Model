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
	 * each line of file has format: 
	 * <id of user><space><lat>:<lng>
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static HashMap<String, PointObject> readProfile(String filename) throws IOException{
		HashMap<String, PointObject> result = new HashMap<>();

		File file = new File(filename);
	    FileReader fr = new FileReader(file);
	    BufferedReader br = new BufferedReader(fr);
	    String line;
	    while((line = br.readLine()) != null){
	    	String[] comp = line.split(" ");
	    	String id = comp[0].trim(); // id of user or venue
	    	
	    	if (!comp[1].equals("?")){
	    		String[] ll = comp[1].split(",");
	    		double lat = Double.parseDouble(ll[0]);
	    		double lng = Double.parseDouble(ll[1]);
	    		PointObject p = new PointObject(lat, lng);
	    		result.put(id, p);
	    	}
	    }
	    br.close();
	    fr.close();
		
		return result;
	}
	
	/**
	 * each line of file has format
	 * <id of user 1><space><id of user 2>
	 * user 1 follows user 2
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
	 * each line of file has format
	 * <venue id><space><user id>:<number of tweet/checkin>
	 * @param filename
	 * @return
	 * @throws IOException 
	 */
	public static HashMap<String, ArrayList<TweetObject>> readTweet(String filename) throws IOException{
		HashMap<String, ArrayList<TweetObject>> result = new HashMap<>();
		
		File file = new File(filename);
	    FileReader fr = new FileReader(file);
	    BufferedReader br = new BufferedReader(fr);
	    String line;
	    while((line = br.readLine()) != null){
	    	String[] comp = line.split(" ");
	    	String id = comp[0].trim();
	    	
	    	ArrayList<TweetObject> l = new ArrayList<>();
	    	for (int i = 1; i < comp.length; i++){
	    		String[] pair = comp[i].split(":");
	    		int numTweet = Integer.parseInt(pair[1]);
	    		if (numTweet != 0){
	    			TweetObject to = new TweetObject(pair[0].trim(), numTweet);
	    			l.add(to);
	    		}
	    	}
	    	
	    	if (l.size() != 0)
	    		result.put(id, l);
	    }
	    br.close();
	    fr.close();
		
		return result;
	}
}
