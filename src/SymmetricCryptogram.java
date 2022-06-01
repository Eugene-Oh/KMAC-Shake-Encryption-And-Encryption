import java.io.Serializable;

/**
 * TCSS 487 - Final Cryptography Project - Alex Trinh, Eugene Oh
 *
 * The class used for representing the symmetric cryptogram.
 * Contains the attributes required for representing a symmetric cryptogram.
 */
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
