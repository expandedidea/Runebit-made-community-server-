package com.model;

import com.packet.PacketBuilder;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class PlayerChat {

	private static final short WORLD = 1;

	public final HashMap<Long, Byte> FRIENDS = new HashMap<Long, Byte>(200);
	public final List<Long> IGNORES = new ArrayList<Long>(200);
	public byte chatPublic = 0, chatPrivate = 0, chatClan = 0, chatTrade = 0;
	private short index;

	/*
	* Constructor is used when we create the Player so we can save the FRIENDS before we obtain player index.
	*/
	public PlayerChat() {

	}

	/*
	* This sets the players index so we can use this player in the methods below.
	*/
	public void setIndex(short index) {
		this.index = index;
	}

	/*
	* When chat's are changed via the client we call this method.
	* This isn't to be used on login as it does update the list when called.
	*/
	public void setChat(byte chatPublic, byte chatPrivate, byte chatTrade) {
		if (this.chatPrivate != chatPrivate) {
			this.chatPrivate = chatPrivate;
			updateList(this.chatPrivate < 2);
		}
		this.chatPublic = chatPublic;
		this.chatTrade = chatTrade;
	}
	
	/*
	* When AddFriend packet is called we sent this.
	*/
	public void addFriend(long name) {
		if (FRIENDS.size() > 199)
			return;
		if (FRIENDS.containsKey(name))
			return;
		FRIENDS.put(name, (byte)0);
		sendFriend(name, (byte)0);
		return;
	}

	/*
	* When RemoveFriend packet is called we sent this.
	*/
	public void removeFriend(long name) {
		Player otherPlayer = World.getPlayerByName(name);
		if (FRIENDS.remove(name) != null && otherPlayer != null) {
			long me = World.getPlayerByClientIndex(index).details().USERNAME_AS_LONG;
			Byte rank = otherPlayer.chat().FRIENDS.get(me);
			if (rank != null && chatPrivate == 1) {
				otherPlayer.getPacket().createPacket(200).addLong(me).addShort(0).addByte(rank);
			}
		}
		return;
	}
	
	/*
	* This is used to add a friend to your list and tell that friend you've added them.
	*/
	public void sendFriend(long name, byte clanRank) {
		Player me = World.getPlayerByClientIndex(index);
		Player otherPlayer = World.getPlayerByName(name);
		short world = 0;
		if (otherPlayer != null) {
			Byte rank = otherPlayer.chat().FRIENDS.get(me.details().USERNAME_AS_LONG);
			if (rank != null) {
				if (otherPlayer.chat().chatPrivate < 2)
					world = WORLD;
				if (chatPrivate < 2)
					otherPlayer.getPacket().createPacket(200).addLong(me.details().USERNAME_AS_LONG).addShort(WORLD).addByte(rank);		
			} else if (otherPlayer.chat().chatPrivate == 0)
					world = WORLD;
		}
		me.getPacket().createPacket(200).addLong(name).addShort(world).addByte(clanRank);
	}

	/*
	* This is to be used where you send all other stuff on login and only to be called 1 time on login.
	*/
	public void sendList() {
		Iterator iterator = FRIENDS.keySet().iterator();
		long t1 = System.nanoTime();
		while (iterator.hasNext()) {
			long key = (Long)iterator.next();
			Byte rank = FRIENDS.get(key);
			sendFriend(key, rank);
		}
		long t2 = System.nanoTime();
		World.getPlayerByClientIndex(index).packetDispatcher().sendMessage("Time to sendList: " + (t2-t1));
		updateList(chatPrivate < 2);
		sendIgnores();
	}

	/*
	* This is used to update all players so they know if your online or offline and how to correctly show you.
	* Depending on chatPrivate type you'll either be offline or online for the other players.
	*/
	public void updateList(boolean show) {
		long t1 = System.nanoTime();
		Player me = World.getPlayerByClientIndex(index);
		if (show) {
			for (int i = 0; i < World.curIndex; i++) {
				if (!World.players[i].details().isActive())
					continue;
				Byte rank = World.players[i].chat().FRIENDS.get(me.details().USERNAME_AS_LONG);
				if (rank != null) {
					if (chatPrivate == 0) {
						World.players[i].getPacket().createPacket(200).addLong(me.details().USERNAME_AS_LONG).addShort(WORLD).addByte(rank);
					} else {
						if (FRIENDS.containsKey(World.players[i].details().USERNAME_AS_LONG))
							World.players[i].getPacket().createPacket(200).addLong(me.details().USERNAME_AS_LONG).addShort(WORLD).addByte(rank);
						else
							World.players[i].getPacket().createPacket(200).addLong(me.details().USERNAME_AS_LONG).addShort(0).addByte(rank);
					}			
				}
			}
		} else {
			for (int i = 0; i < World.curIndex; i++) {
				Byte rank = World.players[i].chat().FRIENDS.get(me.details().USERNAME_AS_LONG);
				if (rank != null)
					World.players[i].getPacket().createPacket(200).addLong(me.details().USERNAME_AS_LONG).addShort(0).addByte(rank);
			}
		}
		long t2 = System.nanoTime();
		me.packetDispatcher().sendMessage("Time to updateList: " + (t2-t1));
	}

	public void addIgnore(long name) {
		if (IGNORES.size() > 199)
			return;
		if (IGNORES.contains(name))
			return;
		IGNORES.add(name);
	}

	public void removeIgnore(long name) {
		IGNORES.remove(name);
	}

	/*
	* This sets the ignore list.
	*/
	private void sendIgnores() {
		PacketBuilder packet = World.getPlayerByClientIndex(index).getPacket();
		packet.createPacketTypeShort(173);
		for (long name : IGNORES) {
			packet.addLong(name);
		}
		packet.endPacketTypeShort();
	}
}