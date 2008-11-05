package nl.utwente.sdm.assigment2.gateway;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			// Read in the whole message. The maximum length of the message is MAX_MESSAGE_LENGTH.
			String encryptedMessage = in.readLine();
			
			String message = encryptedMessage;
			// Decrypt the message send.
			//String message = IBEHelper.decryptMessage(_gateway.getPrivateKey(), _gateway.getSystemParameters(), encryptedMessage.getBytes());
			
			System.out.println("Received message: " + message.toString());
			
			// Now delegate the message to the protocol, which will process it and get the correct response.
			IBEMessageProtocol imp = new IBEMessageProtocol();
			out.write(imp.processInput(message));
			
			// Close the streams.
			out.close();
			in.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
