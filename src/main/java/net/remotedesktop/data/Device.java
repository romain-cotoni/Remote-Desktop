package net.remotedesktop;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.InetAddress;
import java.net.Socket;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class Device {
	
	public static int count = 1; 
	
	private String name = "device"; // Default name
	private String ip;
	private int port;
	private BufferedReader bufferedReader;
	private PrintWriter printWriter;
	
	Device(Socket socket) {
		try {
			this.ip   = socket.getInetAddress().getHostAddress();
			this.port = socket.getPort();
			this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.printWriter = new PrintWriter(socket.getOutputStream(), true);
	    } catch(Exception e) {
			System.out.println("Exception - Device() - new PrintWriter(): " + e);
		}
	}
	
	@Override
	public String toString() {
		return String.format("%s - ip: %s - port: %d", name, ip, port);
	}
	
}