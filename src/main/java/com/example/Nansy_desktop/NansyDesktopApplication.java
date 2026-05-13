package com.example.Nansy_desktop;

public class NansyDesktopApplication {

	// private static CommandExecutor commandExecutor;
	private static String jwtToken;
	private static String pcUsername;

	public static void main(String[] args) {
		// System.out.println("запуск Nansy");
		// commandExecutor = new CommandExecutor();

		pcUsername = System.getProperty("user.name");
		// System.out.println("Имя пк: " + pcUsername);

		new Thread(() -> {
        	UIHandler.launch(UIHandler.class, args);
    	}).start();

		AuthHttpHandler.authenticateAndConnect(pcUsername);
		jwtToken = AuthHttpHandler.getJwtToken();

		new Thread(() -> {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			UIHandler.setQRImage(jwtToken);
		}).start();
		

		try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}