package nl.utwente.sdm.assigment2;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;

import nuim.cs.crypto.bilinear.BilinearMap;
import nuim.cs.crypto.bilinear.ModifiedTatePairing;
import nuim.cs.crypto.ibe.IbeKeyParameters;
import nuim.cs.crypto.ibe.IbeProvider;
import nuim.cs.crypto.ibe.IbePublicKey;

/**
 * A helper class to generate the public and private keys.
 * @author Harmen
 */
public class IBEHelper {
	// The hash we use.
	public static MessageDigest _hash;

	public IBEHelper() {
		try {
			_hash = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the hash we use.
	 * @return The MessageDigest of the hash.
	 */
	public MessageDigest getHash() {
		return _hash;
	}

	/**
	 * Function to generate a public key according to a identity.
	 * @param identity The identity of the client.
	 * @return The public key for the client with the given identity.
	 */
	public PublicKey getPublicKey(String identity) {
		// set the identity
		IbeKeyParameters keyParameters = new IbeKeyParameters(_hash, identity);
		// get the public key based on the identity
		return new IbePublicKey(keyParameters.getPublicKey());
	}

	/**
	 * Generate a private key according to a given identity.
	 * @param identity The identity of the client for this private key.
	 * @return The private key for the client with the given identity.
	 */
	public PrivateKey getPrivateKey(String identity) {
		Provider provider = new IbeProvider();

		// get the ibe key pair generator
		KeyPairGenerator kpg = null;
		try {
			kpg = KeyPairGenerator.getInstance(IbeProvider.IBE, provider);
		} catch (NoSuchAlgorithmException nsae) {
			// handle exception
			nsae.printStackTrace();
		}

		// create the parameters
		// set the system parameters
		BilinearMap map = new ModifiedTatePairing();

		BigInteger masterKey = new BigInteger(map.getQ().bitLength() - 1,
				new SecureRandom());

		boolean fieldTooSmall = false;
		do {
			fieldTooSmall = false;
			// get a count of the maximum possible number of
			// points that we could use, i.e. p / kappa where
			// p is the upper limit of finite field Fp
			BigInteger maxPts = map.getCurve().getField().getChar().divide(
					map.getCurve().getKappa());
			int bytes = maxPts.bitLength() / 8;
			if (bytes < _hash.getDigestLength()) {
				map = new ModifiedTatePairing();
				fieldTooSmall = true;
			}
		} while (fieldTooSmall);

		// initialize the key pair generator
		IbeKeyParameters keyParameters = new IbeKeyParameters(_hash, identity,
				masterKey, map);
		try {
			kpg.initialize(keyParameters);
		} catch (InvalidAlgorithmParameterException iape) {
			// handle exception
			iape.printStackTrace();
		}
		KeyPair keyPair = kpg.generateKeyPair();
		return keyPair.getPrivate();
	}
}
