package net.remotedesktop;

import java.io.BufferedReader;
import java.io.PrintWriter;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.Scanner;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class MessageServer {

	private int index = 1;
	private Control control;
	private Remote remote;
	private Scanner scanner;
	private BufferedReader bufferedReader;
	private PrintWriter printWriter;
	private ServerSocket serverSocket;
	
	MessageServer(Control control) {
		try {			
			this.control = control;
			this.scanner = new Scanner(System.in);
		} catch(Exception e) {
			System.out.println("Exception - MessageServer(): " + e);
		}
	}
	
	public void acceptConnections() {
		try {
			this.serverSocket = new ServerSocket(NetworkConfig.TCP_MESSAGE_PORT);
			while(Control.isControlOn) {
				Socket socket = serverSocket.accept();
				this.control.registerDevice(socket);
			}
		} catch(Exception e) {
			System.out.println("Exception - MessageServer - acceptConnections(): " + e);
		}
	}
	
	public void receiveMessages(Device remote) {
		try {
			String msg;
			while( (msg = remote.getBufferedReader().readLine()) != null) {
				System.out.println("From " + remote.toString() + ": " + msg);
			}
		} catch(Exception e) {
			System.out.println("Exception - MessageServer - receiveMessages(): " + e);
		}
	}
	
	public void send(Device remote) {
		System.out.print("Write a msg: ");
		String msg = this.scanner.nextLine();   // Write msg
		remote.getPrintWriter().println("msg"); // Send data
	}
	
	public void sendTest(Device remote) {
		remote.getPrintWriter().println("Message from Control test " + index++); // Send data
	}
	
}