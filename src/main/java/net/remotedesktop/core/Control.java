package net.remotedesktop;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.Toolkit;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.net.Socket;

import java.util.HashSet;
import java.util.Scanner;

import javax.imageio.ImageIO;

//import lombok.Getter;
//import lombok.Setter;


class Control {
	
	public static boolean isControlOn = true;
	
	private final int WIDTH  = NetworkConfig.SCREEN_SIZE_HORIZONTAL;
	private final int HEIGHT = NetworkConfig.SCREEN_SIZE_VERTICAL;
	
	private volatile boolean isStreamMousePositionOn = false;
	private int remoteCount = 1;
	
	private MessageServer messageServer;
	private MouseSender mouseSender;
	private ScreenReceiver screenReceiver; 
	
	private HashSet<Device> remotes = new HashSet<>();
	private Scanner scanner;
	private Device selectedRemote;
	
	private Frame frame;
	private Dimension screenSize;
	
	// THREADS
	private Thread processCommandsThread;
	private Thread acceptConnectionsThread;
	private Thread receiveMessagesThread;
	private Thread streamMousePositionThread;
	private Thread receiveScreenStreamThread;
	
	
	Control() {
		Thread.setDefaultUncaughtExceptionHandler( (thread, throwable) -> {
			System.err.println("💥 Boom Thread " + thread.getName() + " crashed");
		});
		
		this.scanner       = new Scanner(System.in);
		this.messageServer = new MessageServer(this);
		
		this.frame = new java.awt.Frame();
		this.screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.frame.setSize(WIDTH, HEIGHT);
		this.frame.setLocation(screenSize.width - WIDTH, screenSize.height - HEIGHT);
		this.frame.setVisible(true);
		
		this.acceptConnectionsThread = new Thread( () -> this.messageServer.acceptConnections() );
		this.processCommandsThread   = new Thread( () -> this.processCommands() );
		
		this.acceptConnectionsThread.start();
		this.processCommandsThread.start();
	}
	
	public void registerDevice(Socket socket) {
		try {
			Device remote = new Device(socket);
			remote.setName("Remote-" + remoteCount++);
			remotes.add(remote);
			this.selectedRemote = remote;
			
			this.mouseSender    = new MouseSender(remote);
			this.screenReceiver = new ScreenReceiver();
			
			//this.screenReceiver.setOnFrameReady( frameData -> { displayFrame(frameData); } );// Set the call back
			
			this.receiveMessagesThread     = new Thread( () -> this.messageServer.receiveMessages(remote) );
			this.streamMousePositionThread = new Thread( () -> this.streamMousePosition() );
			this.receiveScreenStreamThread = new Thread( () -> this.receiveScreenStream() );

			this.receiveMessagesThread.start();
			this.streamMousePositionThread.start();
			this.receiveScreenStreamThread.start();
			
			System.out.println("Remote device registered: " + remote.getName());
			
		} catch(Exception e) {
			System.err.println("Exception - Control - registerDevice: " + e);
		}
	}
	
	private void processCommands() {
		String command;
		while(this.isControlOn) {
			command = this.scanner.nextLine();
			switch(command) {
				case "list" :
					//TODO: Display list of devices connected
					break;
				case "select" :
					//TODO: Select device
					break;
				case "unselect" :
					//TODO: Unselect device
					break;	
				case "send" :
					this.messageServer.sendTest(this.selectedRemote);
					break;
				case "historic" :
					//TODO: Display msg historic
					break;
				case "close" :
					//TODO: Close connection to device
					break;
				case "start":						
					this.isStreamMousePositionOn = true;
					break;
				case "stop":
					//TODO: Stop Control (after closing connections to all devices)
					this.isStreamMousePositionOn = false;						
					break;
				default:
					System.out.println("Not a valid command");
					break;
			}
		}
	}
	
	private void streamMousePosition() {
		try {
			while(this.isControlOn) {
				if(this.isStreamMousePositionOn) { 
					this.mouseSender.streamMousePosition();
					Thread.sleep(NetworkConfig.MOUSE_STREAM_ON_DELAY);
				} else {
					Thread.sleep(NetworkConfig.MOUSE_STREAM_OFF_DELAY);
				}
			}
		} catch(InterruptedException e) {
			//TODO
		}
	}
	
	private void receiveScreenStream() {
		try {
			this.screenReceiver.setOnFrameReady( frameData -> { displayFrame(frameData); } );// Set the call back
			while(this.isControlOn) {
				this.screenReceiver.receiveFrame();
				Thread.sleep(NetworkConfig.SCREEN_STREAM_DELAY);
			}
		} catch(Exception e) {
			System.err.println("⚠️ Exception in Control.receiveScreenStream(): " + e);
		}
	}
	
	public void displayFrame(byte[] data) {
	    try {
			BufferedImage image = ImageIO.read(new ByteArrayInputStream(data));
			Graphics graphics   = this.frame.getGraphics();
			graphics.drawImage(
				image.getScaledInstance(screenSize.width,screenSize.height,Image.SCALE_FAST), 
				0, 0, null );
			graphics.dispose();
		} catch(IOException e) {
			System.err.println("⚠️ IOException in Control.displayFrame(): " + e);
		}
	}
	
}