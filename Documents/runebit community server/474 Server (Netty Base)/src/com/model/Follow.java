package com.model;

import com.util.PathObjectSystem;
import com.util.Task;
import com.util.TaskQueue;


public class Follow {

	protected short followIndex;
	protected boolean block, canBlock;
	public byte region_speed = 0;

	private Task follow_task = new Task(0, false) {
		@Override
		public void execute() {

		}
	};

	private static final int[][] FACE_DIRS = {
		{1,-1} ,{0,-1}, {-1,-1},
		{1,0}  ,{0,0} , {-1,0},
		{1,1}  ,{0,1} , {-1,1}
	};

	public void setFollower(final Player me, short index) {
		followIndex = index;
		follow_task.die();
		follow_task = new Task(1, true) { 
			@Override
			public void execute() {
				region_speed = 0;
				move(me);
			}
		};
		TaskQueue.add(follow_task);
	}

	public void pause() {
		if (canBlock) {
			block = canBlock;
			canBlock = !canBlock;
		}
	}

	public void reset() {
		region_speed = 0;
		followIndex = -1;
	}

	public void move(Player me) {
		Player following = World.getPlayerByCheckingIndex(followIndex);
		if (following == null)
			return;
		boolean fighting = me.combat().inCombat;
		if (block && !fighting) {
			block = false;
			canBlock = false;
			return;
		}
		Location myloc = me.location();
		Location other = following.location();
		if (myloc.z != other.z) {
			followIndex = -1;
			return;
		}
		int difX = myloc.x - other.x;
		int difY = myloc.y - other.y;
		int absDifX = difX < 0 ? -difX : difX;
		int absDifY = difY < 0 ? -difY : difY;
		int p2dir = following.walkingQueue().faceDirection;
		if (absDifX < 2 && absDifY < 2) {
			if (!fighting)
				me.walkingQueue().walkToCoords(myloc, other.x + FACE_DIRS[p2dir][0], other.y + FACE_DIRS[p2dir][1]);
		} else {
			me.walkingQueue().walkToCoords(myloc, other.x + FACE_DIRS[p2dir][0], other.y + FACE_DIRS[p2dir][1]);
		}
	}
	
}