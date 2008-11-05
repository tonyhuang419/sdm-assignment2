package nl.utwente.sdm.assigment2.client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import nl.utwente.sdm.assigment2.IBEHelper;
import nuim.cs.crypto.ibe.IbePrivateKey;
import nuim.cs.crypto.ibe.IbeSystemParameters;

/**
 * The Main class for the Client.
 * @author Harmen
 */
public class Client extends JFrame {
	private String _identity;
	
	private IbeSystemParameters _systemParameters;
	
	private IbePrivateKey _privateKey;
	
	private String _gatewayAddress;
	
	private MessageDigest _hash;

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
		
		// Start the client.
		new Client(identity, keyServerAddress, gatewayAddress);
	}
	
	/**
	 * Constructor.
	 * @param identity The identity of the client.
	 */
	public Client(String identity, String keyServerAddress, String gatewayAddress) {
		super("IBEClient - " + identity);
		_identity = identity;
		_gatewayAddress = gatewayAddress;
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
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		createGUI();
	}
	
	/**
	 * Create the GUI for the client and make it visable.
	 */
	public void createGUI() {
		// Set some options for the JFrame.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(700, 300);
		
		Container contentPane = getContentPane();
		
		contentPane.setLayout(new BorderLayout());
		
		JLabel explanationLabel = new JLabel("Enter some keywords (space seperated), a message, the identity of the user to send the message to and click send.");
		explanationLabel.setFont(explanationLabel.getFont().deriveFont(Font.BOLD));
		explanationLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		contentPane.add(explanationLabel, BorderLayout.NORTH);
		
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
		
		contentPane.add(gridPanel, BorderLayout.CENTER);
		
		final JButton clearButton = new JButton(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				keywordsField.setText("");
				messageArea.setText("");
				sendToField.setText("");
			}
		});
		clearButton.setText("Clear");
		
		JButton sendButton = new JButton(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				String keywords = keywordsField.getText();
				String message = messageArea.getText();
				String sendToIdentity = sendToField.getText();
				
				try {
					String sendStatus = IBEHelper.sendMessageToClient(message, keywords, _identity, sendToIdentity, _gatewayAddress, _systemParameters, _hash);
					JOptionPane.showMessageDialog(Client.this, "Message send status: " + sendStatus);
					// Clear the fields.
					clearButton.doClick();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		sendButton.setText("Send");
		
		JPanel sendButtonPanel = new JPanel();
		sendButtonPanel.add(clearButton);
		sendButtonPanel.add(sendButton);
		contentPane.add(sendButtonPanel, BorderLayout.SOUTH);
		
		// Show the frame.
		setVisible(true);
	}

}
