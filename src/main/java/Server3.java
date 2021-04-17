import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server3 {

	static int PORT = 8080;

	public static void main( String[] args ){

		try {
			ServerSocket serverSocket = new ServerSocket(PORT);
			System.out.println("�coute sur le port " + PORT + "...\n");
	
			while(true) {
				Socket clientSocket = serverSocket.accept();
				
				System.out.println("nouveau client connect� : " + clientSocket.getInetAddress().getHostAddress());
				
				Request request = new Request(clientSocket);
				
				Thread thread = new Thread(request);
				
				thread.start();
				
				//ClientHandler clientSocket = new ClientHandler(client);
				
				//new Thread(clientSocket).start();
			}
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}


