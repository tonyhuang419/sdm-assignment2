package nuim.cs.crypto.ibe;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import nuim.cs.crypto.bilinear.BilinearMap;
import nuim.cs.crypto.blitz.point.AffinePoint;

public class IbeSystemParameters implements AlgorithmParameterSpec {
    protected BilinearMap map;
    protected AffinePoint P;
    protected AffinePoint Ppub;
    protected MessageDigest hash;
        
    public IbeSystemParameters( BilinearMap map, MessageDigest hash, 
        BigInteger masterKey ) {
        if( map == null ) {
            throw new NullPointerException( "map cannot be null" );
        }
        if( hash == null ) {
            throw new NullPointerException( "hash cannot be null" );
        }
        if( masterKey == null ) {
            throw new NullPointerException( "masterKey cannot be null" );
        }
        if( masterKey.compareTo( BigInteger.ZERO ) <= 0 ) {
            throw new IllegalArgumentException( 
                "masterKey must be greater than zero" );
        }
        this.map = map;
        this.P = map.getCurve().randomPoint();
        this.Ppub = map.getCurve().multiply( masterKey, P );
        setHash( hash );
    }
    
    public IbeSystemParameters( BilinearMap map, AffinePoint P, 
        AffinePoint Ppub, MessageDigest hash ) {        
        if( map == null ) {
            throw new NullPointerException( "map cannot be null" );
        }
        if( P == null ) {
            throw new NullPointerException( "P cannot be null" );
        }
        if( Ppub == null ) {
            throw new NullPointerException( "Ppub cannot be null" );
        }
        if( hash == null ) {
            throw new NullPointerException( "hash cannot be null" );
        }
        this.map = map;
        this.P = P;
        this.Ppub = Ppub;
        setHash( hash );
    }
    
    public BilinearMap getMap() {
        return( map );
    }
    
    public AffinePoint getP() {
        return( P );
    }
    
    public AffinePoint getPpub() {
        return( Ppub );
    }
    
    public void setHash( MessageDigest hash ) {
        if( hash == null ) {
            throw new NullPointerException( "hash cannot be null" );
        }
        this.hash = hash;
    }
    
    public MessageDigest getHash() {
        return( hash );
    }
}