package net.remotedesktop;

import java.net.Socket;
import java.util.Scanner;

import lombok.Getter;
import lombok.Setter;


class Remote {
	
	public static int count = 1;
	private String control_ip;
	private boolean isRemoteOn = true;
	private MessageClient messageClient;
	private MouseReceiver mouseReceiver;
	private ScreenSender screenSender;
	private Scanner scanner;
	private Device control;
	
	// THREADS
	private Thread processCommandsThread;
	private Thread receiveMessagesThread;
	private Thread receiveMousePositionThread;
	private Thread streamScreenThread;
	
	Remote(String control_ip) {
		try {
			this.control_ip    = control_ip; 
			this.scanner       = new Scanner(System.in);
			this.messageClient = new MessageClient(this);
			this.mouseReceiver = new MouseReceiver();
			this.screenSender  = new ScreenSender();
			this.control       = messageClient.connectToServer();
			this.control.setName("Control");
			this.processCommandsThread      = new Thread( () -> this.processCommands() );
			this.receiveMessagesThread      = new Thread( () -> this.messageClient.receiveMessages(control) );
			this.receiveMousePositionThread = new Thread( () -> this.receiveMousePosition() );
			this.streamScreenThread         = new Thread( () -> this.streamScreen() );
			this.processCommandsThread.start();
			this.receiveMessagesThread.start();
			this.receiveMousePositionThread.start();
			this.streamScreenThread.start();
		} catch(Exception e) {
			System.out.println("Exception - Remote(): " + e);
		}
	}
	
	private void processCommands() {
		try {
			String command;
			while(this.isRemoteOn) {
				command = this.scanner.nextLine();
				switch(command) {	
					case "send" :
						this.messageClient.sendTest(this.control);
						break;
					case "close" :
						//TODO: Close connection to Control
						break;
					case "stop":
						//TODO: Stop Remote (after closing connection to Control)
						break;
					default:
						System.out.println("Not a valid command");
						break;
				}
			}
		} catch(Exception e) {
			System.out.println("Exception - Remote - processCommands(): " + e);
		} finally {
			System.out.println("Stop - Remote - processCommands()");
		}
	}
	
	private void receiveMousePosition() {
		try {
			while(this.isRemoteOn) {
				this.mouseReceiver.receiveMousePosition();
			}
		} catch(Exception e) {
			System.out.println("Exception - Remote - receiveMousePosition(): " + e);
		}
	}
	
	private void streamScreen() {
		try {
			while(this.isRemoteOn) {
				this.screenSender.streamScreen(control);
				Thread.sleep(NetworkConfig.SCREEN_STREAM_DELAY);
			}
		} catch(Exception e) {
			System.out.println("Exception - Remote - streamScreen(): " + e);
		}
	}
	
}
