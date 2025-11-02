package net.remotedesktop;

import java.awt.Point;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

import java.nio.ByteBuffer;


class MouseReceiver {
	
	private static final int MOUSE_DATA_SIZE = 20; // 1 int = 4 bytes -> 2 ints = 8 bytes
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
	
	// Unpack binary data
	/*private Point byteBufferToPoint(byte[] data) {
		ByteBuffer buffer = ByteBuffer.wrap(data);
		int eventId = buffer.getInt();
		switch(eventId) {
			case 1:
			
		}
        Point point = new Point(buffer.getInt(), buffer.getInt());
        System.out.println(buffer.getInt() + " - " + buffer.getInt() + " | " + buffer.getInt());
        System.out.println(point.getX() + " - " + point.getY());
		return point;
	}*/
	
	private Point byteBufferToPoint(byte[] data) {
		ByteBuffer buffer = ByteBuffer.wrap(data);
        //Point point = new Point(buffer.getInt(), buffer.getInt());
        int eventId = buffer.getInt();
        int x = buffer.getInt();
        int y = buffer.getInt();
        Point point = new Point(x, y);
        switch(eventId) {
        	case 1:
        		//System.out.println("type: " + eventId + " | x: " + x + " - y:" + y);
        		break;
        	case 2:
        		int button = buffer.getInt();
        		System.out.println("type: " + eventId + " | x: " + x + " - y:" + y + " | button: " + button);
        		break;
        	case 3:
        		int rotation = buffer.getInt();
        		int scroll   = buffer.getInt();
        		System.out.println("type: " + eventId + " | x: " + x + " - y:" + y + " | rotation: " + rotation + " | scroll: " + scroll);
        		break;
        }
		//System.out.println(point.getX() + " - " + point.getY());
		return point;
	}
	
}
