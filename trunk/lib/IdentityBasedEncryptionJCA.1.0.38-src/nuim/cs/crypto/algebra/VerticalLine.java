package nuim.cs.crypto.algebra;

import java.math.BigInteger;

import nuim.cs.crypto.blitz.field.Element;
import nuim.cs.crypto.blitz.field.Fp;
import nuim.cs.crypto.blitz.point.AffinePoint;

public class VerticalLine extends Function {
    public VerticalLine( AffinePoint p, Fp fp ) {
        super( fp );
        if( p == null ) {
            throw new NullPointerException( "p cannot be null" );
        }
        //Element a = new Element( BigInteger.ZERO, p.x() );
        //Element b = new Element( BigInteger.ZERO, p.x() );
        //Element c = new Element( BigInteger.ZERO, p.x() );
        if( p.infinity() ) {
            //c = new Element( BigInteger.ONE, p.x() );
            c = fp.element( BigInteger.ONE );
        }
        else {
            //a = new Element( BigInteger.ONE, p.x() );
            a = fp.element( BigInteger.ONE );
            c = p.x().negate();
        }
        
        //set( a, b, c );
    }
}