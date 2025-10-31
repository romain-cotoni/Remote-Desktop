# Remote-Desktop
src/
	Main.java                     // Role selector
	core/
		Control.java              // Complete Control implementation & logic
		Remote.java               // Complete Remote implementation & logic
	model/            
		Device.java               // Device info (name, ip, port)
		Frame.java                // Complete frame data
		FramePart.java            // Individual UDP packet data
	view/             
		ConsoleView.java          // Current CLI
		JavaFXView.java           // Future GUI
	network/
		NetworkConfig.java
		tcp/                      // Port 8080
			MessageServer.java    // Control: accept connections
			MessageClient.java    // Remote: connect to control
			MessageProtocol.java  // Shared: MSG:, KEY:, etc.
		udp/
			mouse/		          // Port 8081
				MouseSender.java      // Control send positions to Remote
				MouseReceiver.java    // Remote receives positions
			screen/               // Port 8082
│           	ScreenSender.java     // Remote send screen to Control  
│           	ScreenReceiver.java   // Control receives
│           	FrameAssembler.java   // Reassemble UDP packets received
	utils/
		ScreenCapture.java        // Robot screen capture
		MouseController.java      // Robot mouse control
		FrameCompressor.java      // Compression
		ByteUtils.java            // Byte array helpers
	
	




TO ADD LATER:
	Thread pool
	Synchronise input and video frame using timestamps
