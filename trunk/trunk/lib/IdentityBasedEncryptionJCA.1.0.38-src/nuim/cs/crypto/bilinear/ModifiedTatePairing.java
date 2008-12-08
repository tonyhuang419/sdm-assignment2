package nuim.cs.crypto.bilinear;

import java.math.BigInteger;

import nuim.cs.crypto.algebra.Function;
import nuim.cs.crypto.blitz.constants.Constant;
import nuim.cs.crypto.blitz.field.Element;
import nuim.cs.crypto.blitz.field.Fp;
import nuim.cs.crypto.blitz.field.extension.Fp2Element;
import nuim.cs.crypto.blitz.point.AffinePoint;

public class ModifiedTatePairing extends TatePairing {
    public ModifiedTatePairing() {
        super();
    }
    
    public ModifiedTatePairing( int bitLength ) {
        super( bitLength );
    }
    
    public ModifiedTatePairing( BigInteger p, BigInteger q, BigInteger l ) {
        super( p, q, l );
    }
    
    public Element getPair( AffinePoint P, AffinePoint Q ) {
        if( P == null ) {
            throw new NullPointerException( "P cannot be null" );
        }
        if( Q == null ) {
            throw new NullPointerException( "Q cannot be null" );
        }
        AffinePoint R = AffinePoint.pointAtInfinity();
        boolean problem = true;
        while( problem ) {
            problem = false;
            try {    
                R = randomPoint();
                // if Q == R then Qhat would be equal to oo (infinity)
                if( R.infinity() || Q.equals( R ) ) {
                    problem = true;
                }
                else {
                    return( getPair( P, Q, R ) );
                }
            }
            catch( ArithmeticException ae ) {
                problem = true;
            }
        }
        return( null );
    }
    
    /**
     * Copy of miller's algorithm code to make life easier for the Fault
     * Injector tool. The tool can only work within the method specified in the
     * configuration. It does not have the capability to step into the method.
     */
    public Element getPair( AffinePoint P, AffinePoint Qin, AffinePoint Rin ) {
        if( P == null ) {
            throw new NullPointerException( "P cannot be null" );
        }
        if( Qin == null ) {
            throw new NullPointerException( "Q cannot be null" );
        }
        if( Rin == null ) {
            throw new NullPointerException( "R cannot be null" );
        }
        AffinePoint Q = morphPoint( Qin );
        AffinePoint R = morphPoint( Rin );
        
        AffinePoint R1 = AffinePoint.pointAtInfinity();
        AffinePoint R2 = (AffinePoint) R.clone();
        
        PairingEllipticCurve c = curve;
        AffinePoint Phat = c.add( P, R1 );
        AffinePoint Qhat = c.add( Q, R2 );
        
        AffinePoint Z = AffinePoint.pointAtInfinity();
        Fp fp = (Fp) c.getField();
        Function g1 = g1( P, R1 );
        Element vn1 = g1.evaluate( R2 );
        Element vd1 = g1.evaluate( Qhat );            
            
        BigInteger k = BigInteger.ZERO;
        
        Element vn = fp.element( BigInteger.ONE );
        Element vd = fp.element( BigInteger.ONE );
        AffinePoint Y = AffinePoint.pointAtInfinity();
        for( int i = q.bitLength() - 1; i >= 0; i-- ) {
            g1 = g1( Z, Z );
            // Z = 2Z
            Y = c.doublePoint( Z );
            //Z = c.doublePoint( Z );

            // fb(Aq).fc(Aq)
            Element vn_temp = vn.multiply( vn );
            Element vd_temp = vd.multiply( vd );
            //vn = vn.multiply( vn );
            //vd = vd.multiply( vd );
            // g1(Aq)/g2(Aq)
            Element t = g1.evaluate( Qhat );
            Element b = g1.evaluate( R2 );

            vn = vn_temp.multiply( t );
            vd = vd_temp.multiply( b );
            //vn = vn.multiply( t );
            //vd = vd.multiply( b );

            // k = 2k
            k = k.multiply( Constant.TWO );
            // test to see if bit i is set to one
            if( q.testBit( i ) ) {
                //g1 = g1( Z, P );
                g1 = g1( Y, P );
                // Z = Z + P
                Z = c.add( Y, P );
                //Z = c.add( Z, P );
                                               
                // fb(Aq).fc(Aq)
                vn_temp = vn.multiply( vn1 );
                vd_temp = vd.multiply( vd1 );
                //vn = vn.multiply( vn1 );
                //vd = vd.multiply( vd1 );
                // g1(Aq)/g2(Aq)
                t = g1.evaluate( Qhat );
                b = g1.evaluate( R2 );
                
                vn = vn_temp.multiply( t );
                vd = vd_temp.multiply( b );
                //vn = vn.multiply( t );
                //vd = vd.multiply( b );                
                
                // k = k + 1
                k = k.add( BigInteger.ONE );
            }
            else {
                Z = Y;
            }
        }
        // note that k must be equal to q at this point
        Element result = vn.divide( vd );
        // calculate the exponent result^(p^2 - 1)/q
        BigInteger exp = 
            c.getField().getChar().pow( 2 ).subtract( BigInteger.ONE ).divide( q );
        
        return( result.modPow( exp ) );
    }

    protected Element millersAlgorithm( AffinePoint P, AffinePoint Phat, 
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
        PairingEllipticCurve c = curve;
        AffinePoint Z = AffinePoint.pointAtInfinity();
        Fp fp = (Fp) curve.getField();
        Function g1 = g1( P, R1 );
        Element vn1 = g1.evaluate( R2 );
        Element vd1 = g1.evaluate( Qhat );            
            
        BigInteger k = BigInteger.ZERO;
        
        Element vn = fp.element( BigInteger.ONE );
        Element vd = fp.element( BigInteger.ONE );
        AffinePoint Y = AffinePoint.pointAtInfinity();
        for( int i = q.bitLength() - 1; i >= 0; i-- ) {
            g1 = g1( Z, Z );
            // Z = 2Z
            Y = c.doublePoint( Z );
            //Z = c.doublePoint( Z );

            // fb(Aq).fc(Aq)
            vn = vn.multiply( vn );
            vd = vd.multiply( vd );
            // g1(Aq)/g2(Aq)
            Element t = g1.evaluate( Qhat );
            Element b = g1.evaluate( R2 );

            vn = vn.multiply( t );
            vd = vd.multiply( b );

            // k = 2k
            k = k.multiply( Constant.TWO );
            // test to see if bit i is set to one
            if( q.testBit( i ) ) {
                //g1 = g1( Z, P );
                g1 = g1( Y, P );
                // Z = Z + P
                Z = c.add( Y, P );
                //Z = c.add( Z, P );
                                               
                // fb(Aq).fc(Aq)
                vn = vn.multiply( vn1 );
                vd = vd.multiply( vd1 );
                // g1(Aq)/g2(Aq)
                t = g1.evaluate( Qhat );
                b = g1.evaluate( R2 );
                
                vn = vn.multiply( t );
                vd = vd.multiply( b );
                
                // k = k + 1
                k = k.add( BigInteger.ONE );
            }
            else {
                Z = Y;
            }
        }
        // note that k must be equal to q at this point
        
        return( vn.divide( vd ) );
    }
}