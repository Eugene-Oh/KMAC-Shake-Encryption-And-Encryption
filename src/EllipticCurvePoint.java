import java.math.BigInteger;

// This class represents a single point on Edward's Curve.
public class EllipticCurvePoint {
    private BigInteger myX;
    private BigInteger myY;

    private BigInteger MersennePrime = new BigInteger("2").pow(521).subtract(new BigInteger("1"));
    private BigInteger r = new BigInteger("2").pow(519).subtract(new BigInteger(
            "337554763258501705789107630418782636071" +
            "904961214051226618635150085779108655765"));
    private BigInteger d = new BigInteger("-376014");

    // Constructor for given elements.
    public EllipticCurvePoint(BigInteger x, BigInteger y) {
        myX = x;
        myY = y;
    }

    // Constructor for neutral element.
    public EllipticCurvePoint() {
        myX = new BigInteger("0");
        myY = new BigInteger("1");
    }

    // Getting (x, y) from x and least significant bit of y. Equation is given in the project specification sheet.
    public EllipticCurvePoint(BigInteger x, boolean y) {
        myX = x;
        BigInteger numerator = BigInteger.valueOf(1).subtract(x.modPow(BigInteger.valueOf(2), MersennePrime));
        BigInteger denominator = BigInteger.valueOf(1).add(BigInteger.valueOf(376014).multiply(x.modPow(BigInteger.valueOf(2), MersennePrime)));
        // From the in-class whiteboard example.
        BigInteger divide =  numerator.multiply(denominator.modInverse(MersennePrime)).mod(MersennePrime);
        BigInteger result = sqrt(divide, MersennePrime, y);
        if (result == null) {
            myY = BigInteger.valueOf(1);
        } else {
            myY = result;
        }
    }

    public boolean compare(EllipticCurvePoint point) {
        if (point.getX().equals(myX) && point.getY().equals(myY)) {
            return true;
        } else {
            return false;
        }
    }

    public EllipticCurvePoint opposite(EllipticCurvePoint point) {
        BigInteger resultX = point.getX().modInverse(MersennePrime);
        BigInteger resultY = point.getY().modInverse(MersennePrime);
        return new EllipticCurvePoint(resultX, resultY);
    }

    public EllipticCurvePoint addPoints(EllipticCurvePoint otherPoint) {
        BigInteger x1 = myX;
        BigInteger y1 = myY;
        BigInteger x2 = otherPoint.getX();
        BigInteger y2 = otherPoint.getY();

        BigInteger numeratorX = (x1.multiply(y2)).add(y1.multiply(x2));
        BigInteger denominator = (d.multiply(x1).multiply(x2).multiply(y1).multiply(y2)).add(BigInteger.valueOf(1));
        BigInteger resultX = (numeratorX.multiply(denominator.modInverse(MersennePrime)));

        BigInteger numeratorY = (y1.multiply(y2)).subtract(x1.multiply(x2));
        BigInteger resultY = (numeratorY.multiply(denominator.modInverse(MersennePrime)));

        return new EllipticCurvePoint(resultX, resultY);
    }

    // Scalar multiplication formula from the pseudocode in the project specification sheet.
    public EllipticCurvePoint scalarMultiplcation(EllipticCurvePoint P, BigInteger s) {
        EllipticCurvePoint V = P;
        String temp = s.toString();
        int k = temp.length();
        for (int i = k - 1; i >= 0; i--) {
            V.addPoints(V);
            if (temp.charAt(i) == '1') {
                V = V.addPoints(P);
            }
        }
        return V;
    }

    public BigInteger getX() {
        return myX;
    }

    public BigInteger getY() {
        return myY;
    }

    /**
     * Compute a square root of v mod p with a specified
     * least significant bit, if such a root exists.
     *
     * @param v the radicand.
     * @param p the modulus (must satisfy p mod 4 = 3).
     * @param lsb desired least significant bit (true: 1, false: 0).
     * @return a square root r of v mod p with r mod 2 = 1 iff lsb = true
     * if such a root exists, otherwise null.
     */
    public static BigInteger sqrt(BigInteger v, BigInteger p, boolean lsb) {
        assert (p.testBit(0) && p.testBit(1)); // p = 3 (mod 4)
        if (v.signum() == 0) {
            return BigInteger.ZERO;
        }
        BigInteger r = v.modPow(p.shiftRight(2).add(BigInteger.ONE), p);
        if (r.testBit(0) != lsb) {
            r = p.subtract(r); // correct the lsb
        }
        return (r.multiply(r).subtract(v).mod(p).signum() == 0) ? r : null;
    }
}
