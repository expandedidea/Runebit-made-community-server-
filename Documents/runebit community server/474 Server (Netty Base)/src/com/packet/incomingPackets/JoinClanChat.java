/*
* @ Author - Digistr
* @ info - none.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;

public class JoinClanChat implements PacketAssistant {
	
	public JoinClanChat() {

	}

	public void send(Player p, Packet packet){
		long usernameAsLong = packet.readLong();
		if (usernameAsLong == 0)
			p.packetDispatcher().sendMessage("You're currently already inside a clan chat.");
	}
}