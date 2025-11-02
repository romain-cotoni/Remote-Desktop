package net.remotedesktop;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.Frame;
import java.awt.MouseInfo;
import java.awt.Point;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import java.nio.ByteBuffer;

class MouseSender extends MouseAdapter {
	
	private static final int MOUSE_DATA_MAX_SIZE = 20; // 4 bytes int x, 4 bytes int y, 4 bytes int button, 4 bytes int rotation, 4 bytes int scrollAmount, 1 bytes int mouse event type
	private static final int MOUSE_POSITION_MOVED = 1;
	private static final int MOUSE_BUTTON_PRESSED = 2;
	private static final int MOUSE_WHEEL_MOVED    = 3;
	private byte[] mouseByteArray = new byte[MOUSE_DATA_MAX_SIZE];
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
	
	@Override
	public void mouseMoved(MouseEvent e) {
		Point scaled = this.scaleCoordinates(e.getX(), e.getY());
        this.sendMousePosition((int)scaled.getX(), (int)scaled.getY());
        //System.out.println("mouseMoved: "+ e.getX() + "-" +  e.getY() + " |  point moved scaled: " + scaled.getX() + "-" + scaled.getY());
	}

	@Override
    public void mousePressed(MouseEvent e) {
		Point scaled = this.scaleCoordinates(e.getX(), e.getY());
		this.sendMouseButtonPressed((int)scaled.getX(), (int)scaled.getY(), e.getButton());
		//System.out.println("Button clicked: " + e.getButton()  +  "| mousePressed: "+ e.getX() + "-" +  e.getY() + " |  point pressed  scaled: " + scaled.getX() + "-" + scaled.getY());
	}
	
    @Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		Point scaled = this.scaleCoordinates(e.getX(), e.getY());
		this.sendMouseWheelMoved((int)scaled.getX(), (int)scaled.getY(), e.getWheelRotation(), e.getScrollAmount());
		//System.out.println("WheelRotation: "+e.getWheelRotation()+" | WheelScrollAmount: "+e.getScrollAmount()+" | WheelMoved: "+ e.getX()+"-"+e.getY()+" | WheelMovedScaled: "+scaled.getX()+"-"+scaled.getY());
	}

    public void attachToFrame(Frame frame) {
       	frame.addMouseListener(this);
		frame.addMouseMotionListener(this);
		frame.addMouseWheelListener(this);
	}

	private void sendMousePosition(int x, int y) {
		try {
			//this.pointToByteBuffer(x,y);
			byteBuffer.clear();
			byteBuffer.putInt(MOUSE_POSITION_MOVED);
			byteBuffer.putInt(x);
			byteBuffer.putInt(y);
			this.sendDatagramPacket(12);
		} catch(Exception e) {
			System.out.println("Exception - sendMousePosition(): " + e);
		}
	}

	private void sendMouseButtonPressed(int x, int y, int button) {
		try {
			byteBuffer.clear();
			byteBuffer.putInt(MOUSE_BUTTON_PRESSED);
			byteBuffer.putInt(x);
			byteBuffer.putInt(y);
			byteBuffer.putInt(button);
			this.sendDatagramPacket(16);
		} catch(Exception e) {
			System.out.println("Exception - sendMousePressedButton(): " + e);
		}
	}

	private void sendMouseWheelMoved(int x, int y, int rotation, int scrollAmount) {
		try {
			byteBuffer.clear();
			byteBuffer.putInt(MOUSE_WHEEL_MOVED);
			byteBuffer.putInt(x);
			byteBuffer.putInt(y);
			byteBuffer.putInt(rotation);
			byteBuffer.putInt(scrollAmount);
			this.sendDatagramPacket(20);
		} catch(Exception e) {
			System.out.println("Exception - sendMouseWheelMoved(): " + e);
		}
	}

	private Point scaleCoordinates(int x, int y) {
		//TODO: scale logic
		int xScaled = x;
		int yScaled = y;
		return new Point(xScaled, yScaled); 
	}
	
	private void intToByte() {
	
	}
	
	// Create & send UDP packet holding coordinates in binary format
	private void sendDatagramPacket(int length) {
		try {
			
			DatagramPacket packet = new DatagramPacket(
				this.mouseByteArray, 
				length,
				InetAddress.getByName(this.remoteDevice.getIp()),
				NetworkConfig.UDP_MOUSE_PORT
			);
			
			this.datagramSocket.send(packet);
			
		} catch(Exception e) {
			System.out.println("Exception - sendDatagramPacket(): " + e);
		}
	}
	
	
	/*private DatagramPacket createDatagram() {
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
	}*/
	
	// ByteBuffer convert int into bytes with proper byte order
	/*private void pointToByteBuffer(int x, int y, int rotation, int scrollAmount) {
		byteBufferMouseWheel.clear();
		byteBufferMouseWheel.putInt(x);
		byteBufferMouseWheel.putInt(y);
		byteBufferMouseWheel.putInt(rotation);
		byteBufferMouseWheel.putInt(scrollAmount);
	}*/
	
	/*private void pointToByteBuffer(Point point) {
		byteBuffer.clear();
		byteBuffer.putInt(point.x);
		byteBuffer.putInt(point.y);
	}*/
	
	/*public void streamMousePosition() {
		this.pointToByteBuffer( this.getPosition() );
		try {
			this.sendMousePosition( this.createDatagram() );
		} catch(Exception e) {
			System.out.println("Exception - sendMousePosition(): " + e);
		}
	}*/
	
	/*private Point getPosition() {
		return MouseInfo.getPointerInfo().getLocation();
	}*/
	
	// Send UDP packet
	/*private void sendMousePosition(DatagramPacket packet) throws Exception {
		try {
			this.datagramSocket.send(packet);
		} catch(Exception e) {
			throw new Exception("Exception - sendMousePosition() - Exception e: " + e);
		}
	}*/
	
	
}
