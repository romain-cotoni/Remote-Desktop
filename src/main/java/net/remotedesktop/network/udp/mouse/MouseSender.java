package net.remotedesktop;

import java.awt.MouseInfo;
import java.awt.Point;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import java.nio.ByteBuffer;

class MouseSender {
	
	// 1 Capture mouse position
	// 2 Format mouse int position to ByteBuffer
	// 2 Format ByteBuffer to Datagram
	// 3 Send UDP datagram to Remote socket
	
	private static final int MOUSE_DATA_SIZE = 8; // 1 int = 4 bytes -> 2 ints = 8 bytes
		
	private byte[] mouseByteArray = new byte[MOUSE_DATA_SIZE]; // 4 bytes int x + 4 bytes int y
	private ByteBuffer byteBuffer = ByteBuffer.wrap(mouseByteArray);
	private Device remoteDevice;
	private DatagramSocket datagramSocket;
	
	MouseSender(Device remoteDevice) {
		try {
			this.remoteDevice = remoteDevice;
			this.datagramSocket = new DatagramSocket(); //NetworkConfig.UDP_MOUSE_PORT
		} catch(Exception e) {
			System.out.println("Exception - MouseSender(): " + e);
		}
	}
	
	public void streamMousePosition() {
		this.pointToByteBuffer( this.getPosition() );
		try {
			this.sendMousePosition( this.createDatagram() );
		} catch(Exception e) {
			System.out.println("Exception - sendMousePosition(): " + e);
		}
	}
	
	private Point getPosition() {
		return MouseInfo.getPointerInfo().getLocation();
	}
	
	// ByteBuffer convert int into bytes with proper byte order
	private void pointToByteBuffer(Point point) {
		byteBuffer.clear();
		byteBuffer.putInt(point.x);
		byteBuffer.putInt(point.y);
	}
	
	// Create UDP packet holding coordinates in binary format
	private DatagramPacket createDatagram() {
		try {
			return new DatagramPacket(
				this.mouseByteArray, 
				MOUSE_DATA_SIZE,
				InetAddress.getByName( this.remoteDevice.getIp() ),
				NetworkConfig.UDP_MOUSE_PORT
			);
		} catch(Exception e) {
			System.out.println("Exception - createDatagram(): " + e);
			return null;
		}
	}
	
	// Send UDP packet
	private void sendMousePosition(DatagramPacket packet) throws Exception {
		try {
			this.datagramSocket.send(packet);
		} catch(Exception e) {
			throw new Exception("Exception - sendMousePosition() - Exception e: " + e);
		}
	}
	
	
}