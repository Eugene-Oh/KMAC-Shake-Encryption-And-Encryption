import java.io.Serializable;
import java.security.SecureRandom;

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
