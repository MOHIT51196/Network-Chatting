import java.util.ArrayList;

public class ChatLauncher {

	private static ServerEnd server;
	private static ArrayList<ClientEnd> clientList = new ArrayList<>();
	public static void main(String[] args) {
		
		System.out.println("Alive at console....");
		
		
//		ServerEnd server = new ServerEnd();
		Thread serverThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				server = ServerEnd.newInstance();
				server.initialize();
				server.readMessage();
				System.out.println("Server running.......");
				
			}
		});
	
//		ClientEnd client = new ClientEnd();
	
		Thread client1Thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				ClientEnd client = new ClientEnd();
				client.initialize();
				clientList.add(client);
				ServerEnd.generateClientList(clientList);
				server.repaint();
				client.readMessage();
				
				System.out.println("Client running......");
				
			}
		});

		
//		Thread client2Thread = new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				ClientEnd client = new ClientEnd();
//				client.initialize();
//				clientList.add(client);
//				ServerEnd.generateClientList(clientList);
//				server.repaint();
//				client.readMessage();
//				
//				System.out.println("Client running......");
//				
//			}
//		});
		
		serverThread.start();
		client1Thread.start();
//		client2Thread.start();
		
		
	}
}
