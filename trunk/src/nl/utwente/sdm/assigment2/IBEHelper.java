package nl.utwente.sdm.assigment2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import nuim.cs.crypto.bilinear.BilinearMap;
import nuim.cs.crypto.bilinear.ModifiedTatePairing;
import nuim.cs.crypto.blitz.field.Fp;
import nuim.cs.crypto.blitz.point.AffinePoint;
import nuim.cs.crypto.ibe.IbeKeyParameters;
import nuim.cs.crypto.ibe.IbePrivateKey;
import nuim.cs.crypto.ibe.IbeProvider;
import nuim.cs.crypto.ibe.IbePublicKey;
import nuim.cs.crypto.ibe.IbeSystemParameters;

/**
 * A helper class to generate the public and private keys.
 * @author Harmen
 */
public class IBEHelper {
	/**
	 * Function to generate a public key according to a identity.
	 * @param identity The identity of the client.
	 * @return The public key for the client with the given identity.
	 */
	public static PublicKey getPublicKey(String identity, MessageDigest hash) {
		// set the identity
		IbeKeyParameters keyParameters = new IbeKeyParameters(hash, identity);
		// get the public key based on the identity
		return new IbePublicKey(keyParameters.getPublicKey());
	}

	/**
	 * Generate a private key according to a given identity.
	 * @param identity The identity of the client for this private key.
	 * @return The private key for the client with the given identity.
	 */
	public static PrivateKey getPrivateKey(String identity, BilinearMap map, MessageDigest hash, BigInteger masterKey) {
        try {
        	// Get the key pair generator.
        	KeyPairGenerator kpg = KeyPairGenerator.getInstance( IbeProvider.IBE, new IbeProvider() );
        	
        	// Create the key paramaters.
            IbeKeyParameters keyParameters = new IbeKeyParameters(hash, identity, masterKey, map);
            
            // initialize the key pair generator
            kpg.initialize(keyParameters);
            
            // Generate the key pair.
            KeyPair keyPair = kpg.generateKeyPair();
            
            // Return the private key.
            return keyPair.getPrivate();
        } catch( NoSuchAlgorithmException nsae ) {
            nsae.printStackTrace();
        } catch (InvalidAlgorithmParameterException iape) {
        	iape.printStackTrace();
        }
        return null;
	}
	
	/**
	 * Procedure to encrypt a message using a public key and the system parameters.
	 * @param message The message to encrypt.
	 * @param publicKey The public key to encrypt with.
	 * @param systemParameters The system parameter using while encrypting.
	 * @return The encrypted message, the cipher text as a byte[].
	 */
	public static byte[] encryptMessage(byte[] plaintext, PublicKey publicKey, IbeSystemParameters systemParameters) {
        try {
        	Provider provider = new IbeProvider();
        	// get the ibe cipher
        	Cipher cipher = Cipher.getInstance( IbeProvider.IBE, provider );
        	cipher.init( Cipher.ENCRYPT_MODE, publicKey, systemParameters, new SecureRandom() );
        	// encrypt the plaintext
        	return cipher.doFinal( plaintext );
        } catch( NoSuchAlgorithmException nsae ) {
            // handle exception
            nsae.printStackTrace();
        } catch( NoSuchPaddingException nspe ) {
            // handle exception
            nspe.printStackTrace();
        } catch( InvalidKeyException ike ) {
        	ike.printStackTrace();
        } catch( InvalidAlgorithmParameterException iape ) {
        	iape.printStackTrace();
        } catch( IllegalBlockSizeException ibse ) {
        	ibse.printStackTrace();
        } catch( BadPaddingException bpe ) {
        	bpe.printStackTrace();
        }
        return null;
	}
	
