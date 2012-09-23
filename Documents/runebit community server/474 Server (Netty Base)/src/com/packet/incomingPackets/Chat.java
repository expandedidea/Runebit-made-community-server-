/*
* @ Author - Digistr
* @ info - none.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;
import com.util.GameUtility;

public class Chat implements PacketAssistant {
	
	public Chat() {

	}

	public void send(Player p, Packet packet){
		Short chatEffects = (short) packet.readShort();
		int length = packet.readByte();
		byte[] chatText = new byte[length];
		byte[] encryptedText = new byte[packet.readableBytes()];
		GameUtility.decryptText(chatText,packet.array(),3);
		if (chatText[0] == 47) {
			//clan Message Sent!
			return;
		}
		p.updateFlags().setChat(chatText,chatEffects);
	}
}