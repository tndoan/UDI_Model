package org.object;

public class TweetObject {
	private String userId;
	
	private int numTweet;
	
	/**
	 * 
	 * @param userId
	 * @param numTweet
	 */
	public TweetObject(String userId, int numTweet) {
		this.userId = userId;
		this.numTweet = numTweet;
	}

	public String getUserId() {
		return userId;
	}

	public int getNumTweet() {
		return numTweet;
	}
}
