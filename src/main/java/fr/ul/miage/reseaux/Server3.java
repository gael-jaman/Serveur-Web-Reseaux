package fr.ul.miage.reseaux;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.stream.Stream;

public class Server3 {

	static int PORT = 8080;
	static String webFolder;

	public void runServer(){

		readProperties();

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

				Request request = new Request(clientSocket, webFolder);

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

	public void readProperties () {
		try {
			//File fileProperties = new File("properties.txt");
			//String fileName = "properties.txt";
//			URL url = getClass().getResource("/properties.txt");
//			File fileProperties = new File(url.toURI());
//			Scanner sc = new Scanner(fileProperties);

			//			InputStream inputStream = this.getClass().getResourceAsStream("/properties.txt");
			//			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			//			BufferedReader br = new BufferedReader(inputStreamReader);
			//			Stream<String> lines = br.lines();
			//			
			//			String test = lines.toString();

			InputStream is = getClass().getResourceAsStream("/properties.txt");
			InputStreamReader steamReader = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(steamReader);

			String line;
			String[] properties;

			while ((line = br.readLine()) != null) {
				properties = line.split(":", 2);

				switch (properties[0]) {
				case "PORT" : 
					PORT = Integer.parseInt(properties[1].replaceFirst(" ", ""));
					break;

				case "Répertoire_web" :
					webFolder = properties[1].replaceFirst(" ", "");
					break;
				}
			}

			//lines.forEach();

//			while (sc.hasNextLine()) {
//				String data = sc.next();
//
//				switch (data) {
//				case "PORT:" : 
//					PORT = sc.nextInt();
//					break;
//
//				case "Répertoire_web:" :
//					webFolder = sc.nextLine();
//					break;
//				}
//			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
}


