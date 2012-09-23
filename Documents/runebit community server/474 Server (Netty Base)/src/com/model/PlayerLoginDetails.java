/*
* @ Author - Digistr
* @ Objective - Contains Basic Information We Need For A Login Attempt #2.
* @ Info - if SecondLoginAttempt Is Successfull we create a Player Class.
*/

package com.model;
import org.jboss.netty.channel.Channel;
import java.net.InetSocketAddress;
import com.util.GameUtility;

public class PlayerLoginDetails {

	private boolean isActive,isHandled,isNewPlayer;
	private final int INDENTIFICATION_ID;
	public final String USERNAME_AS_STRING;
	public final long USERNAME_AS_LONG;
	public long CUSTOM_USERNAME;
	public final char[] IP;
	public final char[] PASSWORD;
	public final Channel CHANNEL;

	public void createCustomName(String name) {
		CUSTOM_USERNAME = GameUtility.stringToLong(GameUtility.formatUsernameForProtocol(name));
	}

	public PlayerLoginDetails(long nameLong, String pass, Channel channel, long clientIndentification) {
		USERNAME_AS_STRING = GameUtility.formatUsernameForProtocol(GameUtility.longToString(nameLong));
		USERNAME_AS_LONG = GameUtility.stringToLong(USERNAME_AS_STRING);
		CUSTOM_USERNAME = USERNAME_AS_LONG;
		PASSWORD = pass.toCharArray();
		CHANNEL = channel;
		IP = ((InetSocketAddress)channel.getRemoteAddress()).getHostName().toCharArray();
		INDENTIFICATION_ID = (int)clientIndentification;
		isActive = false;
		isHandled = false;
	}

    /*
    * These methods are extremely important but since they should not be carelessly modify'd iv'e left them as private.
    * @ isActive() - This method return true if the player is aloud to recieve packets. (Prevents Errors).
    * @ isHandled() - This method returns true if the player is currently in the World Player Handler. (Prevents Errors).
    * @ isKeyCorrect() - This method returns true if the players login class gave correct information, meaning correct client.
    */
	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean active) {
		isActive = active;
	}

	public boolean isHandled() {
		return isHandled;
	}

	public boolean isNewPlayer() {
		return isNewPlayer;
	}	

	public void setHandled(boolean handle) {
		isHandled = handle;
	}

	public void setNewPlayer() {
		isNewPlayer = true;
	}

	public boolean isKeyCorrect() {
		return 1052405112 == INDENTIFICATION_ID;
	}
}
