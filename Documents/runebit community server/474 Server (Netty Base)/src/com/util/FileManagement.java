/*
* @ Author - Digistr.
* @ Info - Handles All File Loaders. 
* @ Objectives - Finish This when i figure out the best way to load / write information.
*/

package com.util;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Arrays;
import com.model.Location;
import com.model.Player;

public class FileManagement {

	public static Location[] MAP_REGIONS;
	public static int nextLocation = -1;

	public static Location getMap() {
		if (MAP_REGIONS == null)
			loadMaps();
		nextLocation += 2;
		if (nextLocation >= MAP_REGIONS.length)
			nextLocation = 1;
		return MAP_REGIONS[nextLocation];
	}
	
	public static void loadMaps() {
		MAP_REGIONS = new Location[1884];
		for (Location map : MAP_REGIONS)
			map = new Location(3086, 3486, 0);
		try {
		BufferedReader loadFile = new BufferedReader(new FileReader("data/debug/FloorMaps.txt"));
		String data = null;
			while ((data = loadFile.readLine()) != null) {
				String s[] = data.split(",");
				int id = Integer.valueOf(s[0]);
				MAP_REGIONS[id] = new Location(Short.valueOf(s[1]), Short.valueOf(s[2]), 0);
			}
		} catch (Exception e){ }		
	}

	public static void savePlayer(Player p) {
	   try {
		FileBuilder file = new FileBuilder(10000);
		file.skipBytes(2);
		file.writeString(p.details().PASSWORD);
		file.writeShort(0);
		file.writeShort(0);
		file.writeShort(0);
		file.writeByte((byte)0);
		file.writeByte((byte)0);
		file.writeByte((byte)0);
		for (int i = 0; i < 28; i++) {
			file.writeShort(p.inventory().items[i]);
			file.writeInt(p.inventory().amounts[i]);
		}
		file.writeByte(p.inventory().availableInventorySpace);
		for (int i = 0; i < 14; i++) {
			file.writeShort(p.equipment().items[i]);
			file.writeInt(p.equipment().amounts[i]);
		}
		for (int i = 0; i < 23; i++) {
			file.writeByte(p.skills().levels[i]);
			file.writeByte(p.skills().mainLevels[i]);
			file.writeInt(p.skills().exp[i]);
		}
		for (int i = 0; i < 400; i++) {
			file.writeShort(p.bank().items[i]);
			file.writeInt(p.bank().amounts[i]);	
		}
		for (int i = 2; i < 15; i++) {
			file.writeByte((byte)p.updateFlags().getFeature(i));
		}
		file.writeByte((byte)p.chat().FRIENDS.size());
		Iterator iterator = p.chat().FRIENDS.keySet().iterator();
		while (iterator.hasNext()) {
			long key = (Long)iterator.next();
			Byte rank = p.chat().FRIENDS.get(key);
			file.writeLong(key);
			file.writeByte(rank);
		}
		file.writeByte((byte)p.chat().IGNORES.size());
		for (long name : p.chat().IGNORES)
			file.writeLong(name);
		file.writeByte(p.chat().chatPublic);
		file.writeByte(p.chat().chatPrivate);
		file.writeByte(p.chat().chatTrade);
		file.writeByte(p.chat().chatClan);
		file.writeByte(p.fightType().box);
		file.writeByte(p.walkingQueue().faceDirection);
		file.writeShort(p.location().x);
		file.writeShort(p.location().y);
		file.writeShort(p.location().z);
		int length = file.writeLength();
		DataOutputStream dos = new DataOutputStream(new FileOutputStream("data/characters/"+p.details().USERNAME_AS_STRING));
		dos.write(file.array(),0,length);
		dos.close();
	    } catch (IOException io){ }
	}

	public static byte loadPlayer(Player p) {
		try {
			DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream("data/characters/"+p.details().USERNAME_AS_STRING)));
			int length = dis.readShort();
			if (length < 2) {
				p.details().setNewPlayer();
				p.updateFlags().forceAppearence();
				return 2;
			}
			byte[] data = new byte[length - 2];
			dis.readFully(data, 0, data.length);
			FileBuilder file = new FileBuilder(data);
			char[] password = file.readString().toCharArray();
			int featuresByteLength = file.readShort();
			int featuresIntLength = file.readShort();
			int featuresLongLength = file.readShort();
			int totalQuests = file.readByte();
			int totalFriends = file.readByte();
			int totalIgnores = file.readByte();
			if (!Arrays.equals(password,p.details().PASSWORD)) {
				dis.close();
				return 3;
			}
			for (int i = 0; i < 28; i++) {
				p.inventory().items[i] = (short)file.readShort();
				p.inventory().amounts[i] = file.readInt();
			}
			p.inventory().availableInventorySpace = (byte)file.readByte();
			for (int i = 0; i < 14; i++) {
				p.equipment().items[i] = (short)file.readShort();
				p.equipment().amounts[i] = file.readInt();				
			}
			for (int i = 0; i < 23; i++) {
				p.skills().levels[i] = (byte)file.readByte();
				p.skills().mainLevels[i] = (byte)file.readByte();
				p.skills().exp[i] = file.readInt();
			}
			for (int i = 0; i < 400; i++) {
				p.bank().items[i] = (short)file.readShort();
				p.bank().amounts[i] = file.readInt();				
			}
			short[] looks = new short[13];
			for (int i = 0; i < 13; i++) {
				looks[i] = (short)file.readByte();
			}
			int friend_size = file.readByte();
			for (int i = 0; i < friend_size; i++)
				p.chat().FRIENDS.put(file.readLong(), (byte)file.readByte());		
			int ignore_size = file.readByte();
			for (int i = 0; i < ignore_size; i++)
				p.chat().IGNORES.add(file.readLong());	
			p.chat().chatPublic = (byte)file.readByte();
			p.chat().chatPrivate = (byte)file.readByte();
			p.chat().chatTrade = (byte)file.readByte();
			p.chat().chatClan = (byte)file.readByte();
			p.fightType().box = (byte)file.readByte();
			p.walkingQueue().faceDirection = (byte)file.readByte();
			p.location().setCoords((short)file.readShort(),(short)file.readShort(),(short)file.readShort());
			p.updateFlags().setLooks(looks);
			dis.close();
			return 2;
		} catch (IOException io) {

		}
		p.details().setNewPlayer();
		p.updateFlags().forceAppearence();
		return 2;
	}

	public static byte[] readFile(String s, int length) {
		byte[] bytes = new byte[length];
		try {
			DataInputStream data = new DataInputStream(new BufferedInputStream(new FileInputStream("data/" + s)));
        		data.readFully(bytes,0,length);
			data.close();
		} catch (IOException io) {
		
		}
		return bytes;
	}

	public static void writeFile(String s, byte[] data) {
		try {
			DataOutputStream dos = new DataOutputStream(new FileOutputStream("data/" + s));
        		dos.write(data);
			dos.close();
		} catch (IOException io) {
		
		}
	}

	public static void writeFile(String s, byte[] data, int length) {
		try {
			DataOutputStream dos = new DataOutputStream(new FileOutputStream("data/" + s));
        		dos.write(data,0,length);
			dos.close();
		} catch (IOException io) {
		
		}
	}

}