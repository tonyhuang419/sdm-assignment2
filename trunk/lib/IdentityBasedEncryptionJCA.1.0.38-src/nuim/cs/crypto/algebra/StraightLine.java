package nuim.cs.crypto.algebra;

import java.math.BigInteger;

import nuim.cs.crypto.blitz.field.Element;
import nuim.cs.crypto.blitz.field.Fp;
import nuim.cs.crypto.blitz.point.AffinePoint;

/**
 * Represents a line of the form <code>ax + by + c = 0</code>.
 */
public class StraightLine extends Function {
    public StraightLine( AffinePoint p, AffinePoint q, Fp fp ) {
        super( fp );
        if( p == null ) {
            throw new NullPointerException( "p cannot be null" );
        }
        if( q == null ) {
            throw new NullPointerException( "q cannot be null" );
        }
        //Element a = new Element( BigInteger.ZERO, p.x() );
        //Element b = new Element( BigInteger.ZERO, p.x() );
        //Element c = new Element( BigInteger.ZERO, p.x() );
        if( p.infinity() && q.infinity() ) {
            //c = new Element( BigInteger.ONE, p.x() );
            c = fp.element( BigInteger.ONE );
        }
        else {
            Element x = q.x().subtract( p.x() );
            Element y = q.y().subtract( p.y() );

            a = y.negate();
            b = (Element) x.clone();
            c = a.multiply( p.x() ).add( b.multiply( p.y() ) ).negate();
        }

        //set( a, b, c );
    }    
}