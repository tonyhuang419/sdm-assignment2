package nl.utwente.sdm.assigment2.gateway;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;

import nl.utwente.sdm.assigment2.IBEMessageProtocolCommands;

/**
 * The Server thread for the gateway, these threads are created for each client which connects to the gateway.
 * @author Harmen
 */
public class GatewayServerThread extends Thread {
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
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

			LinkedList<String> lines = new LinkedList<String>();
			String line;
			while((line = in.readLine()) != null) {
				// If it is end of message, then stop reading.
				if (line.equals(IBEMessageProtocolCommands.END_OF_MESSAGE)) {
					break;
				}
				lines.add(line);
			}
			
			// Decrypt the message send.
			//String message = IBEHelper.decryptMessage(_gateway.getPrivateKey(), _gateway.getSystemParameters(), encryptedMessage.getBytes());
			
			System.out.println("Received message (first line): " + lines.get(0));
			
			// Now delegate the message to the protocol, which will process it and get the correct response.
			IBEMessageProtocol imp = new IBEMessageProtocol();
			out.println(imp.processInput(lines));
			out.println(IBEMessageProtocolCommands.END_OF_MESSAGE);
			
			// Close the streams.
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
