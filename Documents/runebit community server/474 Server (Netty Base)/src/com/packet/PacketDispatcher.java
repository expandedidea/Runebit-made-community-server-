/*
* @ author - Digistr
* @ info - contains most packets that the client recieves.
* @ moreinfo - this could be made a bit better not sure how i want it too exactly be / work.
*/

package com.packet;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;

import com.Constants;
import com.model.World;
import com.model.Player;
import com.model.Location;
import com.util.ItemManagement;

public class PacketDispatcher {
	static Calendar cal = new GregorianCalendar();
	static int day = cal.get(Calendar.DAY_OF_MONTH);
	static int month = cal.get(Calendar.MONTH) + 1;
	static int year = cal.get(Calendar.YEAR);
	static final String calc = (month + "-" + day + "-" + year);
	
	private int pingNumber = 0;
	private short index;

	public PacketDispatcher() {
	
	}

	public void setIndex(short index) {
		this.index = index;
	}

	private PacketBuilder getPacket() {
		return World.getPlayerByClientIndex(index).getPacket();
	}

	public void sendLogin() {
		sendMapRegion();
		sendPingPacket();
		sendWelcomeScreen();
		for (int i = 0; i < 23; i++)
			sendSkill(i);
		Player p = World.getPlayerByClientIndex(index);
		sendMultiItems(149,0,93,p.inventory().items,p.inventory().amounts);
		sendMultiItems(387,28,93,p.equipment().items,p.equipment().amounts);
		sendContactsStatus((byte)2);
		p.chat().sendList();
		sendChatTypes(p.chat().chatPublic, p.chat().chatPrivate, p.chat().chatTrade);
		sendTab(137, 90);
		int sidebar = ItemManagement.getSidebarInterface(p.equipment().items[3]);
		p.fightType().wield(sidebar);
		p.packetDispatcher().sendConfig(43, p.fightType().box);
		p.fightType().display(p);
		p.special().sendSpecialBar(sidebar);
		sendTab(sidebar, 99);
		sendTab(320, 100);
		sendTab(274, 101);
		sendTab(149, 102);
		sendTab(387, 103);
		sendTab(271, 104);
		sendTab(192, 105);
		sendTab(589, 106);
		sendTab(550, 107);
		sendTab(551, 108);
		sendTab(182, 109);
		sendTab(261, 110);
		sendTab(464, 111);
		sendTab(239, 112);
		sendConfig(304,0); 
		sendConfig(115,0);
		sendRightClickOption("Attack",1,true);
		sendRightClickOption("Follow",3,false);
		sendRightClickOption("Trade",4,false);
	}

	public void sendDisconnectRemovals() {		
		sendConfig(173, 0);
	}

	public void sendLogout() {
		World.addToLogoutQueue(World.getPlayerByClientIndex(index));
	}

	private void sendWelcomeScreen() {
		sendMessage("Welcome To " + Constants.SERVER_NAME + "!");
   		sendWindowPane(549);
		sendInterface(1,549,378,2);
		sendInterface(1,549,17,3);
		sendInterfaceString("Friend List - World 1",550,2);
		sendInterfaceString("Welcome to " + Constants.SERVER_NAME + "!",378,12);
		sendInterfaceString("The date today is <col=FF0000>" + calc +" <col=000000>",378,13);
		sendInterfaceString("You have not yet set any recovery questions. It <br> is <col=FF9040>strongly <col=FFFF00>recommended that you do so. If you <br> don't you will be <col=FF9040>unable to recover your <br><col=FF9040> password <col=FFFF00>if you forget it, or it is stolen.",378,14);
		sendInterfaceString("<br><br><br><br>" + Constants.SERVER_NAME +" Staff will NEVER email you. We use the <br> message centre on this server instead. <br><br> You have 0 unread messages in your <br> message centre.",378,15);
		sendInterfaceString("You do not have a Bank PIN. Please visit a bank <br> if you would like one.",378,17);
		sendInterfaceString("Remember to donate to RuneBit to keep it alive!",378,19);
		sendInterfaceString("Message of the day",17,2);	
		sendInterfaceString(Constants.LOGIN_MESSAGE(),17,1);
	}

	public void sendUnknownPacket4(byte slot,  byte id) {
		getPacket().createPacket(4).addByte(slot).addByte(id);
	}

	public void sendMinimapType(int type) {
		getPacket().createPacket(5).addByte((byte) type);
	}

	public void sendConfig2(int id, int value) {
		getPacket().createPacket(10).addLEInt(value).addShort(id);
	}

	public void sendChatTypes(byte chatPublic, byte chatPrivate, byte chatTrade) {
		getPacket().createPacket(15).addByte(chatPublic).
		addByte(chatPrivate).addByte(chatTrade);
	}

	public void sendTab(int interfaceId, int childId) {
		sendInterface(1,548,interfaceId,childId);
	}

	public void sendTab(int interfaceId, int childId, int location) {
		Player player = World.getPlayerByClientIndex(index);
		player.interfaceContainer().sidebar[location] = (short)interfaceId;
		sendInterface(1, 548, interfaceId, childId);
	}

