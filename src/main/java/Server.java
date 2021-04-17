import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.SocketHandler;

public class Server implements Runnable{

	static final File WEB_ROOT = new File(".");
	static final String DEFAULT_FILE = "index.html";
	static final String FILE_NOT_FOUND = "404.html";
	static final String METHOD_NOT_SUPPORTED = "not_supported.html";
	
	static final int PORT = 8080;
	
	static final boolean verbose = true;
	
	private Socket connect;
	
	public Server(Socket c) {
		connect = c;
	}
	
	public static void main(String[] args) throws Exception{
		try {
			ServerSocket serverSocket = new ServerSocket(PORT);
			System.out.println("écoute sur le port " + PORT + "...\n");
			
			while (true) {
				
				try (Socket client = serverSocket.accept()){
					handleClient(client);
				}
				
//				Server server = new Server(serverSocket.accept());
//				
//				if (verbose) {
//					System.out.println("connection ouverte. (" + new Date() + ")");
//				}
//				
//				Thread thread = new Thread(server);
//				thread.start();
			}
		} catch (IOException e) {
			System.err.println("erreur de connexion : " + e.getMessage());
		}
	}
	
	private static void handleClient(Socket client) throws IOException{
		System.out.println("connexion entrante  : " + client.toString());
		BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
		
		StringBuilder requestBuilder = new StringBuilder();
		String line;
		while (!(line = br.readLine()).isBlank()) {
			requestBuilder.append(line + "\r\n");
		}
		
		String request = requestBuilder.toString();
		System.out.println(request);
		
	}
	
	public void run() {
		BufferedReader in = null;
		PrintWriter out = null;
		BufferedOutputStream dataOut = null;
		String fichierRequete = null;
		
		try {
			// on lit l'entrée du client via l'entrée de la socket
			in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
			
			out = new PrintWriter(connect.getOutputStream());
			
			dataOut = new BufferedOutputStream(connect.getOutputStream());
			
			
			String input = in.readLine();
			
			StringTokenizer parse = new StringTokenizer(input);
			
			String method = parse.nextToken().toUpperCase();
			
			fichierRequete = parse.nextToken().toLowerCase();
			
			if (!method.equals("GET")) {
				if (verbose) {
					System.out.println("501 : " + method + " method.");
				}
				
				File file = new File(WEB_ROOT, METHOD_NOT_SUPPORTED);
				int fileLength = (int) file.length();
				String contentMimeType = "text/html";
				
				byte[] fileData = readFileData(file, fileLength);
				
				// we send HTTP Headers with data to client
				out.println("HTTP/1.1 501 Not Implemented");
				out.println("Server: Java HTTP Server from SSaurel : 1.0");
				out.println("Date: " + new Date());
				out.println("Content-type: " + contentMimeType);
				out.println("Content-length: " + fileLength);
				out.println(); // blank line between headers and content, very important !
				out.flush(); // flush character output stream buffer
				// file
				dataOut.write(fileData, 0, fileLength);
				dataOut.flush();
								
			} else {
				if (fichierRequete.endsWith("/")) {
					fichierRequete += DEFAULT_FILE;
				}
				
				File file = new File(WEB_ROOT, fichierRequete);
				int fileLength = (int) file.length();
				String content = getContentType(fichierRequete);
				
				if (method.equals("GET")) {
					byte[] fileData = readFileData(file, fileLength);
				
					// send HTTP Headers
					out.println("HTTP/1.1 200 OK");
					out.println("Server: Java HTTP Server from SSaurel : 1.0");
					out.println("Date: " + new Date());
					out.println("Content-type: " + content);
					out.println("Content-length: " + fileLength);
					out.println(); // blank line between headers and content, very important !
					out.flush(); // flush character output stream buffer
					
					dataOut.write(fileData, 0, fileLength);
					dataOut.flush();
				}
				
				if (verbose) {
					System.out.println("File " + fichierRequete + " of type " + content + " returned");
				}
			}
		
		} catch (FileNotFoundException fileE) {
			try {
				fileNotFound(out, dataOut, fichierRequete);
			} catch (IOException ioe) {
				System.err.println(ioe.getMessage());
			}
		} catch (IOException ioe) {
			System.err.println();
		} finally {
			try {
				in.close();
				out.close();
				dataOut.close();
				connect.close(); // we close socket connection
			} catch (Exception e) {
				System.err.println("Error closing stream : " + e.getMessage());
			} 
			
			if (verbose) {
				System.out.println("Connection closed.\n");
			}
		}
	}
	
	private byte[] readFileData(File file, int fileLength) throws IOException {
		FileInputStream fileIn = null;
		byte[] fileData = new byte[fileLength];
		
		try {
			fileIn = new FileInputStream(file);
			fileIn.read(fileData);
		} finally {
			if (fileIn != null)
				fileIn.close();
		}
		
		return fileData;
	}
	
	private String getContentType(String fileRequete) {
		if (fileRequete.endsWith(".htm") || fileRequete.endsWith(".html"))
			return "text/html";
		else
			return "text/plain";
	}
	
	public void fileNotFound(PrintWriter out, OutputStream dataOut, String fileRequested) throws IOException {
		File file = new File(WEB_ROOT, FILE_NOT_FOUND);
		int fileLength = (int) file.length();
		String content = "text/html";
		byte[] fileData = readFileData(file, fileLength);
		
		out.println("HTTP/1.1 404 File Not Found");
		out.println("Server: Java HTTP Server from SSaurel : 1.0");
		out.println("Date: " + new Date());
		out.println("Content-type: " + content);
		out.println("Content-length: " + fileLength);
		out.println(); // blank line between headers and content, very important !
		out.flush(); // flush character output stream buffer
		
		dataOut.write(fileData, 0, fileLength);
		dataOut.flush();
		
		dataOut.write(fileData, 0, fileLength);
		dataOut.flush();
		
		if (verbose) {
			System.out.println("File" + fileRequested + " not found");
		}
	}
	
	public String read (InputStream inputStream) throws IOException{
		StringBuilder result = new StringBuilder();
		do {
			result.append((char) inputStream.read());
		} while (inputStream.available() > 0);
		return result.toString();
	}
}