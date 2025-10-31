package net.remotedesktop;

import java.io.ByteArrayOutputStream;

class FrameAssembler {

	private byte[][] parts;
	private boolean[] received;
	private int totalParts;
	private int receivedParts;
	
	FrameAssembler() {
		
	}
	
	FrameAssembler(int totalParts) {
		this.totalParts    = totalParts;
		this.parts         = new byte[this.totalParts][];
		this.received      = new boolean[this.totalParts];
		this.receivedParts = 0;
	}
	
	public synchronized void addPart(int partNumber, byte[] partData) {
		if(!received[partNumber]) {
			parts[partNumber] = partData;
			this.received[partNumber] = true;
		    this.receivedParts++;
		}
	}
	
	public boolean isComplete() {
		return this.receivedParts == this.totalParts;		
	}
	
	public byte[] getCompleteFrame() throws Exception {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			for(byte[] part : parts) {
				byteArrayOutputStream.write(part);
			}
		} catch(Exception e) {
			throw new Exception("getCompleteFrame() - Exception e: " + e);
		}
		return byteArrayOutputStream.toByteArray();
	}

	
}