package nl.utwente.sdm.assigment2.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.LinkedList;

import nl.utwente.sdm.assigment2.IBEMessageProtocolCommands;

public class ClientListenSocket extends Thread {
	private Client _client;
	
	private ServerSocket _serverSocket;
	private Socket _socket;
	
	public ClientListenSocket(Client client) {
		_client = client;
	}

	@Override
	public void run() {
		super.run();
		
        try {
        	_serverSocket = new ServerSocket(_client.getPortNumber());
   
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + _client.getPortNumber());
            System.exit(-1);
        }
        
        System.out.println("Client socket ready for messages.");

		while(true) {
			try {
	    		// Create the input and output stream.
		     	_socket = _serverSocket.accept();
				PrintWriter out = new PrintWriter(_socket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
				
				LinkedList<String> lines = new LinkedList<String>();
				String line;
				
				// Read in the whole message.
	    		while ((line = in.readLine()) != null) {

	    			lines.add(line);
	    			
	    			if(!line.equals(IBEMessageProtocolCommands.END_OF_MESSAGE)) {
	    				
	    			} else { // line.equals(END OF MESSAGE)
	    				//END OF MESSAGE FOUND, process input and afterwards reset the cached lines
	    				IBEMessageProtocol imp = new IBEMessageProtocol(_client);
	        			out.println(imp.processInput(lines));
	        			out.println(IBEMessageProtocolCommands.END_OF_MESSAGE);
	    				lines=null;
	    			}
	    			
	    			//System.out.println("Received message (first line): " + lines.get(0));
	    			
	    			// Decrypt the message send.
	    			//String message = IBEHelper.decryptMessage(_client.getPrivateKey(), _client.getSystemParameters(), encryptedMessage.getBytes());
	    			
	    			// Now delegate the message to the protocol, which will process it and get the correct response.
	
	   			}
	    		//Close the streams.
				out.close();
				in.close();
				_socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
