package nuim.cs.crypto.algebra;

import java.math.BigInteger;

import nuim.cs.crypto.blitz.constants.Constant;
import nuim.cs.crypto.blitz.field.Element;
import nuim.cs.crypto.blitz.field.Fp;
import nuim.cs.crypto.blitz.point.AffinePoint;

public class TangentLine extends Function {
    public TangentLine( AffinePoint p, BigInteger a4, Fp fp ) {
        super( fp );
        if( p == null ) {
            throw new NullPointerException( "p cannot be null" );
        }
        if( a4 == null ) {
            throw new NullPointerException( "a4 cannot be null" );
        }
        //Element a = new Element( BigInteger.ZERO, p.x() );
        //Element b = new Element( BigInteger.ZERO, p.x() );
        //Element c = new Element( BigInteger.ZERO, p.x() );
        
        if( p.infinity() ) {
            //c = new Element( BigInteger.ONE, p.x() );
            c = fp.element( BigInteger.ONE );
        }
        else if( p.y().equals( BigInteger.ZERO ) ) {
            //a = new Element( BigInteger.ONE, p.x() );
            a = fp.element( BigInteger.ONE );
            c = p.x().negate();
        }
        else {
            Element m = p.x().multiply( Constant.THREE );
            m = m.multiply( p.x() );
            m = m.add( a4 );
            
            a = m.negate();
            
            b = p.y().add( p.y() );
            
            c = p.y().multiply( b ).negate();
            c = c.subtract( p.x().multiply( a ) );
        }
        //set( a, b, c );
    }
}