package protocolo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Files {
	public static void sendFile(String filename, Socket socket) throws Exception {
		System.out.println("File to send: " + filename);
		File localFile = new File(filename);
		BufferedInputStream fromFile = new BufferedInputStream(new FileInputStream(localFile));

		long size = localFile.length();
		System.out.println("size: " + size);

		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
		printWriter.println(filename);

		printWriter.println("Size:" + size);

		BufferedOutputStream toNetwork = new BufferedOutputStream(socket.getOutputStream());

		pause(50);

		byte[] blockToSend = new byte[512];

		int in;

		while ((in = fromFile.read(blockToSend)) != -1) {
			toNetwork.write(blockToSend, 0, in);

		}

		toNetwork.flush();

		fromFile.close();

		pause(50);
		System.out.println("File sent");

	}

	private static void pause(int miliseconds) {
		try {
			Thread.sleep(miliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public static String receiveFile(String folder, Socket socket) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		BufferedInputStream fromNetwork = new BufferedInputStream(socket.getInputStream());

		String filename = reader.readLine();
		filename = folder + File.separator + filename;

		BufferedOutputStream toFile = new BufferedOutputStream(new FileOutputStream(filename));
		System.out.println("File to receive: " + filename);

		String sizeString = reader.readLine();

		long size = Long.parseLong(sizeString.split(":")[1]);

		System.out.println("Size: " + size);

		byte[] blockToReceive = new byte[512];

		int in;
		long remainder = size;
		while ((in = fromNetwork.read(blockToReceive)) != -1) {
			toFile.write(blockToReceive, 0, in);
			remainder -= in;
			if (remainder == 0)
				break;
		}
		pause(50);
		toFile.flush();

		toFile.close();
		System.out.println("File received: " + filename);

		return filename;

	}
}