/*
* @ Author - Digistr
* @ info - Handles attack players in PvP areas.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;
import com.model.World;

public class RightClickOptionAttack implements PacketAssistant {

	public RightClickOptionAttack() {

	}

	public void send(Player p, Packet packet){
		short clientIndex = (short)packet.readLEShortA();
		if (clientIndex == p.INDEX)
			return;
		Player other = World.getPlayerByCheckingIndex(clientIndex);
		if (other != null) {
			p.combat().setAttackingOn(other.combat());
			p.updateFlags().setFaceToPlayer(clientIndex);
		}
	}
}