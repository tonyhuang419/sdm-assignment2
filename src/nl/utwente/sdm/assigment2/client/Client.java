package nl.utwente.sdm.assigment2.client;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;

import nl.utwente.sdm.assigment2.IBEHelper;
import nl.utwente.sdm.assigment2.IBEMessageProtocolConstants;
import nuim.cs.crypto.ibe.IbePrivateKey;
import nuim.cs.crypto.ibe.IbeSystemParameters;

/**
 * The Main class for the Client.
 * @author Harmen
 */
public class Client extends JFrame {
	/** The default serial version uid. */
	private static final long serialVersionUID = 1L;

	/** The identity of the client. */
	private String _identity;
	
	/** The parameters to use, for encrypting messages. */
	private IbeSystemParameters _systemParameters;
	
	/** The private key for the client. */
	private IbePrivateKey _privateKey;
	
	/** The address to the gateway server. */
	private String _gatewayAddress;
	
	/** The address of the local machine of the client, so the gateway can send messages to it. */
	private String _localAddress;
	
	/** The port on which the local machine should listen to. */
	private int _localPort;
	
	/** The MessageDigest to use for the encryption. */
	private MessageDigest _hash;

	private JTable _trapdoorTable;

	private DefaultTableModel _tableModel;

	/**
	 * The procedure will set the correct look and feel,
	 * will ask the user for the identity and start the client.
	 * @param args Is not used.
	 */
	public static void main(String[] args) {
	    try {
		    // Set System L&F so it will look as a 'normal' application.
	        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } 
	    catch (UnsupportedLookAndFeelException e) {}
	    catch (ClassNotFoundException e) {}
	    catch (InstantiationException e) {}
	    catch (IllegalAccessException e) {}
		
	    // Get the information to be able to startup the client.
		String identity = JOptionPane.showInputDialog("What is your identity?");
		String keyServerAddress = JOptionPane.showInputDialog("What is the address of the key server?", "localhost");
		String gatewayAddress = JOptionPane.showInputDialog("What is the address of the gateway?", "localhost");
		String localAddress = JOptionPane.showInputDialog("What is the address of this pc?", "localhost");
		int localPort = new Integer(JOptionPane.showInputDialog("What port should be used for the client?", IBEMessageProtocolConstants.CLIENT_LISTEN_PORT)).intValue();
		while (!(localPort > 10002 && localPort < 20000)) {
			localPort = new Integer(JOptionPane.showInputDialog("What port should be used for the client (between 10003 and 20000)?", IBEMessageProtocolConstants.CLIENT_LISTEN_PORT)).intValue();
		}
		
		// Start the client.
		new Client(identity, keyServerAddress, gatewayAddress, localAddress, localPort);
	}
	
