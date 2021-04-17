import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.StringTokenizer;

public class Request implements Runnable{
	
	final static String CRLF = "\r\n";
	Socket socket;
	
	public Request(Socket socket) {
		super();
		this.socket = socket;
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
		
		String requestLine = br.readLine();
		
		System.out.println();
		System.out.println("Requete : " + requestLine);
		
		String headerLine = br.readLine();
		
		while(headerLine != null && headerLine.length() != 0) {
			//System.out.println(headerLine);
			headerLine = br.readLine();
		}
		
		StringTokenizer tokens = new StringTokenizer(requestLine);
		tokens.nextToken();
		String fileName = tokens.nextToken();
		
		fileName = "." + fileName;
		
		FileInputStream fis = null;
		boolean existFile = true;
		
		try {
			fis = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			existFile = false;
		}
		
		String statusLine = null;
		String contentTypeLine = null;
		String entityBody = null;
		
		if (existFile) {
			statusLine = "200";
			contentTypeLine = "Content-type: " + contentType(fileName) + CRLF;
		}
		else {
			statusLine = "404";
			contentTypeLine = "Le fichier n'existe pas";
			entityBody = entityBody = "<HTML>" + 
					"<HEAD><TITLE>Not Found</TITLE></HEAD>" +
					"<BODY>Not Found</BODY></HTML>";
		}
		
		if (existFile)	{
			sendBytes(fis, outS);
			fis.close();
		} else {
			byte[] notFoundContent = "<h1>Not found </h1>".getBytes();
			sendResponse(socket, "Erreur", contentTypeLine, notFoundContent);
			outS.write(entityBody.getBytes());
		}
		//outS.writeBytes(CRLF);
		
		// Close streams and socket.
		outS.close();
		br.close();
		socket.close();
		System.out.println("Socket is closed now");
		
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

}
