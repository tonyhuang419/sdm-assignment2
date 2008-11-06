package nl.utwente.sdm.assigment2.client;

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
public class ClientListenThread extends Thread {
	/** The socket of the server. */
	private Socket _socket;
	
	/** The reference to the client. */
	private Client _client;
	
	/**
	 * Constructor.
	 * @param socket The socket for the server thread.
	 */
	public ClientListenThread(Socket socket, Client client) {
		super("ClientListenThread");
		_socket = socket;
		_client = client;
	}

	/**
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		try {
			// Create the input and output stream.
			PrintWriter out = new PrintWriter(_socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(_socket.getInputStream()));

			// Read in the whole message.
			LinkedList<String> lines = new LinkedList<String>();
			String line;
			while((line = in.readLine()) != null) {
				// If it is end of message, then stop reading.
				if (line.equals(IBEMessageProtocolCommands.END_OF_MESSAGE)) {
					break;
				}
				lines.add(line);
			}
			
			System.out.println("Received message (first line): " + lines.get(0));
			
			// Decrypt the message send.
			//String message = IBEHelper.decryptMessage(_client.getPrivateKey(), _client.getSystemParameters(), encryptedMessage.getBytes());
			
			// Now delegate the message to the protocol, which will process it and get the correct response.
			IBEMessageProtocol imp = new IBEMessageProtocol(_client);
			out.println(imp.processInput(lines));
			out.println(IBEMessageProtocolCommands.END_OF_MESSAGE);
			
			// Close the streams.
			out.close();
			in.close();
			_socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
