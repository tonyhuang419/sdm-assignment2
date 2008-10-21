package nuim.cs.crypto.ibe;

import java.math.BigInteger;
import java.security.PrivateKey;

import nuim.cs.crypto.blitz.point.AffinePoint;

public class IbePrivateKey extends IbeKey implements PrivateKey {
    protected AffinePoint privateKey;
    
    public IbePrivateKey( AffinePoint privateKey ) {
        if( privateKey == null ) {
            throw new NullPointerException( "privateKey cannot be null" );
        }
        this.privateKey = privateKey;
    }
    
    public AffinePoint getPrivateKey() {
        return( privateKey );
    }
}