/*
* @ Author - Digistr
* @ info - none.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;
import com.util.GameItemEditor;

public class DeadPacket implements PacketAssistant {
	
	public DeadPacket() {

	}

	public void send(Player p, Packet packet){
		if (packet.ID == 167 && Commands.oI > -1) {
			//Commands.commandItem = GameItemEditor.getNext();
			//p.packetDispatcher().sendSingleItem(149, 0, 93, 0, Commands.commandItem,1);
			//p.packetDispatcher().sendMessage("Current Viewed Item ID: "+Commands.commandItem+" CurIndex: "+GameItemEditor.curIndex);
			int start = Commands.oI;
			switch (Commands.oS) {
				case 1:
					for (int i = -4; i < 4; ++i) {
						for (int j = -4; j < 4; ++j) {
							p.packetDispatcher().sendCreateObject(p.location(),Commands.oI++,p.location().x + i,p.location().y + j,p.location().z,Commands.oF,Commands.oT);
						}
					} 
					p.packetDispatcher().sendMessage("Object Start: "+ start + " End: " + (Commands.oI) + " Face: " + Commands.oF + " Type: " + Commands.oT);
				break;
				case 2:
					p.packetDispatcher().sendCreateObject(p.location(),Commands.oI,p.location().x,p.location().y ,p.location().z,Commands.oF,Commands.oT);
					p.packetDispatcher().sendMessage("Object ID: " + (Commands.oI++) + " Face: " + (Commands.oF) + " Type: " + (Commands.oT));
				break;
				case 3:
					p.packetDispatcher().sendCreateObject(p.location(),Commands.oI,p.location().x,p.location().y,p.location().z,Commands.oF,Commands.oT);
					p.packetDispatcher().sendMessage("Object ID: " + (Commands.oI) + " Face: " + (Commands.oF++) + " Type: " + (Commands.oT));
					if (Commands.oF > 3)
						Commands.oF = 0;
				break;
				case 4:
					p.packetDispatcher().sendCreateObject(p.location(),Commands.oI,p.location().x ,p.location().y,p.location().z,Commands.oF,Commands.oT);
					p.packetDispatcher().sendMessage("Object ID: " + (Commands.oI) + " Face: " + (Commands.oF) + " Type: " + (Commands.oT++));
					if (Commands.oT > 22)
						Commands.oT = 0;
				break;
				case 5:
					for (int i = -3; i < 3; i++) {
					    for (int j = -3; j < 5; j++) {
						p.packetDispatcher().sendCreateObject(p.location(),30000,p.location().x + i,p.location().y + j,p.location().z,Commands.oF,Commands.oT > 0 ? Commands.oT - 1 : 22);
				            }
					}
					for (int i = -3; i < 3; i++) {
					    for (int j = -3; j < 5; j++) {
						p.packetDispatcher().sendCreateObject(p.location(),Commands.oI++,p.location().x + i,p.location().y + j,p.location().z,Commands.oF,Commands.oT);
				            }
					}
					p.packetDispatcher().sendMessage("Object Start: "+ start + " End: " + (Commands.oI) + " Face: " + Commands.oF + " Type: " + Commands.oT);					
					if (Commands.oT > 21)
						Commands.oT = 0;
					else {
						++Commands.oT;
						Commands.oI = start;
					}
				break;
				case 6:
					p.packetDispatcher().sendMessage("Scanning Object Types:  ID: " + Commands.oI + " TYPE: " + Commands.oT);
					for (int i = -3; i < 2; i++) {
						for (int j = -3; j < 2; j++) {
							if (Commands.oT < 23) {
								p.packetDispatcher().sendCreateObject(p.location(),Commands.oI,p.location().x + i,p.location().y + j,p.location().z,Commands.oF,Commands.oT);
							}
						}
					}
					Commands.oT = 0;
					for (int i = -3; i < 2; i++) {
						for (int j = -3; j < 2; j++) {
							if (Commands.oT < 23) {
								p.packetDispatcher().sendCreateObject(p.location(),30000,p.location().x + i,p.location().y + j,p.location().z,Commands.oF,Commands.oT++);
							}
						}
					}
			}
		}
	}
}