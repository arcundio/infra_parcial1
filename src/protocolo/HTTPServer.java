package protocolo;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class HTTPServer {

	public static final int PORT = 4000;

	private ServerSocket listener;
	private Socket serverSideSocket;

	private PrintWriter toNetWork;
	private BufferedReader fromNetwork;

	File filePath = new File("resources");
	File notFound = new File("resources/NotFound.html");
	File[] listFilesDirectory = filePath.listFiles();

	public HTTPServer() {
		System.out.println("Echo TCP server is running on port " + PORT);
	}

	public void init() throws Exception {
		listener = new ServerSocket(PORT);
		while (true) {
			serverSideSocket = listener.accept();
			createStreams(serverSideSocket);
			protocol(serverSideSocket);

		}
	}

	private void protocol(Socket socket) throws Exception {

		String message;
		File searchedFile = null;

		while ((message = fromNetwork.readLine()) != null) {

			if (message.contains("GET")) {
				if (searchFile(message.split(" ")[1]) != null) { // toma el nombre del archivo y lo busca
					searchedFile = searchFile(message.split(" ")[1]);
				} else {					
					String date = getDateGMT();
					//Este syso no va, esto hay que mandarselo al cliente cuando encontremos la forma
					String responseNotFound = "HTTP/1.1 404 Not Found\r\n" + "Server: Server HTTP\r\n" + "Date: " + date  + "\r\n"
							+ "Content-Type: " + notFound.getClass().getName()
							+ "\r\n" + "Content-Length: " + notFound.length() + "\r\n" + "\r\n";

					System.out.println(responseNotFound);
					return;
				}
			}

			System.out.println("[Server] From client: " + message);

			if (message.isEmpty()) {
				break;
			}

		}

		String lastModified = getLastModifiedGMT(searchedFile.lastModified()); // Obtiene la ultima modificacion del
		String date = getDateGMT();																	// archivo
		//Este syso no va, esto hay que mandarselo al cliente cuando encontremos la forma
		//Nota: Falta que ponga correctamente el content-type, tanto en esta respuesta como en la del Not Found
		String responseFound = "HTTP/1.1 200 OK\r\n" + "Server: Server HTTP\r\n" + "Date: " + date  + "\r\n"
				+ "Last-Modified: " + lastModified + "\r\n" + "Content-Type: " + searchedFile.getClass().getName()
				+ "\r\n" + "Content-Length: " + searchedFile.length() + "\r\n" + "\r\n";

		System.out.println(responseFound);

		// toNetWork.println(response);

	}

	private String getDateGMT() {
		
		Date localTime = new Date();
		
		String pattern = "dd/MM/yyyy" + " " +"HH:mm:ss";
		DateFormat dateFormat = new SimpleDateFormat(pattern);

		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		return dateFormat.format(localTime);
	}

	private String getLastModifiedGMT(long lastModified) {

		String pattern = "dd/MM/yyyy" + " " +"HH:mm:ss";
		DateFormat dateFormat = new SimpleDateFormat(pattern);

		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		return dateFormat.format(lastModified);
	}

	File searchFile(String fileToSearch) {

		fileToSearch = fileToSearch.split("/")[1]; // Toma el nombre del archivo sin el '/'

		for (File file : listFilesDirectory) {
			if (fileToSearch.equals(file.getName())) {
				return file;
			}
		}

		return null;

	}

	private void createStreams(Socket socket) throws Exception {
		toNetWork = new PrintWriter(socket.getOutputStream(), true);
		fromNetwork = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	public static void main(String[] args) throws Exception {
		HTTPServer es = new HTTPServer();
		es.init();

	}
}