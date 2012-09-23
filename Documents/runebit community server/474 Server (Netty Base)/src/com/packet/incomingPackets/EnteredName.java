/*
* @ Author - Digistr
* @ info - this packet is called when you have entered a person username.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;

public class EnteredName implements PacketAssistant {

	public EnteredName() {

	}

	public void send(Player p, Packet packet){
		long input = packet.readLong();
		System.out.println("Long Input: "+input);
	}
}