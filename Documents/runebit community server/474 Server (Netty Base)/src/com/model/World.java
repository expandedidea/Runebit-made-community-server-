/*
* @ Author - Digistr
* @ Info - Resembles A PlayerHandler.java
*/

package com.model;

import java.util.NoSuchElementException;
import java.util.Arrays;
import java.util.ArrayDeque;
import java.util.Deque;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import com.codec.RSDecoder;
import com.codec.ChannelHandler;
import com.packet.Packet;
import com.packet.PacketBuilder;
import com.packet.PacketReceiver;
import com.util.FileManagement;
import com.util.TaskQueue;

public class World {

	private static Deque<Player> LOGOUTS = new ArrayDeque<Player>();
	private static Deque<PlayerLoginDetails> LOGINS = new ArrayDeque<PlayerLoginDetails>();
	public static Player[] players;
	private static short[] indexs;
	public static short curIndex = 0;
	private static short saverIndex = 0;

	private World() {

	}

    /*
    * Creates The List Based On Size You Choose in Server.java
    */
	public static void createList(int plrSize) {
		players = new Player[plrSize];
		indexs = new short[plrSize + 1];
		for (int i = 0; i <= plrSize; i++)
			indexs[i] = -1;
	}


    /*
    * Adds A Player right into the current Index slot.
    */
	public static byte add(Player plr) {
		if (curIndex >= players.length)
			return 7;
		if (getPlayerByName(plr.details().USERNAME_AS_LONG) != null)
			return 5;
		short index = 0;
		for ( ; ++index < indexs.length ;) {
			if (indexs[index] == -1) {
				break;
			}
		}
		indexs[index] = curIndex;
		players[curIndex++] = plr;
		plr.INDEX = index;
		plr.details().setHandled(true);
		return 2;
	}
	

    /*
    * Removes a player then shifts the array down.
    */
	public static boolean remove(Player plr) {
		if (!plr.details().isHandled())
			return false;
		plr.chat().updateList(false);
		plr.details().setHandled(false);
		plr.details().setActive(false);
		if (curIndex <= saverIndex)
			--saverIndex;
		--curIndex;
		FileManagement.savePlayer(plr);
		for (short s = indexs[plr.INDEX]; s < curIndex; s++) {
			players[s] = players[s + 1];
			indexs[players[s].INDEX] = s;
		}
		indexs[plr.INDEX] = -1;
		return true;
	}

    /*
    * Check's To Verify This Player Isn't On With to many connections.
    * To Change max connections modify (count < 2). 2 being the max aloud.
    */
	public static byte checkIpAddress(char[] IP) {
		long t1 = System.nanoTime();
		int count = 0;
		for (int i = 0; i < curIndex; i++)
			if (Arrays.equals(IP,players[i].details().IP))
				++count;
		if (count < 2000)
			return 2;
		return 9;
		
	}

    /*
    *  Enters a players username (STRING) then retrieves back a Player object (PLAYER) or NULL if not found.
    */
	public static Player getPlayerByName(String USERNAME_AS_STRING) {
		for (int i = 0; i < curIndex; i++)
			if (players[i].details().USERNAME_AS_STRING.equals(USERNAME_AS_STRING))
				return players[i];
		return null;
	}


    /*
    *  Enters a players username (LONG) then retrieves back a Player object (PLAYER) or NULL if not found.
    */
	public static Player getPlayerByName(long USERNAME_AS_LONG) {
		for (int i = 0; i < curIndex; i++)
			if (players[i].details().USERNAME_AS_LONG == USERNAME_AS_LONG)
				return players[i];
		return null;
	}


    /*
    *  Enter in the clientindex to be checked then retrieves back a Player object (PLAYER) or NULL if not found.
    *  This is to be used when you cannot verify if the index is static.
    */
	public static Player getPlayerByCheckingIndex(int index) {
		if (index < 0 || index >= indexs.length || indexs[index] == -1)
			return null;
		return players[indexs[index]];
	}

    /*
    *  This will instantly return the index checked! [WARNING] there is no verification.
    */
	public static Player getPlayerByClientIndex(int index) {
		return players[indexs[index]];
	}