	public void sendHideTabs(int id) {	
		sendInterface(0, 548, id, 97);
	}

	public void sendInterface(int id) {
		Player player = World.getPlayerByClientIndex(index);
		player.interfaceContainer().mainInterface = (short)id;
		sendInterface(0, 548, id, 77);
	}

	public void sendWalkableInterface(int id) {
		Player player = World.getPlayerByClientIndex(index);
		player.interfaceContainer().subInterface = (short)id;
		sendInterface(1, 548, id, 75);
	}

	public void sendInterface(int showId, int windowId, int interfaceId, int childId) {
		getPacket().createPacket(17).addByteS((byte)showId).
		addLEShort(interfaceId).addShort(childId).addShort(windowId);
	}	

	public void sendPlayerFaceOnInterface(int interfaceId, int child) {
		getPacket().createPacket(22).addInt(interfaceId << 16 | child);
	}

	public void sendContactsStatus(byte status) {
		getPacket().createPacket(30).addByte(status);
	}

	public void sendModelsOnInterface(int model, int interfaceId, int child) {
		getPacket().createPacket(33).addShortA(model).addInt1(interfaceId << 16 | child);
	}

	public void sendClanMessage(int length, byte[] encryptedMessage) {
		Player player = World.getPlayerByClientIndex(index);
		//player.getPacket().createPacket(35);
	}

	public void sendInterfaceImageDetails(int rotation, int size, int interfaceId, int child) {
		getPacket().createPacket(36).addShort(0).addLEShort(size).
		addInt(interfaceId << 16 | child).addLEShortA(rotation);
	}

	public void sendEnergy(byte energy) {
		getPacket().createPacket(42).addByte(energy);
	}

	public void sendMapRegion() {
		Player p = World.getPlayerByClientIndex(index);
		boolean forceSend = true;
		if(((p.location().regionX() / 8) == 48 || (p.location().regionX() / 8) == 49) && 
			p.location().regionY() / 8 == 48)
				forceSend = false;
		if((p.location().regionX() / 8) == 48 && (p.location().regionY() / 8) == 148)
				forceSend = false;
		p.getPacket().createPacketTypeShort(61).addLEShortA(p.location().localY());
		p.getPacket().addByte((byte)p.location().z).addShort(p.location().regionX());
		for (int x = (p.location().regionX() - 6) / 8; x <= (p.location().regionX() + 6) / 8; x++) {
			for (int y = (p.location().regionY() - 6) / 8; y <= (p.location().regionY() + 6) / 8; y++) {
				if (forceSend || ((y != 49) && (y != 149) && (y != 147) && 	
				   (x != 50) && ((x != 49) || (y != 47)))) {
					p.getPacket().addInt(0); //WE
					p.getPacket().addInt(0); //DONT
					p.getPacket().addInt(0); //HAVE
					p.getPacket().addInt(0); //474 XTEA.
				}
			}
		}
		p.getPacket().addShortA(p.location().localX()).addShort(p.location().regionY()).endPacketTypeShort();
		p.setLastLocation();
	}

	public void moveChildInterfaces(int interfaceId, int childId, int moveX, int moveY) {
		getPacket().createPacket(76).addLEShort(moveX).
		addLEInt(interfaceId << 16 | childId).addShortA(moveY);
	}

	public void sendRemoveMapFlag() {
		getPacket().createPacket(84);
	}

	public void sendInterfaceAnimation(int emote, int interfaceId, int child) {
		getPacket().createPacket(107).addInt2(interfaceId << 16 | child).addLEShort(emote);
	}

	public void sendMultiItems(int parentId, int childId, int type, short[] items, int[] itemsN) {
		PacketBuilder multiItemPacket = getPacket();
		multiItemPacket.createPacketTypeShort(119).addInt(parentId << 16 | childId).
			addShort(type).addShort(items.length);
		for (int i = 0; i < items.length; i++) {
			if (itemsN[i] > 254) {
				multiItemPacket.addByteC(255).addLEInt(itemsN[i]);
			} else {
				multiItemPacket.addByteC(itemsN[i]);
			}
			multiItemPacket.addLEShortA(itemsN[i] > 0 ? items[i] + 1 : 0);
		}
		multiItemPacket.endPacketTypeShort();
	}
	
	public void sendPlayMusic(int musicIdx) {
		getPacket().createPacket(121).addLEShortA(musicIdx);
	}

	public void sendNpcFaceOnInterface(int npc, int interfaceId, int child) {
		getPacket().createPacket(127).addShortA(npc).addInt(interfaceId << 16 | child);
	}

	public void setInterfaceOptions(int type, int parent, int child, int offset, int length) {
		getPacket().createPacket(133).addLEInt(type).addLEShortA(length)
		.addInt(parent << 16 | child).addShortA(offset);
	}

	public void sendAnimationReset() {
		getPacket().createPacket(138);		
	}

	public void resetInterfaceStrings(int interfaceId) {
		getPacket().createPacket(153).addInt1(interfaceId);
	}

	public void sendCameraReset() {
		getPacket().createPacket(156);
	}

