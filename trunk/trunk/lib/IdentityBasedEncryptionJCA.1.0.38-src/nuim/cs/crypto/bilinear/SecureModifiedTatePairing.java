package nuim.cs.crypto.bilinear;

import java.math.BigInteger;

import nuim.cs.crypto.blitz.field.Element;
import nuim.cs.crypto.blitz.field.Fp;
import nuim.cs.crypto.blitz.point.AffinePoint;
import nuim.cs.crypto.fault.exception.FaultAttackException;

public class SecureModifiedTatePairing extends ModifiedTatePairing {
    public SecureModifiedTatePairing() {
        super();
    }
    
    public SecureModifiedTatePairing( BigInteger p, BigInteger q, 
        BigInteger l ) {
        super( p, q, l );
    }
    
    public Element getPair( AffinePoint P, AffinePoint Q, AffinePoint R ) {
        if( P == null ) {
            throw new NullPointerException( "P cannot be null" );
        }
        if( Q == null ) {
            throw new NullPointerException( "Q cannot be null" );
        }
        if( R == null ) {
            throw new NullPointerException( "R cannot be null" );
        }
        AffinePoint S = AffinePoint.pointAtInfinity();
        AffinePoint QS = AffinePoint.pointAtInfinity();
        boolean problem = true;
        while( problem ) {
            problem = false;
            S = randomPoint();
            // if Q == S then Qhat would be equal to oo (infinity)
            if( S.infinity() || Q.equals( S ) || R.equals( S ) ) {
                problem = true;
            }
            QS = curve.add( Q, S );
            if( QS.infinity() || QS.equals( Q ) || QS.equals( R ) ) {
                problem = true;
            }
        }
        
        Element blind_a = super.getPair( P, QS, R );
        Element blind_b = super.getPair( P, S, R );
        Element blind = blind_a.divide( blind_b );
        Fp field = (Fp) curve.getField();
        Element a = field.random();
        AffinePoint aP = curve.multiply( a.toBigInteger(), P );
        while( aP.infinity() ) {
            a = field.random();
            aP = curve.multiply( a.toBigInteger(), P );
        }
        Element b = field.random();
        AffinePoint bQ = curve.multiply( b.toBigInteger(), Q );
        while( bQ.infinity() ) {
            b = field.random();
            bQ = curve.multiply( b.toBigInteger(), Q );
        }
        Element blind_c = super.getPair( aP, bQ, R );
        Element blind_d = 
            blind.modPow( a.toBigInteger().multiply( b.toBigInteger() ) );
        if( !blind_c.equals( blind_d ) ) {
            throw new FaultAttackException( 
                blind_c.toString() + " does not equal " + blind_d.toString() );            
        }
        
        return( blind );        
    }
    
    public Element getPair( AffinePoint P, AffinePoint Q ) {
        if( P == null ) {
            throw new NullPointerException( "P cannot be null" );
        }
        if( Q == null ) {
            throw new NullPointerException( "Q cannot be null" );
        }
        
        AffinePoint R = AffinePoint.pointAtInfinity();
        AffinePoint QR = AffinePoint.pointAtInfinity();
        boolean problem = true;
        while( problem ) {
            problem = false;
            R = randomPoint();
            // if Q == R then Qhat would be equal to oo (infinity)
            if( R.infinity() || Q.equals( R ) ) {
                problem = true;
            }
        }
        return( getPair( P, Q, R ) );
        /*
        Element blind_a = super.getPair( P, QR );
        Element blind_b = super.getPair( P, R );
        Element blind = blind_a.divide( blind_b );
        Fp field = (Fp) curve.getField();
        Element a = field.random();
        AffinePoint aP = curve.multiply( a.toBigInteger(), P );
        while( aP.infinity() ) {
            a = field.random();
            aP = curve.multiply( a.toBigInteger(), P );
        }
        Element b = field.random();
        AffinePoint bQ = curve.multiply( b.toBigInteger(), Q );
        while( bQ.infinity() ) {
            b = field.random();
            bQ = curve.multiply( b.toBigInteger(), Q );
        }
        Element blind_c = super.getPair( aP, bQ );
        Element blind_d = 
            blind.modPow( a.toBigInteger().multiply( b.toBigInteger() ) );
        if( !blind_c.equals( blind_d ) ) {
            throw new FaultAttackException( 
                blind_c.toString() + " does not equal " + blind_d.toString() );            
        }
        
        return( blind );
         */
    }
}