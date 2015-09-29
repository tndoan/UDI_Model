package org.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.model.GlobalProfiling;
import org.model.LocalProfiling;
import org.object.PointObject;
import org.object.UserObject;
import org.utils.ReadFiles;

public class Test {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
//		LocalProfiling lp = new LocalProfiling("data/users_profile.txt", "data/friendship.txt", "data/tweets.txt", "data/venues_profile.txt");
//		UserObject uo = lp.getUserLocation("1");
//		System.out.println(uo.getHome().toString());
		GlobalProfiling g = new GlobalProfiling("data/users_profile.txt", "data/friendship.txt", "data/tweets.txt", "data/venues_profile.txt");
		g.learnParameters();
		System.out.println(g.getHome("1").toString());
		System.out.println(g.getUserScope("1"));
		System.out.println(g.getHome("6").toString());
		System.out.println(g.getUserScope("6"));
		
		System.out.println(g.getUserScope("4"));
		System.out.println(g.getUserScope("3"));
		
//		for (int i = 1; i < 4; i++)
//			System.out.println(g.getVenueScope(Integer.toString(i)));
	}

}
