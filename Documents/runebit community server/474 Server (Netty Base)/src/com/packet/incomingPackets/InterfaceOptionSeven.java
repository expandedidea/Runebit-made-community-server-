/*
* @ Author - Digistr
* @ info - none.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;

public class InterfaceOptionSeven implements PacketAssistant {
	
	public InterfaceOptionSeven() {

	}

	public void send(Player p, Packet packet){
		int parentId = packet.readShort();
		int childId = packet.readShort();
		int slot = packet.readShort();
		p.packetDispatcher().sendMessage("[BUTTON #7] - Parent: "+parentId+" Child: "+childId+" Slot: "+slot);		
	}
}