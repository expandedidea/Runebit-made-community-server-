/*
* @ Author - Digistr
* @ info - none.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;

public class AddIgnore implements PacketAssistant {

	public AddIgnore() {
	}

	public void send(Player p, Packet packet){
		String username = com.util.GameUtility.longToString(packet.readLong());
		p.packetDispatcher().sendMessage("[ADD IGNORE] Name: "+username);		
	}
}