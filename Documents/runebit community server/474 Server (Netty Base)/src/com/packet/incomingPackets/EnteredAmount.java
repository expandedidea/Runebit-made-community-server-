/*
* @ Author - Digistr
* @ info - this packet is called when you press enter after typing a X amount.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;

public class EnteredAmount implements PacketAssistant {

	public EnteredAmount() {

	}

	public void send(Player p, Packet packet){
		int input = packet.readInt();
		int parent = p.interfaceContainer().xInfo - ((p.interfaceContainer().xInfo >> 10) << 10);
		int slot = p.interfaceContainer().xInfo >> 10;
		switch (parent) {
			case 12:
				if (slot > -1 && slot < 400)
					p.bank().withdraw(slot,input);
			break;
			case 15:
				if (slot > -1 && slot < 28)
					p.bank().deposit(slot,input);
			break;
			default:
				p.packetDispatcher().sendMessage("[Enter Amount: " + input + " Parent: " + parent + " Slot: " + slot);
			break;
		}
		p.packetDispatcher().sendMessage("[Enter Amount: " + input + " Parent: " + parent + " Slot: " + slot);
	}
}