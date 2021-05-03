package fr.ul.miage.reseaux;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
		
		
		
//		// TODO Auto-generated method stub
//		PrintWriter out = null;
//		BufferedReader in = null;
//		
//		try {
//			out = new PrintWriter(clientSocket.getOutputStream(), true);
//			
//			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//			
//			String line;
//			
//			while ((line = in.readLine()) != null) {
//				System.out.printf("Le client a envoy� : %s \n, line");
//				
//				out.println(line);
//				
//			}
//		}
//		catch (IOException e) {
//			e.printStackTrace();
//		}
//		finally {
//			try {
//				if (out != null) {
//					out.close();
//				}
//				if (in != null) {
//					in.close();
//					clientSocket.close();
//				}
//			} catch (IOException e) {
//				// TODO: handle exception
//				e.printStackTrace();
//			}
//		}
	}
	
	
}
