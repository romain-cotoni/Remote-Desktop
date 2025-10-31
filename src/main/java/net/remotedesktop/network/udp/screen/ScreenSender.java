package net.remotedesktop;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;

import java.awt.MouseInfo;
import java.awt.Point;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

class ScreenSender {
	
	private static final String JPG = "jpg";
	private static final int MAX_CHUNK_SIZE = 60000;
	
	private Device controlDevice;
	private DatagramSocket datagramSocket;
	private Rectangle rectangle;
	private Robot robot;
	
	ScreenSender() throws Exception {
		this.datagramSocket = new DatagramSocket();
		this.rectangle      = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		this.robot          = new Robot();
	}
	
	public void streamScreen(Device controlDevice) throws Exception {
	    this.controlDevice = controlDevice;
		BufferedImage bufferedImage = this.captureScreenshot();
		long timestamp = System.currentTimeMillis();
		this.drawCursorOnImage(bufferedImage);
		byte[] imageBytes = this.compressToJPG(bufferedImage);
		this.sendFrameInChunks(imageBytes, timestamp);
	}
	
	private BufferedImage captureScreenshot() {
		return this.robot.createScreenCapture(rectangle);
	}
	
	private void drawCursorOnImage(BufferedImage bufferedImage) {
		Point point = MouseInfo.getPointerInfo().getLocation();
		Graphics2D graphics2D = bufferedImage.createGraphics();
		graphics2D.setColor(Color.RED);
		graphics2D.fillOval(point.x - 5, point.y - 5, 10, 10);  // Cercle rouge
		graphics2D.dispose();
	}
	
	private byte[] compressToJPG(BufferedImage bufferedImage) throws Exception {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, JPG, byteArrayOutputStream);
		return byteArrayOutputStream.toByteArray();
	}
	
	private void sendFrameInChunks(byte[] imageBytes, long timestamp) {
		try {
			int totalChunks = calculateTotalChunks(imageBytes.length);
			
			for(int chunkIndex = 0; chunkIndex < totalChunks; chunkIndex++) {
				byte[] payload = buildChunkPayload(imageBytes, chunkIndex, totalChunks, timestamp);
				DatagramPacket packet = createDatagramPacket(payload);
				this.sendPacket(packet);
			}		
		} catch(Exception e) {
			System.out.println("Exception - sendFrameInChunks(): " + e);
		}
	}
	
	private int calculateTotalChunks(int image_size) {
		return (int) Math.ceil( (double) image_size / MAX_CHUNK_SIZE );
	}
	
	private byte[] buildChunkPayload(byte[] imageBytes, int chunkIndex, int totalChunks, long timestamp) throws Exception {
		int offset    = chunkIndex * MAX_CHUNK_SIZE;
		int endOffset = Math.min( offset + MAX_CHUNK_SIZE , imageBytes.length );
		int chunkSize = endOffset - offset;
			
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		
		DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream); //DataOutpuStream write INTO ByteArrayOutputStream
		dataOutputStream.writeLong(timestamp);
		dataOutputStream.writeInt(chunkIndex);
		dataOutputStream.writeInt(totalChunks);
		dataOutputStream.writeInt(chunkSize);
		dataOutputStream.write(imageBytes, offset, chunkSize);
		return byteArrayOutputStream.toByteArray();
	}
	
	private DatagramPacket createDatagramPacket(byte[] byteArray) throws Exception {
		return new DatagramPacket(byteArray,
			                      byteArray.length,
								  InetAddress.getByName( this.controlDevice.getIp() ),
								  NetworkConfig.UDP_SCREEN_PORT);
	}
	
	private void sendPacket(DatagramPacket datagramPacket) throws Exception {
		this.datagramSocket.send(datagramPacket);
	}
	
	
}