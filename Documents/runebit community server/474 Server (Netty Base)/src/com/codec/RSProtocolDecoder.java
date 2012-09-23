/*
* @ Author - Digistr.
* @ info - Reads 1 byte to decide what login type will be performed.
*/ 
package com.codec;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import com.model.World;
import com.packet.Packet;
import com.packet.PacketBuilder;

public class RSProtocolDecoder extends FrameDecoder {

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
		if (buffer.readableBytes() >= 1) {
			int opcode = buffer.readUnsignedByte();
			switch(opcode) {
				case 15:
					if (buffer.readableBytes() == 4) {
						int version = buffer.readInt();
						if (version == 474) {
							try {
								channel.getPipeline().remove(this);
								channel.getPipeline().addLast("decoder", new ClientRequestDecoder());
								channel.write(new PacketBuilder().addByte((byte) 0));
							} catch (Exception e){ 
								e.printStackTrace(); 
							}
							break;
						} else {
							channel.write(new PacketBuilder().addByte((byte) 6));
							channel.close();
							break;
						}
					}
					break;
				case 14:
					if (buffer.readableBytes() == 1) {
						buffer.skipBytes(1);
						channel.write(new PacketBuilder().addByte((byte) 0).addLong(0));
						channel.getPipeline().remove(this);
						channel.getPipeline().addLast("decoder", new RSLoginDecoder());
					} else {
						channel.write(new PacketBuilder().addByte((byte) 6));
						channel.close();
						break;
					}
					break;
				case 16:
					channel.write(new PacketBuilder().addShort(World.curIndex == 0 ? 1 : World.curIndex));
					channel.close();
					break;
				default:
					channel.close();
					break;
			}
			return buffer;
		}
		channel.close();
		return buffer;
	}

}
