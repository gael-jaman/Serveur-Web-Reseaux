package fr.ul.miage.reseaux;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientHandler implements Runnable{
	private final Socket clientSocket;

	public ClientHandler(Socket clientSocket) {
		super();
		this.clientSocket = clientSocket;
	}

	@Override
	public void run(){
		System.out.println("connexion entrante : " + clientSocket.toString());
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			StringBuilder requestBuilder = new StringBuilder();
			String line;
			
			while(!((line = br.readLine())=="")) {
				requestBuilder.append(line + "\r\n");
			}
			
			String request = requestBuilder.toString();
			System.out.println(request);
						
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}
