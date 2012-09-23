/*
* @ Author - Digistr
* @ info - none.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;
import com.model.World;
import com.util.GameItemEditor;
import com.util.GroundItemManager;
import com.util.ItemManagement;
import com.util.GameUtility;

public class Commands implements PacketAssistant {

	public static int commandItem = -1;
	public static int commandAnim = 0;
	public static boolean fixup = false;
	public static int next = -1;
	public static int oI,oF,oT,oS;

	public Commands() {

	}

	public void send(Player p, Packet packet){
	    try {
		String command = packet.readString();
		String cmd[] = command.split(" ");
		cmd[0] = GameUtility.toLowerCase(cmd[0]);
		if (cmd[0].equals("interface")) {
			if (cmd.length > 1) {
				int location = 77;
				if (cmd.length == 3)
					location = Integer.valueOf(cmd[2]);
				int interfaceId = Integer.valueOf(cmd[1]);
				p.packetDispatcher().sendInterface(0,548,interfaceId,location);
			}
		} else if (cmd[0].equals("yellall")) {
			String message = command.substring(5);
			for (int i = 0; i < World.curIndex; ++i) {
				for (int j = 0; j < World.curIndex; j++) {
					World.players[i].packetDispatcher().sendMessage("[" + GameUtility.longToString(World.players[j].details().CUSTOM_USERNAME) + "]: " + message);
				}
			}
		} else if (cmd[0].equals("walkto")) {
			int x = Integer.parseInt(cmd[1]);
			int y = Integer.parseInt(cmd[2]);
			p.walkingQueue().walkToCoords(p.location(), x, y);	
		} else if (cmd[0].equals("killall")) {
			for (int i = 0; i < World.curIndex; i++) {
				World.players[i].damage().dealDamage(99, "green", 0);
				World.players[i].damage().dealDamage(99, "red", 0);
			}
		} else if (cmd[0].equals("fight")) {
			short clientIndex = (short)Integer.parseInt(cmd[1]);
			Player other = World.getPlayerByCheckingIndex(clientIndex);
			if (other != null && other != p) {
				p.combat().setAttackingOn(other.combat());
				p.updateFlags().setFaceToPlayer(other.INDEX);
				other.combat().setAttackingOn(p.combat());
				other.updateFlags().setFaceToPlayer(p.INDEX);
			} else {
				p.packetDispatcher().sendMessage("invalid index to fight..");
			}
		} else if (cmd[0].equals("comboall")) {
			int anim = Integer.parseInt(cmd[1]);
			int gfx = Integer.parseInt(cmd[2]);
			for (int i = 0; i < World.curIndex; i++) {
				World.players[i].updateFlags().setAnimation(anim, 0);
				World.players[i].updateFlags().setGraphic((short)gfx, 0, 80);
				World.players[i].packetDispatcher().sendMessage("Emote: " + anim + " Gfx: " + gfx);
			}
		} else if (cmd[0].equals("teleall")) {
			int z = p.location().z;
			int y = p.location().y;
			int x = p.location().x;
			if (cmd.length == 2) {
				z = Integer.valueOf(cmd[1]);
			}
			else if (cmd.length == 3) {
				x = Integer.valueOf(cmd[1]);
				y = Integer.valueOf(cmd[2]);
			}
			else if (cmd.length == 4) {
				x = Integer.valueOf(cmd[1]);
				y = Integer.valueOf(cmd[2]);
				z = Integer.valueOf(cmd[3]);
			}
			int k = 0;
			int j = 0;
			for (int i = 0; i < World.curIndex; i++) {
				if (k == 16) {
					k = 0;
					++j;
				}
				World.players[i].setTeleport(x + k, y + j, z);
			}
		} else if (cmd[0].equals("test")) {
			int size = p.chat().FRIENDS.size();
			for (long l = size; l < size + 10; l++)
				p.chat().addFriend(GameUtility.stringToLong("Friend: " + l));
		} else if (cmd[0].equals("yell") && command.length() > 5) {
			String message = command.substring(5);
			for (int i = 0; i < World.curIndex; ++i) {
				World.players[i].packetDispatcher().sendMessage("[" + GameUtility.longToString(p.details().CUSTOM_USERNAME) + "]: " + message);
			}
		} else if (cmd[0].equals("name")) {
			if (command.length() > 5) {
				p.details().createCustomName(command.substring(5));
				p.packetDispatcher().sendMessage("Your new username: " + GameUtility.longToString(p.details().CUSTOM_USERNAME));
				p.updateFlags().forceAppearence();
			}
		} else if (cmd[0].equals("equip")) {
			int slot = Integer.parseInt(cmd[1]);
			short item = p.inventory().items[slot];
			int amount = p.inventory().amounts[slot];
			p.equipment().wield(item, amount, slot);
		} else if (cmd[0].equals("hit")) {
			int damage = 0;
			String colour = "red";
			if (cmd.length > 1)
				damage = Integer.parseInt(cmd[1]) & Integer.MAX_VALUE;
			if (cmd.length > 2) {
				if (cmd[2].length() > 6)
					colour = "red";
				else
					colour = cmd[2];
			}
			p.damage().dealDamage(damage, colour, 0);
		} else if (cmd[0].equals("string")) {
			int interfaceId = Integer.parseInt(cmd[1]);
			p.packetDispatcher().sendInterface(0, 548, interfaceId, 77);
			int start = Integer.parseInt(cmd[2]);
			int end = start;
			String message = "";
			if (cmd.length > 4) {
				end = Integer.parseInt(cmd[3]);
				message = command.substring(command.indexOf(cmd[4]));
			} else {
				message = command.substring(command.indexOf(cmd[3]));
			}
			for (int child = start; child <= end; ++child)
				p.packetDispatcher().sendInterfaceString(message, interfaceId, child);
			if (cmd.length > 4)
				p.packetDispatcher().sendMessage("Stored String: " + message + " On Interface: " + interfaceId + " Childs: " + start + " - " + end);
			else
				p.packetDispatcher().sendMessage("Stored String: " + message + " On Interface: " + interfaceId + " On Child: " + start);
		} else if (cmd[0].equals("rsint")) {
			int idxBeg = cmd.length > 1 ? Integer.parseInt(cmd[1]) : 0;
			int idxEnd = cmd.length > 2 ? Integer.parseInt(cmd[2]) : 255;
			int typeBeg = cmd.length > 3 ? Integer.parseInt(cmd[3]) : 0;
			int typeEnd = cmd.length > 4 ? Integer.parseInt(cmd[4]) : 255;
			for (int i = idxBeg; i <= idxEnd; i++) {
				for (int j = typeBeg; j <= typeEnd; j++) {
					p.packetDispatcher().runScriptInt(i, j);
				}
			}
			System.out.println("Send Type: " + typeBeg + " - " + typeEnd + " Send Index: " + idxBeg + " - " + idxEnd);
		} else if (cmd[0].equals("deadtype")) {
			oS = Integer.parseInt(cmd[1]);
		} else if (cmd[0].equals("gc")) {
			System.gc();
		} else if (cmd[0].equals("superobj")) {
			String msg = "[INVALID FORMAT] - ::superobj [Id] [Face] [Type]";
			if (cmd.length == 4) {
				oI = Integer.parseInt(cmd[1]);
				oF = Integer.parseInt(cmd[2]);
				oT = Integer.parseInt(cmd[3]);
				p.packetDispatcher().sendMessage("[Speed Object Set] - ID: " + oI + " , F: " + oF + " , T: " + oT);
			} else {
				p.packetDispatcher().sendMessage(msg);
			}
		} else if (cmd[0].equals("object")) {
			String msg = "[INVALID FORMAT] - ::object [Id] [Face] [Type]";
			if (cmd.length == 4) {
				int id = Integer.parseInt(cmd[1]);
				int face = Integer.parseInt(cmd[2]);
				int type = Integer.parseInt(cmd[3]);
				p.packetDispatcher().sendCreateObject(p.location(),id,p.location().x,p.location().y,p.location().z,face,type);
				p.packetDispatcher().sendMessage("[Object Spawned] - ID: " + id + " X: " + p.location().x + " Y: " + p.location().y + " Face: " + face + " Type: " + type);
			} else {
				p.packetDispatcher().sendMessage(msg);
			}
		} else if (cmd[0].equals("myitems")) {
			String msg = "";
			int total = 0;
			for (int i = 0; i < 28; i++) {
				if (p.inventory().items[i] != -1 || p.inventory().amounts[i] != 0) {
					msg = msg + "(Item: " + p.inventory().items[i] + " Amount: " + p.inventory().amounts[i] + " Slot: " + i + ") , ";
					++total;
				}
				if (total == 2) {
					p.packetDispatcher().sendMessage(msg);
					msg = "";
					total = 0;
				}
			}
			if (!msg.equals("")) {
				p.packetDispatcher().sendMessage(msg);
			}
			p.packetDispatcher().sendMessage("Available Spots: " + p.inventory().availableInventorySpace);
		} else if (cmd[0].equals("music")) {
			if (cmd.length == 2) {
				int id = Integer.parseInt(cmd[1]);
				p.packetDispatcher().sendPlayMusic(id);
			}
		} else if (cmd[0].equals("intdata")) {
			String s = p.interfaceContainer().toString();
			p.packetDispatcher().sendMessage(s);
		} else if (cmd[0].equals("script")) {
			Object[] data = new Object[cmd[1].length()];
			int type = Integer.valueOf(cmd[2]);
			if (data.length + 3 == cmd.length) {
				for (int i = 0; i < data.length; i++) {
					if (i == data.length || i+3 == cmd.length) {
						System.out.println("Breaking Script ERROR!");
						break;
					}
					if (cmd[1].charAt(i) == 115)
						data[i] = cmd[3 + i];
					else
						data[i] = Integer.valueOf(cmd[3 + i]);
				}
				p.packetDispatcher().sendInterfaceScript(type,data,cmd[1]);
				p.packetDispatcher().sendMessage("Script: "+command.substring(6)+" Data Length: "+data.length+" Type: "+type+" , order: "+cmd[1]);
			}
		} else if (cmd[0].equals("rs") && cmd.length == 3) {
			int start = Integer.parseInt(cmd[1]);
			int end = Integer.parseInt(cmd[2]);
			for (int i = start; i <= end; i++) {
				p.packetDispatcher().sendEnterInterface(i,"T: "+i);
			}
			p.packetDispatcher().sendMessage("Enter Interfaces - "+start+" through "+end);
		} else if (cmd[0].equals("iconfig") && cmd.length == 4) {
			int interfaceId = Integer.valueOf(cmd[1]);
			int child = Integer.valueOf(cmd[2]);
			boolean bool = cmd[3].equals("true");
			p.packetDispatcher().sendInterfaceConfig(interfaceId, child, bool);
		} else if (cmd[0].equals("supericonfig") && cmd.length == 5) {
			int interfaceId = Integer.valueOf(cmd[1]);
			int start = Integer.valueOf(cmd[2]);
			int end = Integer.valueOf(cmd[3]);
			boolean bool = cmd[4].equals("true");
			for (int i = start; i < end; i++)
				p.packetDispatcher().sendInterfaceConfig(interfaceId,i,bool);
			p.packetDispatcher().sendMessage("Interface Config ["+interfaceId+"] Set Childs: "+start+" Through "+end+" to "+bool);
		} else if (cmd[0].equals("coords")) {
			p.packetDispatcher().sendMessage("[Position] - X: "+p.location().x+" Y: "+p.location().y+" Z: "+p.location().z+" LocalX: "+p.location().localX()+" LocalY: "+p.location().localY());
		} else if (cmd[0].equals("tele")) {
			int z = p.location().z;
			int y = p.location().y;
			int x = p.location().x;
			if (cmd.length == 2) {
				z = Integer.valueOf(cmd[1]);
			}
			else if (cmd.length == 3) {
				x = Integer.valueOf(cmd[1]);
				y = Integer.valueOf(cmd[2]);
			}
			else if (cmd.length == 4) {
				x = Integer.valueOf(cmd[1]);
				y = Integer.valueOf(cmd[2]);
				z = Integer.valueOf(cmd[3]);
			}
			p.setTeleport(x,y,z);
		} else if (cmd[0].equals("turn")) {
			short x = (short)Integer.parseInt(cmd[1]);
			short y = (short)Integer.parseInt(cmd[2]);
			p.updateFlags().setTurnToDirection(x,y);
		} else if (cmd[0].equals("face")) {
			int index = Integer.parseInt(cmd[1]);
			p.updateFlags().setFaceToPlayer(index);
		} else if (cmd[0].equals("combo")) {
			if (cmd.length == 3) {
				int anim = Integer.parseInt(cmd[1]);
				int gfx = Integer.parseInt(cmd[2]);
				p.updateFlags().setAnimation(anim, 0);
				p.updateFlags().setGraphic((short)gfx, 0, 80);
				p.packetDispatcher().sendMessage("Emote: " + anim + " Gfx: " + gfx);
			}
		} else if (cmd[0].equals("anim")) {
			int anim = commandAnim;
			int delay = 0;
			if (cmd.length > 1) {
				anim = Integer.valueOf(cmd[1]);
				if (cmd.length == 3)
					delay = Integer.valueOf(cmd[2]);
				p.updateFlags().setAnimation(anim, delay);
			} else {
				p.updateFlags().setAnimation(anim, 0);
				++commandAnim;
			}
			p.packetDispatcher().sendMessage("Animation: " + anim);
		} else if (cmd[0].equals("gfx")) {
			int gfx = commandAnim;
			int delay = 0;
			int height = 80;
			if (cmd.length > 1) {
				gfx = Integer.valueOf(cmd[1]);
				if (cmd.length > 2) {
					delay = Integer.valueOf(cmd[2]);
					if (cmd.length == 4)
						height = Integer.valueOf(cmd[3]);
				}
				p.updateFlags().setGraphic((short)gfx, delay, height);
			} else {
				p.updateFlags().setGraphic((short)gfx, 0, 80);
				++commandAnim;
			}
			p.packetDispatcher().sendMessage("Gfx: " + gfx);
		} else if (cmd[0].equals("a")) {
			p.packetDispatcher().runScriptInt(137, ++commandAnim);
			p.packetDispatcher().sendMessage("Index 137 , Type: "+commandAnim);
		} else if (cmd[0].equals("superdrop")) {
			int x = 3200;
			int y = 3200;
			byte z = 0;
			long t1 = System.nanoTime();
			for (int i = -45; i < 50; i++) {
				for (int j = 0; j < 45; j++) {
					GroundItemManager.add(p,(short)4151,1,(short)(x+i),(short)(y+j),z);
				}
			}
			long t2 = System.nanoTime();
			System.out.println("Adding 4320 Items To The Ground In: "+(t2-t1));
		} else if (cmd[0].equals("item")) {
			if (cmd.length == 2) {
				p.inventory().add(Integer.valueOf(cmd[1]),1);
			} else if (cmd.length == 3) {
				p.inventory().add(Integer.valueOf(cmd[1]),Integer.valueOf(cmd[2]));
			}
		} else if (cmd[0].equals("delete")) {
			if (cmd.length == 2) {
				p.inventory().delete(Integer.valueOf(cmd[1]),1);
			} else if (cmd.length == 3) {
				p.inventory().delete(Integer.valueOf(cmd[1]),Integer.valueOf(cmd[2]));
			}
		} else if (cmd[0].equals("empty")) {
			p.inventory().dispose();
		} else if (cmd[0].equals("config")) {
			if (cmd.length == 3) {
				p.packetDispatcher().sendConfig(Integer.valueOf(cmd[1]),Integer.valueOf(cmd[2]));
			}
		} else if (cmd[0].equals("superconfig")) {
			if (cmd.length == 4) {
				int start = Integer.valueOf(cmd[1]);
				int end = Integer.valueOf(cmd[2]);
				int set = Integer.valueOf(cmd[3]);
				for (int i = start; i <= end; i++)
					p.packetDispatcher().sendConfig(i, set);
				p.packetDispatcher().sendMessage("Sending Configs: "+start+" - "+end+" To Value: "+set);
			}
		} else if (cmd[0].equals("write")) {
			GameItemEditor.write();
		} else if (cmd[0].equals("add")) {
			if (commandItem > -1 && commandItem < 11791) {
				if (cmd.length == 1) {
					GameItemEditor.add(commandItem);
				} else if (cmd.length == 2) {
					int item = Integer.parseInt(cmd[1]);
					GameItemEditor.add(item);
				} else if (cmd.length == 3) {
					int start = Integer.parseInt(cmd[1]);
					int end = Integer.parseInt(cmd[2]);
					for (int i = start; i < end; i++)
						GameItemEditor.add(i);
				}
			}
		} else if (cmd[0].equals("addpos")) {
			if (cmd.length == 3) {
				int item = Integer.parseInt(cmd[1]);
				int position = Integer.parseInt(cmd[2]);
				GameItemEditor.addPosition(item, position);
			}
		} else if (cmd[0].equals("remove")) {
			if (cmd.length == 2) {
				int item = Integer.parseInt(cmd[1]);
				GameItemEditor.remove(item);				
			} else if (cmd.length == 3) {
				int start = Integer.parseInt(cmd[1]);
				int end = Integer.parseInt(cmd[2]);
				for (int i = start; i < end; i++)
					GameItemEditor.remove(i);
			}
		} else if (cmd[0].equals("setcmd")) {
			if (cmd.length == 2) 
				commandAnim = Integer.parseInt(cmd[1]);
		} else if (cmd[0].equals("next")) {
			int item = -1;	
			p.inventory().dispose();
			for (int i = 0; i < 28; i++) {
				item = ItemManagement.getNextItem(commandItem++);
				p.inventory().add(item, 1);
			}
			p.packetDispatcher().sendMessage("Send Item Index's "+ (commandItem - 28) +" Through "+ (commandItem - 1));
		} else if (cmd[0].equals("set")) {
			if (cmd.length == 2) 
				commandItem = Integer.parseInt(cmd[1]);
			p.packetDispatcher().sendMessage("Command Value Set To: "+commandItem);
		} else if (cmd[0].equals("bank")) {
			p.bank().open();
		} else if (cmd[0].equals("trade")) {
			p.trade().open();
		} else if (cmd[0].equals("t1")) {
			for (int i = 0; i < 28; i++) {
				System.out.println("Item: "+p.inventory().items[i]+" Amount: "+p.inventory().amounts[i]+" Idx: "+i);
			}
		} else if (cmd[0].equals("setlevel")) {
			if (cmd.length == 3) {
				p.skills().setLevel(Integer.parseInt(cmd[1]),Integer.parseInt(cmd[2]));
				p.packetDispatcher().sendSkill(Integer.parseInt(cmd[1]));
			} else {
				p.packetDispatcher().sendMessage("Invalid setLevel Args..");
			}
		}
	    } catch (Exception e) { 

	    }
	}
}