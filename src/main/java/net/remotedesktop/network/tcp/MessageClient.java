package net.remotedesktop;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.Socket;

import java.util.Scanner;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class MessageClient {
	
	private int index = 1;
	private BufferedReader bufferedReader;
	private PrintWriter printWriter;
	private Scanner scanner;
	private Socket socket;
	
	MessageClient(Remote remote) {
		try {			
			System.out.println("MessageClient constructor");
			this.scanner = new Scanner(System.in);
		} catch(Exception e) {
			System.out.println("Exception - MessageClient(): " + e);
		}
	}
	
	public Device connectToServer() {
		try {
			this.socket = new Socket(NetworkConfig.LOCALHOST, NetworkConfig.TCP_MESSAGE_PORT);
		} catch(Exception e) {
			System.out.println("Exception - MessageClient - connectToServer(): " + e);
		}
		return new Device(this.socket);
	}
	
	public void receiveMessages(Device control) {
		try {
			String msg;
			while( (msg = control.getBufferedReader().readLine()) != null) {
				System.out.println("From Control: " + msg);
			}
		} catch(Exception e) {
			System.out.println("Exception - MessageClient - receiveMessages(): " + e);
		}
	}
	
	private void send(Device control) {
		System.out.print("Write a msg: ");
		String msg = this.scanner.nextLine();   // Write msg
		control.getPrintWriter().println("msg"); // Send data
	}
	
	public void sendTest(Device control) {
		control.getPrintWriter().println("Message from Remote test " + index++); // Send data
	}
	
}