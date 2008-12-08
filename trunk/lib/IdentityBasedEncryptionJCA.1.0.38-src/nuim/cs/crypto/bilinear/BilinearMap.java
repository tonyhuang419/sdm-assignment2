package nuim.cs.crypto.bilinear;

import java.math.BigInteger;
import java.io.Serializable;

import nuim.cs.crypto.blitz.field.Element;
import nuim.cs.crypto.blitz.point.AffinePoint;

public interface BilinearMap extends Serializable {
    public Element getPair( AffinePoint P, AffinePoint Q );    

    public AffinePoint mapToPoint( BigInteger x );
    
    public PairingEllipticCurve getCurve();
    
    public BigInteger getQ();
    
    public BigInteger getL();
}