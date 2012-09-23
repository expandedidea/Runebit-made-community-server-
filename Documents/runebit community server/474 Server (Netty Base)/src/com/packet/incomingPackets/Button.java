/*
* @ Author - Digistr
* @ info - none.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;
import com.util.ItemManagement;

public class Button implements PacketAssistant {
	
	public Button() {

	}

	public void send(Player p, Packet packet){
		int parentId = packet.readShort();
		int childId = packet.readShort();
		if ((parentId > 74 && parentId < 94) || parentId == 473 || parentId == 474) {
			if (parentId == 90) {
				if (childId > 3) {
					p.packetDispatcher().sendMessage("[BUTTON Unhandled Button]: Parent: "+parentId+" Child: "+childId);
					return;
				}
			}
			if (childId > 0 && childId < 6) {
				p.fightType().pressed(parentId, childId);
			} else {
				p.packetDispatcher().sendMessage("[BUTTON Unhandled Button]: Parent: "+parentId+" Child: "+childId);
			}
			return;
		}
		switch (parentId) {
			case 12:
				if (childId == 98) {
					p.bank().swap = true;
				} else if (childId == 99) { 
					p.bank().swap = false;
				} else if (childId == 92) {
					p.bank().note = true;
				} else if (childId == 93) {
					p.bank().note = false;
				}
				break;
			case 261:
				if (childId == 0)
					p.walkingQueue().running = !p.walkingQueue().running;
				break;
			case 387:
				if (childId == 50) {
					long t1 = System.nanoTime();
					p.itemsOnDeath().open();
					long t2 = System.nanoTime();
					p.packetDispatcher().sendMessage("Spent: " + (t2-t1) + " Calculating/Opening Items On Death.");
				} else if (childId == 51) {
					p.equipment().open();
				}	
				break;
			case 378:
				p.packetDispatcher().sendWindowPane(548);
				if (p.details().isNewPlayer())
					p.packetDispatcher().sendInterface(0, 548, 269, 77);
				break;
			case 182:
				if (p.canDisconnect()) {
					p.details().setActive(false);
					p.packetDispatcher().sendLogout();
				} else {
					p.packetDispatcher().sendMessage("You must wait 10 seconds before logging out from combat.");
				}
				break;
			default:
				p.packetDispatcher().sendMessage("[BUTTON Unhandled Button]: Parent: "+parentId+" Child: "+childId);
		}
	}
}