package nuim.cs.crypto.algebra;

import java.math.BigInteger;

import nuim.cs.crypto.blitz.field.Element;
import nuim.cs.crypto.blitz.field.Fp;
import nuim.cs.crypto.blitz.point.AffinePoint;

public class Function {
    protected Element a;
    protected Element b;
    protected Element c;
    
    public Function( Fp fp ) {
        set( fp.element( BigInteger.ZERO ), fp.element( BigInteger.ZERO ), 
            fp.element( BigInteger.ZERO ) );
    }
    
    public Element evaluate( AffinePoint p ) {
        if( p == null ) {
            throw new NullPointerException( "p cannot be null" );
        }
        if( p.infinity() ) {
            return( new Element( BigInteger.ONE, a ) );
        }
        else {
            Element r = a.multiply( p.x() );
            r = r.add( b.multiply( p.y() ) );
            r = r.add( c );
            
            return( r );
        }
    }    
    
    protected void set( Element a, Element b, Element c ) {
        if( a == null ) {
            throw new NullPointerException( "a cannot be null" );
        }
        if( b == null ) {
            throw new NullPointerException( "b cannot be null" );
        }
        if( c == null ) {
            throw new NullPointerException( "c cannot be null" );
        }
        this.a = (Element) a.clone();
        this.b = (Element) b.clone();
        this.c = (Element) c.clone();
    }
    
    public String toString() {
        return( new String( a + "x + " + b + "y + " + c ) );
    }
    
    public boolean equals( Object obj ) {
        if( obj instanceof Function ) {
            Function f = (Function) obj;
            return( a.equals( f.a ) && b.equals( f.b ) && c.equals( f.c ) );
        }
        else {
            return( false );
        }
    }
}