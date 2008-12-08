package nuim.cs.crypto.bilinear;

import java.math.BigInteger;
import java.util.Random;

import nuim.cs.crypto.algebra.AltTangent;
import nuim.cs.crypto.algebra.Function;
import nuim.cs.crypto.algebra.StraightLine;
import nuim.cs.crypto.algebra.TangentLine;
import nuim.cs.crypto.algebra.VerticalLine;
import nuim.cs.crypto.bilinear.PairingEllipticCurve;
import nuim.cs.crypto.blitz.constants.Constant;
import nuim.cs.crypto.blitz.curve.EllipticCurve;
import nuim.cs.crypto.blitz.field.Element;
import nuim.cs.crypto.blitz.field.Fp;
import nuim.cs.crypto.blitz.point.AffinePoint;
import nuim.cs.crypto.primality.Gordon;

/**
 * Here we use the Weil pairing calculation from Boneh and Franklin's paper.
 */
public class WeilPairing extends TatePairing {
    public WeilPairing() {
        super( 10 );
    }

    public void init( BigInteger p, BigInteger q, BigInteger l ) {
        super.init( p, q, l );
        Fp f = new Fp( p );
        this.curve = 
            new PairingEllipticCurve( BigInteger.ZERO, BigInteger.ONE, f );
    }
    
    public Element getPair( AffinePoint P, AffinePoint Q, 
        AffinePoint R ) {
        if( P == null ) {
            throw new NullPointerException( "P cannot be null" );
        }
        if( Q == null ) {
            throw new NullPointerException( "Q cannot be null" );
        }
        if( R == null ) {
            throw new NullPointerException( "R cannot be null" );
        }
        
        AffinePoint R1 = AffinePoint.pointAtInfinity();
        AffinePoint R2 = (AffinePoint) R.clone();
        
        AffinePoint Phat = curve.add( P, R1 );
        AffinePoint Qhat = curve.add( Q, R2 );

        Element t = millersAlgorithm( Q, Qhat, Phat, R2, R1 );
        Element b = millersAlgorithm( P, Phat, Qhat, R1, R2 );
        //BigInteger f = curve.getField().getChar();
        //return( t.multiply( b.modInverse( f ) ).mod( f ) );
        return( t.divide( b ) );
    }
    
