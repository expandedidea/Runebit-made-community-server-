/*
* @ Author - Digistr
* @ info - none.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;

public class InterfaceOptionThree implements PacketAssistant {
	
	public InterfaceOptionThree() {

	}

	public void send(Player p, Packet packet){
		int parentId = packet.readShort();
		int childId = packet.readShort();
		int slot = packet.readShort();
		p.packetDispatcher().sendMessage("[BUTTON #3] - Parent: "+parentId+" Child: "+childId+" Slot: "+slot);		
	}
}