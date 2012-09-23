/*
* @ Author - Digistr.
* @ Info - Handles trading between 2 players.
*/

package com.model;

public class Trade {

	private short index;
	private long tradeWith = 0;
	private byte tradeStage = 0;
	private static short[] items = new short[28];
	private static int[] amounts = new int[28];
	private static final Object[] TRADE_SCRIPT_MY_ITEMS = {"", "", "", "Examine", "Offer-X", "Offer-All", "Offer-10", "Offer-5", "Offer", -1, 0, 7, 4, 93, 22020096};
        private static final Object[] TRADE_SCRIPT_INVENTORY = {"", "", "", "Examine", "Remove-X", "Remove-All", "Remove-10", "Remove-5", "Remove", -1, 0, 7, 4, 24, 21954609};
        private static final Object[] TRADE_SCRIPT_OTHERS_ITEMS = {"","","","","","","","","",-1, 0, 7, 4, 23, 21954610};
	
	protected Trade() {
		for (int i = 0; i < 28; i++)
			items[i] = -1;
	}

	public void setIndex(short index) {
		this.index = index;
	}

	public void open() {
		Player p = World.getPlayerByClientIndex(index);
		p.packetDispatcher().sendInterfaceScript(150, TRADE_SCRIPT_INVENTORY , "iiiiiisssssssss");
		p.packetDispatcher().sendInterfaceScript(150, TRADE_SCRIPT_MY_ITEMS , "iiiiiisssssssss");
		p.packetDispatcher().sendInterfaceScript(150, TRADE_SCRIPT_OTHERS_ITEMS , "iiiiiisssssssss");
		p.packetDispatcher().setInterfaceOptions(1086, 336,  0, 0, 27);
		p.packetDispatcher().setInterfaceOptions(1086, 335, 49, 0, 27);
		p.packetDispatcher().setInterfaceOptions(1086, 335, 50, 0, 27);
		p.packetDispatcher().sendMultiItems(-1,1,93,p.inventory().items,p.inventory().amounts);
		p.packetDispatcher().sendMultiItems(-1,1,24,p.inventory().items,p.inventory().amounts);
		p.packetDispatcher().sendMultiItems(-1,1,23,p.inventory().items,p.inventory().amounts);
		p.packetDispatcher().sendTab(336,97,0);
		p.packetDispatcher().sendInterface(335);
	}

    /*
    * Stage 0 = Not in trade, Stage 1 = Declined Trade, Stage 2 = First Window , Stage 3 = Accepted First Window.
    * Stage 4 = Second Window, Stage 5 = Accepted Second Window, Stage 6 = Completed Trade. (all stages of trade).
    */
	public void sendRequest(Player p, Player p2) {
		if (tradeStage == 1 || p.INDEX == p2.INDEX)
			return;
		if (p2.interfaceContainer().mainInterface != -1 && tradeWith != p2.details().USERNAME_AS_LONG) {
			p.packetDispatcher().sendMessage("This player is currently busy.");
			return;
		}
		if (tradeStage == 0) {
			if (p2.trade().tradeWith == p.details().USERNAME_AS_LONG) {
				tradeStage = 2;
				tradeWith = p2.details().USERNAME_AS_LONG;
				open();
				p2.trade().open();
			} else {
				tradeWith = p2.details().USERNAME_AS_LONG;
				p2.packetDispatcher().sendMessage(p.details().USERNAME_AS_STRING + ":tradereq:");
				p.packetDispatcher().sendMessage("sending trade offer....");
			}
		}
	}

	public void declineTrade(boolean declined) {
		if (tradeStage < 2 || tradeStage == 6) 
			return;
		boolean giveItems = tradeStage == 5;
		tradeStage = 1;
		Player me = World.getPlayerByClientIndex(index);
		Player other = World.getPlayerByName(tradeWith);
		if (declined) {
			me.packetDispatcher().sendMessage("You declined the trade.");
			other.trade().declineTrade(false);
		} else {
			me.packetDispatcher().sendMessage("Other player has declined the trade.");
		}
		resetVariables(me);
		
	}

	private void resetVariables(Player me) {	
		me.interfaceContainer().resetInterfaces(me,false);
		me.trade().tradeStage = 0;
		me.trade().tradeWith = 0;
	}

	public void flashIcon(int itemSlot, int index) {
		Player p = World.getPlayerByClientIndex(index);
		Object[] TRADE_SCRIPT_4 = new Object[] { itemSlot, 7, 4, 21954560 + index};
		p.packetDispatcher().sendInterfaceScript(143, TRADE_SCRIPT_4, "iiii");
	}
}