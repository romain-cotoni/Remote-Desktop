package net.remotedesktop;

import java.awt.Point;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

import java.nio.ByteBuffer;


class MouseReceiver {
	
	private static final int MOUSE_DATA_SIZE = 8; // 1 int = 4 bytes -> 2 ints = 8 bytes
    private static final int SOCKET_TIMEOUT = 1000;
	
	private DatagramSocket datagramSocket;
	private byte[] mouseByteArray;
	private DatagramPacket packet;
			
	MouseReceiver() {
		try {
			this.datagramSocket = new DatagramSocket(NetworkConfig.UDP_MOUSE_PORT);
			this.datagramSocket.setSoTimeout(SOCKET_TIMEOUT); // Timeout to prevent forever blocking socket
			this.mouseByteArray = new byte[MOUSE_DATA_SIZE];
			this.packet = new DatagramPacket(this.mouseByteArray, MOUSE_DATA_SIZE);
		} catch(Exception e) {
			System.out.println("Exception - MouseReceiver(): " + e);
		}
	}
	
	public Point receiveMousePosition() {
		try {
			this.datagramSocket.receive(this.packet);
			return byteBufferToPoint(this.mouseByteArray);
		} catch(SocketTimeoutException e) {
            return null; // No data received - Stop Socket to prevent blocking
        } catch(Exception e) {
			System.out.println("Exception - receiveMousePosition(): " + e);
			return null;
		}
		
	}
	
	private Point byteBufferToPoint(byte[] data) {
		// Unpack binary data
		ByteBuffer buffer = ByteBuffer.wrap(data);
        Point point = new Point(buffer.getInt(), buffer.getInt());
        System.out.println(point.getX() + " - " + point.getY());
		return point;
	}
	
}