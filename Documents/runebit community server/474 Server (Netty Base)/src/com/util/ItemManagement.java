/*
* @ Author - Digistr.
* @ Info - Handles everything to do with game items. 
* @ moreinfo - Yes it's all done using arrays and is very efficient. No loops required with this system.
*/

package com.util;

public class ItemManagement {

	public static short[] GAME_ITEMS = new short[11791];
	private static int[] STORE_VALUES = new int[11791];
	private static boolean[] STACKABLES = new boolean[11791];
	private static short[] NOTE_IDS = new short[11791];
	private static byte[] WIELD_LOCATIONS = new byte[2112];
	private static byte[] WEAPON_SPEEDS = new byte[594];
	private static short[] SIDEBAR_INTERFACES = new short[594];
	private static short[][] WEAPON_ANIMATIONS = new short[594][12];
	private static short[] DEFAULT_ANIMATION = {808,823,819,820,821,822,824,422,423,422,422,424};
	public static short[] WEAPONS = new short[594];

	public static int getNextItem(int index) {
		for (int i = 0; i < GAME_ITEMS.length; i++)
			if (GAME_ITEMS[i] == index)
				return index;
		return -1;
	}

	public static int storeValue(int item) {
		return STORE_VALUES[item];
	}

	public static int highAlchValue(int item) {
		return (int)(STORE_VALUES[item] * 0.6);
	}

	public static int lowAlchValue(int item) {
		return (int)(STORE_VALUES[item] * 0.4);
	}

	public static boolean isArrayStackable(int item) {
		if (item < 0)
			return false;
		return STACKABLES[item];
	}

	public static int getNoteId(int item) {
		if (item < 0)
			return -1;
		return NOTE_IDS[item];
	}

	public static boolean hasSpecial(int item) {
		if (item < 0 || item > 11790)
			return false;
		return GAME_ITEMS[item] < 665;	
	}

	public static byte getWieldLocation(int item) {
		int index = GAME_ITEMS[item];
		if (index < 0 || index > 2112)
			return -1;
		return WIELD_LOCATIONS[index];
	}

	public static short getSidebarInterface(int item) {
		if (item == -1 || item > 11790)
			return 92;
		int index = GAME_ITEMS[item] - 615;
		if (index < 0 || index > 593)
			return 92;
		System.out.println("Item: " + item + " Index: " + index + " Sidebar: " + SIDEBAR_INTERFACES[index]);
		return SIDEBAR_INTERFACES[index];
	}

	public static boolean showHead(int item) {
		if (item < 0)
			return false;
		return GAME_ITEMS[item] > -1 && GAME_ITEMS[item] < 141;
	}

	public static boolean removeBeard(int item) {
		if (item < 0)
			return false;
		return GAME_ITEMS[item] > 139 && GAME_ITEMS[item] < 249;
	}

	public static boolean showArms(int item) {
		if (item < 0)
			return true;
		return GAME_ITEMS[item] > 1386 && GAME_ITEMS[item] < 1417;
	}

	public static boolean isTwoHanded(int item) {
		if (item < 0)
			return false;
		return GAME_ITEMS[item] > 644 && GAME_ITEMS[item] < 812;
	}

	public static byte getWeaponSpeed(int item) {
		if (item == -1)
			return 4;
		return WEAPON_SPEEDS[GAME_ITEMS[item] - 615];
	}

	public static short getAppearenceAnimation(int item, int slot) {
		if (item == -1 || item > 11790)
			return DEFAULT_ANIMATION[slot];
		int index = GAME_ITEMS[item] - 615;
		if (index < 0 || index > 593)
			return DEFAULT_ANIMATION[slot];
		return WEAPON_ANIMATIONS[index][slot];
	}

	public static short getWeaponAnimation(int item, int box) {
		if (item < 0 || GAME_ITEMS[item] < 615 || GAME_ITEMS[item] > 1208)
			return DEFAULT_ANIMATION[box + 7];
		return WEAPON_ANIMATIONS[GAME_ITEMS[item] - 615][box + 7];
	}

	public static short getBlockAnimation(int item) {
		if (item < 0 || GAME_ITEMS[item] < 615 || GAME_ITEMS[item] > 1208)
			return DEFAULT_ANIMATION[11];
		return WEAPON_ANIMATIONS[GAME_ITEMS[item] - 615][11];		
	}

	public static void load() {	
		FileBuilder stacks = new FileBuilder(FileManagement.readFile("item/stackables.dat",8044));
		for (int readerIndex = -1; ++readerIndex < 4022;)
			STACKABLES[stacks.readShort()] = true;

		FileBuilder stores = new FileBuilder(FileManagement.readFile("item/shopvalues.dat",47164));
		for (int readerIndex = -1; ++readerIndex < 11791;)
			STORE_VALUES[readerIndex] = stores.readInt();

		for (int i = 0; i < 11791; ++i)
			GAME_ITEMS[i] = -1;
		int length = (int)(new java.io.File("data/item/gameitems.dat")).length();
		FileBuilder gameItems = new FileBuilder(FileManagement.readFile("item/gameitems.dat",length));
		for (int readerIndex = -1; ++readerIndex < (length / 4);) {
			short position = (short)gameItems.readShort();
			short item = (short)gameItems.readShort();
			GAME_ITEMS[item] = position;
			if (position > 614 && position < 1209)
				WEAPONS[position - 615] = item;
		}
		GameItemEditor.load(GAME_ITEMS, (length / 4));

		FileBuilder wieldLocs = new FileBuilder(FileManagement.readFile("item/equipslots.dat", 6339));
		for (int i = 0; i < 2112; i++) {
			int item = wieldLocs.readShort();
			byte loc = (byte)wieldLocs.readByte();
			WIELD_LOCATIONS[i] = loc;
		}

		FileBuilder wepAnims = new FileBuilder(FileManagement.readFile("item/wep_animations.dat", 54938));
		for (int i = 0; i < WEAPONS.length; i++) {
			wepAnims.readShort();
			for (int idx = 0; idx < 12; idx++) {
				WEAPON_ANIMATIONS[i][idx] = (short)wepAnims.readShort();
			}
		}

		for (int i = 0; i < WEAPON_SPEEDS.length; i++)
			WEAPON_SPEEDS[i] = (byte)4;

		FileBuilder sidebars = new FileBuilder(FileManagement.readFile("item/sidebars.dat",1190));
		for (int readerIndex = -1; ++readerIndex < 594;)
			SIDEBAR_INTERFACES[readerIndex] = (short)sidebars.readShort();

		FileBuilder notes = new FileBuilder(FileManagement.readFile("item/noteditems.dat",23582));
		for (int readerIndex = -1; ++readerIndex < 11791;)
			NOTE_IDS[readerIndex] = (short)notes.readShort();
	}

}