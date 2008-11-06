package nl.utwente.sdm.assigment2.client;

import java.io.IOException;
import java.net.ServerSocket;

import nl.utwente.sdm.assigment2.IBEMessageProtocolConstants;

public class ClientListenSocket extends Thread {
	private Client _client;
	
	private ServerSocket _socket;
	
	public ClientListenSocket(Client client) {
		_client = client;
	}

	@Override
	public void run() {
		super.run();
		
        try {
        	_socket = new ServerSocket(IBEMessageProtocolConstants.CLIENT_LISTEN_PORT);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + IBEMessageProtocolConstants.CLIENT_LISTEN_PORT);
            System.exit(-1);
        }
        
        System.out.println("Client socket ready for messages.");

        while (true) {
        	try {
				new ClientListenThread(_socket.accept(), _client).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
	}
}
