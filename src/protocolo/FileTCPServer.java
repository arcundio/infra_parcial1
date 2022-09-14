package protocolo;

import java.net.ServerSocket;
import java.net.Socket;

public class FileTCPServer {
	
    final int PORT = 4000;

	private ServerSocket listener;
	private Socket serverSideSocket;

	public FileTCPServer() {
		System.out.println("File TCP Server ...");
	}

	private void init() throws Exception {
		listener = new ServerSocket(PORT);

		while (true) {
			serverSideSocket = listener.accept();

			protocol(serverSideSocket);
		}
	}

	private void protocol(Socket socket) throws Exception {
		Files.receiveFile("Download", socket);
		Files.sendFile("Transferencia de archivos en Java.pdf", socket);
	}

	public static void main(String[] args) throws Exception {
		FileTCPServer os = new FileTCPServer();
		os.init();
	}
}