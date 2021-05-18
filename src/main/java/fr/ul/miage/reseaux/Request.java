package fr.ul.miage.reseaux;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
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

		//On récupère la requête et on parse son contenu
		InputStream inS = socket.getInputStream();
		DataOutputStream outS = new DataOutputStream(socket.getOutputStream());

		BufferedReader br = new BufferedReader(new InputStreamReader(inS));
		
		//----
		
		StringBuilder requestBuilder = new StringBuilder();
        String line;
        while (!(line = br.readLine()).isBlank()) {
            requestBuilder.append(line + "\r\n");
        }

        String request = requestBuilder.toString();
        String[] requestsLines = request.split("\r\n");
        String[] requestLine = requestsLines[0].split(" ");
        String method = requestLine[0];
        String path = requestLine[1];
        String version = requestLine[2];
        String host = requestsLines[1].split(" ")[1];
        
        String[] splitDomain = host.split("\\.");
        String tld = "";
        String domaine = "";
        String subDomaine = "";
        String domainPath = "";
        
        //On récupère le chemin du domaine
        if(splitDomain.length == 3) {
        	subDomaine = splitDomain[0];
        	domaine = splitDomain[1];
        	tld = splitDomain[2];
        	
            domainPath = tld.concat("/").concat(domaine).concat("/").concat(subDomaine);
        } else if (splitDomain.length == 2) {
        	domaine = splitDomain[0];
        	tld = splitDomain[1];
        	
            domainPath = tld.concat("/").concat(domaine);
        }
        
        
        //----

		//String input = br.readLine();

		//StringTokenizer parse = new StringTokenizer(input);

		//String method = parse.nextToken().toUpperCase();

		//String fichierRequete = parse.nextToken().toLowerCase();
        String fichierRequete = path;

		System.out.println();
		System.out.println("méthode : " + method);
		System.out.println("host : " + host);

		//On regarde si c'est une méthode GET
		if (method.equals("GET")) {

			String statusLine = null;
			String contentTypeLine = null;
			String entityBody = null;
			
			System.out.println("requete : " + fichierRequete);
			
			//On créée le chemin vers le fichier avec le domaine
			fichierRequete = domainPath.concat(fichierRequete);

			//On récupère le chemin vers le fichier demandé par la requete
			Path filePath = getFilePath(fichierRequete);
			
			//System.out.println(filePath.toString());

			//On regarde si le fichier existe
			if(Files.exists(filePath)) {
				//On détermine son contenu
				String contentType = guessContentType(filePath);
				
//				String customerKey = "Id";
//				String customerSecret = "mdp";
//				
//				String plainCredentials = customerKey + ":" + customerSecret;
//				String base64Credentials = new String(Base64.getEncoder().encode(plainCredentials.getBytes()));
//				
//				String authorizationHeader = "Basic " + base64Credentials;
//				
//				HttpClient client = HttpClient.newHttpClient();
//				
//				HttpRequest request = HttpRequest.newBuilder()
//						.uri(URI.create("https://api.agora.io/dev/v1/projects"))
//		                .GET()
//		                .header("Authorization", authorizationHeader)
//		                .header("Content-Type", "application/json")
//		                .build();
//				
//				HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
				
				//On envoi le fichier au client
				sendResponse(socket, "200", contentType, Files.readAllBytes(filePath));
			} else {
				//Si le fichier n'est pas trouvé, on envoi le code html pour afficher une erreur 404
				statusLine = "404 File not found";
				contentTypeLine = "File doesn't exists";
				entityBody = "<HTML>" + 
					"<HEAD><TITLE>404 - Not Found</TITLE></HEAD>" +
					"<BODY>Error 404 - File Not Found</BODY></HTML>";
				
				outS.writeBytes(entityBody);
				
//				URL url = getClass().getResource("/tmp/404.html");
//
//				System.out.println("url : " + url);
//				
//				//Path Error404_Path = Paths.get("tmp/404.html");
//				Path Error404_Path = Paths.get(url.toURI());
//				
//				String contentType = guessContentType(Error404_Path);
//				sendResponse(socket, "400", contentType, Files.readAllBytes(Error404_Path));
			}
		} else {
			//Si la méthode n'est pas une méthode GET, on ne fait rien
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

	/*
	 * On envoi la réponse au client
	 */
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

	/*
	 * Si le chemin fini par un '/', on rajoute 'index.html' pour afficher la page index du site.
	 * On retourne ensuite le chemin vers le fichier en ajoutant le chemin vers le dossier contenant les sites web pour obtenir le chemin complet jusqu'au fichier
	 */
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
