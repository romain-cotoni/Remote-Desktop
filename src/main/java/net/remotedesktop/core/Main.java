package net.remotedesktop;


class Main {
	
	Main() {
		
	}
	
	public static void main(String[] args) {
		if(args.length > 0) {
			switch(args[0]) {
				case "control" :
					System.out.println("control mode selected");
					Control control = new Control();					
					break;
				case "remote" :
					System.out.println("remote mode selected");
					String control_ip = args.length > 1 ? args[1] : NetworkConfig.LOCALHOST;
					Remote remote = new Remote(control_ip);
					break;
				default :
					System.out.println("no mode selected");
			}
		} else {
			// TODO: display roles to select
			System.out.println("no mode selected");
		}
		
	}
	
}
