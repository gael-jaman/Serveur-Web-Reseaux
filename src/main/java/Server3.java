import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server3 {

	static int PORT = 8080;

	public static void main( String[] args ){

		// On commence par créer une socket sur le port d'écoute 8080
		try {
			ServerSocket serverSocket = new ServerSocket(PORT);
			System.out.println("écoute sur le port " + PORT + "...\n");
	
			/*
			 * On crée une socket pour chaque nouveau client
			 * On créer un thread pour chaque nouvelle connexion, afin de ne pas bloquer le thread principal
			 */
			while(true) {
				Socket clientSocket = serverSocket.accept();
				
				System.out.println("nouveau client connecté : " + clientSocket.getInetAddress().getHostAddress());
				
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


