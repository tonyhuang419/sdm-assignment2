package nl.utwente.sdm.assigment2.gateway;

import java.net.*;
import java.io.*;

/**
 * The Server thread for the gateway, these threads are created for each client which connects to the gateway.
 * @author Harmen
 */
public class GatewayServerThread extends Thread {
	/** The maximal length of a message, which is received. */
	private static final int MAX_MESSAGE_LENGTH = 1000000;
	
	/** The socket of the server. */
	private Socket socket = null;

	/**
	 * Constructor.
	 * @param socket The socket for the server thread.
	 */
	public GatewayServerThread(Socket socket) {
		super("GatewayServerThread");
		this.socket = socket;
	}

	/**
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		try {
			// Create the input and output stream.
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			// Read in the whole message. The maximum length of the message is MAX_MESSAGE_LENGTH.
			StringBuffer input = new StringBuffer();
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				if (input.length() < MAX_MESSAGE_LENGTH) {
					input.append(inputLine);
				} else {
					out.write("Message too long!");
					System.out.println("Received message too long!");
					break;
				}
			}
			
			// Now delegate the message to the protocol, which will process it and get the correct response.
			IBEMessageProtocol imp = new IBEMessageProtocol();
			out.write(imp.processInput(input.toString()));
			
			// Close the streams.
			out.close();
			in.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
