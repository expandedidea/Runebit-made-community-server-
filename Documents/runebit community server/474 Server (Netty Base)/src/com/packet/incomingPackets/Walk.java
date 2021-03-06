/*
* @ Author - Digistr
* @ info - reads the turns in the walking packets and adds them appropriatly into a Queue.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.PacketReceiver;
import com.packet.Packet;
import com.model.Player;

public class Walk implements PacketAssistant {
	
	public Walk() {

	}

	public void send(Player p, Packet packet){
		p.combat().reset();
		p.follow().reset();
		p.updateFlags().resetFaceTo();
		p.interfaceContainer().resetInterfaces(p, true);
		p.inventory().pickupId = -1;
		if (p.isUnableToWalk())
			return;
		int size = packet.readableBytes();
		if (packet.ID == 11)
			size -= 14;
		int steps = (size - 5) / 2;
		byte[][] path = new byte[steps][2];
		p.walkingQueue().reset();
		for (int i = 0; i < steps; i++) {
			path[i][0] = (byte)packet.readByte();
			path[i][1] = (byte)packet.readByteS();
		}
 		short baseX = (short)packet.readShortA();
		short baseY = (short)packet.readLEShort();
		p.walkingQueue().setFirstDirection(baseX, baseY, p.location());
		packet.skipBytes(1);
		for (int i = 0; i < steps; i++)
			p.walkingQueue().addDirectionChangeStep(path[i][0], path[i][1]);
	}
}