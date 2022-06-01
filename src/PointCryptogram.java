import java.io.Serializable;

/**
 * TCSS 487 - Final Cryptography Project - Alex Trinh, Eugene Oh
 *
 * The class used for representing the point cryptogram.
 * Contains the attributes required for representing a point cryptogram.
 */
public class PointCryptogram implements Serializable {
    EllipticCurvePoint z;
    byte[] c;
    byte[] t;

    public PointCryptogram(EllipticCurvePoint z, byte[] c, byte[] t){
        this.z = z;
        this.c = c;
        this.t = t;
    }

    public EllipticCurvePoint getZ() {
        return this.z;
    }

    public byte[] getC() {
        return this.c;
    }

    public byte[] getT() {
        return this.t;
    }
}
