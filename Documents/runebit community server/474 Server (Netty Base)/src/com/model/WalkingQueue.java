/*
* @ Author - Digistr
* @ Objective - Handles everything to do with players walking.
*/

package com.model;

public class WalkingQueue {

	private short index;
	private byte firstTurnX, firstTurnY;
	private byte[][] steps = new byte[104][3];
	private byte readerIndex = 0;
	private byte readerLength = 0;
	public byte faceDirection;
	public boolean running = false;
	public byte runLeft = 100;
	public byte speed = 0;
	public byte walkDir = -1,runDir = -1;

   /*
   * Creates A Instance of the WalkingQueue.
   * @ index - this is the players index so we can use the player.
   * @ faceDirection - Direction player is currently facing my own version of it for player following.
   */
	protected WalkingQueue() {
		faceDirection = 7;
	}

	public void setIndex(short index) {
		this.index = index;
	}

   /*
   * Returns true if the player is able to run.
   */

	private boolean isRunning() {
		return running && runLeft > 0;
	}

   /*
   * Add steps with the first two coordinates read from Walking Packet.
   */
	public void setFirstDirection(short x, short y, Location l) {
		firstTurnX = (byte)(x - l.x);
		firstTurnY = (byte)(y - l.y);
		byte addX = 1;
		byte addY = 0;
		byte dirX = 2;
		byte dirY = 3;
		int addSteps = firstTurnX;
		if (addSteps == 0) {
			addX = 0;
			dirX = 1;	
		} else if(addSteps < 0) {
			addSteps = -firstTurnX;
			addX = -1;
			dirX = 0;
		} else {
			dirY = 2;
		}
		if (firstTurnY < 0) {
			if (-firstTurnY > addSteps)
				addSteps = -firstTurnY;
			dirY = 5;
			addY = -1;	
		} else if (firstTurnY > 0) {
			if (firstTurnY > addSteps)
				addSteps = firstTurnY;
			addY = 1;
			dirY = 0;
		}
		byte dir = (byte)(dirX + dirY);
		System.out.println("Dir: " + dir);
		for (int i = 0; i < addSteps; i++)
			addStepToQueue(addX, addY, dir);
		firstTurnX = 0;
		firstTurnY = 0;
	}

   /*
   * Adds Steps For each direction turned.
   */
	public void addDirectionChangeStep(byte x, byte y) {
		int difX = x - firstTurnX;
		int difY = y - firstTurnY;
		firstTurnX = x;
		firstTurnY = y;
		byte addX = 1;
		byte addY = 0;
		byte dirX = 2;
		byte dirY = 3;
		int addSteps = difX;
		if (addSteps == 0) {
			addX = 0;
			dirX = 1;	
		} else if(addSteps < 0) {
			addSteps = -difX;
			addX = -1;
			dirX = 0;
		} else {
			dirY = 2;
		}
		if (difY < 0) {
			if (-difY > addSteps)
				addSteps = -difY;
			addY = -1;
			dirY = 5;		
		} else if (difY > 0) {
			if (difY > addSteps)
				addSteps = difY;
			addY = 1;
			dirY = 0;
		}
		byte dir = (byte)(dirX + dirY);
		for (int i = 0; i < addSteps; i++)
			addStepToQueue(addX, addY, dir);
	}

   /*
   * Adds a single step to the walking queue.
   */
	private void addStepToQueue(byte x, byte y, byte dir) {
		if (readerLength >= steps.length)
			return;
		steps[readerLength][0] = x;
		steps[readerLength][1] = y;
		steps[readerLength++][2] = dir;
	}

