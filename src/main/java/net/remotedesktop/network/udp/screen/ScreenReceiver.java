package net.remotedesktop;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import java.util.function.Consumer;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class ScreenReceiver {
	
	private static final int MAX_CHUNK_SIZE = 65536;
	private DatagramSocket datagramSocket;
	
	@Setter
	private Consumer<byte[]> onFrameReady;
	private Map<Long, FrameAssembler> incompleteFrames = new HashMap<>();
	
	ScreenReceiver() throws Exception {
		this.datagramSocket = new DatagramSocket(NetworkConfig.UDP_SCREEN_PORT);
	}
	
	public void receiveFrame() throws Exception {
		byte[] buffer         = new  byte[MAX_CHUNK_SIZE];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		this.datagramSocket.receive(packet);
		
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
		DataInputStream dataInputStream           = new DataInputStream(byteArrayInputStream);
		
		long timestamp  = dataInputStream.readLong();
		int chunkIndex  = dataInputStream.readInt();
		int totalChunks = dataInputStream.readInt();
		int chunkSize   = dataInputStream.readInt();
		
		byte[] chunkByteArray = new byte[chunkSize];
		dataInputStream.read(chunkByteArray);
		
		this.assembleFrame(timestamp, chunkIndex, totalChunks, chunkByteArray);
		
	}
	
	public void assembleFrame(Long timestamp, int chunkIndex, int totalChunks, byte[] chunkByteArray) throws Exception {
		FrameAssembler assembler = incompleteFrames.computeIfAbsent( timestamp, (Long key) -> new FrameAssembler(totalChunks) );
		
		assembler.addPart(chunkIndex, chunkByteArray);
		
		if(assembler.isComplete()) {
			byte[] completeFrame = assembler.getCompleteFrame();
			this.onFrameReady.accept(completeFrame);
			incompleteFrames.remove(timestamp);
		}		
	}

	
}