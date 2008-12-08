package nuim.cs.crypto.ibe;

import java.math.BigInteger;
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
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.suiterunner.Suite;

import nuim.cs.crypto.bilinear.BilinearMap;
import nuim.cs.crypto.bilinear.ModifiedTatePairing;
import nuim.cs.crypto.blitz.point.AffinePoint;

public class IbeProviderTestSuite extends Suite {
    public void testCreateCipher() {
        Provider provider = new IbeProvider();
        
        // get the ibe cipher
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance( IbeProvider.IBE, provider );
        }
        catch( NoSuchAlgorithmException nsae ) {
            verify( false );
        }
        catch( NoSuchPaddingException nspe ) {
            verify( false );
        }
        
        verify( cipher != null );
    }
    
    public void testCreateKeyPairGenerator() {
        Provider provider = new IbeProvider();
        
        // get the ibe key pair generator
        KeyPairGenerator kpg = null;
        try {
            kpg = KeyPairGenerator.getInstance( IbeProvider.IBE, provider );
        }
        catch( NoSuchAlgorithmException nsae ) {
            verify( false );
        }
                
        verify( kpg != null );
    }
    
    public void testCipher() {
        Provider provider = new IbeProvider();
        
        // get the ibe cipher
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance( IbeProvider.IBE, provider );
        }
        catch( NoSuchAlgorithmException nsae ) {
            verify( false, nsae.toString() );
        }
        catch( NoSuchPaddingException nspe ) {
            verify( false, nspe.toString() );
        }
        
        // get the ibe key pair generator
        KeyPairGenerator kpg = null;
        try {
            kpg = KeyPairGenerator.getInstance( IbeProvider.IBE, provider );
        }
        catch( NoSuchAlgorithmException nsae ) {
            verify( false, nsae.toString() );
        }

        MessageDigest hash = null;
        try {
            hash = MessageDigest.getInstance( "MD5" );
        }
        catch( NoSuchAlgorithmException nsae ) {
            verify( false, nsae.toString() );
        }
        // set the system parameters
        BilinearMap map = new ModifiedTatePairing();
        boolean fieldTooSmall = false;
        do {
            fieldTooSmall = false;
            // get a count of the maximum possible number of 
            // points that we could use, i.e. p / kappa where
            // p is the upper limit of finite field Fp
            BigInteger maxPts =
                map.getCurve().getField().getChar().
                    divide( map.getCurve().getKappa() );
            int bytes = maxPts.bitLength() / 8;
            if( bytes < hash.getDigestLength() ) {
                map = new ModifiedTatePairing();
                fieldTooSmall = true;
            }                            
        }
        while( fieldTooSmall );
        // set the identity
        String identity = new String( "bob@company.com" );
        // create the parameters
        BigInteger masterKey = 
            new BigInteger( map.getQ().bitLength() - 1, new SecureRandom() );
        IbeSystemParameters systemParameters = 
            new IbeSystemParameters( map, hash, masterKey );
        IbeKeyParameters keyParameters = new IbeKeyParameters( hash, identity );
        
        PublicKey publicKey = new IbePublicKey( keyParameters.getPublicKey() );
        
        // initialise the cipher for encryption
        try {
            cipher.init( Cipher.ENCRYPT_MODE, publicKey, systemParameters, 
                new SecureRandom() );
        }
        catch( InvalidKeyException ike ) {
            verify( false, ike.toString() );
        }
        catch( InvalidAlgorithmParameterException iape ) {
            verify( false, iape.toString() );
        }
        
        // encrypt the plaintext
        byte plaintext[] = new String( "my secret message" ).getBytes();
        byte ciphertext[] = new byte[0];
        try {
            ciphertext = cipher.doFinal( plaintext );
        }
        catch( IllegalBlockSizeException ibse ) {
            verify( false, ibse.toString() );
        }
        catch( BadPaddingException bpe ) {
            verify( false, bpe.toString() );
        }
        
        // initialise the key pair generator
        keyParameters = new IbeKeyParameters( hash, identity, masterKey, map );
        try {
            kpg.initialize( keyParameters );
        }
        catch( InvalidAlgorithmParameterException iape ) {
            verify( false, iape.toString() );
        }
        KeyPair keyPair = kpg.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        
        // initialise the cipher for decryption
        try {
            cipher.init( Cipher.DECRYPT_MODE, privateKey, systemParameters, 
                new SecureRandom() );
        }
        catch( InvalidKeyException ike ) {
            verify( false, ike.toString() );
        }
        catch( InvalidAlgorithmParameterException iape ) {
            verify( false, iape.toString() );
        }
        
        // decrypt the ciphertext
        byte decryptedPlaintext[] = new byte[0];
        try {
            decryptedPlaintext = cipher.doFinal( ciphertext );
        }
        catch( IllegalBlockSizeException ibse ) {
            verify( false, ibse.toString() );
        }
        catch( BadPaddingException bpe ) {
            verify( false, bpe.toString() );
        }                
    }
}