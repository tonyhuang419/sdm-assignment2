package nuim.cs.crypto.md;

import java.math.BigInteger;
import java.security.MessageDigest;

public class XORMessageDigest extends MessageDigest {
    protected byte digest[];
    protected int nBytes;
    protected int bits;
    
    public XORMessageDigest( int bits ) {
        super( "XOR Message Digest" );
        if( bits <= 0 ) {
            throw new IllegalArgumentException( 
                "bits has to be greater than zero" );
        }
        this.bits = bits;
        engineReset();
    }
    
    /** 
     * Resets the digest for further use.
     */
    protected void engineReset() {
        int bytes = bits / 8;
        if( ( bits % 8 ) > 0 ) {
            bytes++;
        }
        this.digest = new byte[bytes];
        this.nBytes = 0;
    }
    
    /** 
     * Updates the digest using the specified array of bytes,
     * starting at the specified offset.
     *
     * @param input the array of bytes to use for the update.
     * @param offset the offset to start from in the array of bytes.
     * @param len the number of bytes to use, starting at
     * <code>offset</code>.
     */
    protected void engineUpdate( byte[] input, int offset, int len ) {
        for( int i = offset; i < ( offset + len ); i++ ) {
            engineUpdate( input[i] );
        }
    }    

    /** 
     * Updates the digest using the specified byte.
     * @param input the byte to use for the update.
     */
    protected void engineUpdate( byte input ) {
        // get the byte from the array to be updated
        byte hash = digest[nBytes];
        // update the hash
        hash = (byte) ( hash ^ input );
        digest[nBytes] = hash;
        nBytes = ( nBytes + 1 ) % digest.length;
    }
    
    /** 
     * Completes the hash computation by performing final
     * operations such as padding. Once <code>engineDigest</code> has
     * been called, the engine should be reset (see
     * {@link #engineReset() engineReset}).
     * Resetting is the responsibility of the
     * engine implementor.
     *
     * @return the array of bytes for the resulting hash value.
     */
    protected byte[] engineDigest() {
        // make sure the byte array value is less than the number of bits
        byte b = digest[digest.length - 1];
        if( b != 0 ) {
            int valid = bits % 8;
            byte max = (byte) ( 2 ^ valid );
            while( max > b ) {
                b = (byte) ( b >>> 1 );
            }
            digest[digest.length - 1] = b;
        }
        // not sure about this!
        /*
        BigInteger i = new BigInteger( 1, digest );
        if( i.compareTo( BigInteger.ZERO ) == 0 ) {
            digest[0] = 1;
        }
         */
        // end not-sure code
        return( digest );
    }
    
    protected int engineGetDigestLength() {
        return( bits / 8 );
    }
}