/*
* @ Author - Digistr.
* @ Info - NO GUI! This is simply an ingame editor which lets you scan items and insert them
*          into the GameItem container correctly for fast access items.
*/

package com.util;

import com.model.Player;

public class GameItemEditor {

	private static short[] cacheData;
	private static short spotIndex = 0;

   /*
   * Loads up the GameData currently being used.
   */
	public static void load(short[] data, int reads) {
		cacheData = data;
		spotIndex = (short)reads;
	}

   /*
   * Writes the new GameData you must do ::write to execute this.
   */
	public static void write() {
		FileBuilder data = new FileBuilder(spotIndex * 4);
		for (int i = 0; i < 11791; i++) {
			if (cacheData[i] != -1) { 
				data.writeShort(i);
				data.writeShort(cacheData[i]);
			}
		}
		FileManagement.writeFile("item/gameItems[EDIT].dat",data.array());
	}

    /*
    * Get's next available item who has not been given a spot in the Game Data.
    */
	public static int getNextAvailableIndex(int curIndex) {
		for ( ; ++curIndex < 11791; ) {
			if (cacheData[curIndex] == -1)
				break;
		}
		if (curIndex == 11791)
			curIndex = 0;
		return curIndex;
	}

   /*
   * insert's a element(ITEM ID) into the GameData with a new GameSlot.
   */
	public static void add(int element) {
		if (cacheData[element] == -1)
			cacheData[element] = spotIndex++;
	}

	public static void addPosition(int element, int index) {
		System.out.println("Adding Item: " + element + " At Location: " + index + " Game Item Size: " + spotIndex);
		cacheData[element] = (short)index;
		short lastItemSpot = -1;
		for (int i = 0; i < 11791; i++)
			if (cacheData[i] == index)
				lastItemSpot = (short)i;
		System.out.println("Old Item At This Location: " + lastItemSpot + " Old Item Index: " + cacheData[lastItemSpot]);
		if (lastItemSpot != -1) {
			for (int i = 0; i < 11791; i++) {
				if (cacheData[i] >= index) {
					++cacheData[i];
				}
			}
			int size = (cacheData.length - index > 4 ? 4 : cacheData.length - index - 1);
			System.out.println("Finished Incrementing - First " + size + " Items Above New Item: ");
			for (int i = 0; i < size; ++i) {
				System.out.println("Item [" + (index + 1 + i) + "] - " + cacheData[index + 1 + i]);
			}
				
			++spotIndex;
		}	
	}

   /*
   * remove's a element(ITEM ID) from the GameData and 
   * lowers all GameItem's who's index that are greater then this element's index.
   */
	public static void remove(int element) {
		short lastSpotIndex = cacheData[element];
		if (lastSpotIndex != -1) {
			cacheData[element] = -1;
			for (int i = 0; i < 11791; i++)
				if (cacheData[i] > lastSpotIndex)
					--cacheData[i];
			--spotIndex;
		}
	}


   /*
   * Insert's an element into a given spot in the GameData.
   * Will reorganize all data accordonally. 
   */
	public static void insert(int element, int spotIndex) {
		short lastSpotIndex = cacheData[element];
		if (lastSpotIndex == -1) {
			for (int i = 0; i < 11791; i++) {
				if (cacheData[i] >= spotIndex)
					++cacheData[i];
			}
			++spotIndex;
		} else if (lastSpotIndex > spotIndex) {
			for (int i = 0; i < 11791; i++) {
				if (cacheData[i] >= spotIndex && cacheData[i] < lastSpotIndex)
					++cacheData[i];
			}			
		} else if (lastSpotIndex < spotIndex) {
			for (int i = 0; i < 11791; i++) {
				if (cacheData[i] >= lastSpotIndex && cacheData[i] < spotIndex)
					--cacheData[i];
			}		
		}
	}

}