	/**
	 * Constructor.
	 * @param identity The identity of the client.
	 */
	public Client(String identity, String keyServerAddress, String gatewayAddress, String localAddress, int localPort) {
		super("IBEClient - " + identity + " (" + localAddress + ":" + localPort + ")");
		
		// Save the information to instance variables.
		_identity = identity;
		_gatewayAddress = gatewayAddress;
		_localAddress = localAddress;
		_localPort = localPort;
		try {
			_hash = IBEHelper.getMessageDigest();
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		
		try {
			Object[] sysParamsAndPrivateKey = IBEHelper.authenticateWithKeyServer(keyServerAddress, _hash, _identity);
			System.out.println("Authenticating with key server.");
			_systemParameters = (IbeSystemParameters) sysParamsAndPrivateKey[0];
			System.out.println("Succesfully received public parameters.");
			_privateKey = (IbePrivateKey) sysParamsAndPrivateKey[1];
			System.out.println("Succesfully received private key.");
			
			System.out.println("Registering in Gateway server.");
			String gatewayResponse = IBEHelper.registerToGateway(_identity, _localAddress, _localPort, _gatewayAddress);
			System.out.println("Response from gateway: " + gatewayResponse);
			
			// Popup the GUI.
			createGUI();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Now we have to start the client socket, so the client can receive messages.
		System.out.println("Starting client listen socket.");
		ClientListenSocket socket = new ClientListenSocket(this);
		socket.run();
		System.out.println("Client listen socket started.");
	}
	
	/**
	 * Create the GUI for the client and make it visable.
	 */
	public void createGUI() {
		// Set some options for the JFrame.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(700, 300);
		
		getContentPane().setLayout(new BorderLayout());
		
		JTabbedPane tabPane = new JTabbedPane();
		
		/**
		 * Create the pane for messaging.
		 */
		JPanel messagingPane = new JPanel(new BorderLayout());
		
		JLabel explanationLabel = new JLabel("Enter some keywords (space seperated), a message, the identity of the user to send the message to and click send.");
		explanationLabel.setFont(explanationLabel.getFont().deriveFont(Font.BOLD));
		explanationLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		messagingPane.add(explanationLabel, BorderLayout.NORTH);
		
		JPanel gridPanel = new JPanel(new BorderLayout());
		
		JPanel labelPanel = new JPanel(new BorderLayout());
		labelPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		JLabel keywordLabel = new JLabel("Keywords: ");
		labelPanel.add(keywordLabel, BorderLayout.NORTH);
		JLabel messageLabel = new JLabel("Message: ");
		messageLabel.setVerticalAlignment(SwingConstants.TOP);
		labelPanel.add(messageLabel, BorderLayout.CENTER);
		JLabel sendToLabel = new JLabel("Send to: ");
		labelPanel.add(sendToLabel, BorderLayout.SOUTH);
		
		JPanel textFieldPanel = new JPanel(new BorderLayout());
		textFieldPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		final JTextField keywordsField = new JTextField();
		textFieldPanel.add(keywordsField, BorderLayout.NORTH);
		final JTextArea messageArea = new JTextArea();
		textFieldPanel.add(messageArea, BorderLayout.CENTER);
		final JTextField sendToField = new JTextField();
		textFieldPanel.add(sendToField, BorderLayout.SOUTH);
		
		gridPanel.add(labelPanel, BorderLayout.LINE_START);
		gridPanel.add(textFieldPanel, BorderLayout.CENTER);
		
		messagingPane.add(gridPanel, BorderLayout.CENTER);
		
		final JButton clearButton = new JButton(new AbstractAction() {
			/** Default serial version uid. */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent arg0) {
				keywordsField.setText("");
				messageArea.setText("");
				sendToField.setText("");
			}
		});
		clearButton.setText("Clear");
		
		JButton sendButton = new JButton(new AbstractAction() {
			/** Default serial version uid. */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent arg0) {
				String keywords = keywordsField.getText();
				String message = messageArea.getText();
				String sendToIdentity = sendToField.getText();
				
				ProgressMonitor progress = new ProgressMonitor(Client.this, "Sending message", "", 0, 100);
				progress.setProgress(1);
				SendStatusRunnable sendMessage = new SendStatusRunnable(message, keywords, _identity, sendToIdentity, _gatewayAddress, _systemParameters, _hash, progress);
				sendMessage.start();
				// Clear the fields.
				clearButton.doClick();
			}
		});
		sendButton.setText("Send");
		
		JPanel sendButtonPanel = new JPanel();
		sendButtonPanel.add(clearButton);
		sendButtonPanel.add(sendButton);
		messagingPane.add(sendButtonPanel, BorderLayout.SOUTH);
		
		tabPane.addTab("Messaging", messagingPane);
		
		/**
		 * Create the pane for setting trapdoors.
		 */
		JPanel trapdoorPane = new JPanel(new BorderLayout());
		
		// Add some explanation to the pane.
		JLabel trapdoorExplanation = new JLabel("You can create trapdoors here. A trapdoor is an action which is fired when a certain keyword exists in the keyword list of a message, the message is then send to the device of defined in the trapdoor.");
		trapdoorExplanation.setFont(explanationLabel.getFont().deriveFont(Font.BOLD));
		trapdoorExplanation.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		trapdoorPane.add(trapdoorExplanation, BorderLayout.NORTH);
		// Add a table containing the trapdoors.
		JPanel trapdoorTablePanel = new JPanel(new BorderLayout());
		trapdoorTablePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		Vector<String> columnNames = new Vector<String>();
		columnNames.add("Keyword");
		columnNames.add("Address");
		columnNames.add("Port");
		final Vector<Vector<String>> trapdoors = new Vector<Vector<String>>();
		_tableModel = new DefaultTableModel(trapdoors, columnNames);
		_trapdoorTable = new JTable(_tableModel);
		JScrollPane tableScrollPane = new JScrollPane(_trapdoorTable);
		trapdoorTablePanel.add(tableScrollPane, BorderLayout.CENTER);
		trapdoorPane.add(trapdoorTablePanel, BorderLayout.CENTER);
		// Add the buttons for adding trapdoors.
		JPanel trapdoorButtonPane = new JPanel();
		trapdoorButtonPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		JButton addTrapdoorButton = new JButton(new AbstractAction() {
			/** Default serial version uid. */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// Popup a question for adding the trapdoor.
				String keyword = JOptionPane.showInputDialog("On which keyword should the trapdoor be fired?");
				String hashedKeyword = keyword;
				// Get the devices of the client and give the user a selection option.
				String deviceHost = JOptionPane.showInputDialog("What is the host of the device the message should be send to?");
				int devicePort = new Integer(JOptionPane.showInputDialog("What is the port on which the device listens to?")).intValue();
				
				// Now send the request to add a trapdoor to the gateway.
				try {
					String addTrapdoorAtGateway = IBEHelper.addTrapdoorAtGateway(_gatewayAddress, _identity, hashedKeyword, deviceHost, devicePort, _hash);
					JOptionPane.showMessageDialog(Client.this, addTrapdoorAtGateway);
					if (addTrapdoorAtGateway.equals("Trapdoor succesfully added.")) {
						Vector<String> trapdoor = new Vector<String>();
						trapdoor.add(keyword);
						trapdoor.add(deviceHost);
						trapdoor.add("" + devicePort);
						_tableModel.addRow(trapdoor);
					}
				} catch (HeadlessException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		addTrapdoorButton.setText("Add trapdoor");
		trapdoorButtonPane.add(addTrapdoorButton);
		trapdoorPane.add(trapdoorButtonPane, BorderLayout.SOUTH);
		
		tabPane.addTab("Trapdoors", trapdoorPane);
		
		// Add the tabbed pane to the contentpane of the window.
		getContentPane().add(tabPane, BorderLayout.CENTER);
		
		// Show the frame.
		setVisible(true);
	}
	
	public void showReceivedMessage(final String fromIdentity, final String message) {
		Thread showWindows = new Thread() {
			@Override
			public void run() {
				super.run();
				JOptionPane.showMessageDialog(Client.this, "Received message from " + fromIdentity);
			}
		};
		showWindows.start();
	}
	
	public IbePrivateKey getPrivateKey() {
		return _privateKey;
	}
	
	public IbeSystemParameters getSystemParameters() {
		return _systemParameters;
	}

	public int getPortNumber() {
		return _localPort;
	}
	
	private class SendStatusRunnable extends Thread {
		private String _message;
		private String _keywords;
		private String _fromIdentity;
		private String _sendToIdentity;
		private String _gatewayAddress;
		private IbeSystemParameters _systemParameters;
		private MessageDigest _hash;
		private ProgressMonitor _progress;
		
		public SendStatusRunnable(String message, String keywords, String fromIdentity, String sendToIdentity, String gatewayAddress, IbeSystemParameters systemParameters, MessageDigest hash, ProgressMonitor progress) {
			_message = message;
			_keywords = keywords;
			_fromIdentity = fromIdentity;
			_sendToIdentity = sendToIdentity;
			_gatewayAddress = gatewayAddress;
			_systemParameters = systemParameters;
			_hash = hash;
			_progress = progress;
		}
		
		@Override
		public void run() {
			try {
				String sendStatus = IBEHelper.sendMessageToGateway(_message, _keywords, _fromIdentity, _sendToIdentity, _gatewayAddress, _systemParameters, _hash, _progress);
				
				_progress.close();
				JOptionPane.showMessageDialog(Client.this, "Message send status: " + sendStatus);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

}
