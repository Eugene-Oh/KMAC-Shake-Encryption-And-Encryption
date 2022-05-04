import java.io.Serializable;
import java.security.SecureRandom;

public class SymmetricCryptogram implements Serializable {
    byte[] z;
    byte[] c;
    byte[] t;
    public SymmetricCryptogram(byte[] z, byte[] c, byte[] t){
        this.z = z;
        this.c = c;
        this.t = t;
    }
    public byte[] getZ() {
        return this.z;
    }
    public byte[] getC() {
        return this.c;
    }
    public byte[] getT() {
        return this.t;
    }
}
