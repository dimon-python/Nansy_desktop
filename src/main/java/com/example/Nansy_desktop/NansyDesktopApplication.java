package com.example.Nansy_desktop;

public class NansyDesktopApplication {

	private static String jwtToken;
	private static String pcUsername;
	private static StompWebSocketHandler stompHandler;

	public static void main(String[] args) {
		pcUsername = System.getProperty("user.name");
		stompHandler = new StompWebSocketHandler();

		new Thread(() -> {
        	UIHandler.launch(UIHandler.class, args);
    	}).start();

		if (JwtHandler.jwtIsExists() == true) {
			stompHandler.connect(pcUsername);

			stompHandler.subscribe("/topic/echo", message -> {
			System.out.println(message);
			});

			stompHandler.send("/topic/echo", "YEEEEES!");
		}

		jwtToken = JwtHandler.getJwtToken();
		
		new Thread(() -> {
			try {
				Thread.sleep(500);

				UIHandler.setQRImage(jwtToken);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}

		}).start();

		try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}