package nuim.cs.crypto.algebra;

import java.math.BigInteger;

import nuim.cs.crypto.blitz.constants.Constant;
import nuim.cs.crypto.blitz.field.Element;
import nuim.cs.crypto.blitz.field.Fp;
import nuim.cs.crypto.blitz.point.AffinePoint;

public class AltTangent extends Function {
    public AltTangent( AffinePoint p, BigInteger a4, BigInteger a6, Fp fp ) {
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
        else if( p.y().equals( BigInteger.ZERO ) ) {
            //a = new Element( BigInteger.ONE, p.x() );
            a = fp.element( BigInteger.ONE );
            c = p.x().negate();
        }
        else {
            Element m = p.x().add( p.x() ).add( p.x() );
            m = m.multiply( p.x() );
            m = m.add( a4 );
            m = m.divide( p.y().multiply( Constant.TWO ) );
            
            a = m.negate();
            //b = new Element( BigInteger.ONE, p.x() );
            b = fp.element( BigInteger.ONE );
            c = p.x().modPow( Constant.THREE ).negate();
            c = c.add( p.x().multiply( a4 ) );
            c = c.add( Constant.TWO.multiply( a6 ) );
            c = c.divide( p.y().multiply( Constant.TWO ) ).negate();
        }
        //set( a, b, c );
    }
    
    public AltTangent( AffinePoint p, BigInteger a4, Fp fp ) {
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
        else if( p.y().equals( BigInteger.ZERO ) ) {
            //a = new Element( BigInteger.ONE, p.x() );
            a = fp.element( BigInteger.ONE );
            c = p.x().negate();
        }
        else {
            Element m = p.x().add( p.x() ).add( p.x() );
            m = m.multiply( p.x() );
            m = m.add( a4 );
            m = m.divide( p.y().multiply( Constant.TWO ) );
            
            a = m.negate();
            /*
            b = p.y().add( p.y() );
            c = b.mult( p.y() );
            c = c.add( a.mult( p.x() ) );
            c = c.neg();
             */
            
            // the alternative bit
            //b = new Element( BigInteger.ONE, p.x() );
            b = fp.element( BigInteger.ONE );
            //c = p.y().neg().add( m.mult( p.x() ) );
            //c = p.y().add( a.mult( p.x() ) ).negMod();
            c = m.multiply( p.x() ).subtract( p.y() );
        }
        //set( a, b, c );
    }        
}