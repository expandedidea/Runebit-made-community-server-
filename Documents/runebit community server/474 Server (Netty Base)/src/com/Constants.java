package com;

import com.util.Random;

public class Constants {

	
	
	public static String SERVER_NAME = "Runebit Community server";
	public int XP_RATE = 50; //the xp rate is this number times normal runescape xp rates.
	public static String LOGIN_MESSAGE() {
		if(Random.random(4) == 1) {
		return "The only real forum thread was made by tilt";
		
	}
		if(Random.random(6) == 2) {
			return "There is no real owner.  The community owns this server.  Tilt just made it.";		
		}
		if(Random.random(6) == 3) {
			return "We have clan battles all the time!  Who's side are you on?";		
		}
		if(Random.random(6) == 4) {
			return "Runebit was created in january of 2012 by mod vault.";		
		}
		if(Random.random(6) == 5) {
			return "The only moderators on this server are 'Tilt', 'vault', and 'Joshua F'.  Moderators are not allowed to spawn items.";		
		}
		if(Random.random(6) == 6) {
			return "Always virus-scan any file you get on runebit.  You never know what a file has in-store for you...";		
		}
		return null;
}
}