	public void sendCoords(Location loc, Location last) {
		getPacket().createPacket(168).addByte((byte)loc.localX(last)).addByteA(loc.localY(last));
	}

	public void sendCoords(int x, int y) {
		Player p = World.getPlayerByClientIndex(index);
		p.getPacket().createPacket(168).addByte((byte)(x - (p.location().regionX() * 8))).
		addByteA((byte)(y - (p.location().regionY() * 8)));
	}

	protected void sendCoords2(int x, int y) {
		Player p = World.getPlayerByClientIndex(index);
		int regionX = p.lastLocation().regionX(),
	    	regionY = p.lastLocation().regionY();
		p.getPacket().createPacket(168).addByte((byte) (x - ((regionX - 6) * 8))).
		addByteA((byte) (y - ((regionY - 6) * 8)));
	}

	public void sendCloseInterface(int parentLocation) {
		System.out.println("Closing Interface: "+parentLocation);
		getPacket().createPacket(174).addInt(35913728 | parentLocation);
	}

	public void sendInterfaceConfig(int parentId, int childId, boolean show) {
		getPacket().createPacket(184).addByteA((byte)(show ? 0 : 1)).addInt(parentId << 16 | childId);
	}

	public void sendSingleItem(int interfaceId, int index, int type, int slot, int item, int itemN) {
		PacketBuilder singleItemPacket = getPacket().createPacketTypeShort(187);
		singleItemPacket.addInt(interfaceId << 16 | index);
		singleItemPacket.addShort(type).addSmart(slot);
		singleItemPacket.addShort(item + 1);
		if (itemN > 254) {
			singleItemPacket.addByte((byte)255).addInt(itemN);
		} else {
			singleItemPacket.addByte((byte)(itemN > 0 ? itemN : -1));
		}
		singleItemPacket.endPacketTypeShort();
	}

	public void sendCreateObject(Location loc, int id, int x, int y, int z, int dir, int type) {
		if (loc.z != z) 
			return;
		sendCoords2(x,y);
		getPacket().createPacket(188).
		addByte((byte)((type << 2) + (dir & 3))).
		addShortA(id).addByteS(0);		
	}

	public void sendSkill(int slot) {
		Player p = World.getPlayerByClientIndex(index);
		p.getPacket().createPacket(196).addByte((byte)slot).
		addInt2(p.skills().exp[slot]).addByte(p.skills().levels[slot]);	
	}

	public void sendSystemUpdate(int timeLeft) {
		getPacket().createPacket(207).addLEShort(timeLeft * 5 / 3);
	}

	public void sendMessage(String msg) {
		getPacket().createPacketTypeByte(209).addString(msg).endPacketTypeByte();
	}

	public void sendRightClickOption(String option, int slot, boolean top) {
		getPacket().createPacketTypeByte(225).addByteA(top ? 1 : 0).
		addString(option).addByteC(slot).endPacketTypeByte();
	}

	public void sendInterfaceString(String msg, int parentId, int childId) {
		getPacket().createPacketTypeShort(231).addInt2(parentId << 16 | childId).addString(msg).endPacketTypeShort();
	}

	public void sendInterfaceScript(int type, Object[] script, String order) {
		PacketBuilder interfaceScript = getPacket().createPacketTypeShort(237);	
		interfaceScript.addString(order);
		char[] orders = order.toCharArray();
		int spotIndex = -1;
		for (int i = order.length() - 1; i > -1; --i) {
			if (orders[i] == 115)
				interfaceScript.addString((String) script[++spotIndex]);
			else
				interfaceScript.addInt((Integer) script[++spotIndex]);
		}
		interfaceScript.addInt(type).endPacketTypeShort();
	}

	public void sendEnterInterface(int type, String msg) {
		getPacket().createPacketTypeShort(237).addString("s").addString(msg).addInt(type).endPacketTypeShort();
	}

	public void sendForceSelectTab(int tab) {
		getPacket().createPacketTypeShort(237).addString("i").addInt(tab).addInt(71).endPacketTypeShort();
	}

	public void runScriptInt(int idx, int type) {
		getPacket().createPacketTypeShort(237).addString("i").addInt(idx).addInt(type).endPacketTypeShort();
	}

	public void sendPingPacket() {
		getPacket().createPacketTypeShort(238).addInt(pingNumber++).endPacketTypeShort();
	}

	public void sendItemsOnInterface(int interfaceId, int child, int item, int size) {
		getPacket().createPacket(249).addInt1(interfaceId << 16 | child).
		addInt(size).addShort(item);
	}

	public void sendWindowPane(int pane) {
		getPacket().createPacket(251).addLEShort(pane);
	}

	public void sendTest(int interfaceID, int sidebar) {
		getPacket().createPacket(102).addLEShort(interfaceID).addByteC(sidebar);
	}
	public void sendConfig(int id, int value) {
		if (value < 128)
			sendConfig1(id, value);
		else
			sendConfig2(id, value);
	}

	public void sendConfig1(int id, int value) {
		getPacket().createPacket(253).addShort(id).addByteA(value);
	}

}