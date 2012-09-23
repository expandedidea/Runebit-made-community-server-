/*
* @ Author - Digistr.
*/

package com.model;

import java.util.ArrayDeque;
import java.util.Deque;
import com.util.Task;
import com.util.TaskQueue;
import com.util.ItemManagement;

public class Combat {

	private Task combat_task = new Task(0, false) {
		@Override
		public void execute() {

		}
	};

	private final Deque<Hit> HITS = new ArrayDeque<Hit>();
	public boolean inCombat = false;
	private byte delayToBeHit = 0;
	private byte delayToEat = 3;
	private byte delayToAttack = 4;
	private byte lastWeaponDelay = 4;
	private short attackingOn = -1;
	private short attackedBy = -1;
	private short index;

	protected Combat() {

	}

	public void setIndex(short index) {
		this.index = index;
	}

	public void reset() {
		inCombat = false;
	}

	public void clear() {
		HITS.clear();
		attackingOn = -1;
		attackedBy = -1;
		lastWeaponDelay = 4;
		delayToAttack = 5;
		delayToEat = 3;
		delayToBeHit = 0;
	}

	private class Hit {
		public final short INDEX;
		public final byte DAMAGE;
		public final byte COLOR;

		public Hit(short index, byte damage, byte color) {
			INDEX = index;
			DAMAGE = damage;
			COLOR = color;
		}
	}

	/*
	* Every old combat_task will be killed before the new one is created however your able to send multiple
	* Tasks per cycle iv'e tested regular clicking and you can get about 3-5 per cycle 1ms auto clicker gets about 25.
	* These number are very small and every cycle the old tasks are removed.
	*/
	public void execute() {
		combat_task.die();
		combat_task = new Task(1, true) { 
			@Override
			public void execute() {
				Player me = World.getPlayerByCheckingIndex(index);
				Player other = World.getPlayerByCheckingIndex(attackingOn);
				if (other == null || me == null || other.skills().levels[3] == 0) {
					if (me != null) {
						me.updateFlags().resetFaceTo();
						attackingOn = -1;
					}
					combat_task.die();
				}
				int distance = 1 + me.follow().region_speed;
				int rapid = 0;
				int delay = lastWeaponDelay - rapid;
				if ((delayToEat == 3 || delayToEat == delay) && inCombat) {
					if (other == null || me == null || other.skills().levels[3] == 0) {
						me.updateFlags().resetFaceTo();
						combat_task.die();
						attackingOn = -1;
					} else if (delayToAttack >= delay && me.location().getDistance(other.location()) <= (distance + me.walkingQueue().speed)) {
						lastWeaponDelay = ItemManagement.getWeaponSpeed(me.equipment().items[3]);
						me.updateFlags().setForcedAnimation(ItemManagement.getWeaponAnimation(me.equipment().items[3], me.fightType().box));
						other.setLastHitDelay((byte)17);
						other.combat().attackedBy = index;
						other.combat().delayToBeHit = (byte)(lastWeaponDelay - rapid + 10);
						delayToAttack = 0;
						delayToEat = 0;
						other.combat().HITS.add(new Hit(index, getMaxHit(), (byte)1));
					}
				}
			}
		};
		TaskQueue.add(combat_task);
	}

	private void change() {
		if (delayToBeHit > 0)
			--delayToBeHit;
		if (delayToAttack <= lastWeaponDelay)
			++delayToAttack;
		if (delayToEat < 3)	
			++delayToEat;
	}

	public void setAttackingOn(Combat c) {
		Player me = World.getPlayerByClientIndex(index);
		boolean mine = c.attackingOn == index || (attackedBy == c.index && (c.attackingOn == -1 || c.attackingOn == index));
		if ((c.delayToBeHit == 0 && delayToBeHit == 0) || mine) {
			if (!mine) {
				c.attackingOn = -1;
				c.attackedBy = index;
			}
			me.follow().setFollower(me, c.index);
			attackingOn = c.index;
			inCombat = true;
			execute();
		} else {
			me.packetDispatcher().sendMessage("My Details:  -- DELAY: " + delayToBeHit + " HIT BY: " + attackedBy);
			me.packetDispatcher().sendMessage("Other Player Details:  -- OP DELAY: " + c.delayToBeHit + " OP HIT BY: " + c.attackedBy);
		}
	}

	public void sendHits() {
		Hit h = null;
		Player me = World.getPlayerByClientIndex(index);
		change();
		while ((h = HITS.poll()) != null) {
			Player p = World.getPlayerByCheckingIndex(h.INDEX);
			if (p == null || p.skills().levels[3] == 0)
				continue;
			if (me.skills().levels[3] == 0) {
				return;
			}
			me.damage().dealDamage(h.DAMAGE, h.COLOR, p.details().USERNAME_AS_LONG);
		}
	}

	public static byte getMaxHit() {
		return (byte)0;
	}

}