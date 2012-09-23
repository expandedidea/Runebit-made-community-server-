package com;

import java.io.IOException;
import com.model.World;
import com.util.GameUtility;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
/**
 * Main server class.
 * 
 * @author Digistr
 */
public class Server {


   	/*
	* These Utilies Atomatically Startup In The Beggining Before Anything
	* Else in the Server Is Even Looked At.
	*/

	private static void STARTUP_SERVER_UTILITIES() {
		World.createList(2000);           //Creates World List.
		GameUtility.setRandom();          //Sets Random Seed.
		com.util.PathObjectSystem.load(); //Loads Path System.  
		com.util.ItemManagement.load();   //Loads Item System.
	}


     	/*
	* The Main Function For This Server Starts The Server Up.
	* ShutdownHook = Hopefully is called before the Server exits completely (DO NOT RELY ON THIS).
        */

	 public static void main(String[] args) throws IOException {
		long t1 = System.nanoTime();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				World.exit();
			}
		});
		try {
			STARTUP_SERVER_UTILITIES();
			ServerBootstrap bootStrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(
					Executors.newCachedThreadPool(), Executors.newCachedThreadPool()
				)
			);
			bootStrap.setPipelineFactory(new PipelineFactory());
			bootStrap.setOption("child.tcpNoDelay", true);
			bootStrap.setOption("child.keepAlive", true);
			bootStrap.setOption("reuseAddress", true);
			bootStrap.bind(new InetSocketAddress(43594));
			new PreciseTimeExecutor();
		long t2 = System.nanoTime();
		//System.gc();
		System.out.println("Finished Loading All Servers Core Features In "+((t2-t1)/1000000)+" ms.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
