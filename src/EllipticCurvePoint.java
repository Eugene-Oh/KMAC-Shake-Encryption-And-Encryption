import java.io.Serializable;
import java.math.BigInteger;

/**
 * TCSS 487 - Final Cryptography Project - Alex Trinh, Eugene Oh
 *
 * This class represents a single point on Edward's Curve.
 */
public class EllipticCurvePoint implements Serializable {
    private BigInteger myX;
    private BigInteger myY;

    // Various constants used in arithmetic regarding computing points.
    private static BigInteger MersennePrime = new BigInteger("2").pow(521).subtract(new BigInteger("1"));

    public static BigInteger r = new BigInteger("2").pow(519).subtract(new BigInteger(
            "337554763258501705789107630418782636071" +
            "904961214051226618635150085779108655765"));

    private static BigInteger d = new BigInteger("-376014");

    public static EllipticCurvePoint G = new EllipticCurvePoint(BigInteger.valueOf(4), false);

    /**
     * Constructor for a single point.
     * @param x Sets x-coordinate of point.
     * @param y Sets y-coordinate of point.
     */
    public EllipticCurvePoint(BigInteger x, BigInteger y) {
        myX = x;
        myY = y;
    }

    /**
     * Constructor for a neutral point.
     */
    public EllipticCurvePoint() {
        myX = new BigInteger("0");
        myY = new BigInteger("1");
    }

    /**
     * Constructor for a single point using least significant bit.
     * @param x Sets x-coordinate of point.
     * @param y The desired least significant bit.
     */
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

    /**
     * Compares two points for equality.
     * @param point The other point used for comparison.
     * @return Boolean based on equality.
     */
    public boolean compare(EllipticCurvePoint point) {
        if (point.getX().equals(myX) && point.getY().equals(myY)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Calculates the opposite of a given point.
     * @param point The other point used for computation.
     * @return The opposite point.
     */
    public static EllipticCurvePoint opposite(EllipticCurvePoint point) {
        BigInteger resultX = point.getX().modInverse(MersennePrime);
        BigInteger resultY = point.getY().modInverse(MersennePrime);
        return new EllipticCurvePoint(resultX, resultY);
    }

    /**
     * Addition of current and other point based on the equation from the project specification sheet.
     * @param otherPoint The point that is used in addition to the current point.
     * @return The resulting point from calculation.
     */
    public EllipticCurvePoint addPoints(EllipticCurvePoint otherPoint) {
        BigInteger x1 = myX;
        BigInteger y1 = myY;
        BigInteger x2 = otherPoint.getX();
        BigInteger y2 = otherPoint.getY();

        BigInteger numeratorX = (x1.multiply(y2)).add(y1.multiply(x2));
        BigInteger temp = (d.multiply(x1).multiply(x2).multiply(y1).multiply(y2));
        BigInteger denominatorX = BigInteger.ONE.add(temp);
        BigInteger denominatorY = BigInteger.ONE.subtract(temp);
        BigInteger resultX = (numeratorX.multiply(denominatorX.modInverse(MersennePrime)).mod(MersennePrime));
        BigInteger numeratorY = (y1.multiply(y2)).subtract(x1.multiply(x2));
        BigInteger resultY = (numeratorY.multiply(denominatorY.modInverse(MersennePrime)).mod(MersennePrime));
        return new EllipticCurvePoint(resultX, resultY);
    }

    /**
     * Scalar multiplication formula from the pseudocode in the project specification sheet.
     * @param P The point to do the multiplication to.
     * @param s The scalar used to apply to the given point.
     * @return The calculated result from multiplcation.
     */
    public static EllipticCurvePoint scalarMultiplication(EllipticCurvePoint P, BigInteger s) {
        String temp = s.toString(2);
        int k = temp.length();
        EllipticCurvePoint V = new EllipticCurvePoint(P.getX(),P.getY());
        for (int i = k - 1; i >= 0; i--) {
            V = V.addPoints(V);
            char s_i = temp.charAt(i);
            if (s_i == '1') {
                V = V.addPoints(P);
            }
        }
        return V;
    }

    /**
     * Getter for X.
     * @return X.
     */
    public BigInteger getX() {
        return myX;
    }

    /**
     * Getter for Y.
     * @return Y.
     */
    public BigInteger getY() {
        return myY;
    }

    /**
     *
     * This is taken from the project specification sheet.
     * Compute a square root of v mod p with a specified.
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
