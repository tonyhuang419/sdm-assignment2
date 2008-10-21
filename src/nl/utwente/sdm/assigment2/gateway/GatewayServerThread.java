package nl.utwente.sdm.assigment2.gateway;

import java.net.*;
import java.io.*;

public class GatewayServerThread extends Thread {
	private Socket socket = null;

	public GatewayServerThread(Socket socket) {
		super("GatewayServerThread");
		this.socket = socket;
	}

	public void run() {
		try {
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			StringBuffer input = new StringBuffer();
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				if (input.length() < 1000000) {
					input.append(inputLine);
				} else {
					out.write("Message too long!");
					System.out.println("Message too long.");
					break;
				}
			}
			
			IBEMessageProtocol imp = new IBEMessageProtocol();
			out.write(imp.processInput(input.toString()));
			
			out.close();
			in.close();
			socket.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