    /*
    * Process's All players Read To Login After All Updates From Current players Have Been Finished.
    * This Prevents Any Glitches In Processing A Player That Doesn't Quite Exist Yet.
    */
	public static void queueLogins() {
		PlayerLoginDetails d = null;
		while ((d = LOGINS.poll()) != null) {
			final Player plr = new Player(d);
			PacketBuilder pb = new PacketBuilder(6);
			byte returnCode = checkIpAddress(d.IP);
			if (returnCode == 2)
				returnCode = FileManagement.loadPlayer(plr);
			if (returnCode == 2)
				returnCode = World.add(plr);
			pb.addByte(returnCode);
			if (returnCode != 2) {
				plr.getSession().write(pb).addListener(ChannelFutureListener.CLOSE);
				continue;
			}
			plr.finish();
			pb.addByte(plr.RIGHTS);
			pb.addByte((byte) 0);
			pb.addShort(plr.INDEX);
			pb.addByte((byte) 1);
			plr.getSession().write(pb);
			plr.packetDispatcher().sendLogin();
			if (plr.isDisconnected()) {
				remove(plr);
				continue;
			}
			plr.getSession().write(plr.getPacket());
			plr.getPacket().reset();
			ChannelHandler ch = (ChannelHandler)plr.getSession().getPipeline().getLast();
			ch.setAttachment(plr.INDEX);
			plr.getSession().getPipeline().addLast("decoder", new RSDecoder(plr.INDEX));
			plr.details().setActive(true);
		}
	}

    /*
    * Process's All players Ready To Logout 1 Server Cycle After All Updates Are Recieved.
    * This Gives The 1 Server Cycle Delay Effect and prevents Any Null Issue's Or Problems
    * Where The Player Is Logged Out But Is Still Be Processed Result In Bad Information.
    */
	public static void queueLogouts() {
		Player p = null;
		while ((p = LOGOUTS.poll()) != null) {
			System.out.println("Disconnecting Player: " + p.details().USERNAME_AS_STRING);
			p.getSession().write(p.getPacket().createPacket(166));
			remove(p);
		}
	}

    /*
    * Process All players and Specific Tasks That All players Must See.
    * We start With logouts to remove all players who don't need to be executed.
    * We then send all Packets based in order of recieving them.
    * We then send all main player tasks which are executed only when needed.
    * We then send the basic stuff ( tick , updating , flagreset ).
    * We then save the next available player.
    * We then Login all new connections and then start all over. 
    */

	public static void tick() {
	    	queueLogouts();
		for (int i = 0; i < curIndex; i++)
			players[i].combat().sendHits();
		TaskQueue.queue();
		for (int i = 0; i < curIndex; i++)
			players[i].tick();
		for (int i = 0; i < curIndex; i++)
			PlayerUpdating.update(players[i]);
		for (int i = 0; i < curIndex; i++)
			players[i].updateFlags().reset();
		for (int i = 0; i < curIndex; i++) {
			players[i].getSession().write(players[i].getPacket());
			players[i].getPacket().reset();
		}
		saveNextPlayer();
		queueLogins();
	}

   /*
   * Adds A Player To The Logout Queue.
   */
        public static void addToLogoutQueue(Player player) {
		LOGOUTS.add(player);	
	}

   /*
   * Adds A Player To The Login Queue.
   */
        public static void addToLoginQueue(PlayerLoginDetails player) {
		LOGINS.add(player);
	}

   /*
   * Hopefully this method is called everytime the server is exited.
   */
	public static void exit() {
		long t1 = System.nanoTime();
		for (int i = 0; i < curIndex; i++) {
			FileManagement.savePlayer(players[i]);
		}
		long t2 = System.nanoTime();
		System.out.println("Saved "+curIndex+" Players Accounts.");
	}

   /*
   * Best Performance for saving players. With servers that have 2000 players on they get saved once every 20 minutes.
   */
	public static void saveNextPlayer() {
		if (curIndex == 0)
			return;
		if (saverIndex == curIndex || saverIndex < 0)
			saverIndex = 0;
		FileManagement.savePlayer(players[saverIndex++]);
	}

}