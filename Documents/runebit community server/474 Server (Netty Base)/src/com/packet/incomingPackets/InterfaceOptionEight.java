/*
* @ Author - Digistr
* @ info - none.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;

public class InterfaceOptionEight implements PacketAssistant {
	
	public InterfaceOptionEight() {

	}

	public void send(Player p, Packet packet){
		int parentId = packet.readShort();
		int childId = packet.readShort();
		int slot = packet.readShort() & 0xFFFF;
		p.packetDispatcher().sendMessage("[BUTTON #8] - Parent: "+parentId+" Child: "+childId+" Slot: "+slot);		
	}
}