    //public BigInteger millersAlgorithm( AffinePoint P, AffinePoint Phat, 
    public Element millersAlgorithm( AffinePoint P, AffinePoint Phat, 
        AffinePoint Qhat, AffinePoint R1, AffinePoint R2 ) {
        if( P == null ) {
            throw new NullPointerException( "P cannot be null" );
        }
        if( Phat == null ) {
            throw new NullPointerException( "Phat cannot be null" );
        }        
        if( Qhat == null ) {
            throw new NullPointerException( "Qhat cannot be null" );
        }        
        if( R1 == null ) {
            throw new NullPointerException( "R1 cannot be null" );
        }
        if( R2 == null ) {
            throw new NullPointerException( "R2 cannot be null" );
        }
        AffinePoint Z = AffinePoint.pointAtInfinity();
        Element vn1 = new Element( BigInteger.ONE, P.x() );
        Element vd1 = new Element( BigInteger.ONE, P.x() );
        //if( R1.equals( curve.infinity() ) ) {
        if( false ) {
            // as is
        }
        else {
            Function g1 = g1( P, R1 );
            Function g2 = g2( Phat );

            vn1 = g2.evaluate( Qhat ).multiply( g1.evaluate( R2 ) );
            vd1 = g2.evaluate( R2 ).multiply( g1.evaluate( Qhat ) );
            /*
            System.out.println( "g1 : " + g1 );
            System.out.println( "g2 : " + g2 );
            System.out.println( "P : " + P );
            System.out.println( "R1 : " + R1 );
            System.out.println( "R2 : " + R2 );
            System.out.println( "P + R1 : " + Phat );            
            System.out.println( "Qhat = Q + R2 : " + Qhat );
            System.out.println( "g1.evaluate( Qhat ) : " + g1.evaluate( Qhat ) );
            System.out.println( "g1.evaluate( R2 ) : " + g1.evaluate( R2 ) );
            System.out.println( "g2.evaluate( Qhat ) : " + g2.evaluate( Qhat ) );
            System.out.println( "g2.evaluate( R2 ) : " + g2.evaluate( R2 ) );
             */
        }
        //System.out.println( "f1 : " + f1 );        
        BigInteger k = BigInteger.ZERO;
        
        //Element v = P.x().clone( BigInteger.ONE );
        Element vn = new Element( BigInteger.ONE, P.x() );
        Element vd = new Element( BigInteger.ONE, P.x() );
        for( int i = q.bitLength() - 1; i >= 0; i-- ) {
            // test to see if bit i is set to one
            if( q.testBit( i ) ) {
                Function g1 = g1( Z, P );
                // Z = Z + P
                Z = curve.add( Z, P );
                Function g2 = g2( Z );
                                               
                // fb(Aq).fc(Aq)
                vn = vn.multiply( vn1 );
                vd = vd.multiply( vd1 );
                // g1(Aq)/g2(Aq)
                Element t = g1.evaluate( Qhat ).multiply( g2.evaluate( R2 ) );
                Element b = g1.evaluate( R2 ).multiply( g2.evaluate( Qhat ) );
                
                vn = vn.multiply( t );
                vd = vd.multiply( b );
                /*
                System.out.println( "g1 : " + g1 );
                System.out.println( "g2 : " + g2 );
                System.out.println( "Z + P : " + Z );
                System.out.println( "g1.evaluate( Qhat ) : " + g1.evaluate( Qhat ) );
                System.out.println( "g1.evaluate( R2 ) : " + g1.evaluate( R2 ) );
                System.out.println( "g2.evaluate( Qhat ) : " + g2.evaluate( Qhat ) );
                System.out.println( "g2.evaluate( R2 ) : " + g2.evaluate( R2 ) );
                /*
                if( t.equals( BigInteger.ZERO ) ) {
                    System.out.println( "g1.evaluate( Qhat ) : " + g1.evaluate( Qhat ) );
                    System.out.println( "g2.evaluate( R2 ) : " + g2.evaluate( R2 ) );
                    System.out.println( "Top true t = " + t + ", g1 = " + g1 + ", g2 = " + g2 + ", Qhat = " + Qhat + ", R2 = " + R2 );
                }                
                if( b.equals( BigInteger.ZERO ) ) {
                    System.out.println( "Bottom true b = " + b + ", g1 = " + g1 + ", g2 = " + g2 + ", Qhat = " + Qhat + ", R2 = " + R2 );
                } 
                 */               
                
                // k = k + 1
                k = k.add( BigInteger.ONE );
            }
            if( i > 0 ) {
                Function g1 = g1( Z, Z );
                // Z = 2Z
                Z = curve.doublePoint( Z );
                Function g2 = g2( Z );
                
                // fb(Aq).fc(Aq)
                vn = vn.multiply( vn );
                vd = vd.multiply( vd );
                // g1(Aq)/g2(Aq)
                Element t = g1.evaluate( Qhat ).multiply( g2.evaluate( R2 ) );
                Element b = g1.evaluate( R2 ).multiply( g2.evaluate( Qhat ) );

                vn = vn.multiply( t );
                vd = vd.multiply( b );
                /*
                System.out.println( "g1 : " + g1 );
                System.out.println( "g2 : " + g2 );
                System.out.println( "2Z : " + Z );
                System.out.println( "g1.evaluate( Qhat ) : " + g1.evaluate( Qhat ) );
                System.out.println( "g1.evaluate( R2 ) : " + g1.evaluate( R2 ) );
                System.out.println( "g2.evaluate( Qhat ) : " + g2.evaluate( Qhat ) );
                System.out.println( "g2.evaluate( R2 ) : " + g2.evaluate( R2 ) );
                /*
                if( t.equals( BigInteger.ZERO ) ) {
                    System.out.println( "Top greater t = " + t + ", g1 = " + g1 + ", g2 = " + g2 + ", Qhat = " + Qhat + ", R2 = " + R2 );
                }                
                if( b.equals( BigInteger.ZERO ) ) {
                    System.out.println( "Bottom greater b = " + b + ", g1 = " + g1 + ", g2 = " + g2 + ", Qhat = " + Qhat + ", R2 = " + R2 );
                } 
                 */               
                
                // k = 2k
                k = k.multiply( Constant.TWO );
            }
        }     
        /*
        System.out.println( "k : " + k );
        System.out.println( "vn : " + vn );
        System.out.println( "vd : " + vd );
         */
        //return( vn.divMod( vd ).toBigInteger() );
        return( vn.divide( vd ) );
    }
    
    /**
     * Implementation of MapToPoint from Boneh and Franklin's "Identity-Based
     * Encryption from the Weil Pairing", Section 5.2.
     *
     * @param y y value of the point on the curve.
     * @return valid point on the curve.
     * @throws ArithmeticException if non-infinity points of order dividing l
     * are encountered.
     */
    public AffinePoint mapToPoint( BigInteger y ) {
        if( y == null ) {
            throw new NullPointerException( "y cannot be null" );
        }
        BigInteger x = null;
        AffinePoint P = curve.getPoint( x, y );
        // p + 1 = lq => l = (p + 1)/q
        AffinePoint lP = curve.multiply( l, P );
        if( lP.infinity() ) {
            throw new ArithmeticException( "lP = oo (infinity)" );
        }
        else {
            return( lP );
        }
    }    
}
