/*
* @ Author - Digistr.
* @ Info - Taken right from the some Netty tuturial and coverted to fit my needs.
* @ Objective - channelDisconnect() & channelExeception() will disconnect a player from main World List when these are called.
*/

package com.codec;

import java.net.InetSocketAddress;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import com.model.Player;
import com.packet.Packet;
import com.model.World;

public class ChannelHandler extends SimpleChannelHandler {

	private short index = 0;

	public ChannelHandler() {

	}

      /*
      * Sets the players index as an attachment to this handler.
      */ 
	public void setAttachment(short index) {
		this.index = index;
	}

      /*
      * Each method is sent when it's name happens.
      */
	@Override
	public void channelConnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) {
	
	}

	@Override
	public void channelDisconnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) {
		if (index > 0) {
			Player player = World.getPlayerByCheckingIndex(index);
			if (player != null && player.details().isHandled()) {
				if (player.canDisconnect()) {
					player.packetDispatcher().sendDisconnectRemovals();
					World.remove(player);
				}
			}
		}
	}

	@Override
	public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) {
		if (index > 0) {
			Player player = World.getPlayerByCheckingIndex(index);
			if (player != null && player.details().isHandled()) {
				if (player.canDisconnect()) {
					player.packetDispatcher().sendDisconnectRemovals();
					World.remove(player);
				}
			}
		}
	}
}