   /*
   * Not Finished future objective to have a player walk to the coordinates.
   */
	public void walkToCoords(Location loc, int x, int y) {
		reset();
		int toX = x - loc.x;
		int toY = y - loc.y;
		int difX = toX < 0 ? -toX : toX;
		int difY = toY < 0 ? -toY : toY;
		int difference = difX;
		if (difX > difY) {
			if (toX < 0) {
				for (int i = 0; i < (difX - difY); ++i) {
					addStepToQueue((byte)-1, (byte)0, (byte)3);					
				}
			} else {
				for (int i = 0; i < (difX - difY); ++i) {
					addStepToQueue((byte)1, (byte)0, (byte)4);
				}
			}
			difference = difY;
		} else if (difY > difX) {
			if (toY > 0) {
				for (int i = 0; i < (difY - difX); ++i) {
					addStepToQueue((byte)0, (byte)1, (byte)1);
				}	
			} else {
				for (int i = 0; i < (difY - difX); ++i) {
					addStepToQueue((byte)0, (byte)-1, (byte)6);
				}
			}
		}
		if (toX > 0) {
			if (toY > 0) {
				for (int i = 0; i < difference; ++i) {
					addStepToQueue((byte)1, (byte)1, (byte)2);
				}
			} else {
				for (int i = 0; i < difference; ++i) {
					addStepToQueue((byte)1, (byte)-1, (byte)7);
				}
			}
		} else {
			if (toY > 0) {
				for (int i = 0; i < difference; ++i) {
					addStepToQueue((byte)-1, (byte)1, (byte)0);
				}
			} else {
				for (int i = 0; i < difference; ++i) {
					addStepToQueue((byte)-1, (byte)-1, (byte)5);
				}
			}
		}	
	}

   /*
   * walks to the next step(s) in the walking queue.
   */
	public void sendNextPosition() {
		Player player = World.getPlayerByClientIndex(index);
		if (player.isTeleporting()) {
			walkDir = -1;
			runDir = -1;
			reset();
			player.setTeleport();
			player.packetDispatcher().sendMessage("Reseting Teleporting..");
		} else {
			speed = 0;
			walkDir = getNextDir(player);
			if (walkDir != -1) {
				speed = 1;
			} else {
				player.follow().pause();
			}
			runDir = -1;
			if (isRunning() && speed == 1) {
				runDir = getNextDir(player);
				if (runDir != -1) {
					speed = 2;
					runLeft--;
				}
				if (runLeft == 0) {
					running = false;
					player.packetDispatcher().sendConfig(173, 0);
				}
			} else {
				runLeft += (2 - speed);
				if (runLeft > 100)
					runLeft = 100;
			}
			player.packetDispatcher().sendEnergy(runLeft);
			if (speed == 0) {
				player.packetDispatcher().sendRemoveMapFlag();
			}
		}
		int differenceX = player.lastLocation().regionX() - player.location().regionX();
		int differenceY = player.lastLocation().regionY() - player.location().regionY();
		if (differenceX > 3)
			player.updateFlags().flagsAreUpdated[0] = true;
		if (differenceY > 3)
			player.updateFlags().flagsAreUpdated[0] = true;
		if (differenceX < -3)
			player.updateFlags().flagsAreUpdated[0] = true;
		if (differenceY < -3)
			player.updateFlags().flagsAreUpdated[0] = true;
		if (player.updateFlags().flagsAreUpdated[0]) {
			if (walkDir != -1) {
				player.follow().region_speed = 1;
				player.location().minus(steps[--readerIndex][0],steps[readerIndex][1]);
			}
			if (runDir != -1) {
				player.follow().region_speed = 2;
				player.location().minus(steps[--readerIndex][0],steps[readerIndex][1]);
			}
			walkDir = -1;
			runDir = -1;
		}
	}

   /*
   * Gets the next direction of the walking queue and stores the players new position.
   * Along with storing the next face direction used in player follow.
   */
	private byte getNextDir(Player player) {
		if (readerIndex == readerLength) {
			return -1;
		}
		boolean walkable = com.util.PathObjectSystem.isWalkable(player.location().x, player.location().y, player.location().x + steps[readerIndex][0],player.location().y + steps[readerIndex][1],player.location().z,steps[readerIndex][2]);
		player.location().add(steps[readerIndex][0],steps[readerIndex][1]);
		byte[] faceDirs = {0,1,2,3,5,6,7,8,4};
		faceDirection = faceDirs[steps[readerIndex][2]];
		return steps[readerIndex++][2];
	}

   /*
   * Resets the walking queue.
   */
	public void reset() {
		Player player = World.getPlayerByClientIndex(index);
		readerIndex = 0;
		readerLength = 0;
		steps[0][0] = 0;
		steps[0][1] = 0;
		steps[0][2] = -1;
	}
	
}