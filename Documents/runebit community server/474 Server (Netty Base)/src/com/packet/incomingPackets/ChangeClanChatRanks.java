/*
* @ Author - Digistr
* @ info - none.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;
import com.util.GameUtility;

public class ChangeClanChatRanks implements PacketAssistant {

	public ChangeClanChatRanks() {

	}

	public void send(Player p, Packet packet){
		int rank = packet.readByteC();
		long longName = packet.readLong1();
		String username = GameUtility.longToString(longName);
		p.packetDispatcher().sendMessage("Rank: "+rank+" Name: "+username);
	}
}