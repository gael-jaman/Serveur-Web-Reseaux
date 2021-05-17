package fr.ul.miage.reseaux;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringTokenizer;

public class Request implements Runnable{

	final static String CRLF = "\r\n";
	Socket socket;
	static String webFolder;
	
	public Request(Socket socket) {
		super();
		this.socket = socket;
	}

	public Request(Socket socket, String webFolder) {
		super();
		this.socket = socket;
		this.webFolder = webFolder;
	}



	@Override
	public void run() {
		// TODO Auto-generated method stub

		try {
			traitementRequete();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private void traitementRequete() throws Exception {

		InputStream inS = socket.getInputStream();
		DataOutputStream outS = new DataOutputStream(socket.getOutputStream());

		BufferedReader br = new BufferedReader(new InputStreamReader(inS));

		String input = br.readLine();

		StringTokenizer parse = new StringTokenizer(input);

		String method = parse.nextToken().toUpperCase();

		String fichierRequete = parse.nextToken().toLowerCase();

		System.out.println();
		System.out.println("méthode : " + method);

		if (method.equals("GET")) {

			String statusLine = null;
			String contentTypeLine = null;
			String entityBody = null;
			
			System.out.println("requete : " + fichierRequete);

			Path filePath = getFilePath(fichierRequete);
			
			System.out.println(filePath.toString());

			if(Files.exists(filePath)) {
				String contentType = guessContentType(filePath);
				sendResponse(socket, "200", contentType, Files.readAllBytes(filePath));
			} else {
				
				URL url = getClass().getResource("/tmp/404.html");

				System.out.println("url : " + url);
				
				//Path Error404_Path = Paths.get("tmp/404.html");
				Path Error404_Path = Paths.get(url.toURI());
				
				String contentType = guessContentType(Error404_Path);
				sendResponse(socket, "400", contentType, Files.readAllBytes(Error404_Path));
			}
		} else {
			System.out.println("méthode non traitée -> ignorée");
		}

		// Close streams and socket.
		outS.close();
		br.close();
		socket.close();
		//System.out.println("Socket is closed now");

	}

	private static void sendBytes(FileInputStream fin, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];

		int bytes = 0;

		while ((bytes = fin.read(buffer)) != -1) {
			out.write(buffer, 0, bytes);
		}
	}

	private static void sendResponse(Socket client, String status, String contentType, byte[] content) throws IOException {
		OutputStream clientOutput = client.getOutputStream();
		clientOutput.write(("HTTP/1.1 \r\n" + status).getBytes());
		clientOutput.write(("ContentType: " + contentType + "\r\n").getBytes());
		clientOutput.write("\r\n".getBytes());
		clientOutput.write(content);
		clientOutput.write("\r\n\r\n".getBytes());
		clientOutput.flush();
		client.close();
	}

	private static String contentType(String fileName) {
		return "html";
	}

	private static Path getFilePath(String path) {
		if ("/".equals(path)) {
			path = "/index.html";
		} else if (path.charAt(path.length() - 1) == '/'){
			path += "index.html";
		}
		else {
			//path += "/index.html";
		}

		return Paths.get(webFolder, path);
	}

	private static String guessContentType(Path filePath) throws IOException {
		return Files.probeContentType(filePath);
	}

}
