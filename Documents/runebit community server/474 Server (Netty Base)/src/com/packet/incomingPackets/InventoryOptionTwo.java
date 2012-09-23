/*
* @ Author - Digistr
* @ info - This handles the following: Potions , Food , Drinks , Read Books , Setup Cannon.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;

public class InventoryOptionTwo implements PacketAssistant {
	
	public InventoryOptionTwo() {

	}

	public void send(Player p, Packet packet){
		p.packetDispatcher().sendMessage("Inventory Option # 2 Recieved.");		
	}
}