	public static String decryptMessage(PrivateKey privateKey, IbeSystemParameters systemParameters, byte[] ciphertext) {
        try {
        	Cipher cipher = Cipher.getInstance( IbeProvider.IBE, new IbeProvider() );
            cipher.init( Cipher.DECRYPT_MODE, privateKey, systemParameters, new SecureRandom() );
            byte decryptedPlaintext[] = cipher.doFinal( ciphertext );
            return new String(decryptedPlaintext);
        } catch( InvalidKeyException ike ) {
            ike.printStackTrace();
        } catch( InvalidAlgorithmParameterException iape ) {
            iape.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch( IllegalBlockSizeException ibse ) {
			ibse.printStackTrace();
		} catch( BadPaddingException bpe ) {
			bpe.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Procedure to authenticate with the key server to get the private key.
	 * @param keyServerAddress The address of the keyserver.
	 * @param hash The MessageDigest to use.
	 * @param identity The identity to authenticate.
	 * @return The IbeSystemParameters and the PrivateKey in an Object array, first element is parameters, and second the private key.
	 * @throws IOException
	 */
	public static Object[] authenticateWithKeyServer(String keyServerAddress, MessageDigest hash, String identity) throws IOException {
		// Send the get globals command.
		String response = sendMessage(keyServerAddress, IBEMessageProtocolConstants.KEY_SERVER_PORT, IBEMessageProtocolCommands.AUTHENTICATE + " " + identity);
		
		// Response should contain the p, the pPub and variables to construct the map.
		// To construct the map we need the p, q and l.
		// The response should be formatted as follows: [p], [q], [l], [pointPx], [pointPy], [pointPPubx], [pointPPuby], [privateKeyX], [privateKeyY].
		String[] splitResponse = response.split(" ");
		if (splitResponse.length != 9) {
			throw new IOException("Received message contained an invalid number of arguments. (IbeHelper::authenticateWithKeyServer)");
		} else {
			String mapP = splitResponse[0];
			String mapQ = splitResponse[1];
			String mapL = splitResponse[2];
			
			String ibeSysParamsPx = splitResponse[3];
			String ibeSysParamsPy = splitResponse[4];
			String ibeSysParamsPPubx = splitResponse[5];
			String ibeSysParamsPPuby = splitResponse[6];
			
			String x = splitResponse[7];
			String y = splitResponse[8];
			
			BigInteger mapPInteger = new BigInteger(mapP);
			Fp fp = new Fp(mapPInteger);
			
			// Create the map using the send variables for the map.
			ModifiedTatePairing map = new ModifiedTatePairing(mapPInteger, new BigInteger(mapQ), new BigInteger(mapL));
			
			// Construct the AffinePoints for the system parameters. These are the points p and pPub.
			AffinePoint pointP = new AffinePoint(fp.element(new BigInteger(ibeSysParamsPx)), fp.element(new BigInteger(ibeSysParamsPy)));
			AffinePoint pointPpub = new AffinePoint(fp.element(new BigInteger(ibeSysParamsPPubx)), fp.element(new BigInteger(ibeSysParamsPPuby)));
			
			IbePrivateKey privateKey = new IbePrivateKey(new AffinePoint(fp.element(new BigInteger(x)), fp.element(new BigInteger(y))));
			//System.out.println("Received privatekey: " + privateKey.toString());
			
			IbeSystemParameters systemParameters = new IbeSystemParameters(map, pointP, pointPpub, hash);
			
			// Return the systemparameters using the send information.
			return new Object[]{systemParameters, privateKey};
		}
	}
	
	public static String registerToGateway(String identity, String address, String gatewayAddress) throws IOException {
		return sendMessage(gatewayAddress, IBEMessageProtocolConstants.GATEWAY_SERVER_PORT, IBEMessageProtocolCommands.REGISTER + " " + identity + " " + address);
	}
	
	public static String sendMessageToGateway(String message, String keywords, String fromIdentity, String sendToIdentity, String gatewayAddress, IbeSystemParameters systemParameters, MessageDigest hash) throws UnknownHostException, IOException {
		// Encrypt the message to send to the client using the identity of the receiver.
		byte[] encryptedMessage = encryptMessage(message.getBytes(), getPublicKey(sendToIdentity, hash), systemParameters);
		//System.out.println("Size of send encrypted message: " + encryptedMessage.length);
		//System.out.println("Encrypted message to client: " + new String(encryptedMessage));
		
		int nrOfKeywords = keywords.split(" ").length;
		
		// Connect to the gateway.
		Socket socket = new Socket(gatewayAddress, IBEMessageProtocolConstants.GATEWAY_SERVER_PORT);
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		// Send the message command to the gateway.
		out.println(IBEMessageProtocolCommands.MESSAGE + " " + fromIdentity + " " + sendToIdentity + " " + nrOfKeywords + " " + keywords + " ");
		out.println(new String(encryptedMessage));
		out.println(IBEMessageProtocolCommands.END_OF_MESSAGE);
		
		StringBuffer inputBuffer = new StringBuffer();
		String line;
		while((line = in.readLine()) != null) {
			// If it is end of message, then stop reading.
			if (line.equals(IBEMessageProtocolCommands.END_OF_MESSAGE)) {
				System.out.println("Found end of message.");
				break;
			}
			inputBuffer.append(line);
		}
		String result = inputBuffer.toString();
		
		in.close();
		out.close();
		// Read the response from the server.
		return result;
	}
	
	public static String deliverMessageToClient(String fromIdentity, String message, String clientAddress) throws UnknownHostException, IOException {
		// Connect to the client.
		Socket socket = new Socket(clientAddress, IBEMessageProtocolConstants.CLIENT_LISTEN_PORT);
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		// Send the message command to the gateway.
		out.println(message);
		out.println(IBEMessageProtocolCommands.END_OF_MESSAGE);
		
		String result = in.readLine();
		
		in.close();
		out.close();
		// Read the response from the server.
		return result;
	}
	
	/**
	 * Procedure to send a message to a server.
	 * @param serverAddress The address of the server.
	 * @param serverPort The post of the server.
	 * @param message The message which has to be send to the server.
	 * @return The response from the server.
	 * @throws IOException
	 */
	private static String sendMessage(String serverAddress, int serverPort, String message) throws IOException {
		Socket socket = new Socket(serverAddress, serverPort);
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		// Send the authenticate command.
		out.println(message);
		out.println(IBEMessageProtocolCommands.END_OF_MESSAGE);
		
		String response = in.readLine();
		//System.out.println("Received: " + response);
		
		in.close();
		out.close();
		// Read the response from the server.
		return response;
	}
	
	public static MessageDigest getMessageDigest() throws NoSuchAlgorithmException {
		return MessageDigest.getInstance("MD5");
	}